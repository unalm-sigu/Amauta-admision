$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templatePlanCurricular").render(record);
        return html;
    }

    PlanCurricular = {
        init: function () {
        }, verNuevoPlanCurricular: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/planCurricular/nuevo");
        }, editarPlanCurricular: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            location.href = APP.url("academico/planCurricular/" + rec.id + "/editarPlanCurricular");
        }
    };

    PlanCurricular.init();

    $("body").delegate(".nuevo-plan-curricular", "click", function (e) {
        PlanCurricular.verNuevoPlanCurricular(e);
    });

    $("body").delegate(".editar-plan-curricular", "click", function (e) {
        PlanCurricular.editarPlanCurricular($(this), e);
    });

});