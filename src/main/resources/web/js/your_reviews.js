var reviewTemplate = $('#review_template').html();

$(document).ready(function() {


  getJson('get_your_reviews').done(function(e) {



    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, reviewTemplate, '#reviews');
    $('.pic_num-1').addClass('active');

    $('.summernote').summernote();
    $('[name="stars"]').rating();


    var reviewArray = data['reviews'];

    reviewArray.forEach(function(review) {

      var reviewId = review['id'];
      var productId = review['product_id'];

      console.log('product id = ' + productId);

      var formName = '#review-' + productId + '-form';

      var headline = review['headline'];
      var textHtml = review['text_html'];
      var stars = review['stars'];


      if (textHtml != 'Write your review here' || textHtml != null) {
        $(formName + ' [name=text_html]').removeClass('hide');
      }

      console.log(headline);
      console.log(formName);

      setupSummerNoteNormal(productId);

      console.log($(formName));
      $(formName + ' [name=headline]').val(headline);
      $(formName + " [name=stars]").rating('update', stars);
      // $('#input-id').rating('update', 3);

      textHtml = htmlDecode(textHtml);
      
      $(formName + ' textarea[name="text_html"]').code(textHtml);

      // $(formName + " [name=text_html]").val(textHtml);



    });



  });





});

function setupSummerNoteNormal(productId) {

  var formName = '#review-' + productId + '-form';

  console.log('setup form for ' + formName);
  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();

      $(formName + ' [name=text_html]').removeClass('hide');
      // airMode: true
    }).on('success.form.bv', function(event) {
      event.preventDefault();
      console.log($('.summernote').code());
      // set the text input to the summer note code
      $(formName + ' [name=text_html]').val($('#text-' +productId).code());
      standardFormPost('save_product_review/' + productId, formName, null, null, null, null, null);
    });
  // airMode: true;


  // delay(function() {
  //   standardFormPost('set_product_bullet/' + productId + "/" + bulletNum, formName, null, null, null, null, null);
  // }, 1500);

  // $('[name="headline"]').click(function(e) {
  // 	console.log('got here');

  // });

}
