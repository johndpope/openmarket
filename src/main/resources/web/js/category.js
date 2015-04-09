var categoryId = getLastUrlPath();
var categoryTemplate = $('#category_template').html();
var categoryThumbnailsTemplate = $('#category_thumbnails_template').html();

$(document).ready(function() {
  fillCategory();
  fillSquares();

});

function fillSquares() {
  getJson('get_category_thumbnails/' + categoryId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, categoryThumbnailsTemplate, '#category_thumbnails_div');
    $('.pic_num-1').addClass('active');

  });
}

function fillCategory() {
  getJson('get_subcategories/' + categoryId).done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, categoryTemplate, '#category_div');


    // fill the title
    var title = data[0]['name'];
    $('#title').text(title);

    // apply the styling to the first
    $('ul.left-bar-list li:nth-child(2)').addClass('hide');
    // $('ul.left-bar-list li:first a').toggleClass('text-muted');

  });
}
