$(function () {
    SistemaCalificacion = {
        verSistemaCurso: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/docente/cargaAcademica/sistemaCurso");
        },
    };

    $("body").delegate(".sistema-curso", "click", function (e) {
        SistemaCalificacion.verSistemaCurso(e);
    });
});
