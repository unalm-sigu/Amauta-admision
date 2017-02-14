$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/acta/list'),
            perPageDefault: 16
        },
        writers: {
            _rowWriter: ulWriter
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var colorPanel = {PEND: 'default', PARC: 'info', OK: 'success'};
        /*   record.colorPanel = colorPanel["OK"];
         record.colorLabel = colorPanel["OK"];*/

        var html = $.templates("#templateActas").render(record);
        return html;
    }

    $("body").delegate(".depart-academico", "click", function () {
        var id = $(this).attr("rel");
        location.href = APP.url("academico/acta/" + id + "/departamento");
    });

});
