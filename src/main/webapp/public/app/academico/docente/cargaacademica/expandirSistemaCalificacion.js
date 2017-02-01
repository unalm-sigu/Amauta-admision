$(function () {

    var permiteAsig = $("#txtPermiteAsig").val();

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/docente/cargaacademica/listEvaluacionPlan'),
            perPageDefault: 10,
            ajaxData: {evaluacionSeccion: $("#txtEvalSeccionId").val()}
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", APR: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        record.permiteAsign = (permiteAsig == "true");
        if (record.esHijo) {
            record.styleHijo = 'padding-left:90px;';
        }
        var html = $.templates("#templateEvaluacionPlan").render(record);
        return html;
    }

    ExpandirSCN = {
        expandirEvaluacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Expandir Evaluación");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success grabar-expansion" id="cmbAceptar">Aceptar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleExpandirEvaluacion'),
                type: 'POST',
                async: false,
                data: {
                    evaluacion: idx
                },
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        asignarDocente: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Asignar Docentes");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success grabar-asignacion" id="cmbSaveAssign">Aceptar</a>');
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleAsignarDocente'),
                type: 'POST',
                async: false,
                data: {
                    evaluacion: idx,
                    grupoSeccionId: $("#txtGrupoSeccionId").val()
                },
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        deleteEvaluacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar este registro?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/deleteExpansionHija'),
                            type: 'POST',
                            async: false,
                            data: {
                                evaluacion: idx
                            },
                            success: function (response) {
                                dynatable.process();
                                notify(response.message, "info");
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        },
        addTipoEvaluacion: function (e) {
            e.preventDefault();
            var record = {};

            var rowCount = $('#tbodyEvaluaciones tr').length;
            record.index = rowCount;
            record.max = $("#txtPesoEvalForExp").val();

            var html = $.templates("#templateExpandirEvaluacion").render(record);
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
        }, saveExpandir: function () {

            var form = $("#frmExpandirEvals");

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea expandir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/saveExpandir'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    dynatable.process();
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
        }, aceptarAsignacion: function () {

            var form = $("#frmAsignarDocente");

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea expandir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/saveAsignarDocente'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    //   dynatable.process();
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

        , aceptarExpansion: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmAccept,
                title: 'Aceptar Expansión',
                buttons: {
                    confirm: {label: 'Aceptar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/aceptarExpansion'),
                            type: 'POST',
                            async: true,
                            data: {evaluacionSeccionId: $("#txtEvalSeccionId").val()},
                            success: function (response) {
                                MODAL.hideWait();
                                MODAL.hide();
                                if (response.success) {
                                    notify(response.message, "info");
                                    location.href = APP.url("academico/docente/cargaacademica");
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
        }
    };

    $("body").delegate(".expandir-evaluacion", "click", function (e) {
        ExpandirSCN.expandirEvaluacion($(this), e);
    });

    $("body").delegate(".asignar-docente", "click", function (e) {
        ExpandirSCN.asignarDocente($(this), e);
    });

    $("body").delegate(".delete-expansion", "click", function (e) {
        ExpandirSCN.deleteEvaluacion($(this), e);
    });

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        ExpandirSCN.addTipoEvaluacion(e);
    });

    $("body").delegate(".delete-tipo-evaluacion", "click", function (e) {
        ExpandirSCN.deleteTipoEvaluacion($(this), e);
    });

    $("body").delegate(".grabar-expansion", "click", function (e) {
        ExpandirSCN.saveExpandir();
    });

    $("body").delegate(".grabar-asignacion", "click", function (e) {
        ExpandirSCN.aceptarAsignacion();
    });

    $("body").delegate("#btnAceptarExp", "click", function (e) {
        ExpandirSCN.aceptarExpansion()
    });



});
