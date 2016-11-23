$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/systemcalifica/sistema/list'),
            perPageDefault: 10
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
        var html = $.templates("#templateSistema").render(record);
        return html;
    }

    Sistema = {
        verNuevoSistema: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/systemcalifica/sistema/nuevo");
        },
        verEditarSistema: function ($this, e) {
            e.preventDefault();
        },
        verDetalleSistema: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación " + rec.codigo);
            MODAL.show();
            /*
             MODAL.buttons(
             '<a class="btn btn-success">Aprobar</a>' +
             '<a class="btn btn-warning">Observar</a>' +
             '<a class="btn btn-danger">Rechazar</a>');
             */
            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/' + rec.id + '/detalleSistema'),
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
        verSolicitud: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            MODAL.init("lg");
            MODAL.title('Solicitud de creación: <stron>Sistema de Calificación ' + rec.codigo + '</strong>');
            //MODAL.buttons();
            MODAL.show();
            MODAL.buttons(
                    '<a class="btn btn-success aprobar">Aprobar</a>' +
                    '<a class="btn btn-warning observar">Observar</a>' +
                    '<a class="btn btn-danger rechazar">Rechazar</a>');
            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/' + rec.id + '/detalleSolicitud'),
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
        asignarCursos: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            location.href = APP.url('academico/systemcalifica/sistema/' + rec.id + '/cursos');
        },
        aprobar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmApprove,
                title: 'Aprobar Sistema Calificación',
                buttons: {
                    confirm: {label: 'Aprobar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/aprobar'),
                            type: 'POST',
                            async: true,
                            data: {sistema: $("#txtPlanCalificacion").val()},
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
        observar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmObserve,
                title: 'Observar Sistema Calificación',
                buttons: {
                    confirm: {label: 'Observar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/observar'),
                            type: 'POST',
                            async: true,
                            data: {
                                sistema: $("#txtPlanCalificacion").val(),
                                comentario: $("#txtComentario").val()
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
        rechazar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmReject,
                title: 'Rechazar Sistema Calificación',
                buttons: {
                    confirm: {label: 'Rechazar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/rechazar'),
                            type: 'POST',
                            async: true,
                            data: {sistema: $("#txtPlanCalificacion").val()},
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
        activar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmActive,
                title: 'Activar Sistema Calificación',
                buttons: {
                    confirm: {label: 'Activar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/activar'),
                            type: 'POST',
                            async: true,
                            data: {sistema: el.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
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
        inactivar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmActive,
                title: 'Inactivar Sistema Calificación',
                buttons: {
                    confirm: {label: 'Activar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/inactivar'),
                            type: 'POST',
                            async: true,
                            data: {sistema: el.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
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
        anull: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmActive,
                title: 'Activar Grupo',
                buttons: {
                    confirm: {label: 'Activar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/systemcalifica/sistema/anull'),
                            type: 'POST',
                            async: true,
                            data: {sistema: el.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
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
    $("body").delegate(".nuevo-sistema", "click", function (e) {
        Sistema.verNuevoSistema(e);
    });
    $("body").delegate(".caso1-sistema", "click", function (e) {
        Sistema.verEditarSistema($(this), e);
    });
    $("body").delegate(".detalle-sistema", "click", function (e) {
        Sistema.verDetalleSistema($(this), e);
    });
    $("body").delegate(".ver-solicitud", "click", function (e) {
        Sistema.verSolicitud($(this), e);
    });
    $("body").delegate(".asignar-cursos", "click", function (e) {
        Sistema.asignarCursos($(this), e);
    });
    $("body").delegate(".aprobar", "click", function () {
        Sistema.aprobar($(this));
    });
    $("body").delegate(".observar", "click", function () {
        Sistema.observar($(this));
    });
    $("body").delegate(".rechazar", "click", function () {
        Sistema.rechazar($(this));
    });
    $("body").delegate(".activar", "click", function () {
        Sistema.activar($(this));
    });
    $("body").delegate(".inactivar", "click", function () {
        Sistema.inactivar($(this));
    });
    $("body").delegate(".desaprobar", "click", function () {
        Sistema.desaprobar($(this));
    });
    $("body").delegate(".anull", "click", function () {
        Sistema.anull($(this));
    });
});

