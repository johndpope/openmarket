var shippingTemplate = $('#shipping_template').html();
var countryTemplate = $('#country_template').html();
var reviewOrderTemplate = $('#review_order_template').html();

var sellerId = getLastUrlPath();

$(document).ready(function() {


  setupWizard();

  tabLoad();
  createPayment();
  saveShippingForm();
  setupAddresses();




});

function setupReview() {
  getJson('get_cart_grouped/' + sellerId).done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, reviewOrderTemplate, '#review_order_div');

    setupSummerNoteNormal();

    var iframeText = data['order_iframe'].replace(/doublequote/g, '\"').replace(/semicolon/g, ';');
    console.log(iframeText);
    $('#iframe_div').html(iframeText);

    var paymentId = data['payment_id'];

    $('#change_address_btn').click(function() {
      $('#rootwizard').find("a[href*='shipping']").trigger('click');
    });
    // check the payment status, move to that tab if it ever gets to success
    checkStatusOfPayment(paymentId);

  });


}

function setupSummerNoteNormal() {
  $('.summernote').summernote({
    airMode: true
  });
}

function sendNote() {

  var summernoteDiv = $('.summernote');
  var note = summernoteDiv.code();

  if (note != null && note != '') {

    console.log(note);
    var shipmentId = parseInt($('.summernote').attr('shipment_id'));

    // var note = $('.send_note_content_' + shipmentId).code();
    // var shipmentId = $('.send_note_content_' + shipmentId).attr('shipment_id');

    // var jsonMap = {"note": note, "shipmentId":shipmentId};


    simplePost('/send_order_note/' + shipmentId, note, null,
      null, null, null, null);
  }

}

function checkStatusOfPayment(paymentId) {
  var url = externalSparkService + url; // the script where you handle the form input.
  var intervalID2 = setInterval(function() {
    getJson('get_payment/' + paymentId).done(function(e) {
      // 
      var data = JSON.parse(e);
      console.log(data);
      var paymentSuccess = data['completed'];

      // The payment was a success
      if (paymentSuccess == '1') {
        clearInterval(intervalID2);
        console.log('success!');
        sendNote();
        $('#paymentModal').modal('hide');
        $('#rootwizard').find("a[href*='status']").trigger('click');
        setupCart();
      }


    });

  }, 2000);
  // 1000 milliseconds = 1 second.
}

function createPayment() {
  standardFormPost('create_payment/' + sellerId, null, null, null, function() {
    // setupReview();
  }, null, null);
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
    shippingBtn();


  });
}

function shippingBtn() {

  $('.shipping-btn').click(function(e) {
    var addressId = parseInt(this.id.split('_').pop());


    simplePost('save_shipment/' + addressId + '/' + sellerId, null, null, function() {
      $('#rootwizard').find("a[href*='review_order']").trigger('click');
      setupReview();
    });

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
            }, null, null);


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
          }, null, null);


      });
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
        // $('#rootwizard').find('.pager .finish').show();
        $('#rootwizard').find('.pager .finish').removeClass('disabled');
      } else {
        // $('#rootwizard').find('.pager .next').show();
        $('#rootwizard').find('.pager .finish').hide();
      }

      if ($current == 1) {
        // $('#rootwizard').find('.pager .next').hide();
        $('.pager .next').addClass('hide');
        $('.pager .previous').addClass('hide');
        console.log('hidden');
      }
      // the review order tab
      if ($current == 2) {
        $('.pager .previous').removeClass('hide');
        $('#rootwizard').find('.pager .next').hide();

      }
    },
    onTabClick: function(tab, navigation, index) {
      console.log(tab);
      console.log(navigation);
  
       var $clicked = index + 1;
       console.log($clicked);
        if ($clicked == 2 || $clicked ==3) {
          // return false
        }
      // return false;
    }



  });

  $('#rootwizard .finish').click(function() {
    // alert('Finished!, Starting over!');
    // window.location('/wallet');
    $('#rootwizard').find("a[href*='tab1']").trigger('click');
  });
}
