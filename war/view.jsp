<%@ page import="java.io.IOException" %>
<%@ page import="javax.persistence.EntityManager" %>
<%@ page import="com.google.appengine.labs.repackaged.org.json.JSONException" %>
<%@ page import="com.google.appengine.labs.repackaged.org.json.JSONObject" %>

<%@ page import="simulation.Simulation" %>
<%@ page import="org.ddosm.SimulationEntity" %>

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
<script src="http://d3js.org/d3.v3.min.js"></script>

<style>

.bar {
  fill: steelblue;
}

.bar:hover {
  fill: brown;
}

.axis {
  font: 10px sans-serif;
}

.axis path,
.axis line {
  fill: none;
  stroke: #000;
  shape-rendering: crispEdges;
}

.x.axis path {
  display: none;
}

.line {
  fill: none;
  stroke: steelblue;
  stroke-width: 1.5px;
}

.tooltip {
  position: absolute;
  top: 100px;
  left: 100px;
  -moz-border-radius:3px;
  border-radius: 3px;
  border: 2px solid #DDD;
  background: #fff;
  opacity: 1;
  color: #000;
  padding: 10px;
  width: 300px;
  font-size: 15px;
  z-index: 120;
}

.tooltip p.main {
  font-size: 15px;
  text-align: center;
  padding:0;
  margin:0;
}

hr.tooltip-hr {
  padding:3px 0 0 0;
  margin:3px 0 3px 0;
}

.tooltip .title {
  font-size: 20px;
  line-height: 24px;
}
 
.tooltip .name {
  font-weight:bold;
}

</style>

</head>

<%
	String simulationId = request.getParameter("id");

	EntityManager entityManager = Simulation.getEntityManager();
		
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

		System.out.println("params = " + parameterHashString);
		System.out.println("result = " + resultHashString);
		
		JSONObject parameterHash = new JSONObject(parameterHashString);
		JSONObject resultHash = new JSONObject(resultHashString);
		
		packetsCount = resultHash.getInt("packetsanalysed");
		rings = parameterHash.getInt("rings");
		hosts = parameterHash.getInt("hosts");
		routers = resultHash.getInt("routers");
		
		topologyMapString = resultHash.getJSONObject("topologymap").toString();
		
		attacksCount = resultHash.getInt("attackscount");
		attactsDetected = resultHash.getInt("attacksdetected");
		
		falsePositivesString = resultHash.getJSONArray("falsepostives").toString();

		System.out.println("calculating fp....");
		System.out.println("false positives string = " + falsePositivesString);
		ipsParticipationString = resultHash.getJSONArray("ipsparticipation").toString();
		scoreTransmissionString = resultHash.getJSONArray("scoretransmission").toString();
		
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
		System.out.println("json e" + e);
	} finally {
		entityManager.close();
	}
