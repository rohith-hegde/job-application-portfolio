<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<!-- <meta name="viewport" content="width=device-width, initial-scale=1.0"> -->
		<script type="text/javascript" src="resources/jquery-2.1.3.min.js"></script>
		<script type="text/javascript" src="resources/jquery.cookie.js"></script>
		<link rel="stylesheet" type="text/css" href="resources/bootstrap/css/bootstrap-theme.min.css"/>
		<link rel="stylesheet" type="text/css" href="resources/bootstrap/css/bootstrap.min.css"/>
		<script type="text/javascript" src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<link rel="stylesheet" type="text/css" href="styles.css"/>
		<script type="text/javascript" src="scripts.js"></script>
		<title>Unturned3SL | Home</title>
	</head>
	
	<body>
		<div class="container">
			<div class="row">
				<div class="col-lg-3"> <!-- HammerHost branding and logo -->
				</div>
				
				<div class="col-lg-9">
					<h1 style="display: inline;">Unturned3SL 
						<small style="padding-right: 25px;">Live web 3.x server list</small>
						<span class="label label-warning">DEV</span>
					</h1>
					
					<br><br>
					<nav class="navbar navbar-default" role="navigation">
   						<div class="navbar-header">
      						<a class="navbar-brand" href="index.jsp">Unturned3SL</a>
					   </div>
					   <div>
					      <ul class="nav navbar-nav">
					         <li><a href="statistics.jsp">Statistics</a></li>
					         <li><a href="search.jsp">Search</a></li>
					         <li><a href="about.jsp">About</a></li>
					      </ul>
					   </div>
					</nav>
				</div>
			</div>
			
			<div class="row">
				<div class="col-lg-3"> <!-- sidebar with quick list of servers (and advertisements) -->
					<div class="panel panel-default">
   						<div class="panel-heading">
      						<h3 class="panel-title">Server list</h3>
   						</div>
   						<div class="panel-body">
   							<!-- <a data-toggle="collapse" data-target="#server_45-89-132-67-27015_body">45.89.132.76:27015</a><br>
   							<a data-toggle="collapse" data-target="#server_45-89-132-76-25443_body">45.89.132.76:25443</a><br>
   							<a data-toggle="collapse" data-target="#server_173-61-132-242-27017_body">173.61.132.242:27017</a><br>
   							 -->
   							 
   							 <table id="listTools_table">
   							 	<tr>
   							 		<td><button type="button" id="listTools_expandAllButton" onclick="statusManager.listTools.toggleExpandAll();" class="btn btn-primary btn-md">Expand all</button></td>
   							 	</tr>
   							 	<tr>
   							 		<td><button type="button" id="listTools_hideQuickStatusButton" onclick="statusManager.listTools.toggleQuickStatus();" class="btn btn-primary btn-md">Hide quick status</button></td>
   							 	</tr>
   							 	
   							 	<!-- Order functions: order by title (asc./desc.), order by playercount (asc./desc.) -->
   							 	<!-- Filter functions: search for IP address/title, filter by playercount (lowest - highest), filter by vs. mode, filter by difficulty,
   							 	filter by map, filter by passworded, filter by VAC secure -->
   							 	
   							 	<tr>
								<td>
									Order by: 
									<select class="form-control">
										<option value="playercount ascending">Playercount, ascending</option>
										<option value="playercount descending">Playercount, descending</option>
										<option value="title ascending">Title, ascending</option>
										<option value="title descending">Title, descending</option>
									</select>
									<!-- <div class="dropdown">
										<button class="btn btn-default dropdown-toggle" type="button"
											id="listTools_orderByBox" data-toggle="dropdown"
											aria-expanded="true">
											Playercount, descending <span class="caret"></span>
										</button>
										<ul class="dropdown-menu" role="menu"
											aria-labelledby="dropdownMenu1">
											<li role="presentation"><a role="menuitem" tabindex="1"
												href="#">Title, ascending</a></li>
											<li role="presentation"><a role="menuitem" tabindex="2"
												href="#">Title, descending</a></li>
											<li role="presentation"><a role="menuitem" tabindex="3"
												href="#">Playercount, ascending</a></li>
											<li role="presentation"><a role="menuitem" tabindex="4"
												href="#">Playercount, descending</a></li>
										</ul>
									</div> -->
								</td>
							</tr>
   							 </table>
   							
   						</div>
					</div>
					
				</div>
				
				<!-- 
					Server data inside drop-down box titles: (domain name), server title, players online / max. players
					Player badge color: red if full, orange if 2 slots empty, green if > 2 slots empty, gray if no players
				 -->
				 
				<!-- <div class="col-lg-9">
				<div class="col-lg-3"></div> -->
				<div class="col-lg-9">
					<!-- Statistics for today, before the server list -->
					<div class="panel panel-default">
   						<div class="panel-heading">
      						<h3 class="panel-title">Daily stats</h3>
   						</div>
   						<div class="panel-body">
   							Graphs and numbers for 24-hour total playercount and 24-hour server count
   						</div>
					</div>
				
					<div class="panel-group" id="statusPanelHolder">
						<!-- ----------------------------------------- -->
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" id="server_45-89-132-67-27015_title" data-target="#server_45-89-132-67-27015_body" href="#server_45-89-132-67-27015_body">
									45.89.132.76:27015</a>
									<span class="serverTitle_quickStatus">
										&nbsp;&nbsp;&nbsp;<b>HammerHost's test server</b>
										&nbsp;&nbsp;&nbsp;<span class="label label-success">14 / 18</span>
									</span>
								</h4>
					    	</div>
					    
							<div id="server_45-89-132-67-27015_body" class="panel-collapse collapse">
								<div class="panel-body" style="position: relative;">
									<table class="serverInfo_table">
										<tr>
											<td>Players: </td>
											<td><span class="label label-success">14 / 18</span></td>
											<td>Versus mode: </td>
											<td><span class="label label-success">PvE</span></td>
											<td>Difficulty: </td>
											<td><span class="label label-warning">Normal</span></td>
											<td>VAC secure: </td>
											<td><span class="label label-success">Yes</span></td>
										</tr>
										<tr>
											<td>Map: </td>
											<td><span class="label label-default">Devtest</span></td>
											<td>Passworded: </td>
											<td><span class="label label-success">No</span></td>
											<td>Version: </td>
											<td><span class="label label-default">3.4.9.1</span></td>
										</tr>
									</table>
									
									<button type="button" style="position: absolute; top: 21px; left: 695px;" onclick="" class="btn btn-primary btn-lg serverInfo_inline">View details</button>
								</div>
							</div>
						</div>
						<!-- ----------------------------------------- -->
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" id="server_45-89-132-67-27013_title" data-target="#server_45-89-132-67-27013_body" href="#server_45-89-132-67-27013_body">
									45.89.132.76:27013</a>
									<span class="serverTitle_quickStatus">
										&nbsp;&nbsp;&nbsp;<b>HammerHost's friends's server</b>
										&nbsp;&nbsp;&nbsp;<span class="label label-warning">7 / 8</span>
									</span>
								</h4>
					    	</div>
					    
							<div id="server_45-89-132-67-27013_body" class="panel-collapse collapse">
								<div class="panel-body" style="position: relative;">
									<table class="serverInfo_table">
										<tr>
											<td>Players: </td>
											<td><span class="label label-warning">7 / 8</span></td>
											<td>Versus mode: </td>
											<td><span class="label label-danger">PvP</span></td>
											<td>Difficulty: </td>
											<td><span class="label label-gold">Gold</span></td>
											<td>VAC secure: </td>
											<td><span class="label label-success">Yes</span></td>
										</tr>
										<tr>
											<td>Map: </td>
											<td><span class="label label-default">Canada</span></td>
											<td>Passworded: </td>
											<td><span class="label label-danger">Yes</span></td>
											<td>Version: </td>
											<td><span class="label label-default">3.6.1.0</span></td>
										</tr>
									</table>
									
									<button type="button" style="position: absolute; top: 21px; left: 695px;" onclick="" class="btn btn-primary btn-lg serverInfo_inline">View details</button>
								</div>
							</div>
						</div>
						<!-- ----------------------------------------- -->
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" id="server_45-89-132-67-25443_title" data-target="#server_45-89-132-67-25443_body" href="#server_45-89-132-67-25443_body">
									45.89.132.76:25443</a>
									<span class="serverTitle_quickStatus">
										&nbsp;&nbsp;&nbsp;<b>Unturned3SL's hack server</b>
										&nbsp;&nbsp;&nbsp;<span class="label label-default">0 / 24</span>
									</span>
								</h4>
					    	</div>
					    
							<div id="server_45-89-132-67-25443_body" class="panel-collapse collapse">
								<div class="panel-body" style="position: relative;">
									<table class="serverInfo_table">
										<tr>
											<td>Players: </td>
											<td><span class="label label-default">0 / 24</span></td>
											<td>Versus mode: </td>
											<td><span class="label label-danger">PvP</span></td>
											<td>Difficulty: </td>
											<td><span class="label label-danger">Hard</span></td>
											<td>VAC secure: </td>
											<td><span class="label label-danger">No</span></td>
										</tr>
										<tr>
											<td>Map: </td>
											<td><span class="label label-default">Devtest 22222222</span></td>
											<td>Passworded: </td>
											<td><span class="label label-danger">Yes</span></td>
											<td>Version: </td>
											<td><span class="label label-default">3.6.1.1</span></td>
										</tr>
									</table>
									
									<button type="button" style="position: absolute; top: 21px; left: 695px;" onclick="" class="btn btn-primary btn-lg serverInfo_inline">View details</button>
								</div>
							</div>
						</div>
						<!-- ----------------------------------------- -->
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" id="server_142-44-33-22-25446_title" data-target="#server_142-44-33-22-25446_body" href="#server_142-44-33-22-25446_body">
									142.44.33.22:25446</a>
									<span class="serverTitle_quickStatus">
										&nbsp;&nbsp;&nbsp;<b>Unturned3SL's server</b>
										&nbsp;&nbsp;&nbsp;<span class="label label-success">3 / 24</span>
									</span>
								</h4>
					    	</div>
					    
							<div id="server_45-89-132-67-25443_body" class="panel-collapse collapse">
								<div class="panel-body" style="position: relative;">
									<table class="serverInfo_table">
										<tr>
											<td>Players: </td>
											<td><span class="label label-success">3 / 24</span></td>
											<td>Versus mode: </td>
											<td><span class="label label-danger">PvP</span></td>
											<td>Difficulty: </td>
											<td><span class="label label-success">Easy</span></td>
											<td>VAC secure: </td>
											<td><span class="label label-success">Yes</span></td>
										</tr>
										<tr>
											<td>Map: </td>
											<td><span class="label label-default">Terrain</span></td>
											<td>Passworded: </td>
											<td><span class="label label-success">No</span></td>
											<td>Version: </td>
											<td><span class="label label-default">3.6.1.1</span></td>
										</tr>
									</table>
									
									<button type="button" style="position: absolute; top: 21px; left: 695px;" onclick="" class="btn btn-primary btn-lg serverInfo_inline">View details</button>
								</div>
							</div>
						</div>
						<!-- ----------------------------------------- -->
						<div class="panel panel-default">
							<div class="panel-heading">
								<h4 class="panel-title">
									<a data-toggle="collapse" id="server_142-44-33-22-27017_title" data-target="#server_142-44-33-22-27017_body" href="#server_142-44-33-22-27017_body">
									142.44.33.22:27017</a>
									<span class="serverTitle_quickStatus">
										&nbsp;&nbsp;&nbsp;<b>Hammereditor's bot server</b>
										&nbsp;&nbsp;&nbsp;<span class="label label-danger">32 / 32</span>
									</span>
								</h4>
					    	</div>
					    
							<div id="server_45-89-132-67-25443_body" class="panel-collapse collapse">
								<div class="panel-body" style="position: relative;">
									<table class="serverInfo_table">
										<tr>
											<td>Players: </td>
											<td><span class="label label-danger">32 / 32</span></td>
											<td>Versus mode: </td>
											<td><span class="label label-success">PvE</span></td>
											<td>Difficulty: </td>
											<td><span class="label label-warning">Normal</span></td>
											<td>VAC secure: </td>
											<td><span class="label label-danger">No</span></td>
										</tr>
										<tr>
											<td>Map: </td>
											<td><span class="label label-default">Canada</span></td>
											<td>Passworded: </td>
											<td><span class="label label-success">No</span></td>
											<td>Version: </td>
											<td><span class="label label-default">3.6.1.0</span></td>
										</tr>
									</table>
									
									<button type="button" style="position: absolute; top: 21px; left: 695px;" onclick="" class="btn btn-primary btn-lg serverInfo_inline">View details</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<!-- </div> -->
			</div>
		</div>
	</body>
</html>
