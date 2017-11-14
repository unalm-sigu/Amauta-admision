$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/horario/grupo/list'),
            perPageDefault: 12
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'div'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {ACT: 'success', INA: 'danger'};
        var labelName = {ACT: 'Activo', INA: 'Inactivo'};
        record.colorEstado = labelColor[record.estado];
        record.nameEstado = labelName[record.estado];
        var html = $.templates("#grupoTemplate").render(record);
        var outerHTML = $(html).prop('outerHTML');
        return outerHTML;
    }

    var Grupo = {
        form: null,
        body: $('body'),
        init: function () {
        },
    };

    Grupo.init();

});
