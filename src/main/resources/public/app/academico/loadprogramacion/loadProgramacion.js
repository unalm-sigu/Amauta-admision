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
            $("#btnLoadHorario").html('Registrar Horario');
            $("#btnCancel").removeAttr("disabled");
            $("#btnLoadHorario").removeAttr("disabled");

            var json = response.responseJSON;
            var sede = $("#sede").val();
            if (json.success) {
                notify('Carga Finalizada', 'info');

            } else {
                $("#divForm").hide();
                $("#divLogCarga").show();
                LoadHorario.log = json.data;
                $("#titleErrores").text(json.data.length + " errores de carga de horarios");
                $.each(json.data, function (index, value) {
                    var conte = '<tr>';
                    conte += '<td class="col-md-2"><span class="label label-' + (value.tipo == "Error" ? "danger" : "warning") + '">' + value.tipo + '</span></td>';
                    conte += '<td class="col-md-1">' + value.fila + '</td>';
                    conte += '<td class="col-md-2">' + value.nivel + '</td>';
                    conte += '<td class="col-md-7">' + value.mensaje + '</td>';
                    conte += '</tr>';
                    $('#tablaLogCarga tbody').append(conte);
                });

                notify(json.message, 'error');
            }
        },
        error: function (error) {
            notify(MESSAGES.errorComunicacion, 'error');
        }
    });

    LoadHorario = {
        log: null,
        validarFiles: function () {
            var ok = $("#formLoadFiles").parsley().isValid();
            if (!ok) {
                notify("Debe seleccionar todos los archivos", "error");
                return;
            }

            $("#btnLoadHorario").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Procesando');
            $("#btnCancel").attr("disabled", "disabled");
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
            var arrData = JSONData; //typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;
            var CSV = '';

            CSV += titulo + '\r\n\n';
            if (showTitulos) {
                var row = "";
                for (var index in arrData[0]) {
                    row += LoadHorario.capitalize(index) + ',';
                }

                row = row.slice(0, -1);
                CSV += row + '\r\n';
            }

            for (var i = 0; i < arrData.length; i++) {
                var row = "";

                for (var index in arrData[i]) {
                    row += '"' + arrData[i][index] + '",';
                }

                row.slice(0, row.length - 1);
                CSV += row + '\r\n';
            }

            if (CSV == '') {
                alert("Invalid data");
                return;
            }

            var fileName = "Errores_";
            fileName += titulo.replace(/ /g, "_");

            var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);
            var link = document.createElement("a");
            link.href = uri;

            link.style = "visibility:hidden";
            link.download = fileName + ".csv";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        },
        capitalize: function (str) {
            str = str.toLowerCase().replace(/\b[a-z]/g, function (letter) {
                return letter.toUpperCase();
            });
            return str;
        }
    };

    $("#btnLoadHorario").click(function () {
        LoadHorario.validarFiles();
    });

    $(".activar-envio").click(function () {
        LoadHorario.activarEnvioFiles();
    });
    $(".exportar-errores").click(function () {
        var nom = $("#nombreSede").val();
        LoadHorario.exportarJson2Csv(LoadHorario.log, nom, true);
    });


});