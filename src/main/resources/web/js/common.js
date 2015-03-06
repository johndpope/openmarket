$(document).ready(function() {

    showHideElementsLoggedIn();


    setupSignupFormModal();
    setupLoginFormModal();

    setupLogout();



});



function setupSignupFormModal() {
    $('#signup_form_modal').bootstrapValidator({
            message: 'This value is not valid',
            excluded: [':disabled'],
            submitButtons: 'button[type="submit"]'
        })
        .on('success.form.bv', function(event) {
            event.preventDefault();
            standardFormPost('send_signup_email', "#signup_form_modal", "#loginModal", true, null, null, null);

        });

}

function setupLoginFormModal() {
    $('#login_form_modal').bootstrapValidator({
            message: 'This value is not valid',
            excluded: [':disabled'],
            submitButtons: 'button[type="submit"]'
        })
        .on('success.form.bv', function(event) {
            event.preventDefault();
            standardFormPost('login', "#login_form_modal","#loginModal", true, null, null, null);

        });


}