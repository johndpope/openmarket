var productId = getLastUrlPath();
var categoryTemplate = $('#category_template').html();
var shipsToTemplate = $('#ships_to_template').html();
var shipsFromTemplate = $('#ships_from_template').html();
var currencyTemplate = $('#currency_template').html();
var processingTimeTemplate = $('#processing_time_template').html();

$(document).ready(function() {

  setupSummerNoteNormal();

  // setupSummerNoteAir();

  // setupCategories();


  setupPicturesForm();




  setupBulletsForm();

  // fillMustacheWithJson(data, templateHtml, divId)

  $.when(setupCurrencySelects(),
      getJson('get_product_shipping/' + productId))
    .done(function(e, e1) {
    	var shippingData = JSON.parse(e1[0])[0];
      setupShippingForm(shippingData);
    });




  $.when(setupCurrencySelects(),
      getJson('get_product/' + productId))
    .done(function(e, e1) {
      var productData = JSON.parse(e1[0])[0];


      console.log(productData);

      setupDetailsForm(productData);
      setupInfoForm(productData);
      categoryRecursive(productData);
      setupPriceForm(productData);

    });


});

function setupCurrencySelects() {
  return getJson('currencies').done(function(e) {
    var data = JSON.parse(e);

    fillMustacheWithJson(data, currencyTemplate, '.currency-select');
  });
}

var cCatId = 'null';
var cCatNum = 1;


function categoryRecursive(productData) {

  var productCategoryId = productData['category_id'];


  getJson('category_tree/' + productCategoryId).done(function(e2) {

    var catTree = JSON.parse(e2);
    console.log('cat tree:');
    console.log(catTree);

    if (cCatId != 'done') {
      categoryFetch(cCatId, cCatNum).done(function(e) {


        var data = JSON.parse(e);
        // TODO this needs to be from a select

        console.log("cCat id = " + cCatId + " cCatNum = " + cCatNum);
        console.log(data);
        var cCategoryNumbered = "#category_" + cCatNum;

        if (data.length > 0) {
          $(cCategoryNumbered).removeClass('hide');

        } else {
          $(cCategoryNumbered).remove();
        }
        var cNum = cCatNum;

        if (catTree['id_' + cCatNum] != null && cCatId != productCategoryId) {
          cCatNum = cNum;
          for (var k = 1; k < 6; k++) {
            var catToDelete = '#category_' + (k + parseInt(cNum));
            console.log(catToDelete);
            $(catToDelete).remove();
          }

          cCatId = catTree['id_' + cCatNum];
          if (cCatId <= productCategoryId) {
            $(cCategoryNumbered + ' select[name=category]').val(cCatId);
          }

          var subCategory = $(cCategoryNumbered).clone();
          subCategory.attr("id", "category_" + ++cCatNum);
          // subCategory.addClass('hide');
          // var childOptions = subCategory.attr('select[name=category] option');

          // console.log(childOptionsValues);
          subCategory.addClass('hide');
          subCategory.appendTo("#category_forms");
          categoryRecursive(productData);
        }


        $(cCategoryNumbered + ' select[name=category]').change(function(e1) {

          var selectedId = $(this).val();
          var url = 'set_product_category/' + productId + '/' + selectedId;
          simplePost(url, null, null, null, null, null);


          cCatNum = cNum;
          for (var k = 1; k < 6; k++) {
            var catToDelete = '#category_' + (k + parseInt(cNum));
            console.log(catToDelete);
            $(catToDelete).remove();
          }


          if (e == '[]') {
            cCatId = 'done';
            return false;
            console.log('got here');
          } else {

            // cCatId = JSON.parse(e)[0]['id'];

            var val = $(this).val();
            console.log(val);

            cCatId = val;

            var subCategory = $(cCategoryNumbered).clone();
            subCategory.attr("id", "category_" + ++cCatNum);
            // subCategory.addClass('hide');
            // var childOptions = subCategory.attr('select[name=category] option');

            // console.log(childOptionsValues);
            subCategory.addClass('hide');
            subCategory.appendTo("#category_forms");
            categoryRecursive(productData);

          }

        });


      });
    }
  });
}

function categoryFetch(parentId, num) {

  return getJson('category/' + parentId).done(function(e) {
    var data = JSON.parse(e);
    // console.log(data);
    fillMustacheWithJson(data, categoryTemplate, '#category_' + num);

    // newPicture.attr("id", "picture_" + ++pictureNum + "_form");
    // newPicture.appendTo("#pictures_forms");
  });
}

function setupShippingForm(shippingData) {
  getJson('countries').done(function(e) {
    var data = JSON.parse(e);

    fillMustacheWithJson(data, shipsToTemplate, '#ships_to');
    fillMustacheWithJson(data, shipsFromTemplate, '#ships_from');

    

    var shippingNum = 1;
    $('#addShippingBtn').click(function() {

      var newshipping = $("#shipping_location_1").clone();
      newshipping.attr("id", "shipping_location_" + ++shippingNum);
      newshipping.appendTo("#shipping_locations");
      console.log(newshipping);
      // shippingForm(shippingNum);
      return false;
    });

  });

  // shippingForm(1);


}


function setupPriceForm(productData) {

  $("#price_form [name='price']").val(productData['price']);
  $("#price_form [name='currency']").val(productData['native_currency_id']);
  $("#price_form [name='price_select']").val(productData['price_select']);
  $("#price_form [name='variable_price']").val(productData['variable_price']);
  $("#price_form [name='price_1']").val(productData['price_1']);
  $("#price_form [name='price_2']").val(productData['price_2']);
  $("#price_form [name='price_3']").val(productData['price_3']);
  $("#price_form [name='price_4']").val(productData['price_4']);
  $("#price_form [name='price_5']").val(productData['price_5']);

  $("#price_form [name='is_auction']").val(productData['auction']);
  $("#price_form [name='reserve_amount']").val(productData['reserve_amount']);
  $("#price_form [name='currency']").val(productData['native_currency_id']);


  if ($('#auction_radio').is(':checked')) {
    $('#auction_info').removeClass('hide');
    $('#pricing_advanced').removeClass('hide');
  } else {
    $('#auction_info').addClass('hide');
  }

  if ($('#suggested_amounts').is(':checked')) {
    $('#variable_prices').removeClass('hide');
    $('#pricing_advanced').removeClass('hide');
  } else {
    $('#variable_prices').addClass('hide');
  }


  $("#price_form [name='currency']").change(function(e1) {
    console.log('native curr id = ' + $(this).val());
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });

  $('.input-group.date').datepicker({
    format: 'yyyy-mm-dd'
  });

  $('#show_advanced').click(function() {
    $('#pricing_advanced').toggleClass('hide');
    return false;
  });



  $('#variable_price').click(function() {
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });

  $('#suggested_amounts').click(function() {
    if ($('#suggested_amounts').is(':checked')) {
      $('#variable_prices').removeClass('hide');
    } else {
      $('#variable_prices').addClass('hide');
    }
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });



  $('#auction_radio').click(function() {
    if ($('#auction_radio').is(':checked')) {
      $('#auction_info').removeClass('hide');
    } else {
      $('#auction_info').addClass('hide');
    }
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });

  $("#price_form [name='expiration_time']").change(function() {
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });

  $('#price_form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      delay(function() {
        standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
      }, 500);


    });


}

function fillFields() {
  // fillMustacheFromJson(, templateHtml, divId)
}

function setupSummerNoteNormal() {
  $('.summernote').summernote();
}

function setupSummerNoteAir() {
  $('.summernote-air').summernote({
    airMode: true

  });
}

function setupInfoForm(productData) {


  getJson('time_spans').done(function(e) {
    var data = JSON.parse(e);

    fillMustacheWithJson(data, processingTimeTemplate, '#processing_time');

    var processingTime = productData['processing_time_span_id'];
    if (processingTime != null) {
      $("[name='processing_time']").val(processingTime);
    }

    $("[name='processing_time']").change(function(e1) {
      standardFormPost('set_product_info/' + productId, "#info_form", null, null, null, null, null);
    });

  });

  // Now fill the fields if anything was already there
  var title = productData['title'];
  var quantity = productData['quantity'];


  console.log('title = ' + title);
  $("[name='title']").val(title);
  $("[name='quantity']").val(quantity);






  $('#info_form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      delay(function() {
        standardFormPost('set_product_info/' + productId, "#info_form", null, null, null, null, null);
      }, 500);


    });
}

function setupDetailsForm(productData) {

  if (productData['product_html'] != null) {
    var decoded = htmlDecode(productData['product_html']);
    $('.summernote').code(decoded);
  }

  $('#saveDetails').click(function() {

    var sHTML = $('.summernote').code();
    simplePost('set_product_details/' + productId, sHTML, null, null, null, null);
  });

}

function setupPicturesForm() {

  getJson('get_product_pictures/' + productId).done(function(e) {
    pictureForm(1);

    var pictures = JSON.parse(e);

    console.log(pictures);
    var pictureNum = 1;
    pictures.forEach(function(cPicture) {
      console.log(cPicture);
      var cURL = cPicture['url'];
      var cNum = cPicture['num_'];
      pictureNum = cNum;


      if (cNum > 1) {
        var newPicture = $("#picture_1_form").clone();
        newPicture.attr("id", "picture_" + cNum + "_form");

        var inp = newPicture.find("[name='picture_url']");
        inp.val(cURL);
        newPicture.appendTo("#pictures_forms");
        pictureForm(cNum);

      }



    });

    $('#addPictureBtn').click(function() {
      var newPicture = $("#picture_1_form").clone();
      newPicture.attr("id", "picture_" + ++pictureNum + "_form");
      newPicture.appendTo("#pictures_forms");
      var inp = newPicture.find("[name='picture_url']");
      inp.val(null);
      pictureForm(pictureNum);

    });

  });

}

function setupBulletsForm() {


  getJson('get_product_bullets/' + productId).done(function(e) {


    bulletForm(1);




    var bullets = JSON.parse(e);

    console.log(bullets);
    var bulletNum = 1;
    bullets.forEach(function(cBullet) {
      console.log(cBullet);
      var cText = cBullet['text'];
      var cNum = cBullet['num_'];
      bulletNum = cNum;


      if (cNum > 1) {
        var newBullet = $("#bullet_1_form").clone();
        newBullet.attr("id", "bullet_" + cNum + "_form");

        var inp = newBullet.find("[name='bullet']");
        inp.val(cText);
        newBullet.appendTo("#bullets_forms");
        bulletForm(cNum);

      }



    });


    $('#addBulletBtn').click(function() {
      var newBullet = $("#bullet_1_form").clone();
      newBullet.attr("id", "bullet_" + ++bulletNum + "_form");
      newBullet.appendTo("#bullets_forms");
      var inp = newBullet.find("[name='bullet']");
      inp.val(null);

      bulletForm(bulletNum);

    });



  });

}



function bulletForm(bulletNum) {

  var formName = '#bullet_' + bulletNum + '_form';
  console.log('bullet form name = ' + formName);

  $(formName + ' .removeBulletBtn').click(function() {
    simplePost('delete_product_bullet/' + productId + "/" + bulletNum);
    $(formName).remove();
    return false;
  });

  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      delay(function() {
        standardFormPost('set_product_bullet/' + productId + "/" + bulletNum, formName, null, null, null, null, null);
      }, 500);


    });
}

function pictureForm(pictureNum) {
  var formName = '#picture_' + pictureNum + '_form';

  $(formName + ' .removePictureBtn').click(function() {
    simplePost('delete_picture/' + productId + "/" + pictureNum);
    $(formName).remove();
    return false;
  });


  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      delay(function() {
        standardFormPost('set_product_picture/' + productId + "/" + pictureNum, formName, null, null, null, null, null);
      }, 500);


    });
}
