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
					<p class="lead">Create Simulation</p>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-lg-6">
				<div class="well bs-component">
					<form class="form-horizontal">
						<fieldset>
							<legend>Topology</legend>
							<div class="form-group">
								<label for="hosts" class="col-lg-2 control-label">Hosts</label>
								<div class="col-lg-10">
									<select class="form-control" id="hosts" name="hosts">
										<option value="1">1</option>
										<option value="2" selected="true">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label for="rings" class="col-lg-2 control-label">Rings</label>
								<div class="col-lg-10">
									<select class="form-control" id="rings" name="rings">
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3" selected="true">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label for="fanout" class="col-lg-2 control-label">Fanout
									factor</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="fanout"
										placeholder="0.3" value="0.3">
								</div>
							</div>

						</fieldset>
					</form>
				</div>

				<div class="well bs-component">
					<form class="form-horizontal">
						<fieldset>
							<legend>Simulation settings</legend>
							<div class="form-group">
								<label for="traffic" class="col-lg-2 control-label">Traffic</label>
								<div class="col-lg-10">
									<select class="form-control" id="traffic" name="traffic">
										<option value="5">Light</option>
										<option value="10" selected="true">Medium</option>
										<option value="15">Heavy</option>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label for="attacks" class="col-lg-2 control-label">Attacks</label>
								<div class="col-lg-10">
									<select class="form-control" id="attacks" name="attacks">
										<option value="1" selected="true">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
									</select>
								</div>
							</div>

							<div class="form-group">
								<label for="repetitions" class="col-lg-2 control-label">Repetitions</label>
								<div class="col-lg-10">
									<select class="form-control" id="repetitions"
										name="repetitions">
										<option value="1">1</option>
										<option value="2" selected="true">2</option>
										<option value="5">5</option>
										<option value="10">10</option>
									</select>
								</div>
							</div>

						</fieldset>
					</form>
				</div>
				
				<div class="well bs-component">
					<form class="form-horizontal">
						<fieldset>
							<legend>Notification</legend>
							<div class="form-group">
								<label for="email" class="col-lg-2 control-label">Email</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="email"
										placeholder="abdurrahim.ceg@gmail.com" value="abdurrahim.ceg@gmail.com">
								</div>
							</div>

						</fieldset>
					</form>
				</div>
				
			</div>
			<div class="col-lg-6">
				<div class="well bs-component">
					<form class="form-horizontal">
						<fieldset>
							<legend>Parameters</legend>
							<div class="form-group">
								<label class="col-lg-2 control-label" for="alpha">&#945;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="alpha"
										placeholder="0.8" value="0.8">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="beta">&#946;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="beta"
										placeholder="0.4" value="0.4">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="gamma">&#947;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="gamma"
										placeholder="0.4" value="0.4">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="b1">b1</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="b1"
										placeholder="1.0" value="1.0">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="b2">b2</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="b2"
										placeholder="0.65" value="0.65">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="b3">b3</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="b3"
										placeholder="0.8" value="0.8">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="epsilon">&#1013;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="epsilon"
										placeholder="0.01" value="0.01">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="omega">&#969;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="omega"
										placeholder="0.05" value="0.05">
								</div>
							</div>

							<div class="form-group">
								<label class="col-lg-2 control-label" for="upsilon">&#965;</label>
								<div class="col-lg-10">
									<input type="text" class="form-control" id="upsilon"
										placeholder="0.05" value="0.05">
								</div>
							</div>

						</fieldset>
					</form>
				</div>

			</div>
		</div>

		<div class="row" style="margin-bottom: 15px;">
			<button id="submitsimulationbutton" type="button" class="btn btn-primary btn-lg">Submit
				simulation</button>
		</div>

	</div>


	<script src="js/jquery-1.10.2.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootswatch.js"></script>
	
	<script type="text/javascript">
		
		
		
	</script>

</body>
</html>