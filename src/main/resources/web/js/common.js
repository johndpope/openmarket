var cartTemplate = $('#cart_template').html();
var wishlistTemplate = $('#wishlist_template').html();
var topCategoriesTemplate = $('#top_categories_template').html();

$(document).ready(function() {

  showHideElementsLoggedIn();

  setupSignupFormModal();
  setupLoginFormModal();

  setupTopCategories();

  $('[data-toggle="tooltip"]').tooltip();

});

function setupTopCategories() {
  getJson('get_top_categories').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, topCategoriesTemplate, '#top_categories_div');

  });
}


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
      standardFormPost('login', "#login_form_modal", "#loginModal", null, showHideElementsLoggedIn, null, true);

    });


}

function setupCart() {
  getJson('get_cart').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);

    if (data.length == 0) {
      $('#cart_dropdown').addClass('hide');

    } else {
      $('#cart_dropdown_title').html('Cart <span class="badge">' + data.length + '</span> <span class="caret"></span>');
      $('#cart_dropdown').removeClass('hide');
      fillMustacheWithJson(data, cartTemplate, '#cart_div');
    }



  });
}

function setupWishlist() {
  getJson('wishlist_thumbnails').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);

    if (data['products'].length == 0) {
      $('#wishlist_dropdown').addClass('hide');

    } else {
      $('#wishlist_dropdown_title').html('Wishlist <span class="badge">' + data['products'].length + '</span> <span class="caret"></span>');
      $('#wishlist_dropdown').removeClass('hide');
      fillMustacheWithJson(data, wishlistTemplate, '#wishlist_div');
    }



  });
}


function showHideElementsLoggedIn() {
  var sessionId = getCookie("authenticated_session_id");
  console.log(sessionId);
  console.log(getCookies());
  console.log(document.cookie);



  if (sessionId != null) {
    setupCart();
    setupWishlist();
    setupLogout();



    $(".logged-in").removeClass("hide");
    $(".logged-out").addClass("hide");


  } else {
    $(".logged-in").addClass("hide");
    $(".logged-out").removeClass("hide");
    $('#cart_dropdown').addClass('hide');
    $('#cart_dropdown_title').html('Cart <span class="caret"></span>');

  }
  $(".wrapper").removeClass("hide");


}


function setupLogout() {

  $('#logouthref').click(function() {
    logout();



    // showHideElementsLoggedIn();




  });
}

function logout() {
  var sessionId = getCookie("authenticated_session_id");
  var url = sparkService + "/logout";
  $.ajax({
    type: "POST",
    url: url,
    xhrFields: {
      withCredentials: true
    },
    // data: seriesData, 
    success: function(data, status, xhr) {
      // console.log(data);
      // var jsonObj = JSON.parse(data);
      // JSON.useDateParser();
      // var jsonObj = jQuery.parseJSON(data);
      // JSON.useDateParser();


      toastr.success(data);
      // console.log(url);
      delete_cookie("authenticated_session_id");

      // setTimeout(
      //     function() {
      //         var url = "/";
      //         window.location.replace(url);

      //     }, 1500);

      showHideElementsLoggedIn();


    },
    error: function(request, status, error) {

      toastr.error(request.responseText);
    }
  });
}
