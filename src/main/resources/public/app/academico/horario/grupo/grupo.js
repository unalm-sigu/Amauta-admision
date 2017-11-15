$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/horario/grupo/list'),
            perPageDefault: 6,
            ajaxData: {idTipoGrupo: $('[name="idTipoGrupo"]').val()},
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'div'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {ACT: 'success', INA: 'danger'};
        var labelName = {ACT: 'Activo', INA: 'Inactivo'};
        record.colorEstado = labelColor[record.estado];
        record.nameEstado = labelName[record.estado];
        var html = $.templates("#grupoTemplate").render(record);
        var outerHTML = $(html).prop('outerHTML');
        return outerHTML;
    }

    var Grupo = {
        form: null,
        body: $('body'),
        init: function () {
        },
        grupoActivo: null,
        update: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr('rel');

            var mimodal = bootbox.confirm({
                title: "Editar Grupo Horas",
                message: APP.template.spincenter,
                buttons: {
                    confirm: {label: "Guardar", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find('form').parsley().validate() == true) {
                            Grupo.saveGrupo(mimodal);
                        }
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
            $.ajax({
                url: APP.url('academico/horario/grupo/update'),
                type: 'POST',
                async: true,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        mimodal.find('.bootbox-body').html(response.data);
                        mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('[name="tipoSeccion"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('.cp').colorpicker({color: '#4116ff'});
                        mimodal.find('[name="tipoGrupoHoras.id"]').val($("[name=idTipoGrupo]").val());
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
                            Grupo.saveGrupo(mimodal);
                        }
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
            $.ajax({
                url: APP.url('academico/horario/grupo/nuevo'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        mimodal.find('.bootbox-body').html(response.data);
                        mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('[name="tipoSeccion"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('.cp').colorpicker({color: '#4116ff'});
                        mimodal.find('[name="tipoGrupoHoras.id"]').val($("[name=idTipoGrupo]").val());
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
        saveGrupo: function (mimodal) {
            $.ajax({
                url: APP.url('academico/horario/grupo/save'),
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
        eliminar: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el grupo horas?",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/horario/grupo/delete'),
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
        verhorario: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            this.toggleActivo(self);
            Grupo.grupoActivo = id;
            this.getHorario(id);
        },
        toggleActivo: function (self) {
            var activo = $(".list-group-item.grupoactivo");
            if (activo.length > 0) {
                activo.removeClass("grupoactivo");
            }
            var patter = self.parents(".list-group-item:first");
            patter.addClass("grupoactivo");
        },
        getHorario: function () {
            $.ajax({
                url: APP.url('academico/horario/grupo/horario'),
                type: 'POST',
                async: false,
                data: {id: Grupo.grupoActivo},
                success: function (response) {
                    if (response.success) {
                        $("#tablaHorario").html(response.data);
                    } else {
                        notify(response.message, "error");
                        $("#tablaHorario").html('');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });
        },
        asignarHora: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var hora = self.attr("rel");
            var dia = self.attr("rev");
            $.ajax({
                url: APP.url('academico/horario/grupo/asignarHora'),
                type: 'POST',
                async: false,
                data: {
                    'hora.id': hora,
                    'dia.id': dia,
                    'grupoHorario.id': Grupo.grupoActivo
                },
                success: function (response) {
                    if (response.success) {
                        Grupo.getHorario();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });
        }
    };

    Grupo.body.delegate('#nuevo', 'click', function (e) {
        Grupo.nuevo(e);
    });

    Grupo.body.delegate('.eliminar', 'click', function (e) {
        Grupo.eliminar(e);
    });

    Grupo.body.delegate('.estado', 'click', function (e) {
        Grupo.estado(e);
    });

    Grupo.body.delegate('.editar', 'click', function (e) {
        Grupo.update(e);
    });

    Grupo.body.delegate('.verhorario', 'click', function (e) {
        Grupo.verhorario(e);
    });

    Grupo.body.delegate('.asignar-hora', 'dblclick', function (e) {
        Grupo.asignarHora(e);
    });

    Grupo.init();

});
