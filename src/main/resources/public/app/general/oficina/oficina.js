$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/oficina/all'),
            perPageDefault: 10
        },
        writers: {_rowWriter: ulWriter},
        table: {bodyRowSelector: 'tbody tr'}
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {ACT: "success", INA: "default"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;

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
                async: false,
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
                            async: false,
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
                async: false,
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
        previoRetirarJefe: function (e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);
            bootbox.confirm({
                message: "¿Está seguro desea dar por finalizada la <b>Jefatura</b> de esta Unidad?",
                buttons: {
                    confirm: {label: 'Si, proceder con la finalización', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Oficina.retirarJefe(rec);
                    }
                }
            });
        },
        retirarJefe: function (rec) {
            var record = {
                id: rec.id,
                oficina: rec.nombre,
                jefe: rec.jefe,
                fechaInicioJefatura: rec.fechaInicioJefatura,
                idJefe: rec.idJefe,
                form: 'formFinJefatura'
            };

            var mimodal = bootbox.confirm({
                title: "Finalización de Jefatura",
                message: $.templates("#finJefaturaTemplate").render(record),
                buttons: {
                    confirm: {label: 'Finalizar jefatura', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var form = $("#" + record.form);
                        form.parsley().destroy();
                        form.parsley().validate();
                        if (!form.parsley().validate()) {
                            return false;
                        }

                        $.ajax({
                            url: APP.url('general/oficina/retirarJefe'),
                            type: 'POST',
                            async: false,
                            data: form.serialize(),
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

            var parts = rec.fechaInicioJefatura.split('/');
            var ayer = new Date(parts[2], parts[1] - 1, parts[0]);
            var hoy = new Date();
            console.log(hoy)
            console.log(ayer)
            var form = $("#" + record.form);
            var ff = form.find(".date");
            console.log(ff)
            ff.datepicker({maxDate: hoy, minDate: ayer});
            console.log(ff)
        },
        previoRetirarEncargado: function (e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);
            bootbox.confirm({
                message: "¿Está seguro desea dar por finalizada la <b>Encargatura</b> de esta Unidad?",
                buttons: {
                    confirm: {label: 'Si, proceder con la finalización', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Oficina.retirarEncargado(rec);
                    }
                }
            });
        },
        retirarEncargado: function (rec) {
            var record = {
                id: rec.id,
                oficina: rec.nombre,
                encargado: rec.encargado,
                fechaEncargatura: rec.fechaEncargatura,
                idEncargado: rec.idEncargado,
                form: 'formFinEncargatura'
            };

            var mimodal = bootbox.confirm({
                title: "Finalización de Encargatura",
                message: $.templates("#finEncargoTemplate").render(record),
                buttons: {
                    confirm: {label: 'Finalizar encargatura', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var form = $("#" + record.form);
                        form.parsley().destroy();
                        form.parsley().validate();
                        if (!form.parsley().validate()) {
                            return false;
                        }

                        $.ajax({
                            url: APP.url('general/oficina/retirarEncargado'),
                            type: 'POST',
                            async: false,
                            data: form.serialize(),
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

            var form = $("#" + record.form);
            form.find(".date").datepicker();
        },
        asignarJefe: function (e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);
            if (rec.cargoJefe == null) {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            var record = {
                id: rec.id,
                oficina: rec.nombre,
                form: 'formJefe',
                select2Persona: 'select2Persona',
                textTitulo: 'textTitulo'
            };

            var mimodal = bootbox.confirm({
                title: "Asignar Jefe de la Unidad",
                message: $.templates("#jefeTemplate").render(record),
                buttons: {
                    confirm: {label: 'Asignar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var form = $("#" + record.form);
                        form.parsley().destroy();
                        form.parsley().validate();
                        $("#" + record.select2Persona).parsley().validate();
                        if ($("#" + record.select2Persona).select2("val") == "" || !form.parsley().validate()) {
                            return false;
                        }
                        Oficina.ajaxAsignarJefe(form, mimodal);
                        return false;
                    }
                }
            });

            var form = $("#" + record.form);
            form.find(".date").datepicker();
            $("#" + record.select2Persona).select2(Oficina.findPersona());
        },
        previoAsignarEncargado: function (e) {
            e.preventDefault();
            var rec = APP.recDynatable(dynatable, e);
            if (rec.cargoJefe == null) {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            if (rec.jefe != null) {
                Oficina.asignarEncargado(rec);
                return;
            }

            bootbox.confirm({
                message: "Esta Unidad no tiene asignado el jefe oficial, ¿Está seguro que de todos modos desea asignar un <b>Jefe Encargado</b>?",
                buttons: {
                    confirm: {label: 'Si, proceder con asignación', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Oficina.asignarEncargado(rec);
                    }
                }
            });

        },
        asignarEncargado: function (rec) {
            var record = {
                id: rec.id,
                oficina: rec.nombre,
                form: 'formEncargado',
                select2Persona: 'select2Persona',
                textTitulo: 'textTitulo',
                existeJefe: rec.jefe != null
            };

            var mimodal = bootbox.confirm({
                title: "Asignar Encargado de la jefatura de la Unidad",
                message: $.templates("#encargadoTemplate").render(record),
                buttons: {
                    confirm: {label: 'Asignar encargado', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var form = $("#" + record.form);
                        form.parsley().destroy();
                        form.parsley().validate();
                        $("#" + record.select2Persona).parsley().validate();
                        if ($("#" + record.select2Persona).select2("val") == "" || !form.parsley().validate()) {
                            return false;
                        }
                        Oficina.ajaxAsignarEncargado(form.serialize());
                    }
                }
            });

            $("#" + record.select2Persona).select2(Oficina.findPersona());
            var form = $("#" + record.form);
            form.find(".date").datepicker();

        },
        ajaxAsignarJefe: function (form, mimodal) {
            $.ajax({
                url: APP.url('general/oficina/asignarJefe'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        dynatable.process();
                        mimodal.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        ajaxAsignarEncargado: function (parametros) {
            $.ajax({
                url: APP.url('general/oficina/asignarEncargado'),
                type: 'POST',
                async: false,
                data: parametros,
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
                    var html = '<span class="block bold">' + info.nombre + '</span>';
                    if (info.dni) {
                        html += '<small>' + info.tipo + ' ' + info.dni + '</small>';
                    }

                    return html;
                },
                formatSelection: function (info) {
                    Oficina.formTituloAcademico(info);
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        formTituloAcademico: function (info) {
            $("#textTitulo").val(info.titulo);
        }
    };

    Oficina.body.delegate(".asignar-jefe", "click", function (e) {
        Oficina.asignarJefe(e);
    });

    Oficina.body.delegate(".asignar-encargado", "click", function (e) {
        Oficina.previoAsignarEncargado(e);
    });

    Oficina.body.delegate(".retirar-jefe", "click", function (e) {
        Oficina.previoRetirarJefe(e);
    });

    Oficina.body.delegate(".retirar-encargado", "click", function (e) {
        Oficina.previoRetirarEncargado(e);
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