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
            standardFormPost('set_password', "#signupForm", null, null, null, null, null);

        });


}
