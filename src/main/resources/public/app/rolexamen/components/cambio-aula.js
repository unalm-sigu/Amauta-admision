Vue.component("multiselect", window.VueMultiselect.default);

Vue.component("cambio-aula-examen", {
    template: "#mainCambioAula",
    props: {
        seccion: {type: Object, default: null, required: false},
        rolexamenes: {type: Object, default: null, required: false},
        tipoorigen: {type: String, default: null, required: false}
    },
    data: function () {
        return {
            URL: APP.url('rolexamen/cambiaraulaexamen'),
            TIPO_ENUM: {
                GRU_REG: "GRU_REG",
                GRU_ESP: "GRU_ESP"
            },
            seccionRolExamenes: null,
            aulaDestino: null,
            aulasOera: []
        }
    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        loadComponent(rolExamenes) {
            if (!rolExamenes) {
                console.error('ERROR INTENTADO CARGAR COMPONENTE CAMBIO AULA EXAMEN');
            }
            /*
             this.tipoDestinoGrupoExamenes = null;
             this.gruposHorariosDestino = [];
             this.grupoHorarioDestino = null;
             */
            let vue = this;
            $.ajax({
                url: `${vue.URL}/loadComponent`,
                data: {seccion: vue.seccion.id, tipoOrigen: vue.tipoorigen, rolExamenes: rolExamenes.id},
                success: function (response) {
                    if (response.success) {
                        vue.seccionRolExamenes = response.data.seccionRolExamenes;
                        vue.aulasOera = response.data.jAulasOeras;
                        if (response.data.tipoGrupoRolExamenesEnumDefault) {
                            //  vue.tipoDestinoGrupoExamenes = response.data.tipoGrupoRolExamenesEnumDefault;
                        }
                    }
                }
            });
        }, isTipoRegular() {
            return this.tipoorigen == this.TIPO_ENUM.GRU_REG;
        }, isTipoEspecial() {
            return this.tipoorigen == this.TIPO_ENUM.GRU_ESP;
        }, cambiarAula() {
            var form = $("#frmCambioAula");
            if (!form.parsley().validate()) {
                return;
            }

            let cambiarAula = {
                tipoGrupoRolExamenOrigen: this.tipoorigen,
                idSeccionRolExamenesOrigen: this.seccionRolExamenes.id,
                idAulaDestino: this.aulaDestino.id
            };
         
            AXIOS.post(`${this.URL}/cambiarAulaExamenSeccion`, cambiarAula)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');

                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }
    }
});

