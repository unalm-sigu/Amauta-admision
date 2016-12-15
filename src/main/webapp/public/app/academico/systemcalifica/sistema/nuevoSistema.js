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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            $('#persona_id').select2({
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("general/perfil/searchPersona"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term};
                    },
                    results: function (info, page) {
                        return {results: info.data};
                    }
                },
                initSelection: function (element, callback) {
                    //callback({id: element.val(), nombre: $("#alumno_name").val()});
                },
                formatResult: function (info) {
                    return info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
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
                        NuevoSistema.calcularFormula();
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
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            NuevoSistema.calcularFormula();
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/save'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    location.href = APP.url('academico/systemcalifica/sistema');
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
        changeCantidadEval: function ($this) {
            alert($this.val());
            if ($.isNumeric($this.val())) {
                var i = $this.attr('rel');
                var elem = "evaluacionPlan[" + i + "].notaMinimaAnulable";
                alert(elem);
                $("[name='" + elem + "']").attr("disabled", true);
                $("[name='" + elem + "']").val(0);
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
                var pesoTotalNumber = parseInt(pesoTotal.val())
                var cantEvalsNumber = parseInt(cantEvals.val());

                if (anularNotMin.prop('checked')) {
                    cantEvalsNumber--;
                }
                if ((parseInt(pesoTotalNumber) % parseInt(cantEvalsNumber)) != 0) {
                    pesoEval.val("");
                    return;
                }

                var pesoEvalsNumber = parseInt(pesoTotalNumber) / parseInt(cantEvalsNumber);
                pesoEval.val(pesoEvalsNumber);
            }
        },
        calcularFormula: function () {
            var rowCount = $('#tblEvaluaciones tr').length - 1;
            var formula = "";
            for (i = 0; i < rowCount; i++) {
                var tipoEvaluacion = $("[name='evaluacionPlan[" + i + "].tipoEvaluacion.id']").val();
                var cantEvaluaciones = $("[name='evaluacionPlan[" + i + "].cantidadEvaluaciones']").val();
                var anularNotaMin = $("[name='evaluacionPlan[" + i + "].notaMinimaAnulable']").prop('checked');
                var pesoTotal = $("[name='evaluacionPlan[" + i + "].pesoTotal']").val();
                if (tipoEvaluacion == null || tipoEvaluacion == "") {
                    continue;
                }
                var tipoEvaluacionCode = evaluacionCnf[tipoEvaluacion].codigo;

                if (i > 0) {
                    formula += " + ";
                }
                formula += cantEvaluaciones;
                if (anularNotaMin) {
                    formula += "(1)";
                }
                formula += tipoEvaluacionCode + "(";
                formula += pesoTotal + ")";

            }
            $("#spnFormula").html(formula);
            $("#txtFormula").val(formula);
        },
        cambiarTipoEvaluacion: function ($this, e) {
            var tr = $this.closest("tr");
            var cantidadEval = tr.find("[name$='cantidadEvaluaciones']");
            var notaMinimaAnulable = tr.find("[name$='notaMinimaAnulable']");
            var tEval = $this.val();
            cantidadEval.removeAttr("data-parsley-max");
            if (tEval != null && tEval != "") {
                var tipoEvaluacion = evaluacionCnf[tEval];
                // cantidadMaxima   esNotaMinimaAnulable
                cantidadEval.attr("data-parsley-max", tipoEvaluacion.cantidadMaxima);
                if (tipoEvaluacion.esNotaMinimaAnulable == true || tipoEvaluacion.esNotaMinimaAnulable == "true") {
                    notaMinimaAnulable.removeAttr("disabled");
                } else {
                    notaMinimaAnulable.attr("disabled", true);
                    notaMinimaAnulable.prop("checked", false);
                }
            }
        }
    };

    NuevoSistema.init();

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

    $("body").change(function () {
        NuevoSistema.calcularFormula();
    });

});
