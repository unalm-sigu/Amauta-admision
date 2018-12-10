Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/gruporegular'),
        letraGrupoRegular: JSON.parse(jLetraGrupoRegular)
    },
    mounted() {
        this.loadModalSecciones(this.letraGrupoRegular);
    },
    methods: {
        loadModalSecciones(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.$refs.tblSeccionesGrupoRegular.ajaxdata = {letraGrupoRegular: letraGrupoRegular.id};
            this.$refs.tblSeccionesGrupoRegular.loadRemoteData();
        }, excluir(obj, tipoAccion) {
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea excluir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/${tipoAccion}/excluir`, obj)
                                .then(response => {
                                    if (response.data.success) {

                                        switch (tipoAccion) {
                                            case vue.tipoAccion.GRUPO:
                                                vue.$refs.gruposModal.close();
                                                break;
                                            case vue.tipoAccion.SECCION:
                                                vue.$refs.tblSeccionesGrupoRegular.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.alumnosModal.close();
                                                break;
                                        }
                                        vue.listGruposRegulares(vue.rolExamen);
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, incluir(obj, tipoAccion) {
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea incluir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/${tipoAccion}/incluir`, obj)
                                .then(response => {
                                    if (response.data.success) {

                                        switch (tipoAccion) {
                                            case vue.tipoAccion.GRUPO:
                                                vue.$refs.gruposModal.close();
                                                break;
                                            case vue.tipoAccion.SECCION:
                                                vue.$refs.tblSeccionesGrupoRegular.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.alumnosModal.close();
                                                break;
                                        }
                                        vue.listGruposRegulares(vue.rolExamen);
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "GRU_REG";
            this.$refs.moverSeccionComp.loadComponent();
            this.$refs.moverSeccionModal.open();
        }
    }
});
