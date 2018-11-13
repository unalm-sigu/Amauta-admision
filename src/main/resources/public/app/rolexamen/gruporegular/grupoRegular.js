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
        seccionesGrupoRegularSelected: [],
        gruposRegularesSelected: [],
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
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/calcularGruposRegulares`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            console.log("1");
                            // notify(response.data.message, 'info');
                            this.listGruposRegulares(this.rolExamen);
                        } else {
                            //   notify(response.data.message, 'error');
                            console.log("2");
                        }
                        MODAL.hideWait();
                    });
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
            this.seccionesGrupoRegularSelected = letraGrupoRegular.seccionesGruposRegulares;
            this.$refs.seccionModal.open();
        }, loadModalGrupos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.gruposRegularesSelected = letraGrupoRegular.gruposRegularesExamenes;
            this.$refs.gruposModal.open();
        }, loadModalAlumnos(letraGrupoRegular) {
            this.letraSelected = letraGrupoRegular;
            this.alumnosGruposRegulares = letraGrupoRegular.alumnosGruposRegulares;
            this.$refs.alumnosModal.open();
        }, excluir(obj, tipoAccion) {
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/${tipoAccion}/excluir`, obj)
                    .then(response => {
                        if (response.data.success) {
                            obj.estadoEnum = {
                                "name": "EXC",
                                "value": "Excluido"
                            };
                            obj.estado = obj.estadoEnum.name;
                            switch (tipoAccion) {
                                case this.tipoAccion.GRUPO:
                                    this.letraSelected.gruposRegularesActivosCount--;
                                    break;
                                case this.tipoAccion.SECCION:
                                    this.letraSelected.seccionesRegularesActivosCount--;
                                    break;
                                case this.tipoAccion.ALUMNO:
                                    this.letraSelected.alumnosRegularesActivosCount--;
                                    break;
                            }
                        }
                        MODAL.hideWait();
                    });
        }
    }
});
