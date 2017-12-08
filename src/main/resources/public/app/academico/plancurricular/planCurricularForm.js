$(function () {

    var dynatableCursosObl = $('#dynaTableCurObl').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/cursosObligatorios'),
            perPageDefault: 10
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
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        var nroCiclo = NuevaCurricula.numeroCicloElegido.attr("rel");
        var cursos = $("#dynaTableCurObl tbody tr").length - 1;
        var cant = $("#cantCursosCiclo_" + nroCiclo);
        cant.removeClass("text-primary");
        cant.removeClass("text-danger");
        cant.html('(' + cursos + ')');
        if ((cursos == 0 && nroCiclo > 0) || (cursos > 0 && nroCiclo == 0)) {
            cant.addClass("text-danger");
        } else {
            cant.addClass("text-primary");
        }

    }).data('dynatable');

    var dynatableCursosRes = $('#dynaTableCurRes').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/planCurricular/resumenCurricula'),
            perPageDefault: 10
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
            ajaxUrl: APP.url('academico/planCurricular/cursosAdicionales'),
            perPageDefault: 10
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
            ajaxUrl: APP.url('academico/planCurricular/cursosElectivos')
            , perPageDefault: 20
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

    NuevaCurricula = {
        idPlan: $("#txtPlanCurricular").val(),
        numeroCicloElegido: null,
        pestanaCicloCurElecElegida: null,
        tipoCursoCurricula: null,
        record: null,
        init: function () {
            $('#inpDateFechaVig').datepicker();
            if (NuevaCurricula.idPlan != null) {
                $(NuevaCurricula.numeroCicloElegido).attr("rel", "1")
                NuevaCurricula.numeroCicloElegido = $("li.ver-tab-ciclo-cur-obl").first();
                $("li.ver-tab-ciclo-cur-obl").first().addClass(("active"));
                if (dynatableCursosObl != null) {
                    dynatableCursosObl.queries.add("planc", NuevaCurricula.idPlan);
                    dynatableCursosObl.queries.add("numCic", 1);
                    dynatableCursosObl.process();
                }
                if (dynatableCursosRes != null) {
                    dynatableCursosRes.queries.add("planc", NuevaCurricula.idPlan);
                    dynatableCursosRes.process();
                }
                if (dynatableCursosAdc != null) {
                    dynatableCursosAdc.queries.add("planc", NuevaCurricula.idPlan);
                    dynatableCursosAdc.process();
                }
                if (dynatableCursosElec != null) {
                    dynatableCursosElec.queries.add("planc", NuevaCurricula.idPlan);
                    dynatableCursosElec.process();
                }

                $("#cboOrientacion").select2({
                    allowClear: true,
                    placeholder: "Seleccione una orientación"
                });
            }

            if (NuevaCurricula.idPlan == "") {
                $("#cboCarrera").select2({
                    formatResult: function (carrera) {
                        var option = carrera.element;
                        var facultad = $(option).data('facultad');
                        var modalidad = $(option).data('modalidad');
                        var codeModalidad = $(option).data('code-modalidad');
                        var tipo = $(option).data('tipo');

                        var html = '<span class="block h4 m-b-xs m-t-xs"><strong>' + carrera.text + '</strong></span>';

                        if (codeModalidad == 'PRE') {
                            html += '<span class="block">Facultad ' + facultad + ' - Modalidad ' + modalidad + '</span>';
                        }
                        if (codeModalidad == 'EPG') {
                            html += '<span class="block">' + tipo + ' - Modalidad ' + modalidad + '</span>';
                        }

                        return html;
                    },
                    formatSelection: function (carrera) {
                        var option = carrera.element;
                        var facultad = $(option).data('facultad');
                        var modalidad = $(option).data('modalidad');
                        var codeModalidad = $(option).data('code-modalidad');
                        var tipo = $(option).data('tipo');

                        $("#pregrado").addClass("hide");
                        $("#posgrado").addClass("hide");

                        if (codeModalidad == 'PRE') {
                            $("#pregrado").removeClass("hide");
                            $("#facultad").html(facultad);
                            $("#modalidad").html(modalidad);
                        }
                        if (codeModalidad == 'EPG') {
                            $("#posgrado").removeClass("hide");
                            $("#tipo").html(tipo);
                            $("#modalidad2").html(modalidad);
                        }

                        return carrera.text;
                    }
                });
            }
        },
        verPestanaCicloCurObl($this, e) {
            var ciclo = parseInt($this.attr("rel"));
            var nroRomano = $this.attr("roman");
            if (NuevaCurricula.numeroCicloElegido !== null) {
                NuevaCurricula.numeroCicloElegido.removeClass("active");
            }
            $this.addClass("active");
            NuevaCurricula.numeroCicloElegido = $this;
            //$("#spnCicloObl").html(pestana);
            if (ciclo > 0) {
                $("#tituloObligatorios").html('Cursos del ciclo <span id="spnCicloObl" class="font-roman">' + nroRomano + '</span>');
                $("#btnAddCursoObl").show();
            } else {
                $("#tituloObligatorios").html("Cursos que falta clasificar por ciclo");
                $("#btnAddCursoObl").hide();
            }

            dynatableCursosObl.queries.add("planc", NuevaCurricula.idPlan);
            dynatableCursosObl.queries.add("numCic", ciclo);
            dynatableCursosObl.process();

        },
        verPestanaCicloCurElec($this, e) {
            var pestana = $this.attr("rel");
            if (NuevaCurricula.pestanaCicloCurElecElegida !== null) {
                NuevaCurricula.pestanaCicloCurElecElegida.removeClass("active");
            }
            $this.addClass("active");
            NuevaCurricula.pestanaCicloCurElecElegida = $this;
            $("#spnCicloElec").html("Ciclo " + pestana);
        },
        verAddCursoObl($this, e) {
            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Agregar curso en el ciclo " + $("#spnCicloObl").html());
            MODAL.show();
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurObl">Aceptar</a>');
            MODAL.body('');

            $.ajax({
                url: APP.url('academico/planCurricular/' + NuevaCurricula.idPlan + '/addCursoObligatorio'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#txtNumeroCiclo").val(NuevaCurricula.numeroCicloElegido.attr("rel"));
                    $("#cboTipoCursoCurricula").select2({placeholder: "Seleccione un tipo curso"});
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        select2BuscarCursos: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursos"),
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
                return info.codigo + " - " + info.curso;
            },
            escapeMarkup: function (m) {
                return m;
            }
        },
        editarCursoObl($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title("Edición Curso " + rec.curso);
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurObl">Aceptar</a>');
            MODAL.show();

            $.ajax({
                url: APP.url('academico/planCurricular/' + rec.id + '/editarCursoObligatorio'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#txtNumeroCiclo").val(NuevaCurricula.numeroCicloElegido.attr("rel"));

                    $.ajax({
                        url: APP.url('academico/planCurricular/' + $("#txtTipoCurCur").val() + '/cambiarTipoCursoCurricula'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            NuevaCurricula.tipoCursoCurricula = response.data;
                            if (response.data.tieneRequisitos) {
                                $("#cboCursosReq").select2(NuevaCurricula.select2RequisitoCursoCurricula);
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
        },
        editarCursoElec($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosElec.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title("Edición Curso " + rec.curso);
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurElec">Aceptar</a>');
            MODAL.show();

            $.ajax({
                url: APP.url('academico/planCurricular/' + rec.id + '/editarCursoElectivo'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#cboCursoElec").select2(NuevaCurricula.cursoElectivos).on('select2-selecting', function (e) {
                        $("#txtCreditosElec").val(e.object.creditos);
                    });

                    $("#cboTipoCursoCurriculaElec").select2();
                    $("#cboCursosRequistosElectivo").select2(NuevaCurricula.select2RequisitoCursoOpcional);

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        deleteCursoObl($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];

            bootbox.confirm({
                message: '¿Está seguro que desea eliminar este curso?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancel', className: 'btn-link'}
                },
                callback(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/planCurricular/deleteCursoObligatorio'),
                            type: 'POST',
                            async: false,
                            data: {id: rec.id, "planCurricular.id": NuevaCurricula.idPlan},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatableCursosObl.process();
                                    dynatableCursosRes.process();
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
        verTrasladaToCiclo($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];
            var record = {
                id: rec.id,
                curso: rec.curso,
                numeroCiclo: rec.numeroRomano,
                form: "formCambioCiclo",
                idSelect: "selectCambioCiclo",
                idPlan: NuevaCurricula.idPlan
            };

            MODAL.init("md");
            MODAL.title("Trasladar curso a otro ciclo");
            MODAL.buttons('<a class="btn btn-warning" id="btnCambiarCiclo">Cambiar ciclo</a>');
            MODAL.body($.templates("#templateTrasladarCiclos").render(record));
            MODAL.show();

            $("#" + record.idSelect + " option").filter("[value='" + rec.numeroCiclo + "']").remove();
            $("#" + record.idSelect).select2();

            NuevaCurricula.record = record;

        },
        verPreRequisitos($this, e, tipoRequi, tipoObli) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];
            if (tipoObli == "elec") {
                rec = dynatableCursosElec.settings.dataset.records[idx];
            }
            rec.tipoRequisitos = tipoRequi;

            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#templatePreRequisitos").render(rec));
            MODAL.show();

        },
        trasladarToCiclo($this, e) {
            e.preventDefault();
            var record = NuevaCurricula.record;
            var form = $("#" + record.form);
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $("#" + record.idSelect).select2("readonly", true);

            MODAL.disableButtons($this);
            $.ajax({
                url: APP.url('academico/planCurricular/trasladarCiclo'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        dynatableCursosObl.process();
                        dynatableCursosRes.process();
                    } else {
                        MODAL.activateButtons();
                        $("#" + record.idSelect).select2("readonly", false);
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.activateButtons();
                    $("#" + record.idSelect).select2("readonly", false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        verTrasladarToElectivos($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];
            var record = {
                id: rec.id,
                codigo: rec.codigo,
                curso: rec.curso,
                numeroCiclo: rec.numeroRomano,
                form: "formTrasladarElectivos",
                idSelect: "selectTipoCurricula",
                idPlan: NuevaCurricula.idPlan
            };

            MODAL.init("md");
            MODAL.title("Trasladar curso al grupo de Electivos");
            MODAL.buttons('<a class="btn btn-warning" id="btnTrasladarToElectivos">Trasladar a Electivos</a>');
            MODAL.body($.templates("#templateTrasladarElectivos").render(record));
            MODAL.show();

            $("#" + record.idSelect).select2();
            NuevaCurricula.record = record;
        },
        verTrasladarToObligatorios($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosElec.settings.dataset.records[idx];
            var record = {
                id: rec.id,
                codigo: rec.codigo,
                curso: rec.curso,
                form: "formTrasladarObligatorios",
                idSelect: "selectTipoCurricula",
                idPlan: NuevaCurricula.idPlan
            };

            MODAL.init("md");
            MODAL.title("Trasladar curso al grupo de Obligatorios/Generales");
            MODAL.buttons('<a class="btn btn-warning" id="btnTrasladarToObligatorios">Trasladar a Electivos</a>');
            MODAL.body($.templates("#templateTrasladarObligatorios").render(record));
            MODAL.show();

            $("#" + record.idSelect).select2();
            NuevaCurricula.record = record;
        },
        trasladarToElectivos($this, e) {
            e.preventDefault();
            var record = NuevaCurricula.record;
            var form = $("#" + record.form);
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $("#" + record.idSelect).select2("readonly", true);

            MODAL.disableButtons($this);
            $.ajax({
                url: APP.url('academico/planCurricular/trasladarToElectivos'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        dynatableCursosObl.process();
                        dynatableCursosRes.process();
                        dynatableCursosElec.process();

                    } else {
                        MODAL.activateButtons();
                        $("#" + record.idSelect).select2("readonly", false);
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.activateButtons();
                    $("#" + record.idSelect).select2("readonly", false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        trasladarToObligatorios($this, e) {
            e.preventDefault();
            var record = NuevaCurricula.record;
            var form = $("#" + record.form);
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $("#" + record.idSelect).select2("readonly", true);

            MODAL.disableButtons($this);
            $.ajax({
                url: APP.url('academico/planCurricular/trasladarToObligatorios'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        dynatableCursosElec.process();
                        dynatableCursosObl.process();
                        dynatableCursosRes.process();

                    } else {
                        MODAL.activateButtons();
                        $("#" + record.idSelect).select2("readonly", false);
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.activateButtons();
                    $("#" + record.idSelect).select2("readonly", false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        agregarCursoElec($this, e) {
            MODAL.init("lg");
            MODAL.title("Agregando Curso Electivo");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurElec">Aceptar</a>');
            MODAL.body('');

            $.ajax({
                url: APP.url('academico/planCurricular/' + NuevaCurricula.idPlan + '/agregarCursoElectivo'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);

                    $("#cboCursoElec").select2(NuevaCurricula.cursoElectivos).on('select2-selecting', function (e) {
//                        $("#cboCursoElec").val(e.object.id);
                        $("#txtCreditosElec").val(e.object.creditos);
                    });

                    $("#cboTipoCursoCurriculaElec").select2();
                    $("#cboCursosRequistosElectivo").select2(NuevaCurricula.select2RequisitoCursoOpcional);

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        agregarCursoAdc($this, e) {
            MODAL.init("md");
            MODAL.title("Cursos Adicionales");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurAdc">Aceptar</a>');
            MODAL.body('');

            $.ajax({
                url: APP.url('academico/planCurricular/' + NuevaCurricula.idPlan + '/agregarCursoAdicional'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);

                    $("#cboCursoAdc").select2(NuevaCurricula.cursosAdicionales).on('select2-selecting', function (e) {
                        $("#cboCursoAdc").val(e.object.id);
                    });


                    $.ajax({
                        url: APP.url('academico/planCurricular/cursoPorTipoCurricula'),
                        type: 'POST',
                        async: false,
                        data: {tipoCurricula: "ADIC"},
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
        },
        cursoElectivos: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursos"),
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
                return info.codigo + " - " + info.curso;
            },
            escapeMarkup: function (m) {
                return m;
            }
        },
        cursosAdicionales: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursos"),
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
                return info.codigo + " - " + info.curso;
            },
            escapeMarkup: function (m) {
                return m;
            }
        },
        cambiarComboCarrera($this, e) {
            var carr = $("#cboCarrera").val();
            $("#cboOrientacion").select2("destroy");
            $("#cboOrientacion").removeAttr("required");
            $("#cboOrientacion").html("");
            $("#divOrientacion").hide();

            if (!isNaN(carr)) {
                if (carr > 0) {
                    $.ajax({
                        url: APP.url('academico/planCurricular/' + carr + '/orientacionCarrera'),
                        type: 'POST',
                        async: false,
                        success: function (response) {

                            if (response != "") {
                                $("#divOrientacion").show();
                                $("#cboOrientacion").html(response);
                                $("#cboOrientacion").attr("required", "yes");
                                $("#cboOrientacion").select2();
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            }
        },
        cambiarTipoCursoCurricula($this, e) {
            var tipoCursoCurricula = $this.val();
            if (isNaN(tipoCursoCurricula)) {
                return;
            }
            if (tipoCursoCurricula <= 0) {
                return;
            }

            $.ajax({
                url: APP.url('academico/planCurricular/' + tipoCursoCurricula + '/cambiarTipoCursoCurricula'),
                type: 'POST',
                async: false,
                success: function (response) {
                    NuevaCurricula.tipoCursoCurricula = response.data;

                    //$("#txtCreditoReq").removeAttr("required");
                    $("#txtCreditos").attr("required", true);
                    $('#cmbAdd').attr("disabled", "disabled");
                    $('#txtCreditoReq').prop("readonly", true);
                    $('#txtCreditos').prop("readonly", true);


                    $("#cboCurso").select2(NuevaCurricula.select2BuscarCursos).on('select2-selecting', function (e) {
                        if (jQuery.type(NuevaCurricula.tipoCursoCurricula.tieneCreditoManual) === "undefined") {
                            $("#txtCreditos").val(e.object.creditos);
                        } else {
                            if (NuevaCurricula.tipoCursoCurricula.tieneCreditoManual != null) {
                                if (NuevaCurricula.tipoCursoCurricula.tieneCreditoManual) {
                                    $("#txtCreditos").val("");
                                } else {
                                    $("#txtCreditos").val(e.object.creditos);
                                }
                            }
                        }
                    });

                    if (response.data.tieneRequisitos) {
                        $('#txtCreditoReq').prop("readonly", false);
                        //$("#txtCreditoReq").attr("required", true)
                        $("#cmbAdd").removeAttr("disabled")

                        $("#cboCursosReq").select2(NuevaCurricula.select2RequisitoCursoCurricula);
                    }
                    if (response.data.tieneCreditoManual) {
                        $('#txtCreditos').prop("readonly", false);
                        $("#txtCreditos").val("");
                    }
                    if (response.data.cursoDefault != null && response.data.cursoDefault != undefined) {
                        $("#cboCurso").select2("data", response.data.cursoDefault);
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        infoCursoCurricula: null,
        select2RequisitoCursoCurricula: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursosCurricula"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        "curso.nombre": term,
                        "planCurricular.id": NuevaCurricula.idPlan,
                        numeroCiclo: NuevaCurricula.numeroCicloElegido.attr("rel"),
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
                NuevaCurricula.infoCursoCurricula = info;
                return info.codigo + " - " + info.curso;
            },
            escapeMarkup: function (m) {
                return m;
            }
        },
        select2RequisitoCursoOpcional: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursosOpcionales"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        "curso.nombre": term,
                        "planCurricular.id": NuevaCurricula.idPlan,
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
                NuevaCurricula.infoCursoCurricula = info;
                return info.codigo + " - " + info.curso;
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
                            url: APP.url('academico/planCurricular/savePlanCurricular'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    if (response.data.operation == "s") {
                                        location.href = APP.url('academico/planCurricular/' + response.data.planCurricular + '/succesSave');
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
        },
        addCursoObl: function () {
            var form = $("[id='frmAgregarCurso']");
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
                    //$('#txtCreditos').removeAttr("readonly");
                    $.ajax({
                        url: APP.url('academico/planCurricular/saveCursoObligatorio'),
                        type: 'POST',
                        async: true,
                        data: form.serialize(),
                        success: function (response) {
                            if (response.success) {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(response.message, "info");

                                dynatableCursosObl.queries.add("planc", NuevaCurricula.idPlan);
                                dynatableCursosObl.queries.add("numCic", NuevaCurricula.numeroCicloElegido.attr("rel"));
                                dynatableCursosObl.process();
                                dynatableCursosRes.process();

                            } else {
                                MODAL.hideWait();
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            MODAL.hideWait();
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        },
        addCursoAdicional: function (btn) {
            var form = $("[id='frmAgregarCursoAdc']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            MODAL.disableButtons(btn);
            $.ajax({
                url: APP.url('academico/planCurricular/saveCursoAdicional'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");

                        dynatableCursosAdc.queries.add("planc", NuevaCurricula.idPlan);
                        dynatableCursosAdc.process();
                    } else {
                        MODAL.activateButtons();
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.activateButtons();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addCursoElectivo: function (btn) {
            console.log("sdfgsdfg")
            var form = $("[id='frmAgregarCursoElec']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                console.log("error de formulario")
                return;
            }

            MODAL.disableButtons(btn);
            $.ajax({
                url: APP.url('academico/planCurricular/saveCursoElectivo'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");

                        dynatableCursosElec.queries.add("planc", NuevaCurricula.idPlan);
                        dynatableCursosElec.process();
                    } else {
                        MODAL.activateButtons();
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.activateButtons();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addRequisitoCursoObli: function (e) {
            if ($("#cboCursosReq").val() == "") {
                return false;
            }

            var rowCount = $('#tableRequisitosObli tr').length;
            if (rowCount > 0) {
                rowCount = rowCount - 1;
            }

            var found = 0;
            for (i = 0; i < rowCount; i++) {
                var codigoObli = $("[name='codigoObli_" + i + "']").html();
                if (codigoObli == NuevaCurricula.infoCursoCurricula.codigo) {
                    found++;
                }
            }
            if (found > 0) {
                bootbox.alert({
                    message: "Este curso ya fué agregado",
                    size: 'small'
                });
                return false;
            }

            $("#tableRequisitosObli").removeClass("hide");
            NuevaCurricula.infoCursoCurricula.index = rowCount;
            var html = $.templates("#templateRequisitoCursoObligatorio").render(NuevaCurricula.infoCursoCurricula);
            var tbody = $("#tableBodyRequisitosObli");
            tbody.append(html);
            $("#cboCursosReq").select2('val', '');
        },
        addRequisitoCursoElec: function () {

            if ($("#cboCursosRequistosElectivo").val() == "") {
                return false;
            }

            var rowCount = $('#tableRequisitosElec tr').length;
            if (rowCount > 0) {
                rowCount = rowCount - 1;
            }

            var found = 0;
            for (i = 0; i < rowCount; i++) {
                var codigoElec = $("[name='codigoElec_" + i + "']").html();
                if (codigoElec == NuevaCurricula.infoCursoCurricula.codigo) {
                    found++;
                }
            }
            if (found > 0) {
                bootbox.alert({
                    message: "Este curso ya fué agregado",
                    size: 'small'
                });
                return false;
            }

            $("#tableRequisitosElec").removeClass("hide");
            NuevaCurricula.infoCursoCurricula.index = rowCount;
            var html = $.templates("#templateRequisitoCursoElectivo").render(NuevaCurricula.infoCursoCurricula);
            var tbody = $("#tableBodyRequisitosElec");
            tbody.append(html);
            $("#cboCursosRequistosElectivo").select2('val', '');

        },
        deleteRequiCursoObli: function ($this, e) {
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

                        $("#tableBodyRequisitosObli tr").each(function (i, tr) {
                            $(tr).find("select, input").each(function (j, sel) {
                                var inp = $(sel);
                                var nameSel = inp.attr("name");
                                if ((nameSel != undefined && nameSel != "") && nameSel.indexOf("cursosCurricula") > -1) {
                                    inp.attr("name", NuevaCurricula.reindexNameForm(nameSel, i));
                                }
                            });
                        });
                    }
                }
            });
        },
        deleteRequiCursoElec: function ($this, e) {
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

                        $("#tableBodyRequisitosElec tr").each(function (i, tr) {
                            $(tr).find("select, input").each(function (j, sel) {
                                var inp = $(sel);
                                var nameSel = inp.attr("name");
                                if ((nameSel != undefined && nameSel != "") && nameSel.indexOf("cursosOpcionales") > -1) {
                                    inp.attr("name", NuevaCurricula.reindexNameForm(nameSel, i));
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
                            url: APP.url('academico/planCurricular/deleteCursoAdicional'),
                            type: 'POST',
                            async: true,
                            data: {id: rec.id},
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "info");

                                    dynatableCursosAdc.queries.add("planc", NuevaCurricula.idPlan);
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

            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosElec.settings.dataset.records[idx];

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el curso <b>" + rec.curso + "</b> del grupo de Electivos?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Si, eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {



                        $.ajax({
                            url: APP.url('academico/planCurricular/deleteCursoElectivo'),
                            type: 'POST',
                            async: true,
                            data: {id: rec.id},
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "info");

                                    dynatableCursosElec.queries.add("planc", NuevaCurricula.idPlan);
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
        },
        reindexNameForm: function (val, idx, pos) {
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
            location.href = APP.url('academico/planCurricular');
        }
    };

    NuevaCurricula.init();

    $("body").delegate(".ver-tab-ciclo-cur-obl", "click", function (e) {
        NuevaCurricula.verPestanaCicloCurObl($(this), e);
    });

    $("body").delegate(".ver-tab-ciclo-cur-elec", "click", function (e) {
        NuevaCurricula.verPestanaCicloCurElec($(this), e);
    });

    $("body").delegate(".agregar-curso-obl", "click", function (e) {
        NuevaCurricula.verAddCursoObl($(this), e);
    });

    $("body").delegate(".agregar-curso-elec", "click", function (e) {
        NuevaCurricula.agregarCursoElec($(this), e);
    });

    $("body").delegate(".agregar-curso-adc", "click", function (e) {
        NuevaCurricula.agregarCursoAdc($(this), e);
    });

    $("body").delegate("#cboCarrera", "change", function (e) {
        NuevaCurricula.cambiarComboCarrera($(this), e);
    });

    $("body").delegate("#cmbSavePlanCurricular", "click", function (e) {
        NuevaCurricula.savePlanCurricular();
    });

    $("body").delegate("#cmbUpdate", "click", function (e) {
        NuevaCurricula.savePlanCurricular();
    });

    $("body").delegate("#cboTipoCursoCurricula", "change", function (e) {
        NuevaCurricula.cambiarTipoCursoCurricula($(this), e);
    });

    $("body").delegate("#btnAddCurObl", "click", function (e) {
        NuevaCurricula.addCursoObl();
    });

    $("body").delegate("#btnAddCurAdc", "click", function (e) {
        NuevaCurricula.addCursoAdicional($(this));
    });

    $("body").delegate("#btnAddCurElec", "click", function (e) {
        NuevaCurricula.addCursoElectivo($(this));
    });

    $("body").delegate("#btnAddRequisitoCursoObli", "click", function (e) {
        NuevaCurricula.addRequisitoCursoObli(e);
    });
    $("body").delegate("#btnAddRequisitoCursoElec", "click", function (e) {
        NuevaCurricula.addRequisitoCursoElec(e);
    });

    $("body").delegate(".editar-cur-obl", "click", function (e) {
        NuevaCurricula.editarCursoObl($(this), e);
    });

    $("body").delegate(".editar-cur-elec", "click", function (e) {
        NuevaCurricula.editarCursoElec($(this), e);
    });

    $("body").delegate(".delete-cur-obl", "click", function (e) {
        NuevaCurricula.deleteCursoObl($(this), e);
    });

    $("body").delegate(".trasladar-to-ciclo", "click", function (e) {
        NuevaCurricula.verTrasladaToCiclo($(this), e);
    });

    $("body").delegate(".ver-pre-requisitos", "click", function (e) {
        NuevaCurricula.verPreRequisitos($(this), e, "pre", "obli");
    });

    $("body").delegate(".ver-post-requisitos", "click", function (e) {
        NuevaCurricula.verPreRequisitos($(this), e, "post", "obli");
    });

    $("body").delegate(".ver-pre-requisitos-elec", "click", function (e) {
        NuevaCurricula.verPreRequisitos($(this), e, "pre", "elec");
    });

    $("body").delegate(".ver-post-requisitos-elec", "click", function (e) {
        NuevaCurricula.verPreRequisitos($(this), e, "post", "elec");
    });

    $("body").delegate("#btnCambiarCiclo", "click", function (e) {
        NuevaCurricula.trasladarToCiclo($(this), e);
    });

    $("body").delegate(".trasladar-to-electivos", "click", function (e) {
        NuevaCurricula.verTrasladarToElectivos($(this), e);
    });

    $("body").delegate(".trasladar-to-obligatorios", "click", function (e) {
        NuevaCurricula.verTrasladarToObligatorios($(this), e);
    });

    $("body").delegate("#btnTrasladarToElectivos", "click", function (e) {
        NuevaCurricula.trasladarToElectivos($(this), e);
    });

    $("body").delegate("#btnTrasladarToObligatorios", "click", function (e) {
        NuevaCurricula.trasladarToObligatorios($(this), e);
    });

    $("body").delegate(".btn-delete-requisito-obli", "click", function (e) {
        NuevaCurricula.deleteRequiCursoObli($(this), e);
    });

    $("body").delegate(".btn-delete-requisito-elec", "click", function (e) {
        NuevaCurricula.deleteRequiCursoElec($(this), e);
    });

    $("body").delegate(".delete-cur-adc", "click", function (e) {
        NuevaCurricula.deleteCursoAdc($(this), e);
    });

    $("body").delegate(".delete-cur-elec", "click", function (e) {
        NuevaCurricula.deleteCursoElec($(this), e);
    });

    $("body").delegate(".cancelar", "click", function (e) {
        NuevaCurricula.cancelarNuevo($(this), e);
    });
});