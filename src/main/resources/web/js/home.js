var thumbnailsTemplate = $('#thumbnails_template').html();
$(document).ready(function() {




  setupSignupForm();

  setupTrending('#trending_div','review_avg desc');
  setupTrending('#greatest_div','number_of_reviews desc');

});

function setupTrending(div, sortType) {
  getJson('get_trending_thumbnails/' + sortType).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, thumbnailsTemplate, div);
    $('.pic_num-1').addClass('active');
  });
}

function setupSignupForm() {
  $('#signup_form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('send_signup_email', "#signup_form", null, null, null, null, null);

    });

}