%>
<body class="">

	<div class="container">

		<div class="page-header" id="banner">
			<div class="row">
				<div class="col-lg-6">
					<h1>DMCM</h1>
					<p class="lead">Simulation <%= simulationId %></p>
				</div>
			</div>
		</div>
		
		<hr/>
		
		<h1>Stats</h1>
		<h3><%= packetsCount %> packets analyzed</h3>
		
		<h1>Topology</h1>
		<h3><%= rings %> rings, <%= routers %> routers and <%= hosts %> hosts</h3>
		<div id="main" role="main">
		  <div id="vis"></div>
		</div>
		
		<h1>Attacks averted</h1>
		<div class="pie">
		</div>
		
		<h1>False Positives</h1>
		<div class="fp">
		</div>
		
		<h1>IPS participation</h1>
		<div class="ie">
		</div>
		
		<h1>Score transmissions</h1>
		<div class="st">
		</div>
		
		<!-- Parameters
      ================================================== -->
      <div class="bs-docs-section">

        <div class="row">
          <div class="col-lg-12">
            <div class="page-header">
              <h1 id="tables">Details</h1>
            </div>

            <div class="bs-component">
              <table class="table table-striped table-hover ">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Parameter</th>
                    <th>Value</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>1</td>
                    <td>Rings</td>
                    <td><%= rings %></td>
                  </tr>
                  <tr>
                    <td>2</td>
                    <td>Hosts</td>
                    <td><%= hosts %></td>
                  </tr>
                  <tr>
                    <td>3</td>
                    <td>Routers</td>
                    <td><%= routers %></td>
                  </tr>
                  <tr>
                    <td>4</td>
                    <td>Fanout factor</td>
                    <td><%= fanout %></td>
                  </tr>
                  <tr class="active">
                    <td>5</td>
                    <td># of simulations / &#964;</td>
                    <td><%= repetitions %></td>
                  </tr>
				  <tr class="active">
                    <td>6</td>
                    <td>&#945;</td>
                    <td><%= alpha %></td>
                  </tr>
				  <tr class="active">
                    <td>7</td>
                    <td>&#946;</td>
                    <td><%= beta %></td>
                  </tr>
				  <tr class="active">
                    <td>8</td>
                    <td>&#947;</td>
                    <td><%= gamma %></td>
                  </tr>
				  <tr class="active">
                    <td>9</td>
                    <td>b1</td>
                    <td><%= b1 %></td>
                  </tr>
				  <tr class="active">
                    <td>10</td>
                    <td>b2</td>
                    <td><%= b2 %></td>
                  </tr>
				  <tr class="active">
                    <td>11</td>
                    <td>b3</td>
                    <td><%= b3 %></td>
                  </tr>
				  <tr class="active">
                    <td>12</td>
                    <td>&#1013;</td>
                    <td><%= epsilon %></td>
                  </tr>
				  <tr class="active">
                    <td>13</td>
                    <td>&#969;</td>
                    <td><%= omega %></td>
                  </tr>
				  <tr class="active">
                    <td>14</td>
                    <td>&#965;</td>
                    <td><%= upsilon %></td>
                  </tr>
				  <tr class="danger">
                    <td>15</td>
                    <td>Attacks injected</td>
                    <td><%= attacksCount %></td>
                  </tr>
                  <tr class="success">
                    <td>16</td>
                    <td>Attacks averted</td>
                    <td><%= attactsDetected %></td>
                  </tr>
				  <tr class="info">
                    <td>17</td>
                    <td>Packets analyzed</td>
                    <td><%= packetsCount %></td>
                  </tr>
				  <tr class="info">
                    <td>18</td>
                    <td>IPS participation</td>
                    <td><%= ipsParticipationAverage * 100 %>%</td>
                  </tr>
				  <tr class="info">
                    <td>19</td>
                    <td>Additional traffic injection</td>
                    <td><%= additionalTrafficInjectionPercent %>%</td>
                  </tr>
                </tbody>
              </table> 
          </div>
        </div>
      </div>

	</div>


	<script src="js/jquery-1.10.2.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/bootswatch.js"></script>
	<script src="js/Tooltip.js"></script>
	
	<script type="text/javascript">
	
		var attacksCount = <%= attacksCount %>;
		
		(function() {
		  var Network, RadialPlacement, activate, root;

		  root = typeof exports !== "undefined" && exports !== null ? exports : this;

		  RadialPlacement = function() {
			var center, current, increment, place, placement, radialLocation, radius, setKeys, start, values;
			values = d3.map();
			increment = 20;
			radius = 1000;
			center = {
			  "x": 0,
			  "y": 0
			};
			start = -120;
			current = start;
			radialLocation = function(center, angle, radius) {
			  var x, y;
			  x = center.x + radius * Math.cos(angle * Math.PI / 180);
			  y = center.y + radius * Math.sin(angle * Math.PI / 180);
			  return {
				"x": x,
				"y": y
			  };
			};
			placement = function(key) {
			  var value;
			  value = values.get(key);
			  if (!values.has(key)) {
				value = place(key);
			  }
			  return value;
			};
			place = function(key) {
			  var value;
			  value = radialLocation(center, current, radius);
			  values.set(key, value);
			  current += increment;
			  return value;
			};
			setKeys = function(keys) {
			  var firstCircleCount, firstCircleKeys, secondCircleKeys;
			  values = d3.map();
			  firstCircleCount = 360 / increment;
			  if (keys.length < firstCircleCount) {
				increment = 360 / keys.length;
			  }
			  firstCircleKeys = keys.slice(0, firstCircleCount);
			  firstCircleKeys.forEach(function(k) {
				return place(k);
			  });
			  secondCircleKeys = keys.slice(firstCircleCount);
			  radius = radius + radius / 1.8;
			  increment = 360 / secondCircleKeys.length;
			  return secondCircleKeys.forEach(function(k) {
				return place(k);
			  });
			};
			placement.keys = function(_) {
			  if (!arguments.length) {
				return d3.keys(values);
			  }
			  setKeys(_);
			  return placement;
			};
			placement.center = function(_) {
			  if (!arguments.length) {
				return center;
			  }
			  center = _;
			  return placement;
			};
			placement.radius = function(_) {
			  if (!arguments.length) {
				return radius;
			  }
			  radius = _;
			  return placement;
			};
			placement.start = function(_) {
			  if (!arguments.length) {
				return start;
			  }
			  start = _;
			  current = start;
			  return placement;
			};
			placement.increment = function(_) {
			  if (!arguments.length) {
				return increment;
			  }
			  increment = _;
			  return placement;
			};
			return placement;
		  };

		  Network = function() {
			var allData, charge, curLinksData, curNodesData, filter, filterLinks, filterNodes, force, forceTick, groupCenters, height, hideDetails, layout, link, linkedByIndex, linksG, mapNodes, moveToRadialLayout, neighboring, network, node, nodeColors, nodeCounts, nodesG, radialTick, setFilter, setLayout, setSort, setupData, showDetails, sort, sortedArtists, strokeFor, tooltip, update, updateCenters, updateLinks, updateNodes, width;
			width = 960;
			height = 800;
			allData = [];
			curLinksData = [];
			curNodesData = [];
			linkedByIndex = {};
			nodesG = null;
			linksG = null;
			node = null;
			link = null;
			layout = "force";
			filter = "all";
			sort = "songs";
			groupCenters = null;
			force = d3.layout.force();
			nodeColors = d3.scale.category20();
			tooltip = Tooltip("vis-tooltip", 230);
			charge = function(node) {
			  return -Math.pow(node.radius, 2.0) / 2;
			};
			network = function(selection, data) {
			  var vis;
			  allData = setupData(data);
			  vis = d3.select(selection).append("svg").attr("width", width).attr("height", height);
			  linksG = vis.append("g").attr("id", "links");
			  nodesG = vis.append("g").attr("id", "nodes");
			  force.size([width, height]);
			  setLayout("force");
			  setFilter("all");
			  return update();
			};
			update = function() {
			  var artists;
			  curNodesData = filterNodes(allData.nodes);
			  curLinksData = filterLinks(allData.links, curNodesData);
			  if (layout === "radial") {
				artists = sortedArtists(curNodesData, curLinksData);
				updateCenters(artists);
			  }
			  force.nodes(curNodesData);
			  updateNodes();
			  if (layout === "force") {
				force.links(curLinksData);
				updateLinks();
			  } else {
				force.links([]);
				if (link) {
				  link.data([]).exit().remove();
				  link = null;
				}
			  }
			  return force.start();
			};
			network.toggleLayout = function(newLayout) {
			  force.stop();
			  setLayout(newLayout);
			  return update();
			};
			network.toggleFilter = function(newFilter) {
			  force.stop();
			  setFilter(newFilter);
			  return update();
			};
			network.toggleSort = function(newSort) {
			  force.stop();
			  setSort(newSort);
			  return update();
			};
			network.updateSearch = function(searchTerm) {
			  var searchRegEx;
			  searchRegEx = new RegExp(searchTerm.toLowerCase());
			  return node.each(function(d) {
				var element, match;
				element = d3.select(this);
				match = d.name.toLowerCase().search(searchRegEx);
				if (searchTerm.length > 0 && match >= 0) {
				  element.style("fill", "#F38630").style("stroke-width", 2.0).style("stroke", "#555");
				  return d.searched = true;
				} else {
				  d.searched = false;
				  return element.style("fill", function(d) {
					return nodeColors(d.artist);
				  }).style("stroke-width", 1.0);
				}
			  });
			};
			network.updateData = function(newData) {
			  allData = setupData(newData);
			  link.remove();
			  node.remove();
			  return update();
			};
			setupData = function(data) {
			  var circleRadius, countExtent, nodesMap;
			  countExtent = d3.extent(data.nodes, function(d) {
				return 10000;
			  });
			  circleRadius = d3.scale.sqrt().range([10, 22]).domain(countExtent);
			  data.nodes.forEach(function(n) {
				var randomnumber;
				n.x = randomnumber = Math.floor(Math.random() * width);
				n.y = randomnumber = Math.floor(Math.random() * height);
				return n.radius = circleRadius(10000);
			  });
			  nodesMap = mapNodes(data.nodes);
			  data.links.forEach(function(l) {
				l.source = nodesMap.get(l.source);
				l.target = nodesMap.get(l.target);
				return linkedByIndex["" + l.source.id + "," + l.target.id] = 1;
			  });
			  return data;
			};
			mapNodes = function(nodes) {
			  var nodesMap;
			  nodesMap = d3.map();
			  nodes.forEach(function(n) {
				return nodesMap.set(n.id, n);
			  });
			  return nodesMap;
			};
			nodeCounts = function(nodes, attr) {
			  var counts;
			  counts = {};
			  nodes.forEach(function(d) {
				var _name;
				if (counts[_name = d[attr]] == null) {
				  counts[_name] = 0;
				}
				return counts[d[attr]] += 1;
			  });
			  return counts;
			};
			neighboring = function(a, b) {
			  return linkedByIndex[a.id + "," + b.id] || linkedByIndex[b.id + "," + a.id];
			};
			filterNodes = function(allNodes) {
			  var cutoff, filteredNodes, playcounts;
			  filteredNodes = allNodes;
			  if (filter === "popular" || filter === "obscure") {
				playcounts = allNodes.map(function(d) {
				  return d.playcount;
				}).sort(d3.ascending);
				cutoff = d3.quantile(playcounts, 0.5);
				filteredNodes = allNodes.filter(function(n) {
				  if (filter === "popular") {
					return n.playcount > cutoff;
				  } else if (filter === "obscure") {
					return n.playcount <= cutoff;
				  }
				});
			  }
			  return filteredNodes;
			};
			sortedArtists = function(nodes, links) {
			  var artists, counts;
			  artists = [];
			  if (sort === "links") {
				counts = {};
				links.forEach(function(l) {
				  var _name, _name1;
				  if (counts[_name = l.source.artist] == null) {
					counts[_name] = 0;
				  }
				  counts[l.source.artist] += 1;
				  if (counts[_name1 = l.target.artist] == null) {
					counts[_name1] = 0;
				  }
				  return counts[l.target.artist] += 1;
				});
				nodes.forEach(function(n) {
				  var _name;
				  return counts[_name = n.artist] != null ? counts[_name = n.artist] : counts[_name] = 0;
				});
				artists = d3.entries(counts).sort(function(a, b) {
				  return b.value - a.value;
				});
				artists = artists.map(function(v) {
				  return v.key;
				});
			  } else {
				counts = nodeCounts(nodes, "artist");
				artists = d3.entries(counts).sort(function(a, b) {
				  return b.value - a.value;
				});
				artists = artists.map(function(v) {
				  return v.key;
				});
			  }
			  return artists;
			};
			updateCenters = function(artists) {
			  if (layout === "radial") {
				return groupCenters = RadialPlacement().center({
				  "x": width / 2,
				  "y": height / 2 - 100
				}).radius(300).increment(18).keys(artists);
			  }
			};
			filterLinks = function(allLinks, curNodes) {
			  curNodes = mapNodes(curNodes);
			  return allLinks.filter(function(l) {
				return curNodes.get(l.source.id) && curNodes.get(l.target.id);
			  });
			};
			updateNodes = function() {
			  node = nodesG.selectAll("circle.node").data(curNodesData, function(d) {
				return d.id;
			  });
			  node.enter().append("circle").attr("class", "node").attr("cx", function(d) {
				return d.x;
			  }).attr("cy", function(d) {
				return d.y;
			  }).attr("r", function(d) {
				return d.radius;
			  }).style("fill", function(d) {
				return nodeColors(d.type);
			  }).style("stroke", function(d) {
				return strokeFor(d);
			  }).style("stroke-width", 1.0);
			  node.on("mouseover", showDetails).on("mouseout", hideDetails);
			  return node.exit().remove();
			};
			updateLinks = function() {
			  link = linksG.selectAll("line.link").data(curLinksData, function(d) {
				return "" + d.source.id + "_" + d.target.id;
			  });
			  link.enter().append("line").attr("class", "link").attr("stroke", "#ddd").attr("stroke-opacity", 0.8).attr("x1", function(d) {
				return d.source.x;
			  }).attr("y1", function(d) {
				return d.source.y;
			  }).attr("x2", function(d) {
				return d.target.x;
			  }).attr("y2", function(d) {
				return d.target.y;
			  });
			  return link.exit().remove();
			};
			setLayout = function(newLayout) {
			  layout = newLayout;
			  if (layout === "force") {
				return force.on("tick", forceTick).charge(-200).linkDistance(50);
			  } else if (layout === "radial") {
				return force.on("tick", radialTick).charge(charge);
			  }
			};
			setFilter = function(newFilter) {
			  return filter = newFilter;
			};
			setSort = function(newSort) {
			  return sort = newSort;
			};
			forceTick = function(e) {
			  node.attr("cx", function(d) {
				return d.x;
			  }).attr("cy", function(d) {
				return d.y;
			  });
			  return link.attr("x1", function(d) {
				return d.source.x;
			  }).attr("y1", function(d) {
				return d.source.y;
			  }).attr("x2", function(d) {
				return d.target.x;
			  }).attr("y2", function(d) {
				return d.target.y;
			  });
			};
			radialTick = function(e) {
			  node.each(moveToRadialLayout(e.alpha));
			  node.attr("cx", function(d) {
				return d.x;
			  }).attr("cy", function(d) {
				return d.y;
			  });
			  if (e.alpha < 0.03) {
				force.stop();
				return updateLinks();
			  }
			};
			moveToRadialLayout = function(alpha) {
			  var k;
			  k = alpha * 0.1;
			  return function(d) {
				var centerNode;
				centerNode = groupCenters(d.artist);
				d.x += (centerNode.x - d.x) * k;
				return d.y += (centerNode.y - d.y) * k;
			  };
			};
			strokeFor = function(d) {
			  return d3.rgb(nodeColors(d.type)).darker().toString();
			};
			showDetails = function(d, i) {
			  var content;
			  content = '' + d.name + '';
			  console.log("content" + content);
			  tooltip.showTooltip(content, d3.event);
			  if (link) {
				link.attr("stroke", function(l) {
				  if (l.source === d || l.target === d) {
					return "#555";
				  } else {
					return "#ddd";
				  }
				}).attr("stroke-opacity", function(l) {
				  if (l.source === d || l.target === d) {
					return 1.0;
				  } else {
					return 0.5;
				  }
				});
			  }
			  node.style("stroke", function(n) {
				if (n.searched || neighboring(d, n)) {
				  return "#555";
				} else {
				  return strokeFor(n);
				}
			  }).style("stroke-width", function(n) {
				if (n.searched || neighboring(d, n)) {
				  return 2.0;
				} else {
				  return 1.0;
				}
			  });
			  return d3.select(this).style("stroke", "black").style("stroke-width", 2.0);
			};
			hideDetails = function(d, i) {
			  tooltip.hideTooltip();
			  node.style("stroke", function(n) {
				if (!n.searched) {
				  return strokeFor(n);
				} else {
				  return "#555";
				}
			  }).style("stroke-width", function(n) {
				if (!n.searched) {
				  return 1.0;
				} else {
				  return 2.0;
				}
			  });
			  if (link) {
				return link.attr("stroke", "#ddd").attr("stroke-opacity", 0.8);
			  }
			};
			return network;
		  };

		  activate = function(group, link) {
			d3.selectAll("#" + group + " a").classed("active", false);
			return d3.select("#" + group + " #" + link).classed("active", true);
		  };

		  $(function() {
			var myNetwork;
			myNetwork = Network();
			d3.selectAll("#layouts a").on("click", function(d) {
			  var newLayout;
			  newLayout = d3.select(this).attr("id");
			  activate("layouts", newLayout);
			  return myNetwork.toggleLayout(newLayout);
			});
			d3.selectAll("#filters a").on("click", function(d) {
			  var newFilter;
			  newFilter = d3.select(this).attr("id");
			  activate("filters", newFilter);
			  return myNetwork.toggleFilter(newFilter);
			});
			d3.selectAll("#sorts a").on("click", function(d) {
			  var newSort;
			  newSort = d3.select(this).attr("id");
			  activate("sorts", newSort);
			  return myNetwork.toggleSort(newSort);
			});
			$("#song_select").on("change", function(e) {
			  var songFile;
			  songFile = $(this).val();
			  return d3.json("data/" + songFile, function(json) {
				return myNetwork.updateData(json);
			  });
			});
			$("#search").keyup(function() {
			  var searchTerm;
			  searchTerm = $(this).val();
			  return myNetwork.updateSearch(searchTerm);
			});
			// return d3.json("data/network.json", function(json) {
			  // return myNetwork("#vis", json);
			// });
			
			var json = <%= topologyMapString %>;
			return myNetwork("#vis", json);
		  });

		}).call(this);
		
		///////////////// Completed pie chart
		(function() {
			var width = 960,
				height = 500,
				radius = Math.min(width, height) / 2;

			var color = d3.scale.ordinal()
				.range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

			var arc = d3.svg.arc()
				.outerRadius(radius - 10)
				.innerRadius(0);

			var pie = d3.layout.pie()
				.sort(null)
				.value(function(d) { return d.value; });

			var svg = d3.select(".pie").append("svg")
				.attr("width", width)
				.attr("height", height)
			  .append("g")
				.attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");
				
			var data = [
				{
					"label": "Detected",
					"value": <%= attactsDetected %>,
				},
				{
					"label": "Failed",
					"value": <%= attacksCount - attactsDetected %>,
				},
			];

		  data.forEach(function(d) {
			d.value = +d.value;
		  });

		  var g = svg.selectAll(".arc")
			  .data(pie(data))
			.enter().append("g")
			  .attr("class", "arc");

		  g.append("path")
			  .attr("d", arc)
			  .style("fill", function(d) { return color(d.data.label); });

		  g.append("text")
			  .attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")"; })
			  .attr("dy", ".35em")
			  .style("text-anchor", "middle")
			  .text(function(d) { return d.data.label + ": " + (d.data.value / attacksCount * 100).toPrecision(3) + "%"; });
		}).call(this);
		
		//// False positives
		
		(function() {
			var margin = {top: 20, right: 20, bottom: 30, left: 40},
				width = 960 - margin.left - margin.right,
				height = 500 - margin.top - margin.bottom;

			var x = d3.scale.ordinal()
				.rangeRoundBands([0, width], .1);

			var y = d3.scale.linear()
				.range([height, 0]);

			var xAxis = d3.svg.axis()
				.scale(x)
				.orient("bottom");

			var yAxis = d3.svg.axis()
				.scale(y)
				.orient("left");
				//.ticks(10, "%");

			var svg = d3.select(".fp").append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
			  .append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
				
			var data = <%= falsePositivesString %>;

			//d3.tsv("data.tsv", type, function(error, data) {
			 x.domain(data.map(function(d) { return d.tau; }));
			  y.domain([0, d3.max(data, function(d) { return d.fp; })]);

			  svg.append("g")
				  .attr("class", "x axis")
				  .attr("transform", "translate(0," + height + ")")
				  .call(xAxis)
				 .append("text")
				  .attr("x", width)
				  .text("Tau");;

			  svg.append("g")
				  .attr("class", "y axis")
				  .call(yAxis)
				.append("text")
				  .attr("transform", "rotate(-90)")
				  .attr("y", 6)
				  .attr("dy", ".71em")
				  .style("text-anchor", "end")
				  .text("False positives");

			  svg.selectAll(".bar")
				  .data(data)
				.enter().append("rect")
				  .attr("class", "bar")
				  .attr("x", function(d) { return x(d.tau); })
				  .attr("width", x.rangeBand())
				  .attr("y", function(d) { return y(d.fp); })
				  .attr("height", function(d) { return height - y(d.fp); });

			//});

			
		}).call(this);
		
		/// IPS effectiveness
		
		(function() {
			var margin = {top: 20, right: 20, bottom: 30, left: 50},
				width = 960 - margin.left - margin.right,
				height = 500 - margin.top - margin.bottom;

			var x = d3.scale.linear()
				.range([0, width]);

			var y = d3.scale.linear()
				.range([height, 0]);

			var xAxis = d3.svg.axis()
				.scale(x)
				.orient("bottom");

			var yAxis = d3.svg.axis()
				.scale(y)
				.orient("left");

			var line = d3.svg.line()
				.x(function(d) { return x(d.tau); })
				.y(function(d) { return y(d.ipse); });

			var svg = d3.select(".ie").append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
			  .append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
				

				var data = <%= ipsParticipationString %>;

			  data.forEach(function(d) {
				d.tau = +d.tau;
				d.ipse = +d.ipse;
			  });

			  x.domain(d3.extent(data, function(d) { return d.tau; }));
			  y.domain(d3.extent(data, function(d) { return d.ipse; }));

			  svg.append("g")
				  .attr("class", "x axis")
				  .attr("transform", "translate(0," + height + ")")
				  .call(xAxis)
				.append("text")
				  .attr("x", width)
				  .text("Tau");

			  svg.append("g")
				  .attr("class", "y axis")
				  .call(yAxis)
				.append("text")
				  .attr("transform", "rotate(-90)")
				  .attr("y", 6)
				  .attr("dy", ".71em")
				  .style("text-anchor", "end")
				  .text("IPS Participation");

			  svg.append("path")
				  .datum(data)
				  .attr("class", "line")
				  .attr("d", line);
		}).call(this);
		
		//// Score transmissions
		(function() {
			var margin = {top: 20, right: 20, bottom: 30, left: 40},
				width = 960 - margin.left - margin.right,
				height = 500 - margin.top - margin.bottom;

			var x = d3.scale.ordinal()
				.rangeRoundBands([0, width], .1);

			var y = d3.scale.linear()
				.range([height, 0]);

			var xAxis = d3.svg.axis()
				.scale(x)
				.orient("bottom");

			var yAxis = d3.svg.axis()
				.scale(y)
				.orient("left");
				//.ticks(10, "%");

			var svg = d3.select(".st").append("svg")
				.attr("width", width + margin.left + margin.right)
				.attr("height", height + margin.top + margin.bottom)
			  .append("g")
				.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
				
			var data = <%= scoreTransmissionString %>;

			//d3.tsv("data.tsv", type, function(error, data) {
			 x.domain(data.map(function(d) { return d.ring; }));
			  y.domain([0, d3.max(data, function(d) { return d.scores; })]);

			  svg.append("g")
				  .attr("class", "x axis")
				  .attr("transform", "translate(0," + height + ")")
				  .call(xAxis)
				 .append("text")
				  .attr("x", width)
				  .text("Ring");;

			  svg.append("g")
				  .attr("class", "y axis")
				  .call(yAxis)
				.append("text")
				  .attr("transform", "rotate(-90)")
				  .attr("y", 6)
				  .attr("dy", ".71em")
				  .style("text-anchor", "end")
				  .text("Score transmissions");

			  svg.selectAll(".bar")
				  .data(data)
				.enter().append("rect")
				  .attr("class", "bar")
				  .attr("x", function(d) { return x(d.ring); })
				  .attr("width", x.rangeBand())
				  .attr("y", function(d) { return y(d.scores); })
				  .attr("height", function(d) { return height - y(d.scores); });

			//});

			
		}).call(this);
		
	</script>

</body>
</html>