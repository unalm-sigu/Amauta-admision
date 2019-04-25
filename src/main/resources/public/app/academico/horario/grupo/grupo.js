$(function () {
    /* 
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/horario/grupo/list'),
            perPageDefault: 12,
            ajaxData: {idTipoGrupo: $('[name="idTipoGrupo"]').val()},
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'div'
        },
        features: {pushState: false, search: true},
        inputs: {
            processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
        }
    }).data('dynatable');

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        Grupo.foccusActivo();
    });
    

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
                        mimodal.find('[name="letra"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('[name="conHorario"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('.cp').colorpicker();
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
            e.preventDefault();

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
                        mimodal.find('[name="letra"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('[name="conHorario"]').select2({minimumResultsForSearch: -1});
                        mimodal.find('.cp').colorpicker({color: '#000'});
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
                            Grupo.grupoActivo = null;
                            $("#tablaHorario").html('');
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
                                    if (Grupo.grupoActivo == id) {
                                        Grupo.grupoActivo = null;
                                    }
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
            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            this.toggleActivo(self);
            Grupo.grupoActivo = id;

            this.getHorario(id);
            mibox.modal('hide');
        },
        toggleActivo: function (self) {
            var activo = $(".list-group-item.grupoactivo");
            if (activo.length > 0) {
                activo.removeClass("grupoactivo");
            }
            var patter = self.parents(".list-group-item:first");
            patter.addClass("grupoactivo");
        },
        foccusActivo: function () {
            var activo = $(".list-group-item.grupoactivo");
            if (activo.length < 1) {
                if (Grupo.grupoActivo != null) {
                    var patter = $(".verhorario[rel=" + Grupo.grupoActivo + "]").parents(".list-group-item:first");
                    patter.addClass("grupoactivo");
                }
            }
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
            if (Grupo.grupoActivo == null) {
                return;
            }
            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
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
                        //                        dynatable.process();
                    } else {
                        notify(response.message, "error");
                    }
                    mibox.modal('hide');
                },
                error: function () {
                    mibox.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });
        },
        desasignarHora: function ($this, e) {
            e.preventDefault();
            var letra = $this.find(".text-item-horario").text();
            var color = $this.css('border-color');

            if (letra != '' && color == 'rgb(203, 213, 221)') {
                notify("Esta hora - día se encuentra ocupada.", "error")
                return;
            }

            if (Grupo.grupoActivo == null) {
                return;
            }

            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
            var self = $(e.currentTarget);
            var id = self.data("id");
            var grupo = self.data("idgrupo");
            if (Grupo.grupoActivo != grupo) {
                return;
            }
            $.ajax({
                url: APP.url('academico/horario/grupo/desasignarHora'),
                type: 'POST',
                async: false,
                data: {
                    'id': id
                },
                success: function (response) {
                    if (response.success) {
                        dynatable.process();
                        Grupo.getHorario();
                    } else {
                        notify(response.message, "error");
                    }
                    mibox.modal('hide');
                },
                error: function () {
                    mibox.modal('hide');
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

    Grupo.body.delegate('.editar', 'click', function (e) {
        Grupo.update(e);
    });

    Grupo.body.delegate('.verhorario', 'click', function (e) {
        Grupo.verhorario(e);
    });

    Grupo.body.delegate('.asignar-hora', 'dblclick', function (e) {
        Grupo.asignarHora(e);
    });

    Grupo.body.delegate('.desasignar-hora', 'dblclick', function (e) {
        Grupo.desasignarHora($(this), e);
    });

    Grupo.init();
    //*/

});

