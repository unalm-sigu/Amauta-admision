Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/grupoespecial'),
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: null,
        tipoAccion: {
            LETRA: "LETRA",
            GRUPO: "GRUPO",
            SECCION: "SECCION",
            ALUMNO: "ALUMNO"
        },
    },
    mounted() {

    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        }, changeRolExamen() {
            this.$refs.raptor.ajaxdata = {rolexamenes: this.rolExamen.id};
            this.$refs.raptor.loadRemoteData();
        }, calcularGrupoEspecial() {
            let vue = this;
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${vue.URL}/calcularGrupoEspecial`, vue.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');
                            this.$refs.raptor.loadRemoteData();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }, loadModalAlumnos(seccionGrupoEspecial) {
            //    this.letraSelected = letraGrupoRegular;
            this.$refs.tblAlumnosGrupoEspecial.ajaxdata = {seccionGrupoEspecial: seccionGrupoEspecial.id};
            this.$refs.tblAlumnosGrupoEspecial.loadRemoteData();
            this.$refs.alumnosModal.title = "Seccion Grupo Especial " + seccionGrupoEspecial.seccion.codigo2 + " | Alumnos";
            this.$refs.alumnosModal.open();

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
                                            case vue.tipoAccion.SECCION:
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.tblAlumnosGrupoEspecial.loadRemoteData();
                                                break;
                                        }
                                        vue.$refs.raptor.loadRemoteData();
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
                                            case vue.tipoAccion.SECCION:
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.tblAlumnosGrupoEspecial.loadRemoteData();
                                                break;
                                        }
                                        vue.$refs.raptor.loadRemoteData();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "GRU_ESP";
            this.$refs.moverSeccionComp.loadComponent();
            this.$refs.moverSeccionModal.open();
        }
    }
});
