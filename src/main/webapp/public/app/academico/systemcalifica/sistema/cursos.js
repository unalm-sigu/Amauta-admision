$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/systemcalifica/sistema/listCursos'),
            perPageDefault: 10,
            ajaxData: {planCalificacion: $('[name="plancalificacion.id"]').val()}
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        /*  var colorEstado = {APR: "success", CER: "danger", DES: "danger", CRE: "default"};
         record.colorEstado = colorEstado[record.estado];*/
        record.index = rowIndex;

        var html = $.templates("#templateCursos").render(record);
        return html;
    }

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
            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/incluirCurso'),
                type: 'POST',
                async: true,
                data: {curso: Cursos.itemElegido.id, planCalificacion: $('[name="plancalificacion.id"]').val()},
                success: function (response) {

                    if (response.success) {
                        notify(response.message, "info");

                        MODAL.hide();
                        dynatable.process();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.hide();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        desasignarCurso: function ($this, e) {
            e.preventDefault();

            bootbox.confirm({
                message: "¿Está seguro que desea desasignar este curso?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {
                        var id = $this.attr("rel");
                        MODAL.showWait("Espere un momento por favor");

                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/desasignarCurso'),
                            type: 'POST',
                            async: true,
                            data: {curso: id, planCalificacion: $('[name="plancalificacion.id"]').val()},
                            success: function (response) {
                                MODAL.hideWait();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    };

    $("#btnAddCurso").click(function (e) {
        Cursos.verBuscarCurso(e);
    });

    $("body").delegate("#btnIncluirCurso", "click", function (e) {
        Cursos.addCurso(e);
    });

    $("body").delegate(".desasignar-curso", "click", function (e) {
        Cursos.desasignarCurso($(this), e);
    });

});