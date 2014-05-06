$(document).ready(function () {
	$(document).on("click", "#submitsimulationbutton", function() {
		
		var hosts = $("#hosts").val();
		var rings = $("#rings").val();
		var fanout = $("#fanout").val();
		var traffic = $("#traffic").val();
		var attacks = $("#attacks").val();
		var repetitions = $("#repetitions").val();
		var alpha = $("#alpha").val();
		var beta = $("#beta").val();
		var gamma = $("#gamma").val();
		var b1 = $("#b1").val();
		var b2 = $("#b2").val();
		var b3 = $("#b3").val();
		var epsilon = $("#epsilon").val();
		var omega = $("#omega").val();
		var upsilon = $("#upsilon").val();
		var email = $("#email").val();
		
		var url = "/submit";
		$.post(url, {
	        "hosts": hosts,
	        "rings": rings,
	        "fanout": fanout,
	        "traffic": traffic,
	        "attacks": attacks,
	        "repetitions": repetitions,
	        "alpha": alpha,
	        "beta": beta,
	        "gamma": gamma,
	        "b1": b1,
	        "b2": b2,
	        "b3": b3,
	        "epsilon": epsilon,
	        "omega": omega,
	        "upsilon": upsilon,
	        "email": email
	    }).done(function () {
	    	window.location = "/";
	    }).fail(function (xhr, err) {
	    	if (xhr.status === 0 || err === "timeout") {
	    		alert("Please check the Internet connection!");
	        } else {
	        	alert("Something went wrong!");
	        }
	    });
	});
});