$(function () {

    var AnexoForm = {
        init: function () {
            AnexoForm.loadDepartamentos();
            AnexoForm.loadCarreras();
            $('[name="anexoSuperior.id"]').select2({allowClear: true, placeholder: "Seleccione un anexo"});
            $(".numero").numeric({negative: false});

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
                    var data = '<span class="block text-success bold">' + info.nombre + '</span>';
                    data += '<span class="block"><strong>Facultad:</strong> ' + info.facultad + '</span>';
                    return data;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        loadCarreras: function () {
            $("[name='carrera.id']").select2({
                allowClear: true,
                placeholder: "Seleccione una carrera",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/anexo/allCarreras"),
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
                    var data = '<span class="block text-success bold">' + info.nombre + ' - ' + info.codigo + '</span>';
                    data += '<span class="block"> ' + (info.tipoEstudio != '' ? (info.tipoEstudio + ' - ') : '') + info.modalidadEstudio + '</span>';
                    return data;
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
            var form = $("#formularioAula");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        }
    }

    AnexoForm.init();
    $("body").delegate(".save-update-anexo", "click", function (e) {
        AnexoForm.saveUpdate(e);
    });
    $("body").delegate('[name="anexoSuperior.id"]', "change", function () {
        $(this).parsley().destroy();
    });
});