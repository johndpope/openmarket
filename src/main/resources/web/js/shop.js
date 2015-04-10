var sellerId = getLastUrlPath();
var shopThumbnailsTemplate = $('#shop_thumbnails_template').html();

$(document).ready(function() {
  fillSquares();

});

function fillSquares() {
  getJson('get_shop_thumbnails/' + sellerId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, shopThumbnailsTemplate, '#shop_thumbnails_div');
    $('.pic_num-1').addClass('active');

        // fill the title
    var title = data['products'][0]['shop_name'];
    console.log(title);
    $('#title').text(title);

  });
}


