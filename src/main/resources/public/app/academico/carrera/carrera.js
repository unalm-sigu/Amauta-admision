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
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $('[data-toggle="tooltip"]').tooltip();
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {ACT: 'success', INA: 'danger'};
        record.index = rowIndex;
        record.esActivo = record.estado == 'ACT';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#carreraTemplate").render(record);
        return html;
    }

    var Carrera = {
        init: function () {

        },
        modalCarrera: $("#modalEstadoCarrera"),
        form: null,
        viewModal: function (e, $this) {
            e.preventDefault();

            var estado = $this.attr("rev");

            var record = {
                form: "formEstadoCarrera",
                activo: estado == 'ACT',
                id: $this.attr("rel")
            };

            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#divEstadoCarrera").render(record));
            MODAL.buttons('<button type="button" class="btn btn-primary cambio-estado-carrera">Aceptar</button>');
            MODAL.show();
            Carrera.form = $("#" + record.form);

        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Carrera.form;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/cambiarEstadoCarrera'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
//                        Carrera.modalCarrera.modal("hide");
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

    Carrera.init();

    $("body").delegate(".change-estado", "click", function (e) {
        Carrera.viewModal(e, $(this));
    });
    $("body").delegate(".cambio-estado-carrera", "click", function (e) {
        Carrera.cambioEstado(e);
    });

});