$(function () {
    var seccion = $("#seccion").val();
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/docente/alumnosDocente/' + seccion + '/list'),
            perPageDefault: 1000
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        record.index = rowIndex;
        record.nro = rowIndex + 1;
        var html = $.templates("#templateAlumnosDocente").render(record);
        return html;
    }

    AlumnosDocente = {
    };

    $("body").delegate(".alguna-clase", "click", function (e) {
    });


});
