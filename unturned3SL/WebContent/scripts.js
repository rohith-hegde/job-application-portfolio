
function CookieTracker()
{
	//default values
	this.jsonObj = {
		"listTools_expandAll": false,
		"listTools_hideQuickStatus": false,
		"listTools_sort": {
			"by": "playercount", //values: "playercount", "title", "none"
			"order": "descending" //values: "descending", "ascending", "none"
		},
		"listTools_filter": {
			"hostOrTitleSearch": "none", //values: "none", "<IP address fragment>", or "<Hostname fragment>", or "<Title fragment>"
			"playercount": {
				"min": 0, //values: integer >= 0, or default of 0
				"max": 100 //values: integer >= 0, or default of 100
			},
			"versusMode": "none", //values: "none", "PvE", "PvP"
			"difficulty": "Easy", //values: "Easy", ... "Gold"
			"map": "none",
			"passworded": "none", //values: "none", "Yes", "No"
			"vacSecure": "none" //values: "none", "Yes", "No"
		}
	};
}

CookieTracker.prototype.getData = 
function()
{
	return this.jsonObj;
};

CookieTracker.prototype.saveData = 
function()
{
	$.cookie('listTools', JSON.stringify(this.jsonObj), {expires: 180, path: '/'});
};

CookieTracker.prototype.loadData = 
function()
{
	if ($.cookie("listTools") != "")
		this.jsonObj = JSON.parse($.cookie("listTools"));
};

////////////////////////////////////////////////////////////////////////
//Status result class:
var exampleSR = {
	"ipAddr": "45.89.132.76",
	"port": 27015,
	"title": "HammerHost\'s test server",
	"players": 14,
	"maxPlayers": 18,
	"versusMode": "PvE",
	"difficulty": "Normal",
	"vacSecure": "Yes",
	"map": "Canada",
	"passworded": "No",
	"version": "3.4.9.1"	
};

function StatusResult(ipAddr, port, title, players, maxPlayers, versusMode, difficulty, vacSecure, map, passworded, version)
{
	this.statusData = {
		"ipAddr": ipAddr,
		"port": port,
		"title": title,
		"players": players,
		"maxPlayers": maxPlayers,
		"versusMode": versusMode,
		"difficulty": difficulty,
		"vacSecure": vacSecure,
		"map": map,
		"passworded": passworded,
		"version": version
	};
	this.domObj = this.generateDOMobj();
}

StatusResult.prototype.getStatusData =
function()
{
	return this.statusData;
};

StatusResult.prototype.setStatusData =
function(statusData)
{
	this.statusData = statusData;
	this.domObj = undefined;
};

StatusResult.prototype.getDOMobj =
function()
{
	if (this.domObj == undefined)
		this.domObj = this.generateDOMobj();
	return this.domObj;
};

