$(function () {

    $('#formLoadFiles').ajaxForm({
        beforeSend: function () {
            $('#progress .progress-bar').css('width', 0 + '%');
        },
        uploadProgress: function (e, position, total, percent) {
            $('#progress .progress-bar').css('width', percent + '%');
        },
        success: function () {
            $('#progress .progress-bar').css('width', 100 + '%');
        },
        complete: function (response) {
            var json = response.responseJSON;
            if (json.success) {
                $("#btnLoad").html('Carga finalizada');


                if (json.data !== null) {
                    var cuerpo = '<table class="table table-striped"><thead><tr><th>Descripcion</th></tr></thead><tbody>';

                    $.each(json.data, function (i, obj) {
                        cuerpo += '<tr><td>'  + obj + '</td></tr>';
                    });
                    cuerpo += '</tbody></table>';
                    $("#observados").html(cuerpo);
                    $("#pandelObservados").removeClass("hide");
                    $("#footerLoadAbonos").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                    notify(json.message, "error");
                } else {
                    notify(json.message, "info");
                    $("#pandelObservados").addClass("hide");

                    $("#observados").html("");
                    setTimeout(function () {
                        location.href = APP.url("oficinas/matricula/restriccionmatricula");
                    }, 1200);
                }
            } else {
                $("#btnLoad").html('Iniciar Carga');
                $('#mensajeRespuesta').text(json.message).addClass("alert-danger").removeClass("alert-success").removeClass("hide");
                $("#footerLoadAbonos").find("a").each(function (i, item) {
                    $(item).removeAttr("disabled");
                });
            }
        },
        error: function (error) {
            $('#mensajeRespuesta').text("Error de comunicacion con el servidor").addClass("alert-danger");
            $("#footerLoadAbonos").find("a").each(function (i, item) {
                $(item).removeAttr("disabled");
            });
        }
    });

    LoadDeuda = {
        initLoad: function () {
            if (!$('#formLoadFiles').parsley().validate()) {
                return;
            }

            $("#footerLoadAbonos").find("a").each(function (i, item) {
                $(item).attr("disabled", "disabled");
            });

            $('#mensajeRespuesta').addClass("hide");

            $("#btnLoad").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Cargando datos');
            $('#formLoadFiles').submit();
        },
        validarArchivo: function ($this) {
            var fileName = $this.val();
            if (!(fileName.indexOf("xls") > -1 || fileName.indexOf("xlsx") > -1)) {
                $('#mensajeRespuesta').text("TIPO DE ARCHIVO NO VALIDO").addClass("alert-warning").removeClass("alert-danger");
                $("#btnLoad").attr("disabled", "disabled");
            } else {
                $('#mensajeRespuesta').text("").removeClass("alert-warning");
                $("#btnLoad").removeAttr("disabled");
            }
            $('#formLoadFiles').parsley().destroy();
        }
    };

    $("body").delegate("#btnLoad", "click", function () {
        console.log("HOLA");
        LoadDeuda.initLoad();
    });

    $("body").delegate(".archivo", "change", function () {
        LoadDeuda.validarArchivo($(this));
    });

});