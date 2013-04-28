/**
 * Account page for listing of buildings and changing password
 * Written by: Daniel Muller
*/

/**
 * Summary: Sends get /buildingRefs request to server
 * Parameters: n/a
 * Returns: sends response to populate buildings
**/
function getUserBuildings() {
  $.ajax({
    type: "GET",
    url: '/buildingRefs',
    success: function(response) {
      var buildings = response.buildings
      populateUserBuildings(buildings)
    },
    error: function(error) {
      alert('An error occurred please refresh or login again')
    }
  });
}

/**
 * Summary: Populates buildings table on page
 * Parameters: buildings: list of buildings references from server
 * Returns: filled out table of user's buildings
**/
function populateUserBuildings(buildings) {
  for (var i = 0; i < buildings.length; i++) {
      var building = buildings[i]
      console.log(building.name);
      var tableRow = $('<tr></tr>').attr('id', building.id)
      var number = $('<td></td>').text(i).addClass("number");
      var name = $('<td></td>').text(building.name);
      var edit = $('<td></td>').html('<div class="btn btn-info">Edit</div>');
      edit.click(editBuilding.bind(building.id))
      var remove = $('<td></td>').html('<div class="btn btn-danger">Remove</div>');
      remove.click(removeBuilding.bind(building.id))
      tableRow.append(number);
      tableRow.append(name);
      tableRow.append(edit);
      tableRow.append(remove);
      $('#buildings').append(tableRow);
    };
}

/**
 * Summary: Grabs building data from server and saves to local storage for editing
 * Parameters: this: building that was clicked
 * Returns: redirect to authoring tool
**/
function editBuilding() {
  $.ajax({
    type: "GET",
    url: '/building',
    data: {
      buildingId: this
    },
    success: function(response) {
      var building = response.building
      //save in local storage and redirect
      localStorage.setItem('building', JSON.stringify(building.authoData));
      window.location = "/authoringTool.html";
    },
    error: function() {alert('Something bad happened')}
  })
}

/**
 * Summary: Deletes a user's building from both table and server
 * Parameters: this: building that was clicked
 * Returns: redirect to authoring tool
**/
function removeBuilding() {
  // ajax call to be placed here
  $("#"+this).remove();
  // timeout since dom manipulation is async
  setTimeout(function() {
    var numberedRows = $('.number');
    for (var i = 0; i < numberedRows.length; i++) {
      $(numberedRows[i]).text(i);
    };
  })
}

function changeUserPassword() {
  var inputs = $(".newPasswordForm").removeClass('error').removeClass('success');
  var newPassword = $('#newPassword').val();
  var confirmPassword = $('#confirmPassword').val();
  var passwordInfo = $('#passwordInfo').addClass('hidden');
  if (newPassword !== confirmPassword) {
    inputs.addClass('error');
    passwordInfo.removeClass('hidden').text('Not a valid password.');
  }
  else if (!(util.isValidInput(newPassword))) {
    inputs.addClass('error');
    passwordInfo.removeClass('hidden').text('Not a valid password.');
  }
  else {
    $(this).spin('small').addClass('disabled');
    $.ajax({
      type: "POST",
      url: '/changePassword',
      data: {
        newPassword: newPassword
      },
      success: function() {
        $('#submitChange').spin(false).removeClass('disabled')
        inputs.addClass('success');
        passwordInfo.removeClass('hidden').text('Password successfully changed.');
      },
      error: function() {
        $('#submitChange').spin(false).removeClass('disabled')
        inputs.addClass('error');
        passwordInfo.removeClass('hidden').text('Failed to change password.');
      }
    })
  }
}

$(document).ready(function() {
  // Initialize tabs and pills
  $('.note-tabs').tab();
  // Grabs list of a user's buildings from server
  getUserBuildings();
  $('#submitChange').click(changeUserPassword);
});