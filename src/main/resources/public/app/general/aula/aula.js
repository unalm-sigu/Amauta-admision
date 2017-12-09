$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/aula/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $('[data-toggle="tooltip"]').tooltip();
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {CRE: 'default', ACT: 'success', INA: 'danger'};
        record.index = rowIndex;

        record.activar = record.estado == 'ACT' || record.estado == 'CRE';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#aulaTemplate").render(record);
        return html;
    }

    var Aula = {
        form: null,
        init: function () {

        },
        viewCambioEstado: function (e, $this) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);

            var record = {
                form: 'formCambioEstado',
                id: rec.id,
                codigo: rec.codigo,
                seDesactiva: rec.estado == 'ACT',
                nuevoEstado: (rec.estado == 'ACT') ? 'INA' : 'ACT'
            };

            MODAL.init("md");
            if (record.seDesactiva) {
                MODAL.title("Desactivación de Ambiente");
                MODAL.buttons('<button type="button" class="btn btn-danger cambio-estado-aula">Desactivar Ambiente</button>');
            } else {
                MODAL.title("Activación de Ambiente");
                MODAL.buttons('<button type="button" class="btn btn-success cambio-estado-aula">Activar Ambiente</button>');
            }
            MODAL.body($.templates("#divEstadoAula").render(record));
            MODAL.show();
            Aula.form = $("#" + record.form);
        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Aula.form;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('general/aula/cambioEstado'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
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
    };

    Aula.init();

    $("body").delegate(".change-estado", "click", function (e) {
        console.log("dasdfasd")
        Aula.viewCambioEstado(e, $(this));
    });

    $("body").delegate(".cambio-estado-aula", "click", function (e) {
        Aula.cambioEstado(e);
    });

});