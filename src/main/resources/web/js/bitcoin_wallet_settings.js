function setupWallet() {
  $('#setPasswordForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('wallet/set_wallet_password', "#setPasswordForm",
        "#setPasswordModal", false, specialReload);

    });

  $('#removePasswordForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('wallet/remove_wallet_password',
        "#removePasswordForm", "#removePasswordModal", false, specialReload);

    });

  $('#unlockWalletForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('wallet/unlock_wallet',
        "#unlockWalletForm", "#unlockWalletModal", false, specialReload);

    });

  $('#restoreWalletForm').bootstrapValidator({
      message: 'This value is not valid',
      excluded: [':disabled'],
      submitButtons: 'button[type="submit"]'
    })
    .on('success.form.bv', function(event) {
      event.preventDefault();
      standardFormPost('wallet/restore_wallet',
        "#restoreWalletForm", null, false, specialReload);

    });

  specialReload();
  restoreWalletBtn();
}

function restoreWalletBtn() {


  getJson('wallet/wallet_is_encrypted').done(function(result) {
    if (result == 'false') {
      $('#wallet_words').bind('keyup', function(f) {
        console.log('keyup');
        $('#restoreBtn').removeClass('disabled');

      });
    }
  });


}

function specialReload() {
  walletDate();
  walletWords();
  walletIsEncrypted();
  walletIsLocked();
  restoreWalletBtn();
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
