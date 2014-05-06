<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="simulation.Simulation" %>
<%@ page import="org.ddosm.SimulationEntity" %>
<%@ page import="org.ddosm.JSONParserUtils" %>
<%@ page import="javax.persistence.Query" %>
<%@ page import="java.util.List" %>
<%@ page import="java.io.IOException" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<title>DMCM</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="http://bootswatch.com/spacelab/bootstrap.css" media="screen">
<link rel="stylesheet"
	href="http://bootswatch.com/assets/css/bootswatch.min.css">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="../bower_components/html5shiv/dist/html5shiv.js"></script>
      <script src="../bower_components/respond/dest/respond.min.js"></script>
    <![endif]-->

</head>
<body class="">

	<div class="container">

		<div class="page-header" id="banner">
			<div class="row">
				<div class="col-lg-6">
					<h1>DMCM</h1>
					<p class="lead">Simulations</p>
				</div>
			</div>
		</div>

		<hr/>

		<div class="row" style="margin-bottom: 15px;">
			<button id="createsimulationbutton" type="button" class="btn btn-primary btn-lg">Create
				simulation</button>
		</div>

		<!-- Tables
      ================================================== -->
      <div class="bs-docs-section">

        <div class="row">
          <div class="col-lg-12">
            <div class="page-header">
              <h1 id="tables">Simulations</h1>
            </div>

            <div class="bs-component">
              <table class="table table-striped table-hover ">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Simulation ID</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>

                <%
                	EntityManager entityManager = Simulation.getEntityManager();
                	try {
						Query query = entityManager.createQuery(String.format("select from %s as %s", SimulationEntity.class.getName(), SimulationEntity.class.getName()));
						List<SimulationEntity> simulations = (List<SimulationEntity>) query.getResultList();
						
						int i = 0;
						for (SimulationEntity simulation : simulations) {
							i++;
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

							 %>

							 	<tr>
				                    <td><%= i %></td>
				                    <td><%= idString %></td>
				                    <td><%= statusString %></td>
				                </tr>

							 <%
							
						}
					} finally {
						entityManager.close();
					}
				%>	
               
                  
                </tbody>
              </table> 
          </div>
        </div>
      </div>

	</div>


	<script src="js/jquery-1.10.2.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootswatch.js"></script>
	
	<script type="text/javascript">
		
		$(document).ready(function() {
			$("#createsimulationbutton").click(function() {
				window.location = "create.jsp";
			});
		});
		
	</script>

</body>
</html>