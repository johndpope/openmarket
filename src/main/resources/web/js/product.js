// var productId = getLastUrlPath();
var productId = 1;
var productTemplate = $('#product_template').html();

$(document).ready(function() {

  getJson('get_product/' + productId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, productTemplate, '#product_div');
    $('.pic_num-1').addClass('active');

    writeReviewBtn();



  });

});

function writeReviewBtn() {

  $('#writeReviewBtn').click(function(e1) {
    simplePost('create_product_review/' + productId, null, null, reviewRedirect, null, null);


  });

}
function reviewRedirect(){
    var url = sparkService + 'your_reviews';
    window.location.replace(url);
}
