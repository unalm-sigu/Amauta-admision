$(function () {
    $(".date").datepickerBoot();

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
        },
        features: {
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
        record.paraContarCredTotal = '/OBL/GEN/ELC/DEP/'.indexOf(record.tipoCursoCodigo) > 0 ? 'SI' : 'NO';
        record.paraContarCredMin = '/OBL/GEN/ELC/DEP/ELE/CULT/PROD/TECIND/'.indexOf(record.tipoCursoCodigo) > 0 ? 'SI' : 'NO';
        record.index = rowIndex;
        console.log(record.tipoCursoCodigo)
        console.log('/OBL/GEN/ELC/'.indexOf(record.tipoCursoCodigo))
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
        codeModalidad: '',
        idPlan: $("#txtPlanCurricular").val(),
        numeroCicloElegido: null,
        pestanaCicloCurElecElegida: null,
        tipoCursoCurricula: null,
        record: null,
        flag: "",
        init: function () {
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

                $("#pregrado").addClass("hide");
                $("#posgrado").addClass("hide");
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
                        var tipo = $(option).data('tipo');
                        var modalidad = $(option).data('modalidad');
                        NuevaCurricula.codeModalidad = $(option).data('code-modalidad');

                        $("#pregrado").addClass("hide");
                        $("#posgrado").addClass("hide");

                        if (NuevaCurricula.codeModalidad == 'PRE') {
                            $("#pregrado").removeClass("hide");
                            $("#facultad").html(facultad);
                            $("#modalidad-pre").html(modalidad);
                        }
                        if (NuevaCurricula.codeModalidad == 'EPG') {
                            $("#posgrado").removeClass("hide");
                            $("#tipo").html(tipo);
                            $("#modalidad-epg").html(modalidad);
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
        editarCursoEqui($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title("Añadir cursos Equivalentes a " + rec.curso);
            MODAL.show();

            $.ajax({
                url: APP.url('academico/planCurricular/' + rec.id + '/editarCursosEquivalentes'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#cboCursosEquiObli").select2(NuevaCurricula.select2Cursos);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        editarCursoEquiElec($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosElec.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title("Añadir cursos Equivalentes a " + rec.curso);
            MODAL.show();

            $.ajax({
                url: APP.url('academico/planCurricular/' + rec.id + '/editarCursosEquivalentesElectivos'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $("#cboCursosEquiObli").select2(NuevaCurricula.select2Cursos);
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
        editarCursoAdc($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosAdc.settings.dataset.records[idx];

            MODAL.init("md");
            MODAL.title("Edición Curso " + rec.curso);
            MODAL.buttons('<a class="btn btn-primary" id="btnAddCurAdc">Aceptar</a>');
            MODAL.show();

            $.ajax({
                url: APP.url('academico/planCurricular/' + rec.id + '/editarCursoAdicional'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);

                    $("[name='cicloFin.id']").select2({
                        minimumInputLength: 1,
                        ajax: {
                            url: APP.url("academico/profesor/contrato/searchciclo"),
                            dataType: 'json',
                            type: 'post',
                            data: function (term, page) {
                                return {nombre: term, page: page};
                            },
                            results: function (response, page) {
                                return {results: response.data};
                            }
                        },
                        formatResult: function (info) {
                            return info.descripcion;
                        },
                        formatSelection: function (info) {
                            return info.descripcion;
                        }
                    }).on('select2-selecting', function (e) {
                        $("[name='cicloFin.id']").val(e.object.id);
                    });


                    $("[name='cicloInicio.id']").select2({
                        minimumInputLength: 1,
                        ajax: {
                            url: APP.url("academico/profesor/contrato/searchciclo"),
                            dataType: 'json',
                            type: 'post',
                            data: function (term, page) {
                                return {nombre: term, page: page};
                            },
                            results: function (response, page) {
                                return {results: response.data};
                            }
                        },
                        initSelection: function (element, callback) {
                            return null;
                        },
                        formatResult: function (info) {
                            return info.descripcion;
                        },
                        formatSelection: function (info) {
                            return info.descripcion;
                        },
                        escapeMarkup: function (m) {
                            return m;
                        }
                    });

                    axios.post(`/academico/planCurricular/findCursoAdicional/${rec.id}`)
                            .then(response => {
                                if (response.data.data.cicloInicio.id) {
                                    $("[name='cicloInicio.id']").select2("data", response.data.data.cicloInicio);
                                }
                                if (response.data.data.cicloFin.id) {
                                    $("[name='cicloFin.id']").select2("data", response.data.data.cicloFin);
                                }
                            })
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
            MODAL.body($.templates("#preRequisitosTemplate").render(rec));
            MODAL.show();

        },
        verEquivalentes($this, e, tipoObli) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];
            if (tipoObli == "elec") {
                rec = dynatableCursosElec.settings.dataset.records[idx];
            }
            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#equivalentesTemplate").render(rec));
            MODAL.show();

        },
        verEquivalentesElec($this, e, tipoObli) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatableCursosObl.settings.dataset.records[idx];
            rec = dynatableCursosElec.settings.dataset.records[idx];
            MODAL.init("md");
            MODAL.title("");
            console.log(JSON.stringify(rec, null, 2));
            MODAL.body($.templates("#equivalentesElectivoTemplate").render(rec));
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
                        NuevaCurricula.verMalla();

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
                        NuevaCurricula.verMalla();

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
                        NuevaCurricula.verMalla();

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

                    $("[name='cicloFin.id']").select2({
                        minimumInputLength: 1,
                        ajax: {
                            url: APP.url("academico/profesor/contrato/searchciclo"),
                            dataType: 'json',
                            type: 'post',
                            data: function (term, page) {
                                return {nombre: term, page: page};
                            },
                            results: function (response, page) {
                                return {results: response.data};
                            }
                        },
                        formatResult: function (info) {
                            return info.descripcion;
                        },
                        formatSelection: function (info) {
                            return info.descripcion;
                        }
                    });
                    $("[name='cicloInicio.id']").select2({
                        minimumInputLength: 1,
                        ajax: {
                            url: APP.url("academico/profesor/contrato/searchciclo"),
                            dataType: 'json',
                            type: 'post',
                            data: function (term, page) {
                                return {nombre: term, page: page};
                            },
                            results: function (response, page) {
                                return {results: response.data};
                            }
                        },
                        formatResult: function (info) {
                            return info.descripcion;
                        },
                        formatSelection: function (info) {
                            return info.descripcion;
                        },
                        escapeMarkup: function (m) {
                            return m;
                        }
                    });


                    $("#cboCursoAdc").select2(NuevaCurricula.cursosAdicionales).on('select2-selecting', function (e) {
                        $("#cboCursoAdc").val(e.object.id);
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
            $("#cbo-orientacion-pre").select2("destroy");
            $("#cbo-orientacion-pre").removeAttr("name");
            $("#cbo-orientacion-pre").removeAttr("required");
            $("#cbo-orientacion-pre").html("");
            $("#div-orientacion-pre").hide();

            $("#cbo-orientacion-pre").select2("destroy");
            $("#cbo-orientacion-pre").removeAttr("name");
            $("#cbo-orientacion-pre").removeAttr("required");
            $("#cbo-orientacion-pre").html("");
            $("#div-orientacion-epg").hide();

            $("#cbo-ciclos").select2("destroy");
            $("#cbo-ciclos").removeAttr("name");
            $("#cbo-ciclos").removeAttr("required");
            $("#cbo-ciclos").html("");

            $("#cantidad-ciclos").val(0);

            if (!isNaN(carr)) {
                if (carr > 0) {

                    $.ajax({
                        url: APP.url('academico/planCurricular/' + carr + '/orientacionCarrera'),
                        type: 'POST',
                        success: function (response) {
                            console.log(response)
                            console.log(response.success)
                            if (response.success) {

                                if (NuevaCurricula.codeModalidad == 'EPG') {
                                    $("#div-orientacion-epg").show();
                                    $("#cbo-orientacion-epg").html(response.data.orientaciones);
                                    $("#cbo-orientacion-epg").attr("required", "yes");
                                    $("#cbo-orientacion-epg").attr("name", "orientacionCarrera.id");
                                    $("#cbo-orientacion-epg").select2();
                                }
                                if (NuevaCurricula.codeModalidad == 'PRE') {
                                    $("#div-orientacion-pre").show();
                                    $("#cbo-orientacion-pre").html(response.data.orientaciones);
                                    $("#cbo-orientacion-pre").attr("required", "yes");
                                    $("#cbo-orientacion-pre").attr("name", "orientacionCarrera.id");
                                    $("#cbo-orientacion-pre").select2();
                                }

                                $("#cbo-ciclos").html(response.data.ciclos);
                                $("#cbo-ciclos").attr("name", "cicloInicioVigencia.id");
                                $("#cbo-ciclos").attr("required", "true");
                                $("#cbo-ciclos").select2();

                                $("#cantidad-ciclos").val(response.data.cantidad);

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
                    console.log(NuevaCurricula.tipoCursoCurricula)

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

                                $('#txtCreditos').prop("readonly", true);
                                if (e.object.tipoCredito == "VAR") {
                                    $("#txtCreditos").val("");
                                    $('#txtCreditos').prop("readonly", false);
                                }
                            }
                        }
                    });

                    if (response.data.tieneRequisitos) {
                        $('#txtCreditoReq').prop("readonly", false);
                        //$("#txtCreditoReq").attr("required", true)
                        $("#cmbAdd").removeAttr("disabled")
                        $("#cboCursosReq").select2(NuevaCurricula.select2RequisitoCursoCurricula);
                    } else {
                        $("#cboCursosReq").select2("destroy");
                        $("#cboCursosReq").val("");
                    }

                    if (response.data.tieneCreditoManual) {
                        $('#txtCreditos').prop("readonly", false);
                        $("#txtCreditos").val("");
                    } else {
                        if (response.data.cursoDefault != undefined) {
                            $("#txtCreditos").val(response.data.cursoDefault.creditos);
                        }
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
        select2Cursos: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/planCurricular/buscarCursosTodos"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        "nombre": term,
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
            var origen = $("#txtOrigen").val();
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
                                        var url = APP.url('academico/planCurricular/' + response.data.planCurricular + '/succesSave');
                                        url += "?origen=" + origen;
                                        location.href = url;
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
        procesarAlumnos: function () {
            MODAL.showWait();
            var form = $("[id='frmPlanCurricular']");
            $.ajax({
                url: APP.url('academico/planCurricular/procesaralumnos'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    } else {
                        notify(response.message, "error");
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        desvincularCursos: function () {
            MODAL.showWait();
            var form = $("[id='frmPlanCurricular']");
            $.ajax({
                url: APP.url('academico/planCurricular/desvincularCursos'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    } else {
                        notify(response.message, "error");
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
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
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
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
                                    NuevaCurricula.verMalla();

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
                }
            });
        },
        guardarGrupoEquivalente: function (btn) {
            var form = $("[id='frmAgregarCursosEquivalentes']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            var rowCount = $('#tableEquivalentesObli tr').length;
            if (rowCount === 0)
                return;

            $.ajax({
                url: APP.url('academico/planCurricular/saveGrupoEquivalente'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        dynatableCursosObl.process();
                        $("#tableEquivalentesAgregar").removeClass("hide");

                        var html = $.templates("#templateEquivalentesCursos").render(response.data);
                        var tbody = $("#tableBodyEquivalentesAgregar");
                        tbody.append(html);
                        $("#cboCursosEquiObli").select2('val', '');
                        $('#tableBodyEquivalentesObli').empty();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        guardarGrupoEquivalenteElectivo: function (btn) {
            console.log('guardarGrupoEquivalenteElectivo() ENTRA');
            var form = $("[id='frmAgregarCursosEquivalenteElectivo']");
            form.parsley().destroy();
            form.parsley();
            console.log(JSON.stringify(form.serialize(), null, 2));
            if (!form.parsley().validate()) {
                return;
            }
            var rowCount = $('#tableEquivalentesElec tr').length;
            if (rowCount === 0)
                return;

            $.ajax({
                url: APP.url('academico/planCurricular/saveGrupoEquivalenteElectivo'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        dynatableCursosObl.process();
                        dynatableCursosElec.process();
                        $("#tableEquivalentesElectivoAgregar").removeClass("hide");

                        var html = $.templates("#templateEquivalentesCursos").render(response.data);
                        var tbody = $("#tableBodyEquivalentesElectivoAgregar");
                        tbody.append(html);
                        $("#cboCursosEquiElec").select2('val', '');
                        $('#tableBodyEquivalentesElec').empty();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
            console.log('guardarGrupoEquivalenteElectivo() SALE');
        },
        addCursoElectivo: function (btn) {
            var form = $("[id='frmAgregarCursoElec']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
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
        addCursoAdicional: function (btn) {
            var form = $("[id='frmAgregarCursoAdc']");

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
                if (codigoObli === NuevaCurricula.infoCursoCurricula.codigo) {
                    found++;
                }
            }
            if (found > 0) {
                bootbox.alert({
                    message: "Este curso ya fue agregado",
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
        addEquivalenteObli: function (e) {
            if ($("#cboCursosEquiObli").val() === "") {
                return false;
            }
            var rowCount = $('#tableEquivalentesObli tr').length;

            var found = 0;
            for (i = 0; i < rowCount; i++) {
                var id = $("[name='cursoEquivalente[" + i + "].cursoEquivalente.id'").val();
                if (parseInt(id) === NuevaCurricula.infoCursoCurricula.id) {
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
            var html = $.templates("#templateEquivalenteCursoObligatorio").render(NuevaCurricula.infoCursoCurricula);
            var tbody = $("#tableBodyEquivalentesObli");
            tbody.append(html);
            $("#cboCursosEquiObli").select2('val', '');
        },
        addEquivalenteElec: function (e) {
            console.log('aaaaaaaaaaaaaaaaaaaaaaaaa');
            if ($("#cboCursosEquiElec").val() === "") {
                return false;
            }
            var rowCount = $('#tableEquivalentesElec tr').length;

            var found = 0;
            for (i = 0; i < rowCount; i++) {
                var id = $("[name='cursoEquivalenteElectivo[" + i + "].cursoEquivalente.id'").val();
                if (parseInt(id) === NuevaCurricula.infoCursoCurricula.id) {
                    found++;
                }
            }
            if (found > 0) {
                bootbox.alert({
                    message: "Este curso ya fue agregado",
                    size: 'small'
                });
                return false;
            }

            $("#tableRequisitosElec").removeClass("hide");
            NuevaCurricula.infoCursoCurricula.index = rowCount;
            var html = $.templates("#templateEquivalenteCursoObligatorio").render(NuevaCurricula.infoCursoCurricula);
            var tbody = $("#tableBodyEquivalentesElec");
            tbody.append(html);
            $("#cboCursosEquiElec").select2('val', '');
            console.log('aaaaaaaaaaaaaaaaaaaaaaaaa');
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
//        cancelarNuevo: function ($this, e) {
//            location.href = APP.url('academico/planCurricular');
//        },
        verMalla() {
            var id = $("#txtPlanCurricular").val();
            $.ajax({
                url: APP.url('academico/planCurricular/dataCurricula'),
                type: 'POST',
                async: true,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        NuevaCurricula.buildMalla(response.data);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        buildMalla(ciclos) {
            var ww = 170;
            var hh = 60;
            var padx = 30;
            var pady = 40;
            var pad = 40;
            var wwLine = 2;
            var wwBoldLine = 6;

            var colorBG = {
                GEN: "#F39C12", OBL: "#1E8449", EEP: "#7A3CE7", ECP: "#1EB4F1", ECC: "#289C0E",
                ELC: "#AAB7B8", ELF: "#AAB7B8", ELE: "#AAB7B8", PROD: "#C70039", CULT: "#267DD4", TECIND: "#FF5733"};
            var colorLetra = {
                GEN: "#fff", OBL: "#fff", EEP: "#fff", ECP: "#fff", ECC: "#fff",
                ELC: "#fff", ELF: "#fff", ELE: "#fff", PROD: "#fff", CULT: "#fff", TECIND: "#fff"};
            var colorLine = "#E74C3C";
            var colorDot = "#34495E";
            var colorArrow = "#D7DBDD";

            var maxRows = 0;
            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    maxRows = (maxRows > cursos[row].numeroCurso) ? maxRows : cursos[row].numeroCurso;
                }
            }

            $("#divMalla").html("");

            var draw = SVG('divMalla').size((ww + 2 * padx) * ciclos.length, pad + (hh + pady) * maxRows);
            for (var col = 0; col < ciclos.length; col++) {
                var text = draw.text("Ciclo " + ciclos[col].numeroRomano).addClass("h4");
                text.move(((ww + 2 * padx) / 2 + (ww + 2 * padx) * col - 30) + 'px', '5px');
            }

            var lazos = {};
            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var x1 = padx + (ww + 2 * padx) * col;
                    var x2 = x1 + ww;
                    var yc = pad + (hh + pady) * (cursos[row].numeroCurso - 1) + hh / 2;

                    lazos[cursos[row].id] = {
                        "left-x": x1,
                        "right-x": x2,
                        "y": yc,
                        "requisitos": []
                    };
                }
            }

            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var req = cursos[row].requisitos;
                    for (var r = 0; r < req.length; r++) {
                        var x1 = lazos[cursos[row].id]["left-x"];
                        var y1 = lazos[cursos[row].id]["y"];
                        var x2 = lazos[req[r].idReq]["right-x"];
                        var y2 = lazos[req[r].idReq]["y"];

                        var linea = draw.line(x1, y1, x2, y2).stroke({color: colorLine, width: wwLine});
                    }
                }
            }

            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var x1 = padx + (ww + 2 * padx) * col;
                    var x2 = x1 + ww;
                    var xc = x1 + ww / 2 - 60;
                    var tempXC = xc + 'px';
                    var y1 = pad + (hh + pady) * (cursos[row].numeroCurso - 1);
                    var y2 = y1 + hh;
                    var yc = y1 + hh / 2 - 15;
                    var tempYC = yc + 'px';

                    var polygon = draw.rect(ww, hh).radius(5).fill(colorBG[cursos[row]["tipo"]]).move(x1, y1).stroke({color: colorDot, width: 1});
                    var dot1 = draw.rect(10, 10).fill("#fff").move(x1 - 5, yc - 5).stroke({color: colorDot, width: 1});
                    var dot2 = draw.rect(10, 10).fill("#fff").move(x2 - 5, yc - 5).stroke({color: colorDot, width: 1});

                    var arrowDown = draw.polygon("0,8 7,0 14,8 11,8 11,14 3,14 3,8 0,8").fill(colorArrow).move(x2 - 16, y1 + 2).stroke({color: colorDot, width: 1});
                    var arrowUp = draw.polygon("0,6 3,6 3,0 11,0 11,6 14,6 7,14 0,6").fill(colorArrow).move(x2 - 16, y2 - 16).stroke({color: colorDot, width: 1});
                    arrowDown.style('cursor', 'n-resize');
                    arrowUp.style('cursor', 's-resize');

                    arrowDown.data({"idCurso": cursos[row].id});
                    arrowDown.click(function () {
                        var idCurso = this.data("idCurso");
                        NuevaCurricula.moveCurso(idCurso, "DOWN");
                    });

                    arrowUp.data({"idCurso": cursos[row].id});
                    arrowUp.click(function () {
                        var idCurso = this.data("idCurso");
                        NuevaCurricula.moveCurso(idCurso, "UP");
                    });

                    var tncur = draw.text(cursos[row]["numeroCurso"] + "").move((x1 + 4) + 'px', (y2 - 26) + 'px').fill(colorLetra[cursos[row]["tipo"]]).style("font-size", "12px");
                    var group = draw.group();
                    group.add(polygon);
                    group.add(dot1);
                    group.add(dot2);
                    group.add(arrowDown);
                    group.add(arrowUp);
                    group.add(tncur);

                    var req = cursos[row].requisitos;
                    if (req.length > 0) {
                        var dot3 = draw.rect(10, 10).fill(colorDot).move(x1 - 5, yc - 5);
                        group.add(dot3);
                    }

                    for (var r = 0; r < req.length; r++) {
                        var x22 = lazos[req[r].idReq]["right-x"];
                        var y22 = lazos[req[r].idReq]["y"];
                        var dot4 = draw.rect(10, 10).fill(colorDot).move(x22 - 5, y22 - 5);
                        group.add(dot4);
                    }

                    var data = NuevaCurricula.getConteCurso(cursos[row].curso, cursos[row].codigo, cursos[row].creditos);
                    if (data.length == 2) {
                        var y1 = yc - 8;
                        var y1 = y1 + 'px';
                        var y2 = yc + 8;
                        var y2 = y2 + 'px';

                        var t1 = draw.text(data[0]).move(tempXC, y1).fill(colorLetra[cursos[row]["tipo"]]);
                        var t2 = draw.text(data[1]).move(tempXC, y2).fill(colorLetra[cursos[row]["tipo"]]);
                        group.add(t1);
                        group.add(t2);

                    } else if (data.length == 3) {
                        var y1 = (yc - 16) + 'px';
                        var y2 = (yc) + 'px';
                        var y3 = (yc + 16) + 'px';
                        var t1 = draw.text(data[0]).move(tempXC, y1).fill(colorLetra[cursos[row]["tipo"]]);
                        var t2 = draw.text(data[1]).move(tempXC, y2).fill(colorLetra[cursos[row]["tipo"]]);
                        var t3 = draw.text(data[2]).move(tempXC, y3).fill(colorLetra[cursos[row]["tipo"]]);
                        group.add(t1);
                        group.add(t2);
                        group.add(t3);
                    }

                    group.data({"idCurso": cursos[row].id});
                    group.style('cursor', 'pointer');
                    group.mouseover(function () {
                        var idCurso = this.data("idCurso");
                        var reqs = lazos[idCurso]["requisitos"];
                        for (var i = 0; i < reqs.length; i++) {
                            draw.get(reqs[i]).show();
                        }
                    });
                    group.mouseout(function () {
                        var idCurso = this.data("idCurso");
                        var reqs = lazos[idCurso]["requisitos"];
                        for (var i = 0; i < reqs.length; i++) {
                            draw.get(reqs[i]).hide();
                        }
                    });
                }
            }

            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var req = cursos[row].requisitos;
                    for (var r = 0; r < req.length; r++) {
                        var x1 = lazos[cursos[row].id]["left-x"];
                        var y1 = lazos[cursos[row].id]["y"];
                        var x2 = lazos[req[r].idReq]["right-x"];
                        var y2 = lazos[req[r].idReq]["y"];

                        var linea = draw.line(x1, y1, x2, y2).stroke({color: colorLine, width: wwBoldLine}).hide();
                        var reqs = lazos[cursos[row].id]["requisitos"];
                        lazos[cursos[row].id]["requisitos"][reqs.length] = draw.index(linea);
                    }
                }
            }
        },
        getConteCurso(cur, cod, cred) {
            var data = [];
            if (cur.length <= 22) {
                data[0] = cur;
                data[1] = cod + " - " + cred + " crédito";
                data[1] += (cred == 1) ? "" : "s";
                return data;
            }

            var idx = 0;
            var partes = cur.split(" ");
            data[idx] = "";
            for (var i = 0; i < partes.length; i++) {
                if (data[idx].length + partes[i].length < 22) {
                    data[idx] += (data[idx].length == 0 ? "" : " ") + partes[i];
                } else if (idx < 1) {
                    idx++;
                    data[idx] = partes[i].substring(0, 22);
                } else if (idx == 1) {
                    data[idx] += (data[idx].length == 0 ? "" : " ") + partes[i];
                    data[idx] = data[idx].substring(0, 20) + "..";
                }
            }
            idx++;
            data[idx] = cod + " - " + cred + " crédito";
            data[idx] += (cred == 1) ? "" : "s";
            return data;
        },
        moveCurso(idCurso, direccion) {
            MODAL.showWait();
            var idPlan = $("#txtPlanCurricular").val();
            $.ajax({
                url: APP.url('academico/planCurricular/moveCurso'),
                type: 'POST',
                async: true,
                data: {id: idCurso, "planCurricular.id": idPlan, direccion: direccion},
                success: function (response) {
                    MODAL.hideWait();
                    if (response.success) {
                        NuevaCurricula.verMalla();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        deleteNuevoCursoEquivalente($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            tr.remove();
        },
        deleteGrupoEquivalente($this, e) {
            var tr = $this.closest('tr');
            var nGrupo = tr.find("[name='numero-grupo']").text();
            var cursoCurricula = tr.find("[name='cursoCurricula.id']").val();

            bootbox.confirm({
                message: '¿Está seguro que desea eliminar esta equivalencia?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancel', className: 'btn-link'}
                },
                callback(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/planCurricular/deleteGrupoEquivalente'),
                            type: 'POST',
                            async: true,
                            data: {grupo: nGrupo, curso: cursoCurricula},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatableCursosObl.process();
                                    while (true) {
                                        var nexttr = $this.closest('tr').next('tr');
                                        var flag = nexttr.find("[name='numero-grupo']").length > 0;
                                        if (flag)
                                            break;
                                        $this.closest('tr').next('tr').remove();
                                        var flag2 = $this.closest('tr').next('tr').remove();
                                        if (flag2.length === 0)
                                            break;
                                    }
                                    tr.remove();
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
        deleteGrupoEquivalenteElectivo($this, e) {
            var tr = $this.closest('tr');
            var nGrupo = tr.find("[name='numero-grupo']").text();
            var cursoOpcionalCurricula = tr.find("[name='cursoOpcionalCurricula.id']").val();
            console.log(cursoOpcionalCurricula);
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar esta equivalencia?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancel', className: 'btn-link'}
                },
                callback(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/planCurricular/deleteGrupoEquivalenteElectivo'),
                            type: 'POST',
                            async: true,
                            data: {grupo: nGrupo, curso: cursoOpcionalCurricula},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatableCursosObl.process();
                                    while (true) {
                                        var nexttr = $this.closest('tr').next('tr');
                                        var flag = nexttr.find("[name='numero-grupo']").length > 0;
                                        if (flag)
                                            break;
                                        $this.closest('tr').next('tr').remove();
                                        var flag2 = $this.closest('tr').next('tr').remove();
                                        if (flag2.length === 0)
                                            break;
                                    }
                                    tr.remove();
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
        editCreditos($this, e, clase) {
            console.log($this)
            // consuem event
            e.preventDefault();
            e.stopImmediatePropagation();

            $td = $this;
            // if class
            if ($td.children().attr('value') == undefined) {
                return;
            }
            // if already editing, do nothing.
            if ($td.data('editing'))
                return;
            // mark as editing
            $td.data('editing', true);

            // get old text
            var txt = $td.text();

            // store old text
            $td.data('oldText', txt);

            // make input
            var $input = $('<input type="text" class="editfield form-control " >');
            $input.val($.trim(txt));
            $input.attr('rel', $td.children().attr('rel'));
            $input.attr('value', $td.children().attr('value'));
            $input.numeric();
            // clean td and add the input
            $td.empty();
            $td.append($input);
        },
        save($this, e) {
            console.log($this);
            var totalCreditos = null;
            var minCreditos = null;
            $input = $(e.target);
            var val = $input.val();
            var rel = $input.attr('rel');
            var value = $input.attr('value');
            var $td = $input.closest('td');
            var $a = $('<a  class="block bold" > </a>');
            $a.text(val);
            $a.attr('rel', rel);
            $a.attr('value', value);
            $td.empty();
            $td.append($a);
            $td.data('editing', false);
            console.log('Value changed ' + rel);
            console.log('Value changed ' + value);
            if (value == 1) {
                totalCreditos = val;
            } else {
                minCreditos = val;
            }

            $.ajax({
                url: APP.url('academico/planCurricular/updateResumen'),
                type: 'POST',
                async: true,
                data: {idResumen: rel, totalCreditos: totalCreditos, minCreditos: minCreditos},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        dynatableCursosRes.process();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        caducar($this, e) {
            console.log($this);

            bootbox.confirm({
                message: '¿Está seguro que desea caducar este curso?',
                buttons: {
                    confirm: {label: 'Si, caducar', className: 'btn-danger'},
                    cancel: {label: 'Cancel', className: 'btn-link'}
                },
                callback(result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('academico/planCurricular/caducar'),
                            type: 'POST',
                            async: true,
                            data: {idCursoCurricula: $this[0].id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatableCursosObl.process();

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
        }
    };

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

    $("body").delegate("#procesarAlumnos", "click", function (e) {
        NuevaCurricula.procesarAlumnos();
    });

    $("body").delegate("#desvincularCursos", "click", function (e) {
        NuevaCurricula.desvincularCursos();
    });

    $("body").delegate("#cboTipoCursoCurricula", "change", function (e) {
        NuevaCurricula.cambiarTipoCursoCurricula($(this), e);
    });

    $("body").delegate("#btnAddCurObl", "click", function (e) {
        NuevaCurricula.addCursoObl();
    });

    $("body").delegate("#btnGuardarGrupoEquivalente", "click", function (e) {
        NuevaCurricula.guardarGrupoEquivalente();
    });

    $("body").delegate("#btnGuardarGrupoEquivalenteElectivo", "click", function (e) {
        NuevaCurricula.guardarGrupoEquivalenteElectivo();
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

    $("body").delegate("#btnAddEquivalente", "click", function (e) {
        NuevaCurricula.addEquivalenteObli(e);
    });

    $("body").delegate("#btnAddEquivalenteElec", "click", function (e) {
        NuevaCurricula.addEquivalenteElec(e);
    });

    $("body").delegate("#btnAddRequisitoCursoElec", "click", function (e) {
        NuevaCurricula.addRequisitoCursoElec(e);
    });

    $("body").delegate(".editar-cur-obl", "click", function (e) {
        NuevaCurricula.editarCursoObl($(this), e);
    });

    $("body").delegate(".editar-cur-equi-obl", "click", function (e) {
        NuevaCurricula.editarCursoEqui($(this), e);
    });

    $("body").delegate(".editar-cur-equi-elec", "click", function (e) {
        NuevaCurricula.editarCursoEquiElec($(this), e);
    });

    $("body").delegate(".editar-cur-elec", "click", function (e) {
        NuevaCurricula.editarCursoElec($(this), e);
    });

    $("body").delegate(".editar-cur-adc", "click", function (e) {
        NuevaCurricula.editarCursoAdc($(this), e);
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

    $("body").delegate(".ver-equivalentes", "click", function (e) {
        NuevaCurricula.verEquivalentes($(this), e, "obli");
    });

    $("body").delegate(".ver-equivalentes-elec", "click", function (e) {
        NuevaCurricula.verEquivalentesElec($(this), e, "obli");
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

    $("body").delegate(".btn-delete-grupo-equivalente", "click", function (e) {
        NuevaCurricula.deleteGrupoEquivalente($(this), e);
    });

    $("body").delegate(".btn-delete-grupo-equivalente-electivo", "click", function (e) {
        NuevaCurricula.deleteGrupoEquivalenteElectivo($(this), e);
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
//
//    $("body").delegate(".cancelar", "click", function (e) {
//        NuevaCurricula.cancelarNuevo($(this), e);
//    });
//
    $("body").delegate(".btn-delete-curso-equivalente", "click", function (e) {
        NuevaCurricula.deleteNuevoCursoEquivalente($(this), e);
    });

    $("body").delegate(".table-data td.ELC", "click", function (e) {
        NuevaCurricula.editCreditos($(this), e, 'ELC');
    });

    $("body").delegate(".table-data td.EEP", "click", function (e) {
        NuevaCurricula.editCreditos($(this), e), 'EEP';
    });
    $("body").delegate(".caducar", "click", function (e) {
        NuevaCurricula.caducar($(this), e);
    });

    $("body").delegate(".tabledata tr").on('keyup', 'input.editfield', function (e) {
        if (e.which == 13) {
            NuevaCurricula.save($(this), e);
        }
    });

    NuevaCurricula.init();
    NuevaCurricula.verMalla();
});
