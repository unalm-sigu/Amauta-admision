$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/docente/cargaacademica/list'),
            perPageDefault: 100
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {ACT: "success", CER: "danger", CRE: "default"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var secciones = record.secciones.split(",");

        var grupoHoras = "";
        if (record.grupoHoras != "") {
            grupoHoras = record.grupoHoras.toString().split(",");
        }
        var seccionesResult = "";

        for (var i = 0; i < secciones.length; i++) {
            seccionesResult += '<div class="m-l-md inline"><a href="#" ';
            if (record.estado == 'ACEP') {
                seccionesResult += 'class="notas-academicas"';
            } else {
                seccionesResult += 'class="ver-alumnos"';
            }

            var grupoText = "";
            if (grupoHoras[i] != null) {
                grupoText = grupoHoras != "" ? (' - ' + grupoHoras[i].split("|")[1]) : "";
            }

            seccionesResult += ' rel="' + secciones[i].split("|")[0] + '">' + secciones[i].split("|")[1] + grupoText + '</a></div>';

        }
        record.secciones = seccionesResult;
        var html = $.templates("#templateCargaAcademica").render(record);
        return html;
    }

    CargaAcademica = {
        aceptarSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Sistema de Calificación " + rec.sistemaCalificacion);
            MODAL.show();
            if (rec.estado == 'RHZ') {
                MODAL.buttons('<a class="btn btn-danger" id="cmbRechazar">Aceptar rechazo</a>');
            } else {
                MODAL.buttons(
                        '<a class="btn btn-success" id="cmbAceptar">Aceptar</a>' +
                        '<a class="btn btn-danger new-sis-calificacion pull-left">Solicita modificación</a>');

            }
            /*             * 
             +
             '<a class="btn btn-danger new-sis-calificacion pull-left">Solicita modificación</a>'
             */
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + rec.idSistemaCalificacion + "/" + rec.id + '/aceptarSistemaCalificacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación - " + rec.sistemaCalificacion);
            MODAL.show();
            if (rec.estado == 'RHZ') {
                MODAL.buttons('<a class="btn btn-danger" id="cmbRechazar">Aceptar rechazo</a>');
            }
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + rec.idSistemaCalificacion + "/" + rec.id + '/detalleSistemaCalificacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        confirmaSistemaCalificacion: function ($this, e) {
            var form = $("[id='frmAceptarSistCal']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea aceptar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                }, callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/aceptarPropuesta'),
                            type: 'POST',
                            async: true,
                            data:
                                    //  { cursoId: $("#txtCurso").val(),
                                    // grupoId: $("#txtGrupo").val() }
                                    form.serialize()
                            ,
                            success: function (response) {
                                MODAL.hideWait();
                                MODAL.hide();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        expandirSistema: function (e) {
            e.preventDefault();
            bootbox.confirm({
                message: "¿Está seguro que desea expandir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                }, callback: function (result) {
                    if (result) {
                        location.href = APP.url("academico/docente/cargaacademica/expandir/" + $("#txtSeccion").val());
                    }
                }
            });
        },
        aceptarExpandirSistema: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            location.href = APP.url("academico/docente/cargaacademica/expandir/" + rec.id);
        },
        notasAcademicas: function ($this, e) {
            var tr = $this.closest("tr");
            var idx = $this.attr("rel");
            //var rec = dynatable.settings.dataset.records[idx];

            location.href = APP.url('academico/docente/cargaacademica/') + idx + '/notasAcademicas';
        },
        verAlumnos: function ($this, e) {
            var tr = $this.closest("tr");
            var idx = $this.attr("rel");
            location.href = APP.url('academico/docente/alumnosDocente/') + idx + '/alumnosDocente';
        },
        verNuevoSC: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/docente/cargaacademica/nuevo/" + $("#txtGrupo").val());
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
        aceptarRechazo: function (e) {


            bootbox.confirm({
                message: "¿Está seguro que desea rechazar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                }, callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/aceptarRechazo'),
                            type: 'POST',
                            async: true,
                            data: {
                                cursoId: $("#txtCurso").val(),
                                grupoId: $("#txtGrupo").val()
                            },
                            success: function (response) {
                                MODAL.hideWait();
                                MODAL.hide();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
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
        desvincularPlanCalificacion: function ($this) {
            MODAL.showWait("Espere un momento por favor");

            bootbox.confirm({
                message: "¿Está seguro que desea desvincular?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                }, callback: function (result) {


                    var tr = $this.closest("tr");
                    var idx = tr.attr("rel");
                    var rec = dynatable.settings.dataset.records[idx];

                    $.ajax({
                        url: APP.url('academico/docente/cargaacademica/desvincularPlanCalificacion'),
                        type: 'POST',
                        async: true,
                        data: {
                            grupo: rec.id
                        },
                        success: function (response) {
                            MODAL.hideWait();
                            MODAL.hide();

                            if (response.success) {
                                notify(response.message, "info");
                                dynatable.process();
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function (error) {
                            MODAL.hideWait();
                            var message = error.responseJSON.message;
                            notify(message, "error");
                        }
                    });

                }
            });



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
        }
    };

    $("body").delegate(".aceptar-sistema-calificacion", "click", function (e) {
        CargaAcademica.aceptarSistemaCalificacion($(this), e);
    });
    $("body").delegate(".sistema-calificacion", "click", function (e) {
        CargaAcademica.verSistemaCalificacion($(this), e);
    });
    $("body").delegate("#cmbAceptar", "click", function (e) {
        CargaAcademica.confirmaSistemaCalificacion($(this), e);
    });
    $("body").delegate("#cmbRechazar", "click", function (e) {
        CargaAcademica.aceptarRechazo($(this), e);
    });
    $("body").delegate(".expandir-sistema", "click", function (e) {
        CargaAcademica.expandirSistema(e);
    });
    $("body").delegate(".aceptar-expandir-sistema", "click", function (e) {
        CargaAcademica.aceptarExpandirSistema($(this), e);
    });
    $("body").delegate(".notas-academicas", "click", function (e) {
        CargaAcademica.notasAcademicas($(this), e);
    });
    $("body").delegate(".ver-alumnos", "click", function (e) {
        CargaAcademica.verAlumnos($(this), e);
    });
    $("body").delegate(".new-sis-calificacion", "click", function (e) {
        CargaAcademica.verNuevoSC(e);
    });
    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        CargaAcademica.addTipoEvaluacion(e);
    });
    $("body").delegate(".clsCantEvaluaciones", "keyup", function (e) {
        CargaAcademica.changeCantidadEval($(this));
        CargaAcademica.calcularPesoEval($(this));
    });
    $("body").delegate(".calcular-peso-eva", "keyup", function (e) {
        CargaAcademica.calcularPesoEval($(this));
    });
    $("body").delegate(".calcular-peso-eva-chk", "change", function (e) {
        CargaAcademica.calcularPesoEval($(this));
    });
    $("body").delegate(".desvincular-expandir-sistema", "click", function (e) {
        CargaAcademica.desvincularPlanCalificacion($(this));
    });


});
