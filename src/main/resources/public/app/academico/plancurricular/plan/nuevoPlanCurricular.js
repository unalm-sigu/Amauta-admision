

$(function () {

    NuevoPlanCurricular = {
        pestañaCicloCurOblElegida: null,
        pestañaCicloCurElecElegida: null,
        init: function () {
            //$('#dateFechaVig').datepicker("remove");
            // $('#inpDateFechaVig').datepicker({startDate: "today"});
            $('#inpDateFechaVig').datepicker();
            //  $('#dateFechaVig').datepicker('update');
        }, verPestañaCicloCurObl($this, e) {
            var pestaña = $this.attr("rel");
            if (NuevoPlanCurricular.pestañaCicloCurOblElegida !== null) {
                NuevoPlanCurricular.pestañaCicloCurOblElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevoPlanCurricular.pestañaCicloCurOblElegida = $this;
            $("#spnCicloObl").html("Ciclo " + pestaña);
        }, verPestañaCicloCurElec($this, e) {
            var pestaña = $this.attr("rel");
            if (NuevoPlanCurricular.pestañaCicloCurElecElegida !== null) {
                NuevoPlanCurricular.pestañaCicloCurElecElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevoPlanCurricular.pestañaCicloCurElecElegida = $this;
            $("#spnCicloElec").html("Ciclo " + pestaña);
        }, agregarCursoObl($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Curso : ");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurObl">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + 1 + '/agregarCursoOblgPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, agregarCursoElec($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Curso : ");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurElec">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + 1 + '/agregarCursoElecPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, cambiarComboCarrera($this, e) {
            var carr = $("#cboCarrera").val();


            if (!isNaN(carr)) {
                if (carr > 0) {
                    alert("cambio");
                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/' + carr + '/orientacionCarrera'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            alert(response);
                            $("#cboOrientacion").html(response);
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            }
        }
    }
    NuevoPlanCurricular.init();

    $("body").delegate(".ver-tab-ciclo-cur-obl", "click", function (e) {
        NuevoPlanCurricular.verPestañaCicloCurObl($(this), e);
    });

    $("body").delegate(".ver-tab-ciclo-cur-elec", "click", function (e) {
        NuevoPlanCurricular.verPestañaCicloCurElec($(this), e);
    });

    $("body").delegate(".agregar-curso-obl", "click", function (e) {
        NuevoPlanCurricular.agregarCursoObl($(this), e);
    });

    $("body").delegate(".agregar-curso-elec", "click", function (e) {
        NuevoPlanCurricular.agregarCursoElec($(this), e);
    });

    $("body").delegate("#cboCarrera", "change", function (e) {
        NuevoPlanCurricular.cambiarComboCarrera($(this), e);
    });

});