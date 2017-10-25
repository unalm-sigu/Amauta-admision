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
        modalCarrera: $("#modalCambioEstado"),
        formCambioEstadoCarrera: $("#formCambioEstado"),
        viewModal: function (e, $this) {
            e.preventDefault();
            Carrera.formCambioEstadoCarrera.parsley().destroy();
            var modal = Carrera.modalCarrera;

            modal.modal("show");
            modal.find('[name="motivo"]').val("");
            modal.find('[name="id"]').val($this.attr("rel"));
            var estado = $this.attr("rev");
            estado == 'INA' ? $(".tituloCambioEstado").text("Activar Carrera") : $(".tituloCambioEstado").text("¿Por qué motivo desea desactivar la carrera?");
            estado == 'INA' ?
                    $(".campoMotivo").html('¿Desea realmente activar la carrera?') :
                    $(".campoMotivo").html("<textarea class='form-control' name='motivoAnulacion' required='true'></textarea>");
        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Carrera.formCambioEstadoCarrera;
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

    $("body").delegate(".change-estado", "click", function (e) {
        Carrera.viewModal(e, $(this));
    });
    $("body").delegate(".cambio-estado-carrera", "click", function (e) {
        Carrera.cambioEstado(e);
    });

});