var productsTemplate = $('#products_template').html();

$(document).ready(function() {

  setupWizard();
  saveShopName();
  addNewItem();
  tabLoad();
  fillCurrentProducts();
});

function tabLoad() {
  // Javascript to enable link to tab
  var hash = document.location.hash;
  var prefix = "tab_";
  if (hash) {
  	console.log('hash');
    $('.nav-tabs a[href=' + hash.replace(prefix, "") + ']').tab('show');
     $('#rootwizard').find("a[href*='" + hash + "']").trigger('click');
  }

  // Change hash for page-reload
  $('.nav-tabs a').on('shown.bs.tab', function(e) {
  	console.log('got here2');
    window.location.hash = e.target.hash.replace("#", "#" + prefix);
    $('#rootwizard').find("a[href*='" + hash + "']").trigger('click');
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
    alert('Finished!, Starting over!');
    $('#rootwizard').find("a[href*='tab1']").trigger('click');
  });
}

function saveShopName() {
  $('#shopname_form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_shop_name', '#shopname_form', null, null, null, null, null);
      $('#rootwizard').find("a[href*='bitcoin_wallet']").trigger('click');
    });

}

function addNewItem() {
  $('#add_product').click(function() {
    simplePost('create_product', null, null, newItemRedirect, null, null);
  });
}

function fillCurrentProducts() {
getJson('product_thumbnails').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, productsTemplate, '#products');
    $('.pic_num-1').addClass('active');

  });
}

function newItemRedirect(productId) {
  var url = sparkService + 'product/edit/' + productId;
  window.location.replace(url);
}
