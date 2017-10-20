$(function () {
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/carrera/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {ACT: 'success', INA: 'danger'};
        record.index = rowIndex;
        record.esActivo = record.estado == 'ACT';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#carreraTemplate").render(record);
        return html;
    }

    var Carrera = {
        init: function () {

        },
        modalCarrera: $("#modalCambioEstado"),
        formDesactivarCarrera: $("#formCambioEstado"),
        viewModal: function (e, $this) {
            e.preventDefault();
            Carrera.formDesactivarCarrera.parsley().destroy();

            Carrera.modalCarrera.modal("show");
            $('[name="motivo"]').val("");
            $('[name="id"]').val($this.attr("rel"));
        },
        desactivar: function (e) {
            e.preventDefault();
            var form = Carrera.formDesactivarCarrera;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/desactivar'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        Carrera.modalCarrera.modal("hide");
                        notify(response.message, "info");
                        dynatable.process();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }

    Carrera.init();

    $("body").delegate(".desactivar", "click", function (e) {
        Carrera.viewModal(e, $(this));
    });
    $("body").delegate(".desactivar-carrera", "click", function (e) {
        Carrera.desactivar(e);
    });

});