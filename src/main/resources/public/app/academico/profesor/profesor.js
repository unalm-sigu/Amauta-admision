$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/profesor/all'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter

        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $("#opopop").prepend($("#headDynatable"));
        $('#headDynatable').removeClass('hide');
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        record.index = rowIndex;
        var colorEstado = {ACT: "success", INA: "default"};
        var nameEstado = {ACT: "Activo", INA: "Inactivo"};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#profesorTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Docente = {
        init: function () {},
        body: $("body"),
        form: {},
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            Docente.form.id = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                size: 'md',
                message: '¿Seguro que desea cambiar el estado del docente?',
                buttons: {
                    confirm: {label: "Sí, aceptar", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Docente.saveEstado(mimodal);
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('academico/profesor/estado'),
                type: 'POST',
                async: true,
                data: Docente.form,
                success: function (response) {
                    if (response.success) {
                        dynatable.process();
                        notify(response.message, "success");
                        mimodal.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        loadDepartamento: function () {
            var dpto = $("#departamento").val();
            dynatable.queries.add("departamento", dpto);
            dynatable.process();
        }
    };

    Docente.body.delegate(".estado", "click", function (e) {
        Docente.estado(e);
    });

    Docente.body.delegate(".reporte", "click", function (e) {
        var dpto = $("#departamento").val();
        console.log(dpto);
        if (dpto == null || dpto == "") {
            notify("Seleccione el departamento");
            return;
        }
        $.fileDownload("/academico/profesor/reporteProgramacion", {
            httpMethod: "POST",
            data: {departamento: dpto},
            successCallback: function (responseHtml, url) {
//                    console.log('aqui');
            },
            onFail: function (e) {
                console.log(e);
            },
            failCallback: function (responseHtml, url) {
                notify(MESSAGES.errorComunicacion, 'error')
            }
        });
    });

    $("#departamento").change(function () {
        Docente.loadDepartamento();
    });

});