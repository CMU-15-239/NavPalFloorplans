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
	$(this).spin('small').addClass('disabled');
	// make all error messages hidden
	$(".help-block").addClass('hidden');
	// grab input email and password
	var logEmail = $("#loginEmail").val();
	var logPass = $("#loginPass").val();
	// grab elements to change in case of error, reset errors
	var loginForm = $("#loginForm").attr('class', 'control-group');
	var loginInfo = $("#loginInfo");
	// send post request to server with username and password
	$.ajax({
		type: "POST",
		url: '/login',
		data: {
			username: logEmail,
			password: logPass
		},
		success: function() {
			// redirect to account page upon successful login
			window.location = "/account.html";
		},
		error: function() {
			// if an error occurs alert user to login failure
			$("#loginButton").spin(false).removeClass('disabled');
			loginForm.addClass('error');
			loginInfo.removeClass('hidden').text('Login failed, please try again');
		}
	});
})


/**
 * Summary: Handles response of registration request to server
 * Parameters: response.errorCode - type of error that occurred
 	0 - success, 1 - invalid data, 2 - email exists, 3 - auto login failed
 * Returns: rect if successful orelse error message
**/
function handleRegistration(response) {
	$("#registrationButton").spin(false).removeClass('disabled');
	var matchMails = $('#matchEmails');
	var matchEmails = $('#matchEmails').attr('class', 'control-group');
	var registrationEmailInfo = $('#registrationEmailInfo');
	// deal with server response
	switch (response.errorCode) {
		// succssful registration redirects to building creation page
		case 0:
			window.location = '/floorUploads.html';
			break;
		case 1:
			matchEmails.addClass('error');
			registrationEmailInfo.removeClass('hidden').text('Invalid Email');
			break
		case 2:
			matchEmails.addClass('info');
			registrationEmailInfo.removeClass('hidden').text('Email already registered');
			break
		case 3:
			matchEmails.addClass('success');
			registrationEmailInfo.removeClass('hidden').text('Account created! Please login.');
			break
	}
}

/**
 * Summary: Sends post /register request to server
 * Parameters: valid email and password combo
 * Returns: ajax request if data is valid orelse error message
**/
$("#registrationButton").click(function() {
	// make all error messages hidden
	$(".help-block").addClass('hidden');
	// grab email and password input
	var registrationEmail = $("#registrationEmail").val();
	var conEmail = $("#confirmEmail").val();
	var registrationPass = $("#registrationPass").val();
	var confirmPass = $("#confirmPass").val();
	// grab elements to change in case of error, reset errors
	var matchEmails = $('#matchEmails').attr('class', 'control-group');
	var matchPasswords = $('#matchPasswords').attr('class', 'control-group');
	var registrationEmailInfo = $('#registrationEmailInfo');
	var registrationPasswordInfo = $('#registrationPasswordInfo');
	// check if emails match
	if (registrationEmail !== conEmail) {
		matchEmails.addClass('error');
		registrationEmailInfo.removeClass('hidden').text('Emails do not match.');
	}
	// check if passwords match
	else if (registrationPass !== confirmPass) {
		matchPasswords.addClass('error');
		registrationPasswordInfo.removeClass('hidden').text('Passwords do not match');
	}
	else if (!util.isValidInput(registrationPass)) {
		matchPasswords.addClass('error');
		registrationPasswordInfo.removeClass('hidden').text('Not a valid password.');
	}
	// send post request to server with username and password
	else {
		$(this).spin('small').addClass('disabled');
		$.ajax({
			type: "POST",
			url: '/register',
			data: {
				username: registrationEmail,
				password: registrationPass
			},
		}).done(handleRegistration);
	}
});