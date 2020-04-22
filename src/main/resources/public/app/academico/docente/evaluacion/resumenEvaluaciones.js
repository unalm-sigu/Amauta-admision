$(function () {
    Evaluaciones = {
        otraPantalla: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/docente/evaluacion/resumenEvaluacion2");
        },
        verEstadisticaEvaluaciones: function ($this, e) {
            e.preventDefault();

            MODAL.init("lg");
            MODAL.title("Estadistica de evaluaciones");
            MODAL.show();
            /*
             MODAL.buttons(
             '<a class="btn btn-success">Aprobar</a>' +
             '<a class="btn btn-warning">Observar</a>' +
             '<a class="btn btn-danger">Rechazar</a>');
             */
            $.ajax({
                url: APP.url('academico/docente/evaluacion/detalleEstadisticaEvaluacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verResumenEvaluaciones: function ($this, e) {
            e.preventDefault();

            MODAL.init("lg");
            MODAL.title("Resumen de evaluaciones");
            MODAL.show();
            /*
             MODAL.buttons(
             '<a class="btn btn-success">Aprobar</a>' +
             '<a class="btn btn-warning">Observar</a>' +
             '<a class="btn btn-danger">Rechazar</a>');
             */
            $.ajax({
                url: APP.url('academico/docente/evaluacion/detalleResumenEvaluacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
    };

    $("body").delegate(".estadistica-evaluaciones", "click", function (e) {
        Evaluaciones.verEstadisticaEvaluaciones($(this), e);
    });
    $("body").delegate(".resumen-evaluaciones", "click", function (e) {
        Evaluaciones.verResumenEvaluaciones($(this), e);
    });
    $("body").delegate(".otra-pantalla", "click", function (e) {
        Evaluaciones.otraPantalla(e);
    });
});
