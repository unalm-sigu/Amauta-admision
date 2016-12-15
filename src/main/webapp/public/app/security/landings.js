$(function() {

    $('.rol').click(function() {
        $.ajax({
            url: APP.url('rolland'),
            method: 'post',
            data: {rol: $(this).prop('rel')}
        }).done(function(html) {
            location.reload();
        });
    });

});