$(function () {
    NotasAcademicas = {
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
});
