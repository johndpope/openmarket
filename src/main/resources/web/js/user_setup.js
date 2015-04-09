var productsTemplate = $('#products_template').html();
var shippingTemplate = $('#shipping_template').html();
var countryTemplate = $('#country_template').html();

$(document).ready(function() {

  setupWizard();
  tabLoad();



  if (loggedIn()) {
    saveUsername();
    fillUsername();
    saveShippingForm();
    setupAddresses();

    resetPasswordBtn();
    $('#reset_password_div').removeClass('hide');
    $('#set_password_div').addClass('hide');

  } else {
    setPasswordForm();
    $('#set_password_div').removeClass('hide');
    $('#reset_password_div').addClass('hide');
  }

});

function resetPasswordBtn() {
  $('#reset_password').click(function(e) {
    simplePost('send_reset_password_email', null, null,
      function(data) {
        logout();
        delay(function() {
          window.location.replace('/');

        }, 3000);
      }, null, null);
  });
}



function setupWizard() {
  $('#rootwizard').bootstrapWizard({
    onTabShow: function(tab, navigation, index) {
      var $total = navigation.find('li').length;
      var $current = index + 1;
      var $percent = ($current / $total) * 100;
      $('#rootwizard_bar').css({
        width: $percent + '%'
      });

      if ($current >= $total) {
        $('#rootwizard').find('.pager .next').hide();
        $('#rootwizard').find('.pager .finish').show();
        $('#rootwizard').find('.pager .finish').removeClass('disabled');
      } else {
        $('#rootwizard').find('.pager .next').show();
        $('#rootwizard').find('.pager .finish').hide();
      }
    }
  });

  $('#rootwizard .finish').click(function() {
    // alert('Finished!, Starting over!');
    window.location('/wallet');
    $('#rootwizard').find("a[href*='tab1']").trigger('click');
  });
}

function saveUsername() {
  $('#username_form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_username', '#username_form', null, null, null, null, null);
      $('#rootwizard').find("a[href*='shipping']").trigger('click');
    });

}


function fillUsername() {
  getJson('get_user').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    var username = data['name'];

    if (username != null) {
      $('#username_form [name="username"]').val(username);
    }
  });
}



function setPasswordForm() {
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
      standardFormPost('set_password', "#signupForm", null, null, sellerRedirect, null, null);

    });


}

function sellerRedirect(data) {
  console.log('the data msg is = ' + data);
  var url = sparkService;
  // wait 1.5 seconds


  saveUsername();
  fillUsername();
  saveShippingForm();
  setupAddresses();

  if (data == 'Logged in as a seller') {
    url = sparkService + 'seller_setup';

    setTimeout(
      function() {

        window.location.replace(url);

      }, 1500);

  } else {
    $('#rootwizard').find("a[href*='username']").trigger('click');
  }
}


function deleteAddress() {
  $('.delete_shipping').click(function(e) {
    var addressId = this.id.split('_').pop();
    console.log(addressId);
    simplePost('delete_address/' + addressId, null, null,
      function(data) {
        setupAddresses();
      }, null, null);

  });
}

function setupAddresses() {
  getJson('get_addresses').done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, shippingTemplate, '#shipping_div');


    deleteAddress();
    editAddress(data);



  });
}

function editAddress(data) {
  $('.edit_shipping').click(function(e) {
    var addressId = parseInt(this.id.split('_').pop());
    console.log(addressId);

    // find the correct index
    var index;
    for (index = 0; index < data.length; index++) {
      var loopId = parseInt(data[index]['id']);
      if (loopId == addressId) {
        break;
      }
    }

    // Fill the shipping form
    editShippingForm(data[index], addressId);

    // Load up the modal
    $('#shippingModal').modal('show');

  });
}

function saveShippingForm() {

  $('#addAddressBtn').click(function(e1) {
    $('#shippingModal').modal('show');

    getJson('countries').done(function(e) {
      var data = JSON.parse(e);


      fillMustacheWithJson(data, countryTemplate, '#country_div');

      // $("[name='from_country']").val(shippingData['from_country_id']);

      $('#shipping_form').bootstrapValidator('destroy');
      $('#shipping_form').bootstrapValidator({
          message: 'This value is not valid',
          excluded: [':disabled'],
          submitButtons: 'button[type="submit"]'
        })
        .on('success.form.bv', function(event) {
          event.preventDefault();
          standardFormPost('save_address', '#shipping_form', "#shippingModal", null,
            function() {
              setupAddresses();
            }, null, true);


        });
    });
  });
}

function editShippingForm(addressData, addressId) {

  getJson('countries').done(function(e) {
    var data = JSON.parse(e);


    fillMustacheWithJson(data, countryTemplate, '#country_div');
    console.log(addressData);
    $("[name='full_name']").val(addressData['full_name']);
    $("[name='street']").val(addressData['address_line_1']);
    $("[name='addr_two']").val(addressData['address_line_2']);
    $("[name='zipcode']").val(addressData['zip']);
    $("[name='city']").val(addressData['city']);
    $("[name='state']").val(addressData['state']);
    $("[name='country']").val(addressData['country_id']);

    $('#shipping_form').bootstrapValidator('destroy');

    $('#shipping_form').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],
        submitButtons: 'button[type="submit"]'
      })
      .on('success.form.bv', function(event) {
        event.preventDefault();
        standardFormPost('edit_address/' + addressId, '#shipping_form', "#shippingModal", null,
          function() {
            setupAddresses();
          }, null, true);


      });
  });

}
