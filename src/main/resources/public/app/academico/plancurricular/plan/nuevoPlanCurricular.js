$(function () {

    var dynatableCursosObl = $('#dynaTableCurObl').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/plan/listCurObl')
            , perPageDefault: 10
        }, features: {
            paginate: false,
            recordCount: false,
            sorting: false,
            search: false
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    var dynatableCursosRes = $('#dynaTableCurRes').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/plan/listCurResumen')
            , perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriterRes
        }, features: {
            paginate: false,
            recordCount: false,
            sorting: false,
            search: false
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    var dynatableCursosAdc = $('#dynaTableCurAdc').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/plan/listCurAdc')
            , perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriterAdc
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }, features: {
            paginate: false,
            recordCount: false,
            sorting: false,
            search: false
        }
    }).data('dynatable');

    var dynatableCursosElec = $('#dynaTableCurElec').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/plan/listCurElec')
            , perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriterElec
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templateCursoCurricula").render(record);
        return html;
    }

    function ulWriterRes(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templateCursoCurriculaRes").render(record);
        return html;
    }

    function ulWriterAdc(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templateCursoCurriculaAdc").render(record);
        return html;
    }

    function ulWriterElec(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templateCursoCurriculaElec").render(record);
        return html;
    }

    NuevoPlanCurricular = {
        pestanaCicloCurOblElegida: null,
        pestanaCicloCurElecElegida: null,
        tipoCursoCurricula: null,
        init: function () {
            //$('#dateFechaVig').datepicker("remove");
            // $('#inpDateFechaVig').datepicker({startDate: "today"});
            $('#inpDateFechaVig').datepicker();
            //  $('#dateFechaVig').datepicker('update');
            if ($("#txtPlanCurricular").val() != null) {
                $(NuevoPlanCurricular.pestanaCicloCurOblElegida).attr("rel", "1")
                NuevoPlanCurricular.pestanaCicloCurOblElegida = $("li.ver-tab-ciclo-cur-obl").first();
                $("li.ver-tab-ciclo-cur-obl").first().addClass(("active"));
                if (dynatableCursosObl != null) {
                    dynatableCursosObl.queries.add("planc", $("#txtPlanCurricular").val());
                    dynatableCursosObl.queries.add("numCic", 1);
                    dynatableCursosObl.process();
                }
                if (dynatableCursosRes != null) {
                    dynatableCursosRes.queries.add("planc", $("#txtPlanCurricular").val());
                    dynatableCursosRes.process();
                }
                if (dynatableCursosAdc != null) {
                    dynatableCursosAdc.queries.add("planc", $("#txtPlanCurricular").val());
                    dynatableCursosAdc.process();
                }
                if (dynatableCursosElec != null) {
                    dynatableCursosElec.queries.add("planc", $("#txtPlanCurricular").val());
                    dynatableCursosElec.process();
                }
            }
        }, verPestanaCicloCurObl($this, e) {
            var pestana = $this.attr("rel");
            if (NuevoPlanCurricular.pestanaCicloCurOblElegida !== null) {
                NuevoPlanCurricular.pestanaCicloCurOblElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevoPlanCurricular.pestanaCicloCurOblElegida = $this;
            $("#spnCicloObl").html("Ciclo " + pestana);

            dynatableCursosObl.queries.add("planc", $("#txtPlanCurricular").val());
            dynatableCursosObl.queries.add("numCic", pestana);
            dynatableCursosObl.process();

        }, verPestanaCicloCurElec($this, e) {
            var pestana = $this.attr("rel");
            if (NuevoPlanCurricular.pestanaCicloCurElecElegida !== null) {
                NuevoPlanCurricular.pestanaCicloCurElecElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevoPlanCurricular.pestanaCicloCurElecElegida = $this;
            $("#spnCicloElec").html("Ciclo " + pestana);
        }, agregarCursoObl($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Curso : " + $("#spnCicloObl").html());
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurObl">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + $("#txtPlanCurricular").val() + '/agregarCursoOblgPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#txtNumeroCiclo").val(NuevoPlanCurricular.pestanaCicloCurOblElegida.attr("rel"));

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, cursoSel2: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/plan/buscarCursos"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        nombre: term,
                        tipoCursoCurricula: $("#cboTipoCursoCurricula").val(),
                        page: page
                    };
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                return $.templates("#divBuscarCurso").render(info);
            },
            formatSelection: function (info) {
                return info.cursoCodigo + " - " + info.cursoNombre;
            },
            escapeMarkup: function (m) {
                return m;
            }
        }, editarCursoObl($this, e) {
            e.preventDefault();

            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Curso : " + $("#spnCicloObl").html());
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurObl">Aceptar</a>');
            MODAL.body('');

            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];


            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + rec.id + '/editarCursoOblgPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#txtNumeroCiclo").val(NuevoPlanCurricular.pestanaCicloCurOblElegida.attr("rel"));

                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/' + $("#txtTipoCurCur").val() + '/cambiarTipoCursoCurricula'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            NuevoPlanCurricular.tipoCursoCurricula = response.data;
                            if (response.data.tieneRequisitos) {
                                $("#cboCursosReq").select2(NuevoPlanCurricular.cursoCurriculaSel2).on('select2-selecting', function (e) {
                                    $("#txtCursoReq").val(e.object.id);
                                });
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, agregarCursoElec($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Cursos Electivos");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurElec">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + $("#txtPlanCurricular").val() + '/agregarCursoElecPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);

                    $("#cboCursoElec").select2(NuevoPlanCurricular.cursoElectivos).on('select2-selecting', function (e) {
                        $("#cboCursoElec").val(e.object.id);
                        $("#txtCreditosElec").val(e.object.cursoCreditos);
                    });

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, agregarCursoAdc($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Cursos Adicionales");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnAddCurAdc">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/planCurricular/plan/' + $("#txtPlanCurricular").val() + '/agregarCursoAdcPlan'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);

                    $("#cboCursoAdc").select2(NuevoPlanCurricular.cursosAdicionales).on('select2-selecting', function (e) {
                        $("#cboCursoAdc").val(e.object.id);
                    });


                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/cursoPorTipoCurricula'),
                        type: 'POST',
                        async: false,
                        data: {
                            tipoCurricula: "ADIC"
                        },
                        success: function (response) {
                            if (response.success) {
                                $("#cboCursoAdc").select2("data", response.data);
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });


                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, cursoElectivos: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/plan/buscarCursos"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {nombre: term, tipoCurricula: "REG", page: page};
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                return $.templates("#divBuscarCurso").render(info);
            },
            formatSelection: function (info) {
                return info.cursoCodigo + " - " + info.cursoNombre;
            },
            escapeMarkup: function (m) {
                return m;
            }
        }, cursosAdicionales: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/plan/buscarCursos"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {nombre: term, tipoCurricula: "ADIC", page: page};
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                return $.templates("#divBuscarCurso").render(info);
            },
            formatSelection: function (info) {
                return info.cursoCodigo + " - " + info.cursoNombre;
            },
            escapeMarkup: function (m) {
                return m;
            }
        }, cambiarComboCarrera($this, e) {
            var carr = $("#cboCarrera").val();
            if (!isNaN(carr)) {
                if (carr > 0) {
                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/' + carr + '/orientacionCarrera'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            $("#cboOrientacion").html(response);
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            }
        }, cambiarTipoCursoCurricula($this, e) {
            var tipoCursoCurricula = $this.val();
            if (!isNaN(tipoCursoCurricula)) {
                if (tipoCursoCurricula > 0) {
                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/' + tipoCursoCurricula + '/cambiarTipoCursoCurricula'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            NuevoPlanCurricular.tipoCursoCurricula = response.data;

                            $("#txtCreditoReq").removeAttr("required")
                            $("#txtCreditos").attr("required", true);
                            $('#cmbAdd').attr("disabled", "disabled");
                            $('#txtCreditoReq').prop("disabled", true);
                            $('#txtCreditos').prop("readonly", true);


                            $("#cboCurso").select2(NuevoPlanCurricular.cursoSel2).on('select2-selecting', function (e) {
                                $("#txtCurso").val(e.object.id);
                                if (jQuery.type(NuevoPlanCurricular.tipoCursoCurricula.tieneCreditoManual) === "undefined") {
                                    $("#txtCreditos").val(e.object.cursoCreditos);
                                } else {
                                    if (NuevoPlanCurricular.tipoCursoCurricula.tieneCreditoManual != null) {
                                        if (NuevoPlanCurricular.tipoCursoCurricula.tieneCreditoManual) {
                                            $("#txtCreditos").val("");
                                        } else {
                                            $("#txtCreditos").val(e.object.cursoCreditos);
                                        }
                                    }
                                }
                            });

                            if (response.data.tieneRequisitos) {
                                $('#txtCreditoReq').prop("disabled", false);
                                $("#txtCreditoReq").attr("required", true)
                                $("#cmbAdd").removeAttr("disabled")

                                $("#cboCursosReq").select2(NuevoPlanCurricular.cursoCurriculaSel2).on('select2-selecting', function (e) {
                                    $("#txtCursoReq").val(e.object.id);
                                });
                            }
                            if (response.data.tieneCreditoManual) {
                                $('#txtCreditos').prop("readonly", false);
                                $("#txtCreditos").val("");
                            }
                            if (response.data.cursoDefault != null && response.data.cursoDefault != undefined) {
                                $("#txtCurso").val(response.data.cursoDefault.id);
                                $("#cboCurso").select2("data", response.data.cursoDefault);
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            }
        }, cursoCurriculaSel2: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/plan/buscarCursosCurricula"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {nombre: term,
                        planCurricular: $("#txtPlanCurricular").val(),
                        numeroCiclo: NuevoPlanCurricular.pestanaCicloCurOblElegida.attr("rel"),
                        page: page};
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                return $.templates("#divBuscarCurso").render(info);
            },
            formatSelection: function (info) {
                return info.cursoCodigo + " - " + info.cursoNombre;
            },
            escapeMarkup: function (m) {
                return m;
            }
        },
        savePlanCurricular: function () {


            var form = $("[id='frmPlanCurricular']");
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
                            url: APP.url('academico/planCurricular/plan/savePlanCurricular'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    if (response.data.operation == "s") {
                                        location.href = APP.url('academico/planCurricular/plan/' + response.data.planCurricular + '/succesSave');
                                    }
                                } else {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        }, addCursoObl: function () {
            var form = $("[id='frmAgregarCurso']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea agregar el curso?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");
                    $('#txtCreditos').removeAttr("readonly");
                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/saveAgregarCursoObl'),
                        type: 'POST',
                        async: true,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "info");

                                dynatableCursosObl.queries.add("planc", $("#txtPlanCurricular").val());
                                dynatableCursosObl.queries.add("numCic", NuevoPlanCurricular.pestanaCicloCurOblElegida.attr("rel"));
                                dynatableCursosObl.process();
                            } else {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            MODAL.hideWait();
                            MODAL.hide();
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        }, addCursoAdc: function () {
            var form = $("[id='frmAgregarCursoAdc']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea agregar el curso?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");

                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/saveAgregarCursoAdc'),
                        type: 'POST',
                        async: true,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "info");

                                dynatableCursosAdc.queries.add("planc", $("#txtPlanCurricular").val());
                                dynatableCursosAdc.process();
                            } else {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            MODAL.hideWait();
                            MODAL.hide();
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        }, addCursoElec: function () {
            var form = $("[id='frmAgregarCursoElec']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea agregar el curso?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");

                    $.ajax({
                        url: APP.url('academico/planCurricular/plan/saveAgregarCursoEle'),
                        type: 'POST',
                        async: true,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "info");

                                dynatableCursosElec.queries.add("planc", $("#txtPlanCurricular").val());
                                dynatableCursosElec.process();
                            } else {
                                MODAL.hide();
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            MODAL.hide();
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        }, addCursoReq: function (e) {
            var cursoCurriculaReq = $("#txtCursoReq").val();
            if ($("#txtCursoReq").val() == "") {
                return false;
            }
            $.ajax({
                url: APP.url('academico/planCurricular/plan/incluirCursoReq'),
                type: 'POST',
                async: true,
                data: {cursoCurriculaReq: cursoCurriculaReq},
                success: function (response) {

                    if (response.success) {
                        notify(response.message, "info");

                        var rowCount = $('#tblCursosReq tr').length;
                        if (rowCount > 0) {
                            rowCount = rowCount - 1;
                        }


                        var found = 0;
                        for (i = 0; i < rowCount; i++) {
                            var codigoCurCur = $("[name='codigoCurCur" + i + "']").html();
                            if (codigoCurCur == response.data.cursoCodigo) {
                                found++;
                            }
                        }
                        if (found > 0) {
                            bootbox.alert(
                                    {
                                        message: "Este curso ya fué agregado.",
                                        size: 'small'
                                    }
                            );
                            return false;
                        }

                        response.data.index = rowCount;
                        var html = $.templates("#templateCursosReqBody").render(response.data);
                        var tbody = $("#tblCursosReqBody");
                        tbody.append(html);
                        $("#cboCursosReq").select2('val', '');
                        $("#txtCursoReq").val("");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.hide();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        deleteCurCurso: function ($this, e) {
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

                        $("#tblCursosReqBody tr").each(function (i, tr) {
                            $(tr).find("select, input").each(function (j, sel) {
                                var inp = $(sel);
                                var nameSel = inp.attr("name");
                                if ((nameSel != undefined && nameSel != "") && nameSel.indexOf("requisitosCurricula") > -1) {
                                    inp.attr("name", NuevoPlanCurricular.reindexNameForm(nameSel, i));
                                }
                            });
                        });
                    }
                }
            });
        },
        deleteCursoAdc: function ($this, e) {
            e.preventDefault();

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el curso adicional?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {

                        var tr = $this.closest("tr");
                        var idx = tr.attr("rel");
                        var rec = dynatableCursosAdc.settings.dataset.records[idx];

                        $.ajax({
                            url: APP.url('academico/planCurricular/plan/deleteCurAdi'),
                            type: 'POST',
                            async: true,
                            data: {cursoCurriculaReq: rec.cCurriculaAdcId},
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "info");

                                    dynatableCursosAdc.queries.add("planc", $("#txtPlanCurricular").val());
                                    dynatableCursosAdc.process();
                                } else {
                                    MODAL.hide();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        deleteCursoElec: function ($this, e) {
            e.preventDefault();

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el curso electivo?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {

                        var tr = $this.closest("tr");
                        var idx = tr.attr("rel");
                        var rec = dynatableCursosElec.settings.dataset.records[idx];

                        $.ajax({
                            url: APP.url('academico/planCurricular/plan/deleteCurElec'),
                            type: 'POST',
                            async: true,
                            data: {cCurriculaOpcId: rec.cCurriculaOpcId},
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "info");

                                    dynatableCursosElec.queries.add("planc", $("#txtPlanCurricular").val());
                                    dynatableCursosElec.process();
                                } else {
                                    MODAL.hide();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }, reindexNameForm: function (val, idx, pos) {
            pos = typeof pos !== 'undefined' ? pos : 1;
            var nom = val;
            var ini = nom.indexOf("[");
            for (var i = 0; i < pos - 1; i++) {
                ini = nom.indexOf("[", ini + 1);
            }
            var fin = nom.indexOf("]", ini);
            nom = nom.substring(0, ini + 1) + idx + nom.substring(fin, nom.length);
            return nom;
        },
        cancelarNuevo: function ($this, e) {
            location.href = APP.url('academico/planCurricular/plan');
        }
    }
    NuevoPlanCurricular.init();

    $("body").delegate(".ver-tab-ciclo-cur-obl", "click", function (e) {
        NuevoPlanCurricular.verPestanaCicloCurObl($(this), e);
    });

    $("body").delegate(".ver-tab-ciclo-cur-elec", "click", function (e) {
        NuevoPlanCurricular.verPestanaCicloCurElec($(this), e);
    });

    $("body").delegate(".agregar-curso-obl", "click", function (e) {
        NuevoPlanCurricular.agregarCursoObl($(this), e);
    });

    $("body").delegate(".agregar-curso-elec", "click", function (e) {
        NuevoPlanCurricular.agregarCursoElec($(this), e);
    });

    $("body").delegate(".agregar-curso-adc", "click", function (e) {
        NuevoPlanCurricular.agregarCursoAdc($(this), e);
    });

    $("body").delegate("#cboCarrera", "change", function (e) {
        NuevoPlanCurricular.cambiarComboCarrera($(this), e);
    });

    $("body").delegate("#cmbSavePlanCurricular", "click", function (e) {
        NuevoPlanCurricular.savePlanCurricular();
    });

    $("body").delegate("#cmbUpdate", "click", function (e) {
        NuevoPlanCurricular.savePlanCurricular();
    });

    $("body").delegate("#cboTipoCursoCurricula", "change", function (e) {
        NuevoPlanCurricular.cambiarTipoCursoCurricula($(this), e);
    });

    $("body").delegate("#btnAddCurObl", "click", function (e) {
        NuevoPlanCurricular.addCursoObl();
    });

    $("body").delegate("#btnAddCurAdc", "click", function (e) {
        NuevoPlanCurricular.addCursoAdc();
    });

    $("body").delegate("#btnAddCurElec", "click", function (e) {
        NuevoPlanCurricular.addCursoElec();
    });

    $("body").delegate("#cmbAddCurReq", "click", function (e) {
        NuevoPlanCurricular.addCursoReq(e);
    });

    $("body").delegate(".editar-cur-obl", "click", function (e) {
        NuevoPlanCurricular.editarCursoObl($(this), e);
    });

    $("body").delegate(".cbo-celete-cur-cur", "click", function (e) {
        NuevoPlanCurricular.deleteCurCurso($(this), e);
    });

    $("body").delegate(".delete-cur-adc", "click", function (e) {
        NuevoPlanCurricular.deleteCursoAdc($(this), e);
    });

    $("body").delegate(".delete-cur-elec", "click", function (e) {
        NuevoPlanCurricular.deleteCursoElec($(this), e);
    });

    $("body").delegate(".cancelar", "click", function (e) {
        NuevoPlanCurricular.cancelarNuevo($(this), e);
    });
});