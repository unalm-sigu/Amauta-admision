Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/gruporegular'),
        tipoAccion: {
            LETRA: "LETRA",
            GRUPO: "GRUPO",
            SECCION: "SECCION",
            ALUMNO: "ALUMNO"
        },
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: null,
        letraSelected: null,
        letrasGruposRegulares: [],
        seccionesGrupoRegulares: [],
        gruposRegulares: [],
        alumnosGruposRegulares: []
    },
    computed: {
        generarDisponible() {
            try {
                return this.rolExamen && this.rolExamen.isEstadoConfigurando && (this.rolExamen.isSituacionConfigurarCursoMasivo || this.rolExamen.isSituacionConfigurarGrupoRegular);
            } catch (error) {
                return false;
            }
        }
    },
    mounted() {
        if (jRolExamenes != null) {
            this.rolExamen = JSON.parse(jRolExamenes);
            this.listGruposRegulares(this.rolExamen);
        }
    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        }, calcularGrupoRegular() {
            $('#frmCalcular').find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            $('#frmCalcular').find('.multiselect__input').each(function () {
                var input = $(this);
                let element = input.closest('.multiselect').find('.multiselect__single');
                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });
            var form = $("[id='frmCalcular']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            if (this.letrasGruposRegulares.length > 0) {
                let vue = this;
                bootbox.confirm({
                    message: "Si continua se perdera el avance de los grupos regulares. Esta seguro que desea continuar?",
                    buttons: {
                        confirm: {label: 'Si', className: "btn-warning"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            MODAL.showWait("Espere un momento por favor");
                            AXIOS.post(`${vue.URL}/calcularGruposRegulares`, vue.rolExamen)
                                    .then(response => {
                                        if (response.data.success) {
                                            // notify(response.data.message, 'info');
                                            vue.listGruposRegulares(vue.rolExamen);
                                        } else {
                                            //   notify(response.data.message, 'error');
                                        }
                                        MODAL.hideWait();
                                    });
                        }
                    }
                });
            } else {
                MODAL.showWait("Espere un momento por favor");
                AXIOS.post(`${this.URL}/calcularGruposRegulares`, this.rolExamen)
                        .then(response => {
                            if (response.data.success) {
                                // notify(response.data.message, 'info');
                                this.listGruposRegulares(this.rolExamen);
                            } else {
                                //   notify(response.data.message, 'error');
                            }
                            MODAL.hideWait();
                        });
            }


        }, changeRolExamen() {
            this.listGruposRegulares(this.rolExamen);
        }, eliminarGruposRegulares() {
            let vue = this;
            bootbox.confirm({
                message: "¿Si continua se perdera el avance de los grupos regulares y especiales?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/eliminarGruposRegulares`, vue.rolExamen)
                                .then(response => {
                                    if (response.data.success) {
                                        // notify(response.data.message, 'info');
                                        vue.listGruposRegulares(vue.rolExamen);
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, listGruposRegulares(rolExamen) {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/listGruposRegulares`, rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            this.letrasGruposRegulares = response.data.data;
                        }
                        MODAL.hideWait();
                    });
        }, loadModalSecciones(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            //  MODAL.showWait("Espere un momento por favor");
            //   MODAL.hideWait();
            this.$refs.seccionModal.title = "Letra Grupo Horario " + letraGrupoRegular.letra + " | Secciones";
            this.$refs.tblSeccionesGrupoRegular.ajaxdata = {letraGrupoRegular: letraGrupoRegular.id};
            this.$refs.tblSeccionesGrupoRegular.loadRemoteData();
            this.$refs.seccionModal.open();
        }, loadModalGrupos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.$refs.gruposModal.title = "Letra Grupo Horario " + letraGrupoRegular.letra + " | Grupo Horarios";
            AXIOS.post(`${this.URL}/${this.tipoAccion.GRUPO}/loadLetraGrupoRegularInfo`, letraGrupoRegular)
                    .then(response => {
                        if (response.data.success) {
                            this.gruposRegulares = response.data.data;
                            console.dir(response.data.data);
                            this.$refs.gruposModal.open();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                    });
        }, loadModalAlumnos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.$refs.tblAlumnosGrupoRegular.ajaxdata = {letraGrupoRegular: letraGrupoRegular.id};
            this.$refs.tblAlumnosGrupoRegular.loadRemoteData();
            this.$refs.alumnosModal.title = "Letra Grupo Horario " + letraGrupoRegular.letra + " | Alumnos";
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
        }, letraRegularSecciones(item) {
            location.href = `${this.URL}/secciones/${item.id}`;
        }
    }
});
