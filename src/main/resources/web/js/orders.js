var orderGroupsTemplate = $('#order_groups_template').html();


$(document).ready(function() {
  setupOrderPage();


});

function setupOrderPage() {
  var viewAllOrOpen = getUrlParameter("view");
  console.log(viewAllOrOpen);

  var url = 'get_orders_grouped';
  if (viewAllOrOpen != null) {
    url += '?view=' + viewAllOrOpen;
  }

  getJson(url).done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, orderGroupsTemplate, '#order_groups_div');

    setupSummerNoteNormal();
    setupSendNote();
  });

}

function setupSummerNoteNormal() {
  $('.summernote').summernote({
    airMode: true
  });
}

function setupSendNote() {
  $('.send-note-btn').click(function() {

    var shipmentId = parseInt(this.id.split('_').pop());
    var btnId = '#send_note_btn_' + shipmentId;

    var note = $('.send_note_content_' + shipmentId).code();
    // var shipmentId = $('.send_note_content_' + shipmentId).attr('shipment_id');

    // var jsonMap = {"note": note, "shipmentId":shipmentId};
    simplePost('/send_order_note/' + shipmentId, note, null,
      null, null, null, btnId);

  });
}

// var messageDiv = '#feedback-form-' + feedbackId;

// $(formName + ' [name=text_html]').removeClass('hide');

// console.log($('.summernote').code());
// // set the text input to the summer note code
// $(formName + ' [name=comments]').val($('#comments-' + feedbackId).code());
// standardFormPost('save_feedback/' + feedbackId, formName, null, null, null, null, null);
// });
// }
