$(function () {

    var permiteAsig = $("#txtPermiteAsig").val();
    var evaluacionPlanes;

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

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        $(".chkAnularNotaMinCls").each(function () {
            if ($(this).attr('rel') == $(this).attr('value')) {
                $(this).attr('checked', true);
            }
        });
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", APR: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        record.permiteAsign = (permiteAsig == "true");
        record.evaluacionPlan = evaluacionPlanes[record.tipoEvalCod];
        record.classArg = 'porcentajes-variables';
        if (record.esHijo) {
            record.styleHijo = 'padding-left:60px;';
        } else if (record.esNieto) {
            record.styleHijo = 'padding-left:120px;';
        }
        var docentes = "";
        if (!record.esPadre) {
            $.each(record.evaluadores, function (i, item) {
                docentes += '<span class="block m-b-xxs">';
                docentes += '<span class="label label-' + (item.docente == "" ? "danger" : "success") + '">' + item.seccion + '</span> ';
                docentes += '<small>&nbsp;&nbsp;' + item.docente + '</small>';
                docentes += '</span>';
            });
        }
        record.docentes = docentes;
        if (record.editarPorcentaje == true) {
            $("#btnAceptarExpandir").css("display", "");
        }
        var html = $.templates("#templateEvaluacionPlan").render(record);
        return html;
    }

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        $('select').select2();
    });

    ExpandirSCN = {
        init: function () {
            evaluacionPlanes = JSON.parse($("#txtEvalPlan").val());
            $.each(evaluacionPlanes, function (key, value) {
                evaluacionPlanes[key] = JSON.parse(value);
            });

            $("#btnAceptarExpandir").css("display", "none");
        },
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
                    $(".item-select2").select2();
                    $(".item-select2").each(function () {
                        $(this).removeClass("item-select2");
                    });
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
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
                    $(".item-select2").select2();
                    $(".item-select2").each(function () {
                        $(this).removeClass("item-select2");
                    });
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
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/validarEvaluacionesIngresadas'),
                            type: 'POST',
                            async: true,
                            data: {evalExp: $this.attr('rel')},
                            success: function (response) {
                                if (response.success) {
                                    tr.remove();
                                } else {
                                    console.dir(response)
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
        saveExpandir: function () {

            var form = $("#frmExpandirEvals");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $('#tbodyEvaluaciones tr').each(function (i, tr) {
                var tr = $(this);
                tr.find("input, select, textarea").each(function (idx) {
                    var nameInp = $(this).attr("name");
                    if (typeof nameInp !== "undefined") {
                        $(this).attr("name", ExpandirSCN.reindexNameForm(nameInp, i));
                    }
                });
            });

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
        },
        aceptarAsignacion: function () {

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
        },
        aceptarExpansion: function (el) {
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
        },
        cambiarTipoSecEval: function ($this) {
            bootbox.confirm({
                message: MESSAGES.confirmAccept,
                title: 'Aceptar Cambio Seccion',
                buttons: {
                    confirm: {label: 'Aceptar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/cambiarTipoSecEval'),
                            type: 'POST',
                            async: true,
                            data: {
                                tipoSeccionEval: $this.val(),
                                evaluacionExp: $this.attr("rel")
                            }
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
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        cambiarAnularNotaMin: function ($this) {
            var tr = $this.closest("tr");
            var evaPlanId = tr.attr("rel");
            var checked = $this.prop('checked');
            checked = (checked == true) ? 1 : 0;

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/cambiarAnularNotaMinima'),
                type: 'POST',
                async: true,
                data: {
                    notaMinimaAnulable: checked,
                    evaluacionExp: evaPlanId
                }
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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        cancelarExpansion: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/docente/cargaacademica");
        },
        aceptarExpandir: function (e) {
            var form = $("#frmExpansion");

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            var jsonObj = [];
            $("input[name='porcentajes-variables']").each(function () {
                $(this).attr("data-parsley-whitespace", "trim");
                $(this).attr("required", true);

                var id = $(this).attr("rel");
                var peso = $(this).val();

                var item = {};

                item["id"] = id;
                item["peso"] = peso;
                item["evaluacionSeccion"] = {
                    id: $("#txtEvalSeccionId").val()
                };

                jsonObj.push(item);
            });
            console.dir(jsonObj)
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/aceptarExpandir'),
                type: 'POST',
                async: false,
                data: JSON.stringify(jsonObj),
                dataType: "json",
                contentType: "application/json",
                success: function (response) {


                    MODAL.showWait("Espere un momento por favor");
                    $.ajax({
                        url: APP.url('academico/docente/cargaacademica/cambiarTipoSecEval'),
                        type: 'POST',
                        async: true,
                        data: {
                            tipoSeccionEval: $this.val(),
                            evaluacionExp: $this.attr("rel")
                        }
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
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                    /*
                     if (response.success) {
                     notify(response.message, "info");
                     
                     } else {
                     notify(response.message, "error");
                     }
                     */
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }
    };

    ExpandirSCN.init();

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

    $("body").delegate(".cboTipoSecEval", "change", function () {
        ExpandirSCN.cambiarTipoSecEval($(this));
    });

    $("body").delegate(".chkAnularNotaMinCls", "change", function () {
        ExpandirSCN.cambiarAnularNotaMin($(this));
    });

    $("body").delegate(".cancelarExpansion", "click", function (e) {
        ExpandirSCN.cancelarExpansion(e);
    });

    $("body").delegate("#btnAceptarExpandir", "click", function (e) {
        ExpandirSCN.aceptarExpandir(e);
    });


});
