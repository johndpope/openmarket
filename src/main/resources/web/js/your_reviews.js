$(document).ready(function() {

  setupSummerNoteNormal();
});

function setupSummerNoteNormal() {

  $('#review-1-form').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.field.bv', function(event) {
      event.preventDefault();
      $('.summernote').summernote({});
      $('#review').removeClass('hide');
      // airMode: true
    });
    

  // delay(function() {
  //   standardFormPost('set_product_bullet/' + productId + "/" + bulletNum, formName, null, null, null, null, null);
  // }, 1500);

  // $('[name="headline"]').click(function(e) {
  // 	console.log('got here');

  // });

}
