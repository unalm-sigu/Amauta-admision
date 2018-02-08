$(function() {

    $('.ciclo').click(function() {
        $.ajax({
            url: APP.url('academico/cicloacademico/cicloland'),
            method: 'post',
            data: {ciclo: $(this).prop('rel')}
        }).done(function(html) {
            location.href = "/route66";
        });
    });

});