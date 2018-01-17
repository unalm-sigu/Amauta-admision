$(function() {

    Contenido = {

        Init: function() {

            if ($("#tipo").val() == 'CONT') {
                CKEDITOR.replace('contenido', {height: 380});
            }

            $('#fileupload').fileupload({
                url: APP.url('archivo/upload'),
                maxNumberOfFiles: 1,
                dataType: 'json',
                add: function(e, data) {
                    if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                        notify("Formato de archivo no soportado.", "error");
                        return;
                    }
                    data.submit();
                },
                progress: function(e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    if (progress === 100) {
                    }
                },
                done: function(e, data) {
                    $('input:submit').removeAttr("disabled");
                    if (data.result.success) {

                        var fileName = data.result.data;
                        $('#imagen').attr("src", APP.url("archivo/downloadfn/" + fileName));
                        $('#imgUrl').val(fileName);
                        console.log(fileName);
                        notify(data.result.message, "info");

                    } else {
                        notify(data.result.message, "error");
                    }

                },
                fail: function(e, data) {
                    $('input:submit').removeAttr("disabled");
                    notify(data.result.message, "error");
                }
            });
        },
        updateContenido: function() {

            for (instance in CKEDITOR.instances)
            {
                CKEDITOR.instances[instance].updateElement();
            }
            var idCont = $("#idCont").val();
            var contenido = $("#contenido").val();
            var sistema = $("#sistema").val();
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/updateContenido'),
                data: {idContenido: idCont, contenido: contenido, idSistema: sistema},
                success: function(response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        updateImg: function() {
            var img = $("#imgUrl").val();
            var idCont = $("#idCont").val();
            if (img == '' || idCont == '') {
                notify("No ha cargado ninguna imagen", 'warning');
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/updateImg'),
                data: {idContenido: idCont, fileName: img},
                success: function(response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }

    };

    Contenido.Init();

    $("body").delegate("#updateContenido", "click", function(e) {
        Contenido.updateContenido();
    });
    $("body").delegate("#updateImg", "click", function(e) {
        Contenido.updateImg();
    });
});