$(function () {

    var Departamento = {
        init: function () {
            $("[name='facultad.id']").select2({
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("academico/facultad/allFacultad"),
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
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("rev")});
                    }
                },
                formatResult: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                formatSelection: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        }
    };
    Departamento.init();
});