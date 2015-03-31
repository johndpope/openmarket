var productId = getLastUrlPath();
// var productId = 1;
var reviewTemplate = $('#review_template').html();

$(document).ready(function() {

  $('.wrapper').addClass('hide');

  getJson('get_reviews/' + productId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, reviewTemplate, '#review_div');
    voteBtn();
    var reviews = data['reviews'];
    fillReviewVotes(reviews);
  });

  getJson('get_product/' + productId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);

    $('#title').text(data['title']);
    $('#title').attr('href', sparkService + "product/" + data['id']);
    $('.wrapper').removeClass('hide');
  });

  writeReviewBtn();



});

function writeReviewBtn() {

  $('#writeReviewBtn').click(function(e1) {
    simplePost('create_product_review/' + productId, null, null, reviewRedirect, null, null);


  });

}

function reviewRedirect() {
  var url = sparkService + 'your_reviews';
  window.location.replace(url);
}
