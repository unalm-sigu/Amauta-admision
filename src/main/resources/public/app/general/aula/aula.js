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
        inti: function () {

        },
        viewCambioEstado: function (e, $this) {
            e.preventDefault();
            var estado = $this.attr('rev');

            var record = {
                form: 'formCambioEstado',
                id: $this.attr('rel'),
                seDesactiva: estado == 'ACT'
            }

            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#divEstadoAula").render(record));
            MODAL.buttons('<button type="button" class="btn btn-primary cambio-estado-aula">Aceptar</button>');
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
    }

    Aula.init();
    $("body").delegate(".change-estado", "click", function (e) {
        Aula.viewCambioEstado(e, $(this));
    });
    $("body").delegate(".cambio-estado-aula", "click", function (e) {
        Aula.cambioEstado(e);
    });

});