Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url(rutaModulo + "/" + urlSeccion),
        letraGrupoRegular: JSON.parse(jLetraGrupoRegular),
        rolExamenex: JSON.parse(jRolExamenes),
        tipoAccion: {
            LETRA: "LETRA",
            GRUPO: "GRUPO",
            SECCION: "SECCION",
            ALUMNO: "ALUMNO"
        },
        observaciones: {cantidad: 0, message: "", rows: 4, forzar: false},
        seccionGpoRegTempo: {},
        seccionGpoRegSelect: {},
        configCambioAula: VUE_MODAL.structFormAjax({
            id: "idModalCambioAula",
            modalsize: "modal-lg"
        })
    },
    mounted() {
        this.loadModalSecciones(this.letraGrupoRegular);
    },
    computed: {
        accionesDisponibles() {
            try {
                const rolExamenes = this.rolExamenex;
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
        },
        incluir(obj, tipoAccion) {
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
        },
        trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "GRU_REG";
            const rolExamenes = this.rolExamenex;
            this.$refs.moverSeccionComp.loadComponent(rolExamenes);
            this.$refs.moverSeccionModal.open();
        },
        cambiarAulaOld(item) {
            console.log("entro");
            console.dir(item);
            const rolExamenes = this.rolExamenex;
            this.$refs.cambiarAulaExamenComp.seccion = item.seccion;
            this.$refs.cambiarAulaExamenComp.seccion = item.seccion;
            this.$refs.cambiarAulaExamenComp.tipoorigen = "GRU_REG";
            this.$refs.cambiarAulaExamenComp.loadComponent(rolExamenes);
            this.$refs.cambiarAulaModal.open();
        },
        cambiarAula(item) {
            let $vue = this;
            $vue.seccionGpoRegSelect = item;
            $vue.seccionGpoRegTempo = JSON.parse(JSON.stringify(item));
            $vue.observaciones = {cantidad: 0, message: "", rows: 4, forzar: false};
            $vue.$refs.modalCambioAula.open();
        },
        saveCambioAula() {
            let $vue = this;
            $vue.$refs.modalCambioAula.beginProcessing();
            $vue.observaciones = {cantidad: 0, message: "", rows: 4, forzar: false};

            axios.post(APP.url(rutaModulo + "/cambiarAula"), $vue.seccionGpoRegTempo).then(response => {
                $vue.$refs.modalCambioAula.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.tblSeccionesGrupoRegular.loadRemoteData();
                    notify(response.data.message, "info");
                } else {
                    notify(response.data.message, "error");
                }
                let restricc = response.data.data;
                if (restricc != null) {
                    $vue.observaciones.cantidad = restricc.length;
                    $vue.observaciones.forzar = true;
                    $vue.observaciones.rows = restricc.length > 7 ? 7 : restricc.length;
                    for (var i = 0; i < restricc.length; i++) {
                        $vue.observaciones.message += (i + 1) + ") " + restricc[i] + "\n";
                    }
                }

            }).catch(e => {
                $vue.$refs.modalCambioAula.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });
        }
    }
});
