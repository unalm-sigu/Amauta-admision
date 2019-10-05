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
        cambiarAulamodal: VUE_MODAL.structFormAjax({
            id: 'cambiarAulamodal',
            header: true,
            title: 'Cambiar Aula y/o Grupo Horario',
            cancelbtn: 'Cancelar',
            okbtn: 'Cambiar',
            modalsize: 'modal-lg'
        }),
        asignarHorarioModal: {
            id: 'asignarHorarioModal',
            header: true,
            title: 'Asignar Horario',
            cancelbtn: 'Cancelar',
            okbtn: 'Asignar',
            modalsize: 'modal-lg',
            showaccept: true
        },
        grupoHoras: [],
        grupoEspTemp: {},
        grupoEsp: {},
        observaciones: {cantidad: 0, message: "", rows: 4}
    },
    mounted() {
        if (jRolExamenes != null) {
            this.rolExamen = JSON.parse(jRolExamenes);
            this.changeRolExamen();
        }
    },
    computed: {
        calcularDisponible() {
            return this.rolExamen && this.rolExamen.isEstadoConfigurando && (this.rolExamen.isSituacionAsignarHorarioCursosMasivos || this.rolExamen.isSituacionConfigurarGrupoEspecial);
        },
        accionesSeccionDisponibles() {
            const situacionValida = this.rolExamen && this.rolExamen.isSituacionConfigurarGrupoEspecial;
            return this.rolExamen && ((this.rolExamen.isEstadoConfigurando && situacionValida) || this.rolExamen.isEstadoModificando)
        }
    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        },
        changeRolExamen() {
            this.$refs.raptor.ajaxdata = {rolexamenes: this.rolExamen.id};
            this.$refs.raptor.loadRemoteData();
        },
        calcularGrupoEspecial() {
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
        },
        limpiarGrupoEspecial() {
            let vue = this;
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${vue.URL}/limpiarExamenGrupoEspecial`, vue.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');
                            this.$refs.raptor.loadRemoteData();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        },
        loadModalAlumnos(seccionGrupoEspecial) {
            //    this.letraSelected = letraGrupoRegular;
            this.$refs.tblAlumnosGrupoEspecial.ajaxdata = {seccionGrupoEspecial: seccionGrupoEspecial.id};
            this.$refs.tblAlumnosGrupoEspecial.loadRemoteData();
            this.$refs.alumnosModal.title = "Sección Grupo Especial " + seccionGrupoEspecial.seccion.codigo2 + " | Alumnos";
            this.$refs.alumnosModal.open();

        },
        excluir(obj, tipoAccion) {
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea excluir?",
                buttons: {
                    confirm: {label: 'Sí', className: "btn-warning"},
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
        },
        incluir(obj, tipoAccion) {
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea incluir?",
                buttons: {
                    confirm: {label: 'Sí', className: "btn-warning"},
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
        },
        trasladar(item) {
            this.$refs.moverSeccionComp.seccion = item.seccion;
            this.$refs.moverSeccionComp.tipoorigen = "GRU_ESP";
            this.$refs.moverSeccionComp.loadComponent(this.rolExamen);
            this.$refs.moverSeccionModal.open();
        },
        removerHorario(item) {
            console.log(item);
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea quitar el horario?",
                buttons: {
                    confirm: {label: 'Sí', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/quitarGrupo`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.$refs.raptor.loadRemoteData();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        },
        removerAula(item) {
            console.log(item);
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea quitar el aula?",
                buttons: {
                    confirm: {label: 'Sí', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/quitarAula`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.$refs.raptor.loadRemoteData();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        },
        cambiarAula(item) {
            console.log(item);
            let $vue = this;
            $vue.loadGrupos(item);
            $vue.grupoEsp = item;
            $vue.grupoEspTemp = JSON.parse(JSON.stringify(item));
            $vue.observaciones = {cantidad: 0, message: "", rows: 4};
            $vue.$refs.cambiarAulamodal.open();
        },
        asignarHorario(item) {
            let $vue = this;

            $vue.$refs.asignarHorarioModal.open();
        },
        loadGrupos(item) {
            let $vue = this;
            AXIOS.post(`${$vue.URL}/allGrupoHE`, $vue.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            $vue.grupoHoras = response.data.data;
                        }
                    });
        },
        cLabelGrupo(item) {
            if (item.grupoHoras) {
                return item.grupoHoras.codigo;
            }
        },
        saveCambiarAulaGrupo() {
            let $vue = this;
            $vue.$refs.cambiarAulamodal.beginProcessing();
            $vue.observaciones = {cantidad: 0, message: "", rows: 4};

            axios.post(`${$vue.URL}/cambiarAulaGrupo`, $vue.grupoEspTemp)
                    .then(response => {
                        $vue.$refs.cambiarAulamodal.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.$refs.raptor.loadRemoteData();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                        let restricc = response.data.data;
                        if (restricc != null) {
                            $vue.observaciones.cantidad = restricc.length;
                            $vue.observaciones.rows = restricc.length > 7 ? 7 : restricc.length;
                            for (var i = 0; i < restricc.length; i++) {
                                $vue.observaciones.message += (i + 1) + ") " + restricc[i] + "\n";
                            }
                        }

                    }).catch(e => {
                $vue.$refs.cambiarAulamodal.confirmReaction(false);
                notify(MESSAGES.errorComunicacion, "error");
            });
        }
    }
});