//private function
StatusResult.prototype.generateDOMobj =
function()
{
	var hostStrDashes = statusResult_hostDotsToDashes(this.statusData.ipAddr, this.statusData.port);
	var panel = document.createElement("div");
	panel.className = "panel panel-default";
	
	var panel_heading = document.createElement("div");
	panel_heading.className = "panel-heading";
	panel.appendChild(panel_heading);
	
	var panel_heading_h4 = document.createElement("h4");
	panel_heading_h4.innerHTML = 
			"<a data-toggle=\"collapse\" id=\"server_" + hostStrDashes + "_title\" data-target=\"#server_" + hostStrDashes + "_body\" " +
			"href=\"#server_" + hostStrDashes + "_body\">" + this.statusData.ipAddr + ":" + this.statusData.port + "</a>" + 
			"<span class=\"serverTitle_quickStatus\">" +
				"&nbsp;&nbsp;&nbsp;<b>" + this.statusData.title + "</b>" +
				"&nbsp;&nbsp;&nbsp;<span class=\"label label-success\">" + this.statusData.players + " / " + this.statusData.maxPlayers + "</span>" +
			"</span>";
	panel_heading.appendChild(panel_heading_h4);
	
	var serverInfo = document.createElement("div");
	serverInfo.id = "server_" + hostStrDashes + "_body";
	serverInfo.className = "panel-collapse collapse";
	panel.appendChild(serverInfo);
	
	var serverInfoBody = document.createElement("div");
	serverInfoBody.className = "panel-body";
	serverInfoBody.style.position = "relative";
	serverInfoBody.innerHTML = 
		"<table class=\"serverInfo_table\">" +
			"<tr>" +
				"<td>Players: </td>" + "<td><span class=\"" + labelClass_players + "\">" + this.statusData.players + " / " + this.statusData.maxPlayers + "</span></td>" +
				"<td>Versus mode: </td>" + "<td><span class=\"" + labelClass_versusMode + "\">" + this.statusData.versusMode + "</span></td>" +
				"<td>Difficulty: </td>" + "<td><span class=\"" + labelClass_difficulty + "\">" + this.statusData.difficulty + "</span></td>" +
				"<td>VAC secure: </td>" + "<td><span class=\"" + labelClass_vacSecure + "\">" + this.statusData.vacSecure + "</span></td>" +
			"</tr>" +
			"<tr>" +
				"<td>Map: </td>" + "<td><span class=\"label label-default\">" + this.statusData.map + "</span></td>" +
				"<td>Passworded: </td>" + "<td><span class=\"" + labelClass_passworded + "\">" + this.statusData.passworded + "</span></td>" +
				"<td>Version: </td>" + "<td><span class=\"label label-default\">" + this.statusData.version + "</span></td>" +
			"</tr>" +
		"</table>" +
		"<button type=\"button\" style=\"position: absolute; top: 0px; left: 675px;\" onclick=\"\" class=\"btn btn-primary btn-lg serverInfo_inline\">View details</button>";
	serverInfo.appendChild(serverInfoBody);
	
	return panel;
};

function statusResult_hostDotsToDashes(ipAddr, port)
{
	var hostStr = ipAddr + "-" + port;
	return hostStr.replaceAll(".", "-");
}

////////////////////////////////////////////////////////////////////////
//StatusResultManager class:

function StatusResultManager()
{
	this.statusResults = [];
}

StatusResultManager.prototype.addSR = 
function(statusResult)
{
	this.statusResults.push(statusResult);
};

StatusResultManager.prototype.getSR = 
function(ipAddr, port)
{
	for (var i = 0; i < this.statusResults.length; i++)
	{
		var currSR = this.statusResults[i];
		if (currSR.getStatusData().ipAddr == ipAddr && currSR.getStatusData().port == port)
			return currSR;
	}
	
	return undefined;
};

StatusResultManager.prototype.removeAll = 
function(statusResult)
{
	this.statusResults = [];
};

//'Z' to 'A'
StatusResultManager.prototype.sortByDescendingTitle = function()
{
	this.statusResults.sort(function(a,b) 
	{
	    if (b.getStatusData().title < a.getStatusData().title)
	        return -1;
	    else if (b.getStatusData().title > a.getStatusData().title)
	        return 1;
	    return 0;
	});
};

//'A' to 'Z'
StatusResultManager.prototype.sortByAscendingTitle = function()
{
	this.statusResults.sort(function(a,b) 
	{
	    if (a.getStatusData().title < b.getStatusData().title)
	        return -1;
	    else if (a.getStatusData().title > b.getStatusData().title)
	        return 1;
	    return 0;
	});
};

StatusResultManager.prototype.sortByDescendingPlayercount = function()
{
	this.statusResults.sort(function(a, b){ return b.getStatusData().players - a.getStatusData().players; });
};

StatusResultManager.prototype.sortByAscendingPlayercount = function()
{
	this.statusResults.sort(function(a, b){ return a.getStatusData().players - b.getStatusData().players; });
};

StatusResultManager.prototype.removeAll = 
function(statusResult)
{
	this.statusResults = [];
};

StatusResultManager.prototype.getAll = 
function(statusResult)
{
	return this.statusResults;
};

////////////////////////////////////////////////////////////////////////
//StatusHTMLmanager class

function StatusHTMLmanager()
{
	this.panelHolderObj = $("#statusPanelHolder");
}

