$(function () {

    var permiteAsig = $("#txtPermiteAsig").val();
    var evaluacionPlanes;

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('docente/notasacademica/listEvaluacionPlan'),
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
        record.evaluacionPlan = evaluacionPlanes[record.tipoEvalCod];
        record.classArg = 'porcentajes-variables';
        if (record.esHijo) {
            record.styleHijo = 'padding-left:60px;';
        } else if (record.esNieto) {
            record.styleHijo = 'padding-left:120px;';
        }
        if (record.esAbuelo) {
            record.styleAbuelo = 'font-weight:bold;font-size:15px !important;';
        }
        /*
         if (record.porcentajeFail) {
         record.stryleRow = 'text-decoration:line-through !important;font-color:red !important;';
         } else {
         record.stryleRow = '';
         }
         */
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
                url: APP.url('docente/notasacademica/detalleExpandirEvaluacion'),
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        anularEvaluacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");

            bootbox.confirm({
                message: "¿Está seguro que desea anular la evaluación?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Anular", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('docente/cargaacademica/anularEvaluacionExp'),
                            type: 'POST',
                            async: false,
                            data: {
                                evaluacion: idx
                            },
                            success: function (response) {
                                if (response.success) {
                                    dynatable.process();
                                    notify(response.message, "info");
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
                url: APP.url('docente/notasacademica/detalleAsignarDocente'),
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
                    notify(Messages.errorComunicacion, "error");
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
                            url: APP.url('docente/cargaacademica/deleteExpansionHija'),
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
                                notify(Messages.errorComunicacion, "error");
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
                            url: APP.url('docente/notasacademica/validarEvaluacionesIngresadas'),
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
                            error: function (error) {
                                console.dir(error);
                                notify(Messages.errorComunicacion, "error");
                            }
                        });

                    }
                }
            });
        },
        saveExpandir: function (e) {
            e.preventDefault();
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
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('docente/notasacademica/saveExpandir'),
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
                                MODAL.hideWait();
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
        aceptarAsignacion: function (e) {
            e.preventDefault();
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
                            url: APP.url('docente/notasacademica/saveAsignarDocente'),
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
                                notify(Messages.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        },
        aceptarExpansion: function (el) {
            el.preventDefault();
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
                            url: APP.url('docente/cargaacademica/aceptarExpansion'),
                            type: 'POST',
                            async: true,
                            data: {evaluacionSeccionId: $("#txtEvalSeccionId").val()},
                            success: function (response) {
                                MODAL.hideWait();
                                MODAL.hide();
                                if (response.success) {
                                    notify(response.message, "info");
                                    location.href = APP.url("docente/cargaacademica");
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
                            url: APP.url('docente/notasacademica/cambiarTipoSecEval'),
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
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        cancelarExpansion: function (e) {
            e.preventDefault();
            location.href = APP.url("docente/cargaacademica");
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
                url: APP.url('docente/cargaacademica/aceptarExpandir'),
                type: 'POST',
                async: false,
                data: JSON.stringify(jsonObj),
                dataType: "json",
                contentType: "application/json",
                success: function (response) {


                    MODAL.showWait("Espere un momento por favor");
                    $.ajax({
                        url: APP.url('docente/cargaacademica/cambiarTipoSecEval'),
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
                            notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
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
        ExpandirSCN.saveExpandir(e);
    });

    $("body").delegate(".grabar-asignacion", "click", function (e) {
        ExpandirSCN.aceptarAsignacion(e);
    });

    $("body").delegate("#btnAceptarExp", "click", function (e) {
        ExpandirSCN.aceptarExpansion(e)
    });

    $("body").delegate(".cboTipoSecEval", "change", function () {
        ExpandirSCN.cambiarTipoSecEval($(this));
    });

    $("body").delegate(".cancelarExpansion", "click", function (e) {
        ExpandirSCN.cancelarExpansion(e);
    });

    $("body").delegate("#btnAceptarExpandir", "click", function (e) {
        ExpandirSCN.aceptarExpandir(e);
    });

    $("body").delegate(".anular-evaluacion-exp", "click", function (e) {
        ExpandirSCN.anularEvaluacion($(this), e);
    });


});
