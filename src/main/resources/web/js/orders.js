var orderGroupsTemplate = $('#order_groups_template').html();


$(document).ready(function() {
  setupCart();


});

function setupCart() {
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


  });

}


