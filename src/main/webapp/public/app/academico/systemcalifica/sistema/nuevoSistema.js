$(function () {
    NuevoSistema = {
        addTipoEvaluacion: function (e) {
            e.preventDefault();
            var record = {};

            var rowCount = $('#tblEvaluaciones tr').length;
            record.index = rowCount - 1;
            var html = $.templates("#templateNuevoSistema").render(record);

            var tbody = $("#tbodyEvaluaciones");
            tbody.append(html);

            $(".item-select2").select2();
            $(".item-select2").each(function () {
                $(this).removeClass("item-select2");
            });
        },
        deleteTipoEvaluacion: function ($this, e) {
            e.preventDefault();

            var tr = $this.closest("tr");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar este registro?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {
                        tr.remove();
                    }
                }
            });
        },
        regresar: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/systemcalifica/sistema");
        },
        saveSistema: function () {
            var form = $("[id='frmSistemaCalifica']");
            form.submit();
            /*   form.parsley().destroy();
             form.parsley();
             if (!form.parsley().validate()) {
             return;
             }*/
            /*
             $.ajax({
             url: APP.url('academico/systemcalifica/sistema/save'),
             type: 'POST',
             async: true,
             data: form.serialize(),
             success: function (response) {
             if (response.success) {
             MODAL.hide();
             notify(response.message, "info");
             
             } else {
             notify(response.message, "error");
             }
             },
             error: function () {
             notify(MESSAGES.errorComunicacion, "error");
             }
             });
             */
        },
        changeCantidadEval: function (el) {
            if ($.isNumeric(el.val())) {
                var i = el.attr('rel');
                var elem = "evaluacionPlan[" + i + "].anulaNotaMinima";
                $("[name='" + elem + "']").attr("disabled", true);
                $("[name='" + elem + "']").val(0);
                $("[name='" + elem + "']").attr("checked", false);
                if (parseInt(el.val()) > 1) {
                    $("[name='" + elem + "']").removeAttr("disabled");
                }

            }
        },
        calcularPesoEval: function (el) {
            var i = el.attr('rel');
            var pesoTotal = $("[name='evaluacionPlan[" + i + "].pesoTotal']");
            var cantEvals = $("[name='evaluacionPlan[" + i + "].cantidadEvaluaciones']");
            var anularNotMin = $("[name='evaluacionPlan[" + i + "].esNotaMinimaAnulable']");
            var pesoEval = $("[name='evaluacionPlan[" + i + "].pesoEvaluacion']");

            if ($.isNumeric(pesoTotal.val()) && $.isNumeric(cantEvals.val())) {
                var pesoTotalNumber = parseInt(pesoTotal.val())
                var cantEvalsNumber = parseInt(cantEvals.val());

                if (anularNotMin.prop('checked')) {
                    cantEvalsNumber--;
                }
                var pesoEvalsNumber = parseInt(pesoTotalNumber) / parseInt(cantEvalsNumber);
                pesoEval.val(pesoEvalsNumber);
            }
        }
    };

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        NuevoSistema.addTipoEvaluacion(e);
    });

    $("body").delegate(".delete-tipo-evaluacion", "click", function (e) {
        NuevoSistema.deleteTipoEvaluacion($(this), e);
    });

    $("body").delegate(".cancelar", "click", function (e) {
        NuevoSistema.regresar(e);
    });

    $("body").delegate("#cmbSaveSistema", "click", function (e) {
        NuevoSistema.saveSistema();
    });

    $("body").delegate(".clsCantEvaluaciones", "keyup", function (e) {
        NuevoSistema.changeCantidadEval($(this));
        NuevoSistema.calcularPesoEval($(this));
    });

    $("body").delegate(".calcular-peso-eva", "keyup", function (e) {
        NuevoSistema.calcularPesoEval($(this));
    });

    $("body").delegate(".calcular-peso-eva-chk", "change", function (e) {
        NuevoSistema.calcularPesoEval($(this));
    });

});