StatusHTMLmanager.prototype.insertHTML = 
function(statusResultManager)
{
	var frag = document.createDocumentFragment();
	var statusResults = statusResultManager.getAll();
	for (var i = 0; i < statusResults.length; i++)
		frag.appendChild(statusResults[i]);
	
	this.panelHolderObj.appendChild(frag);
};

StatusHTMLmanager.prototype.clearHTML = 
function()
{
	this.panelHolderObj.innerHTML = "";
};

StatusHTMLmanager.prototype.listTools_expandAll = 
function()
{
	if (listTools_expandAll_closed)
	{
		cookieTracker.getData().listTools_expandAll = true;
		$('.panel-collapse').collapse('show');
	    $('.panel-title').attr('data-toggle', '');
	    listTools_expandAll_closed = false;
	  	$("#listTools_expandAllButton").text("Collapse all");
	}
	else
	{
		cookieTracker.getData().listTools_expandAll = false;
		$('.panel-collapse').collapse('hide');
	    $('.panel-title').attr('data-toggle', 'collapse');
	    listTools_expandAll_closed = true;
	    $("#listTools_expandAllButton").text("Expand all");
	}
	cookieTracker.saveData();
};

////////////////////////////////////////////////////////////////////////
//ListTools class: also manages cookies

function ListTools()
{
	this.expandAll_closed = true;
	this.hideQuickStatus_qsShowing = true;
	this.cookieTracker = new CookieTracker();
	
	this.cookieTracker.loadData();
	this.applyCookieBehaviors();
}

//private function
ListTools.prototype.applyCookieBehaviors = 
function()
{
	if (this.cookieTracker.getData().listTools_expandAll)
		this.toggleExpandAll();
	if (this.cookieTracker.getData().listTools_hideQuickStatus)
		this.toggleQuickStatus();
};

ListTools.prototype.toggleExpandAll =
function()
{
	if (this.expandAll_closed)
	{
		this.cookieTracker.getData().listTools_expandAll = true;
		$('.panel-collapse').collapse('show');
        $('.panel-title').attr('data-toggle', '');
        this.expandAll_closed = false;
      	$("#listTools_expandAllButton").text("Collapse all");
	}
	else
	{
		this.cookieTracker.getData().listTools_expandAll = false;
		$('.panel-collapse').collapse('hide');
        $('.panel-title').attr('data-toggle', 'collapse');
        this.expandAll_closed = true;
        $("#listTools_expandAllButton").text("Expand all");
	}
	
	this.cookieTracker.saveData();
};

ListTools.prototype.toggleQuickStatus =
function()
{
	if (!this.hideQuickStatus_qsShowing)
	{
		this.cookieTracker.getData().listTools_hideQuickStatus = false;
		changecss(".serverTitle_quickStatus", "display", "inline");
		this.hideQuickStatus_qsShowing = true;
      	$("#listTools_hideQuickStatusButton").text("Hide quick status");
	}
	else
	{
		this.cookieTracker.getData().listTools_hideQuickStatus = true;
		changecss(".serverTitle_quickStatus", "display", "none");
		this.hideQuickStatus_qsShowing = false;
        $("#listTools_hideQuickStatusButton").text("Show quick status");
	}
	
	this.cookieTracker.saveData();
};

////////////////////////////////////////////////////////////////////////
//StatusManager class: the top class which controls the statusresults page

function StatusManager(requestDelayMs)
{
	this.statusHTMLmanager = new StatusHTMLmanager();
	this.statusResultManager = new StatusResultManager();
	this.listTools = new ListTools();
	this.requestDelayMs = requestDelayMs;
	
	this.stopRequest = false;
	this.requestLoop();
}

StatusManager.prototype.requestLoop =
function()
{
	if (!this.stopRequest)
	{
		this.requestJSON();
		window.setTimeout(this.requestLoop, this.requestDelayMs);
	}
};

StatusManager.prototype.stopRequestLoop =
function()
{
	this.stopRequest = true;
};

StatusManager.prototype.requestJSON = 
function()
{
	$.ajax(
	{
		type: "GET",
		url: "API/getRecentResults.jsp",
		async: false,
		cache: false,
		success: function(data) { this.populate(data); }
	});
};

