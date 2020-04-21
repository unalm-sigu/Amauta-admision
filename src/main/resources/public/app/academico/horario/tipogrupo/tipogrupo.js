Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#tipoGrupoVUE',
    data: {
        tipoGrupoURL: APP.url('academico/horario/list'),
        colorEstado: {ACT: 'success', INA: 'danger', CRE: "default"},
        colorEstadoGpos: {COMP: 'success', INCOMP: 'danger'},
        tipoGrupoModal: {
            id: 'refTipoGrupo',
            header: true,
            title: 'Nuevo Tipo Grupo',
            modalsize: 'modal-md'
        },
        tipoGrupoHoras: {},
        tiposCiclo: [],
    },
    created() {
        this.tiposCiclo = JSON.parse(tiposCicloJson);
    },
    methods: {
        changeEstado(item) {
            let $vue = this;
            bootbox.confirm({
                title: "Cambiar Estado",
                size: 'medium',
                message: '¿Desea cambiar el estado del tipo de grupo horas?',
                buttons: {
                    confirm: {label: "Cambiar Estado", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/horario/estado'),
                            type: 'POST',
                            async: false,
                            data: {id: item.id},
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.raptorTipoGpos.loadRemoteData();
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        eliminar(item) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el tipo de grupo horas?",
                size: 'medium',
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
                            data: {id: item.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.$refs.raptorTipoGpos.loadRemoteData();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        verEditar(item) {
            let $vue = this;
            let clon = Object.assign({}, item);
            $vue.tipoGrupoHoras = clon;
            $vue.$refs.refTipoGrupo.open();
        },
        verNuevo() {
            let $vue = this;
            $vue.tipoGrupoHoras = {};
            $vue.$refs.refTipoGrupo.open();
        },
        saveTipoGrupo() {
            let $vue = this;
            var form = $("#formTipoGrupo");
            if (!form.parsley().validate()) {
                return;
            }

//            $vue.tipoGrupoHoras.tipoCiclo = $vue.tipoGrupoHoras.tipoCicloEnum.name;

            $.ajax({
                url: APP.url('academico/horario/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.tipoGrupoHoras),
                success: function (response) {
                    if (response.success) {
                        if (response.data) {
                            $vue.$refs.refTipoGrupo.close();
                            $vue.$refs.raptorTipoGpos.loadRemoteData();
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        getLabelEstado(item) {
            let $vue = this;
            return "label-" + $vue.colorEstado[item.estado];
        },
        getLabelEstadoGpos(item) {
            let $vue = this;
            return "label-" + $vue.colorEstado[item.estadoGrupos];
        },
        goGrupos(item) {
            location.href = APP.url('academico/horario/grupo/' + item.id);
        }
    }
});
