var productId = getLastUrlPath();
// var productId = 1;
var productTemplate = $('#product_template').html();

$(document).ready(function() {

  getJson('get_product/' + productId).done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, productTemplate, '#product_div');
    $('.pic_num-1').addClass('active');

    writeReviewBtn();
    askQuestionBtn();

    voteBtn();

    replyBtn();

    // Fill the potential users info, stuff like vote buttons, etc
    if (loggedIn()) {
      console.log('your logged in');
      var reviews = data['reviews'];
      fillReviewVotes(reviews);

      var questions = data['questions'];
      fillQuestionVotes(questions);

      questions.forEach(function(question) {
        var answers = question['answers'];
        fillAnswerVotes(questions);
      })

    }

  });

});



function reviewRedirect() {
  var url = sparkService + 'your_reviews';
  window.location.replace(url);
}

function askQuestionBtn() {
  var formName = '#question_form';
  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();

      standardFormPost('ask_question/' + productId, formName, null, null, null, null, null);

    });
}
