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
    mounted() {

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
                let vue=this;
                bootbox.confirm({
                    message: "¿Si continua se perdera el avance de los grupos regulares?",
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
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/${this.tipoAccion.SECCION}/loadLetraGrupoRegularInfo`, letraGrupoRegular)
                    .then(response => {
                        if (response.data.success) {
                            this.seccionesGrupoRegulares = response.data.data;
                            this.$refs.seccionModal.open();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });

        }, loadModalGrupos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;

            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/${this.tipoAccion.GRUPO}/loadLetraGrupoRegularInfo`, letraGrupoRegular)
                    .then(response => {
                        if (response.data.success) {
                            this.gruposRegulares = response.data.data;
                            console.dir(response.data.data);
                            this.$refs.gruposModal.open();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }, loadModalAlumnos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/${this.tipoAccion.ALUMNO}/loadLetraGrupoRegularInfo`, letraGrupoRegular)
                    .then(response => {
                        if (response.data.success) {
                            this.alumnosGruposRegulares = response.data.data;
                            this.$refs.alumnosModal.open();
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
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
                                        obj.estadoEnum = {
                                            "name": "EXC",
                                            "value": "Excluido"
                                        };
                                        obj.estado = obj.estadoEnum.name;

                                        switch (tipoAccion) {
                                            case vue.tipoAccion.GRUPO:
                                                vue.$refs.gruposModal.close();
                                                break;
                                            case vue.tipoAccion.SECCION:
                                                vue.$refs.seccionModal.close();
                                                vue;
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
        }
    }
});
