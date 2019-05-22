Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/gruporegular'),
        letraGrupoRegular: JSON.parse(jLetraGrupoRegular),
        tipoAccion: {
            LETRA: "LETRA",
            GRUPO: "GRUPO",
            SECCION: "SECCION",
            ALUMNO: "ALUMNO"
        },
    },
    mounted() {
        this.loadModalSecciones(this.letraGrupoRegular);
    },
    computed: {
        accionesDisponibles() {
            try {
                const rolExamenes = this.letraGrupoRegular.rolExamenes;
                return (rolExamenes.isEstadoConfigurando || rolExamenes.isEstadoModificando);
            } catch (error) {
                console.error(error);
                return false;
            }
        }
    },
    methods: {
        loadModalSecciones(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.$refs.tblSeccionesGrupoRegular.ajaxdata = {letraGrupoRegular: letraGrupoRegular.id};
            this.$refs.tblSeccionesGrupoRegular.loadRemoteData();
        },
        excluir(obj, tipoAccion) {
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
                                        //  vue.listGruposRegulares(vue.rolExamen);
//                                        vue.$refs.tblSeccionesGrupoRegular.ajaxdata = {letraGrupoRegular: vue.letraGrupoRegular.id};
//                                        vue.$refs.tblSeccionesGrupoRegular.loadRemoteData();
                                    } else {
                                        notify(response.data.message, "error");
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
//                                        vue.$refs.tblSeccionesGrupoRegular.loadRemoteData();
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                    MODAL.hideWait();
                                })
                                .catch(function (error) {
                                    console.dir(error);
                                    notify(error, "error");
                                });
                        ;
                    }
                }
            });
        }, trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "GRU_REG";
            const rolExamenes = this.letraGrupoRegular.rolExamenes;
            this.$refs.moverSeccionComp.loadComponent(rolExamenes);
            this.$refs.moverSeccionModal.open();
        }, cambiarAula(item) {
            console.log("entro");
            console.dir(item);
            const rolExamenes = this.letraGrupoRegular.rolExamenes;
            this.$refs.cambiarAulaExamenComp.seccion = item.seccion;
            this.$refs.cambiarAulaExamenComp.seccion = item.seccion;
            this.$refs.cambiarAulaExamenComp.tipoorigen = "GRU_REG";
            this.$refs.cambiarAulaExamenComp.loadComponent(rolExamenes);
            this.$refs.cambiarAulaModal.open();
        }
    }
});
