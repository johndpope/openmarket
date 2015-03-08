    $(document).ready(function() {
      $('#rootwizard').bootstrapWizard({
        onTabShow: function(tab, navigation, index) {
          var $total = navigation.find('li').length;
          var $current = index + 1;
          var $percent = ($current / $total) * 100;
          $('#rootwizard_bar').css({
            width: $percent + '%'
          });

          if ($current >= $total) {
            $('#rootwizard').find('.pager .next').hide();
            $('#rootwizard').find('.pager .finish').show();
            $('#rootwizard').find('.pager .finish').removeClass('disabled');
          } else {
            $('#rootwizard').find('.pager .next').show();
            $('#rootwizard').find('.pager .finish').hide();
          }
        }
      });

      $('#rootwizard .finish').click(function() {
        alert('Finished!, Starting over!');
        $('#rootwizard').find("a[href*='tab1']").trigger('click');
      });

    });
