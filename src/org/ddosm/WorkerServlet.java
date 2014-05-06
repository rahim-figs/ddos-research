package org.ddosm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simulation.Attack;
import simulation.Simulation;
import simulation.Topology;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class WorkerServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String simulationId = request.getParameter("simulationid");
		
		JSONObject parametersHash = new JSONObject();
		try {
			parametersHash.put("hosts", request.getParameter("hosts"));
			parametersHash.put("rings", request.getParameter("rings"));
			parametersHash.put("fanout", request.getParameter("fanout"));
			parametersHash.put("traffic", request.getParameter("traffic"));
			parametersHash.put("attacks", request.getParameter("attacks"));
			parametersHash.put("repetitions", request.getParameter("repetitions"));
			parametersHash.put("alpha", request.getParameter("alpha"));
			parametersHash.put("beta", request.getParameter("beta"));
			parametersHash.put("gamma", request.getParameter("gamma"));
			parametersHash.put("b1", request.getParameter("b1"));
			parametersHash.put("b2", request.getParameter("b2"));
			parametersHash.put("b3", request.getParameter("b3"));
			parametersHash.put("epsilon", request.getParameter("epsilon"));
			parametersHash.put("omega", request.getParameter("omega"));
			parametersHash.put("upsilon", request.getParameter("upsilon"));
		} catch (JSONException e) {
		}
		
		EntityManager entityManager = Simulation.getEntityManager();
		try {
			SimulationEntity simulationEntity = entityManager.find(SimulationEntity.class, simulationId);
			simulationEntity.setStarted(System.currentTimeMillis());
			simulationEntity.setParametersHash(new Text(parametersHash.toString()));
			
			entityManager.persist(simulationEntity);
		} finally {
			entityManager.close();
		}
		
		int hosts = Integer.parseInt(request.getParameter("hosts"));
		int rings = Integer.parseInt(request.getParameter("rings"));
		int traffic = Integer.parseInt(request.getParameter("traffic"));
		int attacks = Integer.parseInt(request.getParameter("attacks"));
		int repetitions = Integer.parseInt(request.getParameter("repetitions"));
		
		float fanout = Float.parseFloat(request.getParameter("fanout"));
		float alpha = Float.parseFloat(request.getParameter("alpha"));
		float beta = Float.parseFloat(request.getParameter("beta"));
		float gamma = Float.parseFloat(request.getParameter("gamma"));
		float b1 = Float.parseFloat(request.getParameter("b1"));
		float b2 = Float.parseFloat(request.getParameter("b2"));
		float b3 = Float.parseFloat(request.getParameter("b3"));
		float epsilon = Float.parseFloat(request.getParameter("epsilon"));
		float omega = Float.parseFloat(request.getParameter("omega"));
		float upsilon = Float.parseFloat(request.getParameter("upsilon"));
		
		long startTime = System.currentTimeMillis();
		
		Simulation simulation = new Simulation()
			.setGamma(gamma)
			.setAlpha(alpha)
			.setA(0.5f)
			.setB1(b1)
			.setB2(b2)
			.setB3(b3)
			.setEpsilon(epsilon)
			.setOmega(omega)
			.setBeta(beta)
			.setTau(0.5f)
			.setUpsilon(upsilon)
			.init();
		
		Topology topology = simulation.getTopology();
		topology.initHosts(hosts, 200, 1);
		topology.initRouters(3, fanout, rings, 1);
		topology.computeRoutes();
		
		JSONObject topologyGraph = topology.getTopologyMap();
		simulation.getStatsManager().setTopologyMap(topologyGraph);
		
		
		simulation.getAnalysisManager().addRouterSet(topology.getAllRouters());
		simulation.getAnalysisManager().addRouterSet(topology.getRings());
		
		ArrayList<Attack> attacksList = simulation.addRandomAttacks(attacks);
		
		float[] taus = { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f };
		for (int i = 0; i < taus.length; i++) {
			simulation.getStatsManager().addTau(i, taus[i]);
		}
		simulation.getStatsManager().setSimulationsPerParameter(repetitions);
		
		for (int i = 0; i < 9 * repetitions; i++) {
			
			System.out.println("########################################    " + i);
			
			simulation.setTau(taus[i / repetitions]);
			
			simulation.getEventManager().reset();
			simulation.getAnalysisManager().reset();
			
			for (Attack attack : attacksList) {
				simulation.getAnalysisManager().addAttack(attack);
				topology.generateAttack(attack);
			}
			
			topology.generateTraffic(
					rings, // ring
					5, // number of routers
					1, // begin
					5, // interval
					10, // steps
					traffic // number of packets
				);
			
			simulation.getEventManager().play();
			simulation.getAnalysisManager().computeRates(i);
			
			simulation.getStatsManager().updatePacketsCount();
			
		}
		
		simulation.getAnalysisManager().displayDetectionRate();
		simulation.getAnalysisManager().displayFalsePositives();
		
		simulation.getAnalysisManager().displayAverageDetectionRate(repetitions);
		simulation.getAnalysisManager().displayAverageFalsePositive(repetitions);
		
		simulation.getAnalysisManager().displayIPSEffectivenessRate();
		
		String snapshot = simulation.getStatsManager().generateSimulationSnapShot().toString();
		
		entityManager = Simulation.getEntityManager();
		try {
			SimulationEntity simulationEntity = entityManager.find(SimulationEntity.class, simulationId);
			simulationEntity.setFinished(System.currentTimeMillis());
			simulationEntity.setResultHash(new Text(snapshot));
			
			entityManager.persist(simulationEntity);
		} finally {
			entityManager.close();
		}
		
		String email = request.getParameter("email");
		if (email != null) {
			sendMail("DDOS - Simulation done!", "Your simulation job <a href=http://rahim-ddos-research.appspot.com/view.jsp?id=" + simulationId + ">" + simulationId + "</a> completed successfully!", email);
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Simulation completed in " + ((endTime - startTime) / 1000) + " seconds");
		System.out.println("***********************");
	}
	
	public static void sendMail(String subject, String message, String user) {
		
		if (subject == null || message == null || user == null) {
			return;
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		try {
			
			Multipart multipart = new MimeMultipart();
			
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(message, "text/html");
			multipart.addBodyPart(messageBodyPart);
			
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("abdurrahim.ceg@gmail.com",
                    "DDOS"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    user, "Snoop user"));
            
            msg.setSubject(subject);
            msg.setContent(multipart);
            Transport.send(msg);
 
        } catch (Exception e) {
        	Logger.getLogger("Mail").log(Level.SEVERE, "Sending Email failed: " + e);
        }
		
	}
		
}
