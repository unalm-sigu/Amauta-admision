$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/oficina/all'),
            perPageDefault: 10
        },
        writers: {_rowWriter: ulWriter},
        features: {pushState: false},
        table: {bodyRowSelector: 'tbody tr'}
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        record.index = rowIndex;
        var colorEstado = {ACT: "success", INA: "default"};
        var nameEstado = {ACT: "Activo", INA: "Inactivo"};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#oficinaTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Oficina = {
        init: function () {},
        body: $("body"),
        form: {},
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            Oficina.form.id = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                size: 'small',
                message: '¿Desea cambiar el estado de la oficina?',
                buttons: {
                    confirm: {label: "Cambiar Estado", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Oficina.saveEstado(mimodal);
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('general/oficina/estado'),
                type: 'POST',
                async: true,
                data: Oficina.form,
                success: function (response) {
                    if (response.success) {
                        dynatable.process();
                        mimodal.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la oficina?",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/oficina/delete'),
                            type: 'POST',
                            async: true,
                            data: {id: id},
                            success: function (response) {
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
        colaborador: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            var mialert = bootbox.alert({
                message: "¿Está seguro que desea eliminar la oficina?",
                title: "Colaboradores",
                buttons: {
                    ok: {label: 'Cerrar', className: "btn-primary"},
                },
                callback: function (result) {
                    if (result) {
                    }
                }
            });
            $.ajax({
                url: APP.url('general/oficina/allColaborador'),
                type: 'POST',
                async: true,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        var html = $.templates("#colaboradorTemplate").render(response.data);
                        mialert.find('.bootbox-body').html(html);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        retirarJefe: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea retirar el jefe de la oficina?",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Retirar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/oficina/retirarJefe'),
                            type: 'POST',
                            async: true,
                            data: {id: id},
                            success: function (response) {
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
        retirarEncargado: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            var officina = {};
            officina.id = id;
            var mimodal = bootbox.confirm({
                message: "¿Está seguro que desea retirar el encargado de la oficina?",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Retirar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find("[name='motivo']").parsley().validate() != true) {
                            return false;
                        }
                        officina['motivo'] = mimodal.find("[name='motivo']").val();
                        $.ajax({
                            url: APP.url('general/oficina/retirarEncargado'),
                            type: 'POST',
                            async: true,
                            data: officina,
                            success: function (response) {
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
            var html = $.templates("#jefeMotivoTemplate").render({});
            mimodal.find('.bootbox-body').append(html);
        },
        asignarJefe: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            var officina = {};
            officina['id'] = id;
            var mimodal = bootbox.confirm({
                title: "Asignar Jefe",
                message: "Seleccione un usuario  para asignar como jefe.",
                buttons: {
                    confirm: {label: 'Sí, Aceptar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find("[name='persona']").parsley().validate() == true) {
                            officina['personaJefe.id'] = mimodal.find("[name='persona']").select2('val');
                            Oficina.ajaxAsignarJefe(officina);
                            return true;
                        }
                        return false;
                    }
                }
            });
            var html = $.templates("#jefeTemplate").render({});
            mimodal.find('.bootbox-body').append(html);
            mimodal.find("[name='persona']").select2(Oficina.findPersona());
        },
        asignarEncargado: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            var officina = {};
            officina.id = id;
            var mimodal = bootbox.confirm({
                title: "Asignar Encargado",
                message: "Seleccione un usuario  para asignar como encargado.",
                buttons: {
                    confirm: {label: 'Sí, Aceptar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find("[name='persona']").parsley().validate() != true) {
                            return false;
                        }
                        if (mimodal.find("[name='motivo']").parsley().validate() != true) {
                            return false;
                        }
                        officina['jefeEncargado.id'] = mimodal.find("[name='persona']").select2('val');
                        officina['motivo'] = mimodal.find("[name='motivo']").val();
                        Oficina.ajaxAsignarEncargado(officina);
                    }
                }
            });
            var html = $.templates("#jefeTemplate").render({});
            mimodal.find('.bootbox-body').append(html);
            var htmlMotivo = $.templates("#jefeMotivoTemplate").render({});
            mimodal.find('.bootbox-body>.panel-body').append(htmlMotivo);
            mimodal.find("[name='persona']").select2(Oficina.findPersona());

        },
        ajaxAsignarJefe: function (officina) {
            $.ajax({
                url: APP.url('general/oficina/asignarJefe'),
                type: 'POST',
                async: true,
                data: officina,
                success: function (response) {
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
        ajaxAsignarEncargado: function (officina) {
            $.ajax({
                url: APP.url('general/oficina/asignarEncargado'),
                type: 'POST',
                async: true,
                data: officina,
                success: function (response) {
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
        findPersona: function () {
            return {
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("general/oficina/allPersona"),
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
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), titulo: element.attr("rev")});
                    }
                },
                formatResult: function (info) {
                    return '<b>' + info.titulo + '</b>   ' + info.nombre;
                },
                formatSelection: function (info) {
                    return '<b>' + info.titulo + '</b>   ' + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    };

    Oficina.body.delegate(".asignar-jefe", "click", function (e) {
        Oficina.asignarJefe(e);
    });

    Oficina.body.delegate(".asignar-encargado", "click", function (e) {
        Oficina.asignarEncargado(e);
    });

    Oficina.body.delegate(".retirar-jefe", "click", function (e) {
        Oficina.retirarJefe(e);
    });

    Oficina.body.delegate(".retirar-encargado", "click", function (e) {
        Oficina.retirarEncargado(e);
    });


    Oficina.body.delegate(".delete", "click", function (e) {
        Oficina.eliminar(e);
    });

    Oficina.body.delegate(".colaborador", "click", function (e) {
        Oficina.colaborador(e);
    });

    Oficina.body.delegate(".estado", "click", function (e) {
        Oficina.estado(e);
    });

});