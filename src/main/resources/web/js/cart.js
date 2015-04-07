var cartGroupsTemplate = $('#cart_groups_template').html();


$(document).ready(function() {
  setupCart();


});

function setupCart() {
  getJson('get_cart_grouped').done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, cartGroupsTemplate, '#cart_groups_div');
    setupSummerNoteNormal();

    setupRemoveFromCart();

  });

}

function setupSummerNoteNormal() {
  $('.summernote').summernote({
    airMode: true
  });
}

function setupRemoveFromCart() {
  $('.remove_from_cart').click(function(e) {
    var cartItemId = this.id.split('_').pop();
    console.log(cartItemId);
    simplePost('remove_from_cart/' + cartItemId, null, null,
      function(data) {
      	setupCart();
      }, null, null);

  });
}
