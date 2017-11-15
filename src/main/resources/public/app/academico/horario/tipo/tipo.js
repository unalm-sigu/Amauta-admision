$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/horario/list'),
            perPageDefault: 12
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'div'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var labelColor = {ACT: 'success', DES: 'danger', CRE: "default"};
        var labelName = {ACT: 'Activo', CRE: "Creado", DES: "Desactivado"};
        record.colorEstado = labelColor[record.estado];
        record.nameEstado = labelName[record.estado];

        var labelColorGrupos = {COM: 'success', INC: 'danger'};
        var labelNameGrupos = {COM: 'Completo', INC: 'Incompleto'};
        record.colorEstadoGrupos = labelColorGrupos[record.estadoGrupos];
        record.nameEstadoGrupos = labelNameGrupos[record.estadoGrupos];

        var html = $.templates("#tipoGrupoTemplate").render(record);
        var outerHTML = $(html).prop('outerHTML');
        return outerHTML;
    }

    var TipoGrupo = {
        form: {},
        body: $('body'),
        init: function () {
        },
        update: function (e) {


            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr('rel');

            var mimodal = bootbox.confirm({
                title: "Editar Tipo Grupo Horas",
                message: APP.template.spincenter,
                buttons: {
                    confirm: {label: "Guardar", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find('form').parsley().validate() == true) {
                            console.log(mimodal.find('form').parsley().validate());
                            TipoGrupo.saveTipoGrupo(mimodal);
                        }
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
            $.ajax({
                url: APP.url('academico/horario/update'),
                type: 'POST',
                async: true,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        mimodal.find('.bootbox-body').html(response.data);
                        mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                    } else {
                        notify(response.message, "error");
                        mimodal.modal('hide');
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        nuevo: function (e) {
            var mimodal = bootbox.confirm({
                title: "Nuevo Tipo Grupo Horas",
                message: APP.template.spincenter,
                buttons: {
                    confirm: {label: "Guardar", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find('form').parsley().validate() == true) {
                            console.log(mimodal.find('form').parsley().validate());
                            TipoGrupo.saveTipoGrupo(mimodal);
                        }
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
            $.ajax({
                url: APP.url('academico/horario/nuevo'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        mimodal.find('.bootbox-body').html(response.data);
                        mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                    } else {
                        notify(response.message, "error");
                        mimodal.modal('hide');
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        opengroup: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr('rel');
            location.replace(APP.url('academico/horario/grupo/' + id));
        },
        saveTipoGrupo: function (mimodal) {
            $.ajax({
                url: APP.url('academico/horario/save'),
                type: 'POST',
                async: true,
                data: mimodal.find('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        if (response.data.existecodigo) {
                            var inputCodigo = mimodal.find('[name="codigo"]').parsley();
                            window.ParsleyUI.removeError(inputCodigo, "errorValidacionCodigo");
                            window.ParsleyUI.addError(inputCodigo, "errorValidacionCodigo", "Código ya registrado");
                        } else {
                            dynatable.process();
                            mimodal.modal('hide');
                        }
                    } else {
                        notify(response.message, "error");
                        mimodal.modal('hide');
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            TipoGrupo.form['id'] = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                size: 'small',
                message: '¿Desea cambiar el estado del tipo de grupo horas?',
                buttons: {
                    confirm: {label: "Cambiar Estado", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        TipoGrupo.saveEstado(mimodal);
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
                data: TipoGrupo.form,
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
                message: "¿Está seguro que desea eliminar el tipo de grupo horas?",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/horario/delete'),
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
        }
    };
    TipoGrupo.body.delegate('#nuevo', 'click', function (e) {
        TipoGrupo.nuevo(e);
    });
    TipoGrupo.body.delegate('.eliminar', 'click', function (e) {
        TipoGrupo.eliminar(e);
    });
    TipoGrupo.body.delegate('.estado', 'click', function (e) {
        TipoGrupo.estado(e);
    });
    TipoGrupo.body.delegate('.editar', 'click', function (e) {
        TipoGrupo.update(e);
    });
    TipoGrupo.body.delegate('.opengroup', 'click', function (e) {
        TipoGrupo.opengroup(e);
    });
    TipoGrupo.init();

});
