$(function () {


    var evaluacionCnf;

    NuevoSistema = {
        init: function () {
            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/tiposEvaluacion'),
                type: 'POST',
                async: true,
                data: {},
                success: function (response) {
                    if (response.success) {
                        evaluacionCnf = response.data;
                        $.each(evaluacionCnf, function (key, value) {
                            evaluacionCnf[key] = JSON.parse(value);
                        });
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        addTipoEvaluacion: function (e) {
            e.preventDefault();
            var record = {};

            var rowCount = $('#tblEvaluaciones tr').length;
            record.index = rowCount - 1;
            var html = $.templates("#templateNuevoSistemaCalificacion").render(record);

            var tbody = $("#tbodyEvaluaciones");
            tbody.append(html);

            $(".item-select2").select2();
            $(".item-select2").each(function () {
                $(this).removeClass("item-select2");
            });
        },
        changeCantidadEval: function ($this) {
            if ($.isNumeric($this.val())) {
                var i = $this.attr('rel');
                var elem = "evaluacionPlan[" + i + "].notaMinimaAnulable";
                $("[name='" + elem + "']").attr("disabled", true);
                //     $("[name='" + elem + "']").val(0);
                $("[name='" + elem + "']").attr("checked", false);
                if (parseInt($this.val()) > 1) {
                    $("[name='" + elem + "']").removeAttr("disabled");
                }

            }
        },
        calcularPesoEval: function (el) {
            var i = el.attr('rel');
            var pesoTotal = $("[name='evaluacionPlan[" + i + "].pesoTotal']");
            var cantEvals = $("[name='evaluacionPlan[" + i + "].cantidadEvaluaciones']");
            var anularNotMin = $("[name='evaluacionPlan[" + i + "].notaMinimaAnulable']");
            var pesoEval = $("[name='evaluacionPlan[" + i + "].pesoEvaluacion']");

            if ($.isNumeric(pesoTotal.val()) && $.isNumeric(cantEvals.val())) {
                var pesoTotalNumber = parseFloat(pesoTotal.val())
                var cantEvalsNumber = parseFloat(cantEvals.val());

                if (anularNotMin.prop('checked')) {
                    cantEvalsNumber--;
                }
                /*
                 if ((parseInt(pesoTotalNumber) % parseInt(cantEvalsNumber)) != 0) {
                 pesoEval.val("");
                 return;
                 }
                 */
                var pesoEvalsNumber = parseFloat(pesoTotalNumber) / parseFloat(cantEvalsNumber);
                pesoEval.val(pesoEvalsNumber.toFixed(2));
            }
        },
        calcularFormula: function () {
            var rowCount = $('#tblEvaluaciones tr').length - 1;
            var formula = "";
            for (i = 0; i < rowCount; i++) {
                var tipoEvaluacion = $("[name='evaluacionPlan[" + i + "].tipoEvaluacion.id']").val();
                //     var cantEvaluaciones = $("[name='evaluacionPlan[" + i + "].cantidadEvaluaciones']").val();
                var anularNotaMin = $("[name='evaluacionPlan[" + i + "].notaMinimaAnulable']").prop('checked');
                var pesoTotal = $("[name='evaluacionPlan[" + i + "].pesoTotal']").val();
                if (tipoEvaluacion == null || tipoEvaluacion == "") {
                    continue;
                }
                var tipoEvaluacionCode = evaluacionCnf[tipoEvaluacion].codigo;
                if (i > 0) {
                    formula += " + ";
                }
                //         formula += cantEvaluaciones;
                if (anularNotaMin) {
                    formula += "(1)";
                }
                formula += tipoEvaluacionCode + "(";
                formula += pesoTotal + ")";

            }
            $("#spnFormula").html(formula);
            $("#txtFormula").val(formula);
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
                        NuevoSistema.calcularFormula();
                    }
                }
            });
        }, saveSistema: function (e) {
            e.preventDefault();
            var form = $("[id='frmSistemaCalifica']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('docente/notasacademica/saveSistema'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    location.href = APP.url('docente/notasacademica');
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        },
        cambiarTipoEvaluacion: function ($this, e) {
            var formula = $("#txtFormula").val();

            var tr = $this.closest("tr");
            var cantidadEval = tr.find("[name$='cantidadEvaluaciones']");
            var notaMinimaAnulable = tr.find("[name$='notaMinimaAnulable']");
            var tEval = $this.val();
            cantidadEval.removeAttr("data-parsley-max");
            if (tEval != null && tEval != "") {
                var tipoEvaluacion = evaluacionCnf[tEval];


                var rowCount = $('#tblEvaluaciones tr').length - 1;
                var found = 0;
                for (i = 0; i < rowCount; i++) {
                    var tipoEvalEach = $("[name='evaluacionPlan[" + i + "].tipoEvaluacion.id']").val();
                    if (parseInt(tipoEvalEach) == parseInt(tEval)) {
                        found++;
                    }
                }
                if (found >= 2) {
                    bootbox.alert(
                            {
                                message: "Seleccione una evaluación distinta.",
                                size: 'small'
                            }
                    );
                    $this.select2("val", "CAC");
                    return false;
                }

                // cantidadMaxima   esNotaMinimaAnulable
                cantidadEval.attr("data-parsley-max", tipoEvaluacion.cantidadMaxima);
                if (tipoEvaluacion.esNotaMinimaAnulable == true || tipoEvaluacion.esNotaMinimaAnulable == "true") {
                    notaMinimaAnulable.removeAttr("disabled");
                } else {
                    notaMinimaAnulable.attr("disabled", true);
                    notaMinimaAnulable.prop("checked", false);
                }
            }
        },
        regresar: function (e) {
            e.preventDefault();
            location.href = APP.url("docente/notasacademica");
        }
    }

    NuevoSistema.init();

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        NuevoSistema.addTipoEvaluacion(e);
    });

    $("body").delegate(".delete-tipo-evaluacion", "click", function (e) {
        NuevoSistema.deleteTipoEvaluacion($(this), e);
    });

    $("body").delegate("#cmbSaveSistema", "click", function (e) {
        NuevoSistema.saveSistema(e);
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

    $("body").change(function () {
        NuevoSistema.calcularFormula();
    });

    $("body").delegate(".cbo-tipo-evaluacion", "change", function (e) {
        NuevoSistema.cambiarTipoEvaluacion($(this), e);
    });

    $("body").delegate(".cancelar", "click", function (e) {
        NuevoSistema.regresar(e);
    });
});