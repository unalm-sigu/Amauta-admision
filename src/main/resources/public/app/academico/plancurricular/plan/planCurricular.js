$(function () {

    PlanCurricular = {
        init: function () {
        }, verNuevoPlanCurricular: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/planCurricular/plan/nuevo");
        }
    }

    PlanCurricular.init();

    $("body").delegate(".nuevo-plan-curricular", "click", function (e) {
        PlanCurricular.verNuevoPlanCurricular(e);
    });




});