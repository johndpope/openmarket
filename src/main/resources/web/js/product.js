// var productId = getLastUrlPath();
var productTemplate = $('#product_template').html();

$(document).ready(function() {

  getJson('get_product/1').done(function(e) {
  	var data = JSON.parse(e);
  	console.log(data);
    fillMustacheWithJson(data, productTemplate, '#product_div');
    $('.pic_num-1').addClass('active');
  });

});
