package org.ddosm;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simulation.Simulation;

@SuppressWarnings("serial")
public class DDOS_mitigationServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		
		
		
		
		
		
		// Temp
		
		String simulationId = ";";
		
		EntityManager entityManager = Simulation.getEntityManager();
		
		/*
		int packetsCount = 0;
		int rings = 0;
		int routers = 0;
		int hosts = 0;
		String topologyMapString = "";
		
		int attacksCount = 0;
		int attactsDetected = 0;
		
		String falsePositivesString = "";
		String ipsParticipationString = "";
		String scoreTransmissionString = "";
		
		float fanout = 0;
		int repetitions = 0;
		float alpha = 0;
		float beta = 0;
		float gamma = 0;
		float b1 = 0;
		float b2 = 0;
		float b3 = 0;
		float epsilon = 0;
		float omega = 0;
		float upsilon = 0;
		float ipsParticipationAverage = 0;
		float additionalTrafficInjectionPercent = 0;
		
		try {
			SimulationEntity simulationEntity = entityManager.find(SimulationEntity.class, simulationId);
			String parameterHashString = simulationEntity.getParametersHash().getValue();
			String resultHashString = simulationEntity.getResultHash().getValue();
			
			JSONObject parameterHash = new JSONObject(parameterHashString);
			JSONObject resultHash = new JSONObject(resultHashString);
			
			packetsCount = resultHash.getInt("packetsanalysed");
			rings = parameterHash.getInt("rings");
			hosts = parameterHash.getInt("hosts");
			routers = resultHash.getInt("routers");
			
			topologyMapString = resultHash.getJSONObject("topologymap").toString();
			
			attacksCount = resultHash.getInt("attackscount");
			attactsDetected = resultHash.getInt("attacksdetected");
			
			falsePositivesString = resultHash.getJSONObject("falsepositives").toString();
			ipsParticipationString = resultHash.getJSONObject("ipsparticipation").toString();
			scoreTransmissionString = resultHash.getJSONObject("scoretransmission").toString();
			
			fanout = (float) parameterHash.getDouble("fanout");
			repetitions = parameterHash.getInt("repetitions");
			alpha = (float) parameterHash.getDouble("alpha");
			beta = (float) parameterHash.getDouble("beta");
			gamma = (float) parameterHash.getDouble("gamma");
			b1 = (float) parameterHash.getDouble("b1");
			b2 = (float) parameterHash.getDouble("b2");
			b3 = (float) parameterHash.getDouble("b3");
			epsilon = (float) parameterHash.getDouble("epsilon");
			omega = (float) parameterHash.getDouble("omega");
			upsilon = (float) parameterHash.getDouble("upsilon");
			
			ipsParticipationAverage = (float) resultHash.getDouble("ipsparticipationaverage");
			additionalTrafficInjectionPercent = (float) resultHash.getDouble("additionaltrafficinjection");
			
		} catch (JSONException e) {
		} finally {
			entityManager.close();
		}
		*/
		
		try {
			Query query = entityManager.createQuery(String.format("select from %s as %s", SimulationEntity.class.getName(), SimulationEntity.class.getName()));
			List<SimulationEntity> simulations = (List<SimulationEntity>) query.getResultList();
			
			for (SimulationEntity simulation : simulations) {
				
				String statusString = "";
				String idString = simulation.getId();
				
				if (simulation.getFinished() == 0 && simulation.getStarted() == 0) {
					statusString = "Submitted on " + JSONParserUtils.getDateString(simulation.getSubmitted(), "Asia/Calcutta");
				} else if (simulation.getFinished() == 0) {
					statusString = "Started on " + JSONParserUtils.getDateString(simulation.getStarted(), "Asia/Calcutta");
				} else {
					statusString = "Finished on " + JSONParserUtils.getDateString(simulation.getFinished(), "Asia/Calcutta");
					idString = "<a href=view.jsp?id=" + simulation.getId() + ">" + simulation.getId() + "</a>";
				}
				
			}
		} finally {
			entityManager.close();
		}
	}
}
