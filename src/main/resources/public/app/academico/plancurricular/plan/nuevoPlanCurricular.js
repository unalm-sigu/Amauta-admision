

$(function () {

    NuevoPlanCurricular = {
        pestañaCicloElegida: null,
        init: function () {
            //$('#dateFechaVig').datepicker("remove");
            // $('#inpDateFechaVig').datepicker({startDate: "today"});
            $('#inpDateFechaVig').datepicker();
            //  $('#dateFechaVig').datepicker('update');
        }, verPestañaCiclo($this, e) {
            var pestaña = $this.attr("rel");
            if (NuevoPlanCurricular.pestañaCicloElegida !== null) {
                NuevoPlanCurricular.pestañaCicloElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevoPlanCurricular.pestañaCicloElegida = $this;
            $("#spnCiclo").html("Ciclo " + pestaña);
        }
    }
    NuevoPlanCurricular.init();

    $("body").delegate(".ver-tab-ciclo", "click", function (e) {
        NuevoPlanCurricular.verPestañaCiclo($(this), e);
    });

});