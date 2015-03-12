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


  setupPriceForm();

  setupBulletsForm();

  // fillMustacheWithJson(data, templateHtml, divId)
  setupShippingForm();

  categoryRecursive();

  setupCurrencySelects();

  getJson('product/' + productId).done(function(e) {
    var productData = JSON.parse(e)[0];

    console.log(productData);

    setupDetailsForm(productData);
    setupInfoForm(productData);
  });


});

function setupCurrencySelects() {
  getJson('currencies').done(function(e) {
    var data = JSON.parse(e);

    fillMustacheWithJson(data, currencyTemplate, '.currency-select');
  });
}

var cCatId = 'null';
var cCatNum = 1;


function categoryRecursive() {

  if (cCatId != 'done') {
    categoryFetch(cCatId, cCatNum).done(function(e) {



      var data = JSON.parse(e);
      // TODO this needs to be from a select
      console.log(e);

      console.log(cCatId, cCatNum);
      var cCategoryNumbered = "#category_" + cCatNum;

      if (data.length > 0) {
        $(cCategoryNumbered).removeClass('hide');

      } else {
        $(cCategoryNumbered).remove();
      }
      var cNum = cCatNum;
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
          categoryRecursive();

        }

      });


    });
  }
}

function categoryFetch(parentId, num) {

  return getJson('category/' + parentId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, categoryTemplate, '#category_' + num);

    // newPicture.attr("id", "picture_" + ++pictureNum + "_form");
    // newPicture.appendTo("#pictures_forms");
  });
}

function setupShippingForm() {
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


function setupPriceForm() {

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
  });

  $('#auction_radio').click(function() {
    if ($('#auction_radio').is(':checked')) {
      $('#auction_info').removeClass('hide');
    } else {
      $('#auction_info').addClass('hide');
    }
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
    $("[name='processing_time']").val(processingTime);

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

  pictureForm(1);

  var pictureNum = 1;
  $('#addPictureBtn').click(function() {
    var newPicture = $("#picture_1_form").clone();
    newPicture.attr("id", "picture_" + ++pictureNum + "_form");
    newPicture.appendTo("#pictures_forms");

    pictureForm(pictureNum);

  });

}

function setupBulletsForm() {
  bulletForm(1);

  var bulletNum = 1;
  $('#addBulletBtn').click(function() {
    var newBullet = $("#bullet_1_form").clone();
    newBullet.attr("id", "bullet_" + ++bulletNum + "_form");
    newBullet.appendTo("#bullets_forms");

    bulletForm(bulletNum);

  });

}

function bulletForm(bulletNum) {

  var formName = '#bullet_' + bulletNum + '_form';
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
