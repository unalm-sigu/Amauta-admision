$(function () {

    Cursos = {
        itemElegido: null,
        buscarCurso: function ($this) {
            $this.select2({
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/cursosSCA"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarCurso").render(info);
                },
                formatSelection: function (info) {
                    return info.codigo + " - " + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            }).on('select2-selecting', function (e) {
                Cursos.itemElegido = e.object;
            });
        },
        verBuscarCurso: function (e) {
            e.preventDefault();
            var info = {};
            MODAL.init("md");
            MODAL.title("Buscar curso");
            MODAL.body($.templates("#divAddCurso").render(info));
            MODAL.buttons('<a href="#" class="btn btn-primary" id="btnIncluirCurso">Agregar curso</a>');
            MODAL.show();

            var input = MODAL.body().find("input");
            Cursos.buscarCurso(input);
        },
        addCurso: function (e) {
            var d = new Date();
            var hoy = d.getDate() + "/" + (d.getMonth() + 1) + "/" + d.getFullYear();
            Cursos.itemElegido.fechaInclusion = hoy;
            var tbody = $("#tbodyCursos");
            tbody.append($.templates("#templateCursos").render(Cursos.itemElegido));
            MODAL.hide();
        }
    };

    $("#btnAddCurso").click(function (e) {
        Cursos.verBuscarCurso(e);
    });

    $("body").delegate("#btnIncluirCurso", "click", function (e) {
        Cursos.addCurso(e);
    });

});