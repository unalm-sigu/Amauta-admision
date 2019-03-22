$(function () {

    var Oficina = {
        init: function () {},
        body: $("body"),
        form: {},
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

//    Oficina.body.delegate(".asignar-jefe", "click", function (e) {
//        Oficina.asignarJefe(e);
//    });
//
//    Oficina.body.delegate(".asignar-encargado", "click", function (e) {
//        Oficina.previoAsignarEncargado(e);
//    });
//
//    Oficina.body.delegate(".retirar-jefe", "click", function (e) {
//        Oficina.previoRetirarJefe(e);
//    });
//
//    Oficina.body.delegate(".retirar-encargado", "click", function (e) {
//        Oficina.previoRetirarEncargado(e);
//    });
//
//    Oficina.body.delegate(".delete", "click", function (e) {
//        Oficina.eliminar(e);
//    });
//
//    Oficina.body.delegate(".colaborador", "click", function (e) {
//        Oficina.colaborador(e);
//    });
//
//    Oficina.body.delegate(".estado", "click", function (e) {
//        Oficina.estado(e);
//    });
    //*/
});

new Vue({
    el: '#pageOficinasVUE',
    data: {
        oficinasURL: APP.url(rutaModulo + '/all'),
        oficina: {personaJefe: {}},
        cfgColaboradores: {
            id: 'colaboradoresModal',
            header: true,
            title: 'Relación de colaboradores',
            showaccept: false,
            cancelbtn: 'Aceptar'
        },
        configModalAsignarJefe: {
            id: 'idModalAsignarJefe',
            header: true,
            title: 'Asignar jefe de oficina',
            cancelbtn: 'Aceptar'
        },
        colaboradores: [],
        modalBootbox: {},
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        }
    },
    mounted() {

    },
    methods: {
        classEstado(item) {
            var classesEstado = {ACT: "label-success", INA: "label-danger"};
            return classesEstado[item.estado];
        },
        urlNuevaOficina() {
            let $vue = this;
            return APP.url(rutaModulo + '/nuevo') + $vue.getOrigenURL();
        },
        urlEditar(item) {
            let $vue = this;
            return APP.url(rutaModulo + "/" + item.id + '/update') + $vue.getOrigenURL();
        },
        urlColaboradores(item) {
            let $vue = this;
            return APP.url('general/oficina/' + item.id + '/colaboradores') + $vue.getOrigenURL();
        },
        cambiarEstado(item, accion) {
            let $vue = this;
            let title = "Cambiar estado de la Oficina";
            let msg = "¿Desea cambiar el estado de la oficina?";
            let classBtn = "btn-info";
            let labelBtn = "Cambiar Estado";

            if (accion == "activar") {
                title = "Activar oficina";
                msg = "¿Está seguro que desea activar esta oficina?";
                classBtn = "btn-success";
                labelBtn = "Si, activar";
            } else if (accion == "desactivar") {
                title = "Desactivar oficina";
                msg = "¿Está seguro que desea desactivar esta oficina?";
                classBtn = "btn-warning";
                labelBtn = "Si, desactivar";
            } else if (accion == "eliminar") {
                title = "Eliminar oficina";
                msg = "¿Está seguro que desea eliminar esta oficina?";
                classBtn = "btn-danger";
                labelBtn = "Si, eliminar";
            }

            $vue.modalBootbox = bootbox.confirm({
                title: title,
                size: 'small',
                message: msg,
                buttons: {
                    confirm: {label: labelBtn, className: classBtn},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $vue.saveEstado(item, accion);
                    } else {
                        $vue.modalBootbox.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado(item, accion) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/oficina/cambiarEstado/' + accion),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify(item),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptorOficinas.loadRemoteData();
                        $vue.modalBootbox.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        verColaboradores(item) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/oficina/allColaborador'),
                type: 'POST',
                async: false,
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.cfgColaboradores.title = "Colaboradores: " + item.nombre;
                        $vue.colaboradores = response.data;
                        $vue.$refs.colaboradoresModal.open();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        asignarJefe(item) {
            let $vue = this;
            if (item.cargoJefe.nombre == '') {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }
            
            $vue.oficina = Object.assign({}, item, {});
            $vue.$refs.modalAsignarJefe.open();

            return;

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
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
    }
});
    