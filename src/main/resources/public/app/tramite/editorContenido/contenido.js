$(function () {

    Contenido = {

        Init: function () {

            CKEDITOR.replace('contenido', {height: 380});

        },
        updateContenido: function () {

            for (instance in CKEDITOR.instances)
            {
                CKEDITOR.instances[instance].updateElement();
            }
            var idCont = $("#idCont").val();
            var contenido = $("#contenido").val();
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/updateContenido'),
                data: {id: idCont, contenido: contenido},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }
    };

    Contenido.Init();

    $("body").delegate("#updateContenido", "click", function (e) {
        Contenido.updateContenido();
    });

});