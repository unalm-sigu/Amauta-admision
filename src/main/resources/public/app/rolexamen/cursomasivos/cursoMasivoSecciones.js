new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/cursomasivos'),
        cursoMasivoExamen: JSON.parse(jCursoMasivoExamen),
    },
    mounted() {
        this.$refs.tblSeccionesCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
        this.$refs.tblSeccionesCursosMasivos.loadRemoteData();
    },
    computed: {
        accionesDisponibles() {
            try {
                return this.cursoMasivoExamen.rolExamenes.isEstadoModificando || this.cursoMasivoExamen.rolExamenes.isEstadoConfigurando;
            } catch (error) {
                console.error(error);
                return false;
            }
        }
    },
    methods: {
        excluir(item, tipoAccion) {
            item.cursoMasivoExamen = {id: this.cursoMasivoExamen.id};

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
                        AXIOS.post(`${vue.URL}/${tipoAccion}/excluir`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.findCursoMasivoExamen();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, incluir(item, tipoAccion) {
            item.cursoMasivoExamen = {id: this.cursoMasivoExamen.id};

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
                        AXIOS.post(`${vue.URL}/${tipoAccion}/incluir`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.findCursoMasivoExamen();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "CUR_MAS";
            const rolExamenes = this.cursoMasivoExamen.rolExamenes;
            this.$refs.moverSeccionComp.loadComponent(rolExamenes);
            this.$refs.moverSeccionModal.open();
        }
    }
});
