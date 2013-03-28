$("#login-btn").click(function() {
	var logEmail = $("#login-email").val()
	var logPass = $("#login-pass").val()
	$.ajax({
		type: "POST",
		url: '/login',
		data: {
			username: logEmail,
			password: logPass
		},
	}).done(function(response) {
		console.log(response)
	});
})

$("#reg-btn").click(function() {
	var regEmail = $("#reg-email").val()
	var conEmail = $("#confirm-email").val()
	var regPass = $("#reg-pass").val()
	var conPass = $("#confirm-pass").val()
	if (regEmail !== conEmail) {
		alert("emails don't match")
	}
	else if (regPass !== conPass) {
		alert("passwords don't match")
	}
	else {
		$.ajax({
			type: "POST",
			url: '/register',
			data: {
				username: regEmail,
				password: regPass
			},
		}).done(function(response) {
			console.log(response)
		});
	}
})