$(function () {
    SistemaCalificacion = {
        verSistemaCurso: function ($this, e) {
            e.preventDefault();

            MODAL.init("lg");
            MODAL.title("Sistema de Calificación - SC-003");
            MODAL.show();
            MODAL.buttons(
                    '<a class="btn btn-success">Aceptar</a>' +
                    '<a class="btn btn-warning">Expandir</a>' +
                    '<a class="btn btn-danger">Solicita modificación</a>');
            $.ajax({
                url: APP.url('academico/docente/carga/detalleCargaAcademica'),
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
        verSistemaCursoModal2: function ($this, e) {
            e.preventDefault();

            MODAL.init("lg");
            MODAL.title("Sistema de Calificación - SC-003");
            MODAL.show();
           
            $.ajax({
                url: APP.url('academico/docente/carga/detalleCargaAcademica'),
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
    $("body").delegate(".sistema-curso", "click", function (e) {
        SistemaCalificacion.verSistemaCurso($(this),e);
    });
    $("body").delegate(".sistema-curso-m2", "click", function (e) {
        SistemaCalificacion.verSistemaCursoModal2($(this),e);
    });
});
