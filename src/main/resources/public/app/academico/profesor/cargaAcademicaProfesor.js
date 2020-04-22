$(function () {

    var dynatable = $('#dynaTable').dynatable({
        
        dataset: {
            ajaxUrl: APP.url('academico/profesor/'+$("#idDocente").val()+'/listCargaAcademicaDocente'),
            perPageDefault: 100,
            recordCountText: '{x} to {y} out of {z} {params.records}'
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        },
        features: {
            paginate: false,
            search: false
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {ACT: "success", CER: "danger", CRE: "default"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;

        var seccionesHtml = "";
        var secciones = record.secciones.split(",");

        for (var i = 0; i < secciones.length; i++) {
            seccionesHtml += '<div class="m-l-md inline"><a href="#" ';
            if (record.estado == 'ACEP' && secciones[i].split("|")[4] == "VER") {
                seccionesHtml += 'class="notas-academicas"';
            } else if (secciones[i].split("|")[4] == "VER") {
                seccionesHtml += 'class="ver-alumnos"';
            } else {
                seccionesHtml += 'class="text-danger no-ver-alumnos"';
            }
            seccionesHtml += ' rel="' + secciones[i].split("|")[0] + '">' + secciones[i].split("|")[1];
            if (secciones[i].split("|")[3] != " ") {
                seccionesHtml += " - " + secciones[i].split("|")[3];
            }
            seccionesHtml += '</a></div>';
        }
        record.seccionesHtml = seccionesHtml;

        if (record.responsable != null) {
            record.responsablePlan = '<div class="block"><strong>Responsable:</strong> ' + record.responsable + '</div>';
        } else {
            record.responsablePlan = '<div class="text-danger block">Sin responsable</div>';
        }

        if (record.sistemas != "") {
            var sistemasCursos = record.sistemas.split("-");

            var sistemasHtml = '<div class="m-l-md inline">';

            for (var i = 0; i < sistemasCursos.length; i++) {
                var sistema = sistemasCursos[i].split(',');
                sistemasHtml += '<a href="#" rel="' + sistema[0] + '" class="label label-warning sistema-calificacion">';
                sistemasHtml += sistema[1];
                sistemasHtml += '</a> ';
            }
            sistemasHtml += '</div>';
            console.log(sistemasHtml);
            record.sistemasHtml = sistemasHtml;
        }


        var html = $.templates("#templateCargaAcademicaDocente").render(record);
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
            MODAL.title("Curso : " + rec.nombre);
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
             url: APP.url('academico/docente/cargaacademica/' + rec.idSistemaCalificacion + "/" + rec.id + '/aceptarSistemaCalificacion'),
             */
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + rec.id + '/aceptarSistemaCalificacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    $(".item-select2").select2();
                    MODAL.body(response);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            var plan = $this.attr("rel");

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación");
            MODAL.show();
            if (rec.estado == 'RHZ') {
                MODAL.buttons('<a class="btn btn-danger" id="cmbRechazar">Aceptar rechazo</a>');
            }
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + plan + "/" + rec.id + '/detalleSistemaCalificacion'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        confirmaSistemaCalificacion: function ($this, e) {
            var form = $("#frmAceptarSistCal");
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
                                notify(Messages.errorComunicacion, "error");
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
        noVerAlumnos: function ($this, e) {
            notify("Usted no es docente de esta clave", "error");
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
                                notify(Messages.errorComunicacion, "error");
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

                    if (result) {
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

                                MODAL.hide();

                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function (error) {

                                var message = error.responseJSON.message;
                                notify(message, "error");
                            }
                        });
                    }
                    MODAL.hideWait();
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
        },
        changeCambiarSistema: function ($this) {
            var cboSistemaAceptar = $this.val();

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + $("#txtGrupo").val() + '/ aceptarSistemaCalificacion'),
                type: 'POST',
                async: false,
                data: {planCalificacion: cboSistemaAceptar},
                success: function (response) {
                    $(".item-select2").select2();
                    //    $(".modal-body").html(response);
                    MODAL.body('');
                    MODAL.body(response);
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
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
    $("body").delegate(".no-ver-alumnos", "click", function (e) {
        CargaAcademica.noVerAlumnos($(this), e);
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
    $("body").delegate(".cbo-cambiar-sistema", "change", function (e) {
        CargaAcademica.changeCambiarSistema($(this));
    });


});
