var wishlistTemplate = $('#wishlist_page_template').html();

$(document).ready(function() {

  fillWishList();
});



function fillWishList() {
  getJson('wishlist_thumbnails').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, wishlistTemplate, '#wishlist_page_div');
    $('.pic_num-1').addClass('active');

    setupRemoveFromWishlist();

  });

  function setupRemoveFromWishlist() {
    $('.remove_from_wishlist').click(function(e) {
      var productId = this.id.split('_').pop();
      console.log(productId);
      simplePost('remove_from_wishlist/' + productId, null, null,
        function(data) {
          fillWishList();
        }, null, null);

    });
  }
}
