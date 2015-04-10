var networkTemplate = $('#network_template').html();

$(document).ready(function() {
  fillNetworkInfo();

});

function fillNetworkInfo() {
  getJson('get_network').done(function(e) {
    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, networkTemplate, '#network_div');
    
  });
}