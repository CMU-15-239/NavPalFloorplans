$("#loginButton").click(function() {
	$(".help-block").addClass('hidden');
	var logEmail = $("#loginEmail").val();
	var logPass = $("#loginPass").val();
	var loginForm = $("#loginForm").attr('class', 'control-group');
	var loginInfo = $("#loginInfo");
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
			loginInfo.removeClass('hidden').text('Login failed, please try again');
		},
	});
})

$("#regButton").click(function() {
	$(".help-block").addClass('hidden');
	var regEmail = $("#regEmail").val();
	var conEmail = $("#confirmEmail").val();
	var regPass = $("#regPass").val();
	var confirmPass = $("#confirmPass").val();
	var matchEmails = $('#matchEmails').attr('class', 'control-group');
	var matchPasswords = $('#matchPasswords').attr('class', 'control-group');
	var regEmailInfo = $('#regEmailInfo');
	var regPasswordInfo = $('#regPasswordInfo');
	if (regEmail !== conEmail) {
		matchEmails.addClass('error');
		regEmailInfo.removeClass('hidden').text('Emails do not match.');
	}
	else if (regPass !== confirmPass) {
		matchPasswords.addClass('error');
		regPasswordInfo.removeClass('hidden').text('Passwords do not match');
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
			switch (response.errorCode) {
				case 0:
					alert ("success");
					break
				case 1:
					matchEmails.addClass('info');
					regEmailInfo.removeClass('hidden').text('Invalid Email');
					break
				case 2:
					matchEmails.addClass('info');
					regEmailInfo.removeClass('hidden').text('Email already registered');
					break
				case 3:
					break
			}
		});
	}
})