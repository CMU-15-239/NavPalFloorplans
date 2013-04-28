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
    url: '/buildingsRefs',
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
      var numberCell = $('<td></td>').text(i).addClass("number");
      var nameCell = $('<td></td>').text(building.name);
      var editButton = $('<div class="btn btn-info">Edit</div>').attr('id','edit'+building.id);
      // allow user to edit building when they click edit button
      editButton.click(editBuilding.bind(building.id));
      var editCell = $('<td></td>').append(editButton)
      var removeButton = $('<div class="btn btn-danger">Remove</div>').attr('id','remove'+building.id);
      // allow user to delete building when the press remove button
      removeButton.click(removeBuilding.bind(building.id))
      var removeCell = $('<td></td>').append(removeButton);
      tableRow.append(numberCell);
      tableRow.append(nameCell);
      tableRow.append(editCell);
      tableRow.append(removeCell);
      $('#buildings').append(tableRow);
    };
}

/**
 * Summary: Grabs building data from server and saves to local storage for editing
 * Parameters: this: building that was clicked
 * Returns: redirect to authoring tool
**/
function editBuilding() {
  $("#"+'edit'+this).spin('small','#fff');
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
    error: function() {
      $("#"+'edit'+this).spin(false);
      alert('An error occurred, please try again.')
    }.bind(this)
  })
}

/**
 * Summary: Deletes a user's building from both table and server
 * Parameters: this: building that was clicked
 * Returns: redirect to authoring tool
**/
function removeBuilding() {
  $("#"+'remove'+this).spin('small','#fff');
  $.ajax({
    type: "GET",
    url: '/deleteBuilding',
    data: {
      buildingId: this
    },
    success: function(response) {
      $("#"+'remove'+this).spin(false);
      $("#"+this).remove();
      // timeout since dom manipulation is async
      setTimeout(function() {
        var numberedRows = $('.number');
        for (var i = 0; i < numberedRows.length; i++) {
          $(numberedRows[i]).text(i);
        };
      })
    }.bind(this),
    error: function() {
      $("#"+'remove'+this).spin(false);
      alert('An error occurred, please try again.')
    }.bind(this)
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
  // Remove whatever building is currently in local storage
  localStorage.setItem('building', '');
  // Initialize tabs and pills
  $('.note-tabs').tab();
  // Grabs list of a user's buildings from server
  getUserBuildings();
  $('#submitChange').click(changeUserPassword);
});