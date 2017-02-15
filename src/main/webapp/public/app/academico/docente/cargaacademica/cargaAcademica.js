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
        var grupoHoras = record.grupoHoras != "" ? record.grupoHoras.split(", ") : "";
        var seccionesResult = "";

        for (var i = 0; i < secciones.length; i++) {
            seccionesResult += '<div class="col-md-4"><a href="#" ';
            if (record.estado == 'ACEP') {
                seccionesResult += 'class="notas-academicas"';
            }
            var grupoText = grupoHoras != "" ? (' - ' + grupoHoras[i].split("|")[1]) : "";
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
                        '<a class="btn btn-danger new-sis-calificacion">Solicita modificación</a>');
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
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
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
            var rec = dynatable.settings.dataset.records[idx];

            location.href = APP.url('academico/docente/cargaacademica/') + idx + '/notasAcademicas';
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
    $("body").delegate(".new-sis-calificacion", "click", function (e) {
        CargaAcademica.verNuevoSC(e);
    });

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        CargaAcademica.addTipoEvaluacion(e);
    });


});
