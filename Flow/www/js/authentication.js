$("#loginButton").click(function() {
	var logEmail = $("#loginEmail").val()
	var logPass = $("#loginPass").val()
	var loginForm = $("#loginForm").attr('class', 'control-group');
	var loginInfo = $("#loginInfo").css('display', 'none');
	$.ajax({
		type: "POST",
		url: '/login',
		data: {
			username: logEmail,
			password: logPass
		},
		success: function() {
			alert('success');
		},
		error: function() {
			loginForm.addClass('error');
			loginInfo.css('display', 'block');
			loginInfo.text('Login failed, please try again');
		},
	});
})

$("#regButton").click(function() {
	var regEmail = $("#regEmail").val()
	var conEmail = $("#confirmEmail").val()
	var regPass = $("#regPass").val()
	var confirmPass = $("#confirmPass").val()
	var matchEmails = $('#matchEmails').attr('class', 'control-group');
	var matchPasswords = $('#matchPasswords').attr('class', 'control-group');
	var regEmailInfo = $('#regEmailInfo').css('display', 'none');
	var regPasswordInfo = $('#regPasswordInfo').css('display', 'none');
	if (regEmail !== conEmail) {
		matchEmails.addClass('error');
		regEmailInfo.css('display', 'block');
		regEmailInfo.text('Emails do not match.');
	}
	else if (regPass !== confirmPass) {
		matchPasswords.addClass('error');
		regPasswordInfo.css('display', 'block');
		regPasswordInfo.text('Passwords do not match');
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
			console.log(response.errorCode)
			
			switch (response.errorCode) {
				case 0:
					alert ("success");
					break
				case 1:
					matchEmails.addClass('info');
					regEmailInfo.css('display', 'block');
					regEmailInfo.text('Invalid Email');
					break
				case 2:
					matchEmails.addClass('info');
					regEmailInfo.css('display', 'block');
					regEmailInfo.text('Email already registered');
					break
				case 3:
					break
			}
		});
	}
})