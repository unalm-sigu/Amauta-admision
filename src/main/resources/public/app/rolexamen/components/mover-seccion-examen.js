Vue.component("multiselect", window.VueMultiselect.default);

Vue.component("mover-seccion-examen", {
    template: "#mainMoverSeccion",
    props: {
        parentinfo: {type: Object, default: null, required: false},
        seccion: {type: Object, default: null, required: false},
        tipoorigen: {type: String, default: null, required: false}
    },
    data: function () {
        return {
            URL: APP.url('rolexamen/moverseccionexamen'),
            tiposDestinoGrupoExamenes: [],
            tipoDestinoGrupoExamenes: null,
            seccionRolExamenes: null,
            //   cursosMasivosExamen: null,
            //    letrasGrupoRegular: null,
            gruposHorariosDestino: [],
            grupoHorarioDestino: null,
            TIPO_ENUM: {
                CUR_MAS: "CUR_MAS",
                GRU_REG: "GRU_REG",
                GRU_ESP: "GRU_ESP"
            }
        }
    },
    mounted: function () {

        let $vue = this;
        /*   $global.$on("loadGrupoComponent", function (seccion) {
         $vue.loadGruposHorario($vue, seccion);
         });*/
        this.loadTiposGrupoExamen();

    },
    methods: {
        isTipoMasivo() {
            return this.tipoorigen == this.TIPO_ENUM.CUR_MAS;
        }, isTipoRegular() {
            return this.tipoorigen == this.TIPO_ENUM.GRU_REG;
        }, isTipoEspecial() {
            return this.tipoorigen == this.TIPO_ENUM.GRU_ESP;
        }, isTipoDestinoMasivo() {
            if (this.tipoDestinoGrupoExamenes == null)
                return false;
            return this.tipoDestinoGrupoExamenes.code == this.TIPO_ENUM.CUR_MAS;
        }, isTipoDestinoRegular() {
            if (this.tipoDestinoGrupoExamenes == null)
                return false;
            return this.tipoDestinoGrupoExamenes.code == this.TIPO_ENUM.GRU_REG;
        }, isTipoDestinoEspecial() {
            if (this.tipoDestinoGrupoExamenes == null)
                return false;
            return this.tipoDestinoGrupoExamenes.code == this.TIPO_ENUM.GRU_ESP;
        }, loadTiposGrupoExamen() {
            let vue = this;
            $.ajax({
                url: `${vue.URL}/listTipoGrupoRolExamenes`,
                success: function (response) {
                    if (response.success) {
                        vue.tiposGrupoExamenes = response.data;
                    }
                }
            });
        }, loadComponent() {
            let vue = this;
            $.ajax({
                url: `${vue.URL}/loadComponent`,
                data: {seccion: vue.seccion.id, tipoOrigen: vue.tipoorigen},
                success: function (response) {
                    if (response.success) {
                        vue.seccionRolExamenes = response.data.seccionRolExamenes;
                        if (response.data.tipoGrupoRolExamenesEnumDefault) {
                            vue.tipoDestinoGrupoExamenes = response.data.tipoGrupoRolExamenesEnumDefault;
                        }
                    }
                }
            });
        }, cambiarTipoDestinoGrupo() {
            MODAL.showWait("Espere un momento por favor");
            this.grupoHorarioDestino = null;
            AXIOS.post(`${this.URL}/cambiarTipoDestinoGrupo/${this.tipoDestinoGrupoExamenes.code}`, this.seccionRolExamenes.grupoHorasExamen)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');
                            /*
                             this.cursosMasivosExamen = response.data.data.jCursosMasivosExamen;
                             this.letrasGrupoRegular = response.data.data.jLetrasGrupoRegular;
                             */
                            if (response.data.data.jCursosMasivosExamen != null)
                                this.gruposHorariosDestino = response.data.data.jCursosMasivosExamen;
                            if (response.data.data.jLetrasGrupoRegular != null)
                                this.gruposHorariosDestino = response.data.data.jLetrasGrupoRegular;
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }, moverSeccion() {
            MODAL.showWait("Espere un momento por favor");
            let cambioHorarioExamenSeccion = {
                tipoGrupoRolExamenOrigen: this.tipoorigen,
                idSeccionRolExamenesOrigen: this.seccionRolExamenes.id,
                tipoGrupoRolExamenDestino: this.tipoDestinoGrupoExamenes.code,
                idTipoGrupoExamenDestino: this.grupoHorarioDestino.id
            };
            AXIOS.post(`${this.URL}/cambioHorarioExamenSeccion`, cambioHorarioExamenSeccion)
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

