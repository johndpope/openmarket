var cartTemplate = $('#cart_template').html();
var wishlistTemplate = $('#wishlist_template').html();
var topCategoriesTemplate = $('#top_categories_template').html();

$(document).ready(function() {

  showHideElementsLoggedIn();

  setupSignupFormModal();
  setupLoginFormModal();

  setupTopCategories();

  setupSearch();

  $('[data-toggle="tooltip"]').tooltip();

});

function setupTopCategories() {
  getJson('get_top_categories').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, topCategoriesTemplate, '#top_categories_div');
    $(".wrapper").removeClass("hide");


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


function setupSearch() {

  var categoryURL = sparkService + 'category_search/%QUERY';
  var categoryList = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('name'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: categoryURL
  });

  var productURL = sparkService + 'product_search/%QUERY';
  var productList = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('title'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: productURL
  });

  var shopURL = sparkService + 'shop_search/%QUERY';
  var shopList = new Bloodhound({
    datumTokenizer: Bloodhound.tokenizers.obj.whitespace('title'),
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    // prefetch: '../data/films/post_1960.json',
    remote: shopURL
  });

  categoryList.initialize();
  productList.initialize();
  shopList.initialize();

  var typeAhead = $('#search_box .typeahead').typeahead({
    hint: true,
    highlight: true,
    minLength: 1,
  }, {
    name: 'category_list',
    displayKey: 'name',
    source: categoryList.ttAdapter(),
    templates: {
      header: '<h3 class="search-set">Categories</h3>'
    }

  }, {
    name: 'product_list',
    displayKey: 'title',
    source: productList.ttAdapter(),
    templates: {
      header: '<h3 class="search-set">Products</h3>',
      suggestion: function(context) {
        $.extend(context, numToStars);
        return Mustache.render('<div class="row"><div class="col-xs-3"><div class="img-responsive center-block"><a href="/product/{{product_id}}"><img class="cart-picture scale" src="{{pictures.0.url}}"></a></div></div><div class="col-xs-9"><div class="text-overflow"> {{title}}</div> <div class="text-info"> {{number_of_reviews}} <small>{{#numToStars}} {{review_avg}} {{/numToStars}}</small></div><p class="text-muted"><small>{{shop_name}}</small></p></div></div>', context);
      }
    }
  }, {
    name: 'shop_list',
    displayKey: 'shop_name',
    source: shopList.ttAdapter(),
    templates: {
      header: '<h3 class="search-set">Shops</h3>'
    }
  }).on('typeahead:selected', function(e, data) {
    console.log(data);

    // add a class for the type
    var searchId;
    if (data['name'] != null) {
      $("#search_form").addClass('category-search-type');
      searchId = data['id'];

    } else if (data['title'] != null) {
      $("#search_form").addClass('product-search-type');
      searchId = data['product_id'];
    } else if (data['shop_name'] != null) {
      $("#search_form").addClass('shop-search-type');
      searchId = data['id'];
    }

    $('#search_id').val(searchId);

    $(this).submit();
  });

  // $('[name=search_input]').focus();

  // $('.tt-input').focus();
  setTimeout("$('[name=search_input]').focus();", 0)

  $("#search_form").submit(function(event) {
    var formData = $("#search_form").serializeArray();



    // var classList = document.getElementsByName('creators_list').className.split(/\s+/);
    // console.log(classList);
    console.log(formData);
    var searchId = formData[0].value;
    var searchString = formData[1].value;


    var redirectUrl;
    if ($(this).hasClass('category-search-type')) {
      console.log('its a category');
      redirectUrl = "/category/" + searchId;
    } else if ($(this).hasClass('product-search-type')) {
      console.log('its a product');
      redirectUrl = "/product/" + searchId;
    } else if ($(this).hasClass('shop-search-type')) {
      console.log('its a shop');
      redirectUrl = "/shop/" + searchId;
    }
    // console.log(searchString);

    if (redirectUrl == null) {
      redirectUrl = '/browse';
    }
    window.location.replace(redirectUrl);

    event.preventDefault();
  });
}
