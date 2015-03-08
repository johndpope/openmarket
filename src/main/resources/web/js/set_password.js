$(document).ready(function() {




  setupSignupForm();




});


function setupSignupForm() {
  var token = getUrlParameter('token');

  console.log('token = ' + token);
  $('#token').val(token);
  $('#signupForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('set_password', "#signupForm", null, null, pageRedirect, null, null);

    });


}

function pageRedirect(data) {
  console.log('the data msg is = ' + data);
  var url = sparkService;
  // wait 1.5 seconds


  if (data == 'Logged in as a seller') {
    url = sparkService + '/seller_setup';
  }

  setTimeout(
    function() {

      window.location.replace(url);

    }, 1500);

}