new Vue({
    el: '#grupoVUE',
    data: {
        grupoURL: '',
        tipoGpo: {},
        dias: [],
        horas: [],
        horarioRegular: [],
        horarioGpo: [],
        ciclo: {},
        paginationGpo: {'total-items': 0, 'items-per-page': 12, 'max-size': 3, 'boundary-link-numbers': true},
        grupoActivo: {},
        dataCloneCiclo: {
            id: 'modalCloneCiclo',
            title: 'Copiar Ciclo',
            header: true,
            showaccept:true
        }
    },
    created() {
        this.tipoGpo = JSON.parse(tipoGpoJson);
        this.dias = JSON.parse(diasJson);
        this.horas = JSON.parse(horasJson);
        this.horarioRegular = JSON.parse(horarioRegularJson);
        this.grupoURL = APP.url('academico/horario/grupo/list?idTipoGrupo=' + this.tipoGpo.id);
    },
    mounted() {
        let $vue = this;
        $vue.$refs.raptorGrupo.loadRemoteData();
    },
    methods: {
        styleHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
                }
            }
            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        },
        styleConteHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return "";
                }
            }
            return "color:#E40DEB;";
        },
        conteHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return $vue.horarioGpo[i].grupoHorario.codigo;
                }
            }
            for (var i = 0; i < $vue.horarioRegular.length; i++) {
                if ($vue.horarioRegular[i].hora.id == hora.id && $vue.horarioRegular[i].dia.id == dia.id) {
                    return $vue.horarioRegular[i].grupoHorario.codigo;
                }
            }
            return "";
        },
        verHorario(item) {
            let $vue = this;
            $vue.grupoActivo = item;

            $.ajax({
                url: APP.url('academico/horario/grupo/horario'),
                type: 'POST',
                async: false,
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.horarioRegular = response.data.horarioRegular;
                        $vue.horarioGpo = response.data.horarioGpo;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        styleBorder(item) {
            let $vue = this;
            if (item.id == $vue.grupoActivo.id) {
                return "background-color:gray;";
            }
            return "border-color:" + item.color + ";";
        },
        classHoras(item) {
            if (item.horas == 0) {
                return "label-danger";
            }
            return "label-success";
        },
        asignarHora(dia, hora) {
            let $vue = this;
            if ($vue.grupoActivo == null) {
                return;
            }
            if (!$vue.validarDia(dia, hora)) {
                notify("No puede haber horario por intervalos", "error");
                return;
            }

            $vue.data = {};
            $vue.data.dia = dia;
            $vue.data.hora = hora;
            $vue.data.grupoHorario = $vue.grupoActivo;

            axios.post('/academico/horario/grupo/asignarHora', $vue.data)
                    .then(response => {
                        if (response.data.success) {
                            $vue.verHorario($vue.grupoActivo);
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        vue.generando = false;
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        desasignarHora(dia, hora) {
            let $vue = this;

            if ($vue.grupoActivo == null) {
                return;
            }
            $vue.data = {};
            $vue.data.id = $vue.conterObjHdia(dia, hora).id;
            axios.post('/academico/horario/grupo/desasignarHora', $vue.data)
                    .then(response => {
                        if (response.data.success) {
                            $vue.verHorario($vue.grupoActivo);
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        conterObjHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return $vue.horarioGpo[i];
                }
            }
            return null;
        },
        validarDia(dia, hora) {
            let $vue = this;
            var res = false;
            var obj = [];
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].dia.id == dia.id) {
                    obj.push($vue.horarioGpo[i]);
                }
            }

            if (obj.length == 0) {
                res = true;
            } else {
                var temp = parseInt(hora.codigo);
                obj.forEach(function (item) {
                    var horNext = parseInt(item.hora.codigo) + 100;
                    var horAnt = parseInt(item.hora.codigo) - 100;
                    if (temp == horAnt || temp == horNext) {
                        res = true;
                    }
                });
            }
            return res;
        },
        clonarCiclo() {
            let $vue = this;
            $vue.ciclo = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {
            let $vue = this;
            console.log($vue.ciclo);

            axios.post('/academico/horario/grupo/clonarGrupos', $vue.ciclo)
                    .then(response => {
                        if (response.data.success) {
                            $vue.$refs.modalCloneCiclo.close();
                            $vue.$refs.raptorGrupo.loadRemoteData();
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        notify(MESSAGES.errorComunicacion, "error");
                    });

        }
    }
});
