var currenciesTemplate = null;

$(document).ready(function() {

  // Fill the currency options
  currenciesTemplate = $('#currencies_template').html();

  fillMustacheFromJson('api/currencies', currenciesTemplate, "#currencies");

  advancedButton();

  $('#generatorForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
  .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('api/generate_button', "#generatorForm",
        null, false, successStuff, true);

    });


});

function advancedButton() {
  $('#advancedBtn').click(function(e) {
    e.preventDefault();
    $('#advanced_stuff').toggleClass('hide');
  });


  $('#suggested_amounts').click(function() {

    if ($("#suggested_amounts").is(':checked')) {

      $("#variable_prices").removeClass('hide'); // checked
    } else {

      $("#variable_prices").addClass('hide'); // unchecked
    }
  });
}

function successStuff(data) {
  var html = $('<div/>').html(data).text();
  console.log(html);

  $('#embed_code').html(data);

  // set the html of preview to the iframe or the button
  $('#preview').html(html);

  $('#after_generate').removeClass('hide');

}



function walletDate() {
  $('.input-group.date').datepicker({
    format: 'yyyy-mm-dd'
  });

  getJson('wallet/wallet_creation_date', true).done(function(result) {
    $('.input-group.date').datepicker(
      'update', result);
    console.log(result);
  });
}

function walletWords() {
  getJson('wallet/wallet_words', true).done(function(result) {
    $('#wallet_words').html(result);
  }).error(function(result) {





  });

}

function walletIsEncrypted() {
  getJson('wallet/wallet_is_encrypted').done(function(result) {
    console.log(result);
    if (result == 'true') {
      console.log("Wallet is encrypted");
      $('.wallet-is-unencrypted').addClass('hide');
      $('.wallet-is-encrypted').removeClass('hide');
    } else {
      console.log("Wallet is unencrypted");
      $('.wallet-is-unencrypted').removeClass('hide');
      $('.wallet-is-encrypted').addClass('hide');
    }
    // Hide the class of things that need to be hidden


  });
}

function walletIsLocked() {
  getJson('wallet/wallet_is_locked').done(function(result) {
    // Hide the class of things that need to be hidden
    if (result == 'true') {
      console.log("Wallet is locked");
      $('.wallet-is-unlocked').addClass('hide');
      $('.wallet-is-locked').removeClass('hide');
    } else {
      console.log("Wallet is unlocked");
      $('.wallet-is-unlocked').removeClass('hide');
      $('.wallet-is-locked').addClass('hide');
    }

  });

}
