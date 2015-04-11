var saleGroupsTemplate = $('#sale_groups_template').html();


$(document).ready(function() {
  setupSales();


});

function setupSales() {


  getJson('get_sales_grouped').done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, saleGroupsTemplate, '#sale_groups_div');

    var salesArray = data['sale_groups'];
    salesArray.forEach(function(sale) {

      setupUpdateTrackingForm(sale);


    });



  });

}

function setupUpdateTrackingForm(sale) {

  var shipmentId = sale['shipment_id'];


  var formName = '#update_shipment_' + shipmentId;

  // fill the tracking url
  var trackingUrl = sale['tracking_url'];
  if (trackingUrl != null) {
    $(formName + " [name=tracking_url]").val(trackingUrl);
  }

  $(formName).bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('save_tracking_url/' + shipmentId, formName, null, null, null, null, null);
    });
}
