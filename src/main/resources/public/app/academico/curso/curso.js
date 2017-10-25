$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/curso/list'),
            perPageDefault: 5
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
        var html = $.templates("#cursoTemplate").render(record);
        return html;
    }


    var Curso = {
        formCambioEstadoCurso: $("#formCambioEstado"),
        modalCurso: $("#modalCambioEstado"),
        init: function () {

        },
        viewModal: function (e, $this) {
            e.preventDefault();
            Curso.formCambioEstadoCurso.parsley().destroy();
            var modal = Curso.modalCurso;

            modal.modal("show");
            modal.find('[name="motivo"]').val("");
            modal.find('[name="id"]').val($this.attr("rel"));
            var estado = $this.attr("rev");
            estado == 'INA' ? $(".tituloCambioEstado").text("Activar Curso") : $(".tituloCambioEstado").text("¿Por qué motivo desea cambiar el estado?");
            estado == 'INA' ?
                    $(".campoMotivo").html('¿Desea activar el estado del curso?') :
                    $(".campoMotivo").html("<textarea class='form-control' name='motivoAnulacion' required='true'></textarea>");
        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Curso.formCambioEstadoCurso;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/curso/cambiarEstadoCurso'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        Curso.modalCurso.modal("hide");
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

    Curso.init();

    $("body").delegate(".change-estado", "click", function (e) {
        Curso.viewModal(e, $(this));
    });
    $("body").delegate(".cambio-estado-curso", "click", function (e) {
        Curso.cambioEstado(e);
    });

});