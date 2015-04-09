var feedbackTemplate = $('#feedback_template').html();

$(document).ready(function() {


  getJson('get_your_feedback').done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, feedbackTemplate, '#feedback_div');
    $('.pic_num-1').addClass('active');

    $('.summernote').summernote({minHeight: 100});
    $('[name="stars"]').rating();


    var feedbackArray = data['feedback'];

    feedbackArray.forEach(function(feedback) {

      var feedbackId = feedback['id'];

      var formName = '#feedback-form-' + feedbackId;

      var stars = feedback['stars'];
      var arrivedOnTime = feedback['arrived_on_time'];
      var correctlyDescribed = feedback['correctly_described'];
      var promptService = feedback['prompt_service'];
      var comments = feedback['comments'];




      // if (comments != 'Write your review here' || comments != null) {
      //   $(formName + ' [name=text_html]').removeClass('hide');
      // }
      console.log(formName);

      setupSummerNoteNormal(feedbackId);

      console.log($(formName));
      $(formName + " [name=stars]").rating('update', stars);
      if (arrivedOnTime != null && arrivedOnTime == '1') {
        $(formName + ' [name=arrived_on_time]').prop('checked', arrivedOnTime);
      }
      if (correctlyDescribed != null && correctlyDescribed == '1') {
        $(formName + ' [name=correctly_described]').prop('checked', correctlyDescribed);
      }
      if (promptService != null && promptService == '1') {
        $(formName + ' [name=prompt_service]').prop('checked', promptService);
      }



      // $('#input-id').rating('update', 3);

      if (comments != null) {
        comments = htmlDecode(comments);
        $(formName + ' textarea[name="comments"]').code(comments);
      }
      // $(formName + " [name=text_html]").val(textHtml);



    });



  });





});

function setupSummerNoteNormal(feedbackId) {

  var formName = '#feedback-form-' + feedbackId;

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
      $(formName + ' [name=comments]').val($('#comments-' + feedbackId).code());
      standardFormPost('save_feedback/' + feedbackId, formName, null, null, null, null, null);
    });
  // airMode: true;


  // delay(function() {
  //   standardFormPost('set_product_bullet/' + productId + "/" + bulletNum, formName, null, null, null, null, null);
  // }, 1500);

  // $('[name="headline"]').click(function(e) {
  //  console.log('got here');

  // });

}
