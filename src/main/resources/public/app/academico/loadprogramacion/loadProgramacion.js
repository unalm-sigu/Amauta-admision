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
                notify('Carga Inicializada', 'info');
                LoadHorario.stop = false;
                LoadHorario.revisarLog();
            } else {
                $("#btnLoadHorario").removeAttr("disabled");
                $("#btnLoadHorario").html('Cargar Horarios');
                $("#btnStopLoadHorario").attr("disabled");
                notify(json.message, 'error');
            }
        },
        error: function (error) {
            notify(Messages.errorComunicacion, 'error');
        }
    });

    LoadHorario = {
        log: null,
        stop: false,
        cargarArchivos: function () {
            var ok = $("#formLoadFiles").parsley().isValid();
            if (!ok) {
                notify("Debe seleccionar todos los archivos", "error");
                return;
            }

            $("#btnLoadHorario").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            $("#btnStopLoadHorario").removeAttr("disabled");
            $("#btnLoadHorario").attr("disabled", "disabled");
            $('#formLoadFiles').submit();
        },
        activarEnvioFiles: function () {
            $("#divForm").show();
            $("#divLogCarga").hide();
            $("#formLoadFiles")[0].reset();
            $('#tablaLogCarga tbody').html("");
            $('#progress .progress-bar').css('width', 0 + '%');
        },
        exportarJson2Csv: function (JSONData, titulo, showTitulos) {
        },
        capitalize: function (str) {
            str = str.toLowerCase().replace(/\b[a-z]/g, function (letter) {
                return letter.toUpperCase();
            });
            return str;
        },
        revisarLog: function () {
            $.ajax({
                url: APP.url('academico/loadprogramacion/logVisor'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        LoadHorario.crearLog(response.data);
                    } else {
                        notify(response.message, "error");
                    }

                    if (LoadHorario.stop) {
                        $("#btnLoadHorario").removeAttr("disabled");
                        $("#btnLoadHorario").html('Cargar Horarios');
                        $("#btnStopLoadHorario").attr("disabled");
                        return;
                    }

                    setTimeout(function () {
                        LoadHorario.revisarLog();
                    }, 1000);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        crearLog(data) {
            $("#divLog").removeClass("hide");
            var body = $("#bodyLog");
            var html = "";
            $.each(data, function (k, v) {
                html += '<tr><td>' + (k + 1) + '</td><td>' + v.info + '</td></tr>';
                if (v.tipo == "error" || v.tipo == "fin") {
                    LoadHorario.stop = true;
                }
            });
            body.html(html);
        },
        inicio() {
            var visor = $("#visor");
            console.log(visor.val())
            if (visor.val() == "true") {
                $("#btnLoadHorario").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
                $("#btnLoadHorario").attr("disabled", "disabled");
                $("#btnStopLoadHorario").removeAttr("disabled");
                LoadHorario.revisarLog();

            } else {
                $("#btnStopLoadHorario").attr("disabled", "disabled");
                $("#btnLoadHorario").removeAttr("disabled");
                $("#btnLoadHorario").html('Cargar Horarios');
            }
        },
        detenerCarga() {
            $.ajax({
                url: APP.url('academico/loadprogramacion/detenerCarga'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    };

    $("#btnLoadHorario").click(function () {
        LoadHorario.cargarArchivos();
    });

    $("#btnStopLoadHorario").click(function () {
        LoadHorario.detenerCarga();
    });

    $(".activar-envio").click(function () {
        LoadHorario.activarEnvioFiles();
    });

    $(".exportar-errores").click(function () {
        var nom = $("#nombreSede").val();
        LoadHorario.exportarJson2Csv(LoadHorario.log, nom, true);
    });

    LoadHorario.inicio();


});