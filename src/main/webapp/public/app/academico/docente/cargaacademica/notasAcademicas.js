$(function () {
    
    NotasAcademicas = {
        cambioNA: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Cambio de nota");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="cmbGuardar">Guardar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleCambioNota'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verDetalleReporte: function ($this, e) {
            e.preventDefault();

            MODAL.init("sm");
            MODAL.title("Reporte de Notas");
            MODAL.show();
            /*
             MODAL.buttons(
             '<a class="btn btn-success">Aprobar</a>' +
             '<a class="btn btn-warning">Observar</a>' +
             '<a class="btn btn-danger">Rechazar</a>');
             */
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleNotasAcademicas'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    };
    $('.nota-alumno').keyup(function (event) {
        var keyCode = (event.keyCode ? event.keyCode : event.which);
        if (keyCode == 13) {
            var index = $('.nota-alumno').index(this) + 1;
            $('.nota-alumno').eq(index).focus();
        }
    });
    $('.activar-evaluacion').click(function (event) {
        var record = {};
        MODAL.init("md");
        MODAL.title("Activación de evaluación");
        MODAL.buttons('<a class="btn btn-primary" id="btnActivarEvaluacion">Activar</a>');
        MODAL.body($.templates("#divActivarEvaluacion").render(record));
        MODAL.show();
    });
    $("body").delegate("#btnActivarEvaluacion", "click", function (e) {
        MODAL.hide();
        var evaluacion = 23;
        location.href = APP.url("academico/docente/cargaacademica/") + evaluacion + "/evaluacion";
    });
    $("body").delegate(".solicitar-cambio-nota", "click", function (e) {
        NotasAcademicas.cambioNA($(this), e);
    });
    $("body").delegate(".detalle-reporte", "click", function (e) {
        NotasAcademicas.verDetalleReporte($(this), e);
    });
});
