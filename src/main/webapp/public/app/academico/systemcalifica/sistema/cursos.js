$(function () {

    Cursos = {
       

    };

    $("body").delegate(".asignar-cursos", "click", function (e) {
        NuevoSistema.addTipoEvaluacion(e);
    });

});