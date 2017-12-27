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
        record.editar = (record.estado == 'ACT' || record.estado == 'CRE');
        record.index = rowIndex;
        var html = $.templates("#templatePlanCurricular").render(record);
        return html;
    }

    PlanCurricular = {
        init: function () {
        },
        verNuevoPlan: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/planCurricular/nuevo");
        },
        editarPlan: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            location.href = APP.url("academico/planCurricular/" + rec.id + "/editarPlanCurricular");
        },
        deletePlan: function ($this, e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar este plan?",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            data: {id: rec.id},
                            url: APP.url('academico/planCurricular/eliminarPlan'),
                            success: function (response) {
                                if (response.success) {
                                    dynatable.process();
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        desactivarPlan: function ($this, e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);

            bootbox.confirm({
                message: "¿Está seguro que desea desactivar este plan?",
                buttons: {
                    confirm: {label: "Si, desactivar", className: "btn-warning"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            data: {id: rec.id},
                            url: APP.url('academico/planCurricular/desactivarPlaan'),
                            success: function (response) {
                                if (response.success) {
                                    dynatable.process();
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        clonarPlan: function ($this, e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);

            bootbox.confirm({
                message: "¿Está seguro que desea clonar este plan?",
                buttons: {
                    confirm: {label: "Si, clonar", className: "btn-primary"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            data: {id: rec.id},
                            url: APP.url('academico/planCurricular/clonarPlan'),
                            success: function (response) {
                                if (response.success) {
                                    location.href = APP.url("academico/planCurricular/" + response.data + "/editarPlanCurricular");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    };

    PlanCurricular.init();

    $("body").delegate(".nuevo-plan-curricular", "click", function (e) {
        PlanCurricular.verNuevoPlan(e);
    });

    $("body").delegate(".editar-plan", "click", function (e) {
        PlanCurricular.editarPlan($(this), e);
    });

    $("body").delegate(".delete-plan", "click", function (e) {
        PlanCurricular.deletePlan($(this), e);
    });

    $("body").delegate(".desactivar-plan", "click", function (e) {
        PlanCurricular.desactivarPlan($(this), e);
    });

    $("body").delegate(".clonar-plan", "click", function (e) {
        PlanCurricular.clonarPlan($(this), e);
    });

});