$(function () {

    var CursoForm = {
        init: function () {
            $(".numerico").numeric({negatice: false});
            $('[name="tipoCurso"]').select2({placeholder: "Seleccione el tipo curso"});
            CursoForm.loadCoordinadores();
            CursoForm.loadDepartamentos();

        },
        loadDepartamentos: function () {
            $("[name='departamentoAcademico.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un departamento",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("comun/buscar/allDepartamentoAcademico"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        loadCoordinadores: function () {
            $("[name='coordinador.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un coordinador",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("comun/buscar/allCoordinadores"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        saveUpdate: function () {
            var form = $("#formularioCurso");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();

        }
    };

    CursoForm.init();
    $("body").delegate(".save-update-curso", "click", function (e) {
        CursoForm.saveUpdate(e);
    });

    $("body").delegate("[name='coordinador.id']", "change", function () {
        $(this).parsley().destroy();
    });

    $("body").delegate("[name='departamentoAcademico.id']", "change", function () {
        $(this).parsley().destroy();
    });

    $("body").delegate("[name='tipoCurso']", "change", function () {
        $(this).parsley().destroy();
    });

});