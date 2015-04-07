var ordersTemplate = null;

$(document).ready(function() {

 ordersTemplate = $('#orders_template').html();

  ordersTable(ordersTemplate);
});

function ordersTable(templateHTML) {

  pageNumbers['#orders_table'] = 1;
  setupPagedTable('api/orders', templateHTML, '#orders', '#orders_table');

}