//StatusResult(ipAddr, port, title, players, maxPlayers, versusMode, difficulty, vacSecure, map, passworded, version)
	
StatusManager.prototype.populate = 
function(json)
{
	var rootObj = JSON.parse(json);
	if (rootObj.error != undefined)
	{
		//display error on top of page later
		console.log("Error while getting server data: " + rootObj.error);
		return;
	}
	var statusArray = rootObj.results;
	
	for (var i = 0; i < statusArray.length; i++)
	{
		var currSRO = statusArray[i];
		var currSR = new StatusResult(currSRO.ipAddr, currSRO.port, currSRO.title, currSRO.players, currSRO.maxPlayers, currSRO.versusMode, currSRO.difficulty, currSRO.vacSecure, currSRO.map, currSRO.passworded, currSRO.version);
		this.statusResultManager.addSR(currSR);
	}
};

////////////////////////////////////////////////////////////////////////
//Global things:

var statusManager = undefined;

$(document).ready(function()
{
	statusManager = new StatusManager(60000);
	
});

/*
function listTools_applyCookieBehvaiors()
{
	if (cookieTracker.getData().listTools_expandAll)
		listTools_expandAll();
	if (cookieTracker.getData().listTools_hideQuickStatus)
		listTools_hideQuickStatus();
}

var listTools_expandAll_closed = true;

function listTools_expandAll()
{
	if (listTools_expandAll_closed)
	{
		cookieTracker.getData().listTools_expandAll = true;
		$('.panel-collapse').collapse('show');
        $('.panel-title').attr('data-toggle', '');
        listTools_expandAll_closed = false;
      	$("#listTools_expandAllButton").text("Collapse all");
	}
	else
	{
		cookieTracker.getData().listTools_expandAll = false;
		$('.panel-collapse').collapse('hide');
        $('.panel-title').attr('data-toggle', 'collapse');
        listTools_expandAll_closed = true;
        $("#listTools_expandAllButton").text("Expand all");
	}
	
	cookieTracker.saveData();
}

var listTools_hideQuickStatus_qsShowing = true;

function listTools_hideQuickStatus()
{
	if (!listTools_hideQuickStatus_qsShowing)
	{
		cookieTracker.getData().listTools_hideQuickStatus = false;
		changecss(".serverTitle_quickStatus", "display", "inline");
		listTools_hideQuickStatus_qsShowing = true;
      	$("#listTools_hideQuickStatusButton").text("Hide quick status");
	}
	else
	{
		cookieTracker.getData().listTools_hideQuickStatus = true;
		changecss(".serverTitle_quickStatus", "display", "none");
		listTools_hideQuickStatus_qsShowing = false;
        $("#listTools_hideQuickStatusButton").text("Show quick status");
	}
	
	cookieTracker.saveData();
}*/


function changecss(theClass, element, value) {
	// Last Updated on July 4, 2011
	// documentation for this script at
	// http://www.shawnolson.net/a/503/altering-css-class-attributes-with-javascript.html
	var cssRules;
	for (var S = 0; S < document.styleSheets.length; S++) {
		try {
			document.styleSheets[S].insertRule(theClass + ' { ' + element
					+ ': ' + value + '; }',
					document.styleSheets[S][cssRules].length);

		} catch (err) {
			try {
				document.styleSheets[S].addRule(theClass, element + ': '
						+ value + ';');
			} catch (err) {
				try {
					if (document.styleSheets[S]['rules']) {
						cssRules = 'rules';
					} else if (document.styleSheets[S]['cssRules']) {
						cssRules = 'cssRules';
					} else {
						// no rules found... browser unknown
					}
					for (var R = 0; R < document.styleSheets[S][cssRules].length; R++) {
						if (document.styleSheets[S][cssRules][R].selectorText == theClass) {
							if (document.styleSheets[S][cssRules][R].style[element]) {
								document.styleSheets[S][cssRules][R].style[element] = value;
								break;
							}
						}
					}
				} catch (err) {
				}
			}
		}
	}
}

String.prototype.replaceAll = function(find, replace)
{
	var str = this;
	return str.split(find).join(replace);
};
