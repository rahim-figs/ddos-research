package org.ddosm;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withTaskName;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import simulation.Simulation;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

@SuppressWarnings("serial")
public class SubmitSimulationServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String simulationId = "S_" + System.currentTimeMillis();
		
		EntityManager entityManager = Simulation.getEntityManager();
		try {
			SimulationEntity simulationEntity = new SimulationEntity();
			simulationEntity.setId(simulationId);
			simulationEntity.setSubmitted(System.currentTimeMillis());
			
			entityManager.persist(simulationEntity);
		} finally {
			entityManager.close();
		}
		
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(
				withTaskName(simulationId)
				.url("/worker")
				.param("hosts", request.getParameter("hosts"))
				.param("rings", request.getParameter("rings"))
				.param("fanout", request.getParameter("fanout"))
				.param("traffic", request.getParameter("traffic"))
				.param("attacks", request.getParameter("attacks"))
				.param("repetitions", request.getParameter("repetitions"))
				.param("alpha", request.getParameter("alpha"))
				.param("beta", request.getParameter("beta"))
				.param("gamma", request.getParameter("gamma"))
				.param("b1", request.getParameter("b1"))
				.param("b2", request.getParameter("b2"))
				.param("b3", request.getParameter("b3"))
				.param("epsilon", request.getParameter("epsilon"))
				.param("omega", request.getParameter("omega"))
				.param("upsilon", request.getParameter("upsilon"))
				.param("email", request.getParameter("email"))
				.param("simulationid", simulationId)
			);
	}
}
