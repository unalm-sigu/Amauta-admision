$(function () {

    ActaDepartamento = {
        grupoInicio: $("#grupoInicio"),
        elegirGrupo: function (item, e) {
            var grupo = item.attr("rel");
            alert(grupo);

            if (ActaDepartamento.grupoInicio !== null) {
                ActaDepartamento.grupoInicio.removeClass("active");
            }
            item.addClass("active");
            ActaDepartamento.grupoInicio = item;
            /*
             AdmisionSede.loadInfoGrado(item.attr("rel"), sede);
             if (grado == "0") {
             dynatable.queries.remove("g.id");
             } else {
             dynatable.queries.add("g.id", grado);
             }
             dynatable.process();
             e.preventDefault();
             */
        }
    };

    $(".ver-grupo").click(function (e) {
        ActaDepartamento.elegirGrupo($(this), e);
    });
});