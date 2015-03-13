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
      getJson('get_shipping/' + productId),
      getJson('get_shipping_costs/' + productId))
    .done(function(e, e1, e2) {
      console.log(e1);
      var shippingData = JSON.parse(e1[0]);
      console.log(shippingData);

      var shippingCosts = JSON.parse(e2[0]);
      setupShippingForm(shippingData, shippingCosts);
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

function setupShippingForm(shippingData, shippingCostsData) {
  getJson('countries').done(function(e) {
    var data = JSON.parse(e);

    console.log(shippingData);
    console.log(shippingCostsData);

    fillMustacheWithJson(data, shipsToTemplate, '#ships_to');
    fillMustacheWithJson(data, shipsFromTemplate, '#ships_from');

    $("[name='from_country']").val(shippingData['from_country_id']);

    $("#ships_from [name='from_country']").change(function(e1) {
      standardFormPost('set_product_shipping/' + productId, "#shipping_form", null, null, null, null, null);
    });



    shippingForm(1);



    console.log(shippingCostsData);
    var shippingNum = 1;
    shippingCostsData.forEach(function(cShipping) {
      console.log(cShipping);
      var price = cShipping['price'];
      var curr = (cShipping['native_currency_id'] != null) ? cShipping['native_currency_id'] : 1;
      var toCountry = cShipping['to_country_id'];


      var cNum = cShipping['num_'];
      shippingNum = cNum;



      if (cNum > 1) {
        var newShipping = $("#shipping_cost_1_form").clone();
        newShipping.attr("id", "shipping_cost_" + cNum + "_form");

        var priceInp = newShipping.find("[name='shipping_cost']");
        priceInp.val(price);

        var currInp = newShipping.find("[name='currency']");
        currInp.val(curr);

        var toCountryInp = newShipping.find("[name='to_country']");
        toCountryInp.val(toCountry);


        newShipping.appendTo("#shipping_cost_forms");
        shippingForm(cNum);

      } else if (cNum == 1) {
        $("#shipping_cost_1_form [name='shipping_cost']").val(price);
        $("#shipping_cost_1_form [name='currency']").val(curr);
        $("#shipping_cost_1_form [name='to_country']").val(toCountry);
      }



    });


    $('#addShippingBtn').click(function() {
      var newShipping = $("#shipping_cost_1_form").clone();
      newShipping.attr("id", "shipping_cost_" + ++shippingNum + "_form");
      newShipping.appendTo("#shipping_cost_forms");

      var priceInp = newShipping.find("[name='shipping_cost']");
      priceInp.val(null);

      var currInp = newShipping.find("[name='currency']");
      currInp.val(1);

      var toCountryInp = newShipping.find("[name='to_country']");
      toCountryInp.val(null);

      shippingForm(shippingNum);

      return false;

    });





  });

  // shippingForm(1);


}


function setupPriceForm(productData) {

  $("#price_form [name='price']").val(productData['price']);

  var currId = (productData['native_currency_id'] != null) ? productData['native_currency_id'] : 1;
  console.log('curr id = ' + currId);
  $("#price_form [name='currency']").val(currId);


  if (productData['variable_price'] != null) {
    $("#price_form [name='check_variable_price']").prop('checked', productData['variable_price']);
  }

  if (productData['price_select'] != null) {
    $("#price_form [name='check_price_select']").prop('checked', productData['price_select']);
  }

  if (productData['auction'] != null) {
    $("#price_form [name='is_auction']").prop('checked', productData['auction']);
  }

  $("#price_form [name='price_1']").val(productData['price_1']);
  $("#price_form [name='price_2']").val(productData['price_2']);
  $("#price_form [name='price_3']").val(productData['price_3']);
  $("#price_form [name='price_4']").val(productData['price_4']);
  $("#price_form [name='price_5']").val(productData['price_5']);


  $("#price_form [name='reserve_amount_auction']").val(productData['reserve_amount']);
  $("#price_form [name='start_amount_auction']").val(productData['start_amount']);

  if (productData['expire_time'] != null) {
    var exprString = new Date(parseInt(productData['expire_time'])).toISOString().substring(0, 10);
    console.log(exprString);

    $("#price_form [name='expiration_time']").val(exprString);
  }


  if ($("[name='is_auction']").is(':checked')) {
    $('#auction_info').removeClass('hide');
    $('#pricing_advanced').removeClass('hide');
  } else {
    $('#auction_info').addClass('hide');
  }

  if ($("[name='check_price_select']").is(':checked')) {
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



  $("[name='check_variable_price']").click(function() {
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });

  $("[name='check_price_select']").click(function() {
    if ($("[name='check_price_select']").is(':checked')) {
      $('#variable_prices').removeClass('hide');
    } else {
      $('#variable_prices').addClass('hide');
    }
    standardFormPost('set_product_price/' + productId, "#price_form", null, null, null, null, null);
  });



  $("[name='is_auction']").click(function() {
    if ($("[name='is_auction']").is(':checked')) {
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

      console.log(cNum);


      if (cNum > 1) {
        var newPicture = $("#picture_1_form").clone();
        newPicture.attr("id", "picture_" + cNum + "_form");

        var inp = newPicture.find("[name='picture_url']");
        inp.val(cURL);

        var pic = newPicture.find(".picture_image");
        pic.attr('src', cURL);

        newPicture.appendTo("#pictures_forms");

        pictureForm(cNum);

      } else if (cNum == 1) {
        $("#picture_1_form [name='picture_url']").val(cURL);
        console.log($("#picture_1_form .picture_image"));
        $("#picture_1_form .picture_image").attr('src', cURL);
      }



    });

    $('#addPictureBtn').click(function() {
      var newPicture = $("#picture_1_form").clone();
      newPicture.attr("id", "picture_" + ++pictureNum + "_form");
      newPicture.appendTo("#pictures_forms");
      var inp = newPicture.find("[name='picture_url']");
      inp.val(null);

      var pic = newPicture.find(".picture_image");
      pic.attr('src', null);


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

      } else if (cNum == 1) {
        $("#bullet_1_form [name='bullet']").val(cText);
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
        var pictureURL = $(formName).find('input[name="picture_url"]').val();
        console.log(pictureURL);
        $(formName).find('.picture_image').attr('src', pictureURL);
      }, 500);


    });
}

function shippingForm(shippingNum) {

  var formName = '#shipping_cost_' + shippingNum + "_form";
  console.log('shipping form name = ' + formName);

  $(formName + ' .removeShippingBtn').click(function() {
    simplePost('delete_product_shipping_cost/' + productId + "/" + shippingNum);
    $(formName).remove();
    return false;
  });

  $(formName + " [name='to_country']").change(function(e1) {
    standardFormPost('set_product_shipping_cost/' + productId + "/" + shippingNum, formName, null, null, null, null, null);
  });

  $(formName + " [name='currency']").change(function(e1) {
    standardFormPost('set_product_shipping_cost/' + productId + "/" + shippingNum, formName, null, null, null, null, null);
  });

  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      delay(function() {
        standardFormPost('set_product_shipping_cost/' + productId + "/" + shippingNum, formName, null, null, null, null, null);
      }, 500);


    });
}
