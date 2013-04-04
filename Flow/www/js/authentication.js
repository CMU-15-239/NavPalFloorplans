/**
 * Handles login and registration requests to server
 * Written by: Daniel Muller
*/

/**
 * Summary: Sends post /login request to server
 * Parameters: valid email and password combo
 * Returns: redirect if successful orelse error message
**/
$("#loginButton").click(function() {
	// make all error messages hidden
	$(".help-block").addClass('hidden');
	// grab input email and password
	var logEmail = $("#loginEmail").val();
	var logPass = $("#loginPass").val();
	// grab elements to change in case of error, reset errors
	var loginForm = $("#loginForm").attr('class', 'control-group');
	var loginInfo = $("#loginInfo");
	// send post request to server
	$.ajax({
		type: "POST",
		url: '/login',
		data: {
			username: logEmail,
			password: logPass
		},
		success: function() {
			console.log(window.location)
			window.location = "/floorUploads.html";
		},
		error: function() {
			loginForm.addClass('error');
			loginInfo.removeClass('hidden').text('Login failed, please try again');
		},
	});
})


/**
 * Summary: Sends post /register request to server
 * Parameters: valid email and password combo
 * Returns: redirect if successful orelse error message
**/
$("#regButton").click(function() {
	// make all error messages hidden
	$(".help-block").addClass('hidden');
	// grab email and password input
	var regEmail = $("#regEmail").val();
	var conEmail = $("#confirmEmail").val();
	var regPass = $("#regPass").val();
	var confirmPass = $("#confirmPass").val();
	// grab elements to change in case of error, reset errors
	var matchEmails = $('#matchEmails').attr('class', 'control-group');
	var matchPasswords = $('#matchPasswords').attr('class', 'control-group');
	var regEmailInfo = $('#regEmailInfo');
	var regPasswordInfo = $('#regPasswordInfo');
	// check if emails match
	if (regEmail !== conEmail) {
		matchEmails.addClass('error');
		regEmailInfo.removeClass('hidden').text('Emails do not match.');
	}
	// check if passwords match
	else if (regPass !== confirmPass) {
		matchPasswords.addClass('error');
		regPasswordInfo.removeClass('hidden').text('Passwords do not match');
	}
	// send post request to server
	else {
		$.ajax({
			type: "POST",
			url: '/register',
			data: {
				username: regEmail,
				password: regPass
			},
		}).done(function(response) {
			// deal with server response
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