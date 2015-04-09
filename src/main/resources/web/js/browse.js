var browseTemplate = $('#browse_template').html();

$(document).ready(function() {
  fillBrowse();

});

function fillBrowse() {
  getJson('get_browse').done(function(e) {

    var data = JSON.parse(e);
    console.log(data);
    fillMustacheWithJson(data, browseTemplate, '#browse_div');
  });
}
