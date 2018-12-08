Vue.component("grupohorario-component", {
    template: "#mainMoverSeccion",
    props: {
        parentinfo: {type: Object, default: null, required: true},
        seccion: {type: Object, default: null, required: true},
        tipo: {type: String, default: null, required: true}
    },
    data: function () {
        return {
            TIPO_ENUM: {
                MASIVO: "MASIVO",
                REGULAR: "REGULAR",
                ESPECIAL: "ESPECIAL"
            }
        }
    },
    mounted: function () {

        let $vue = this;
        /*   $global.$on("loadGrupoComponent", function (seccion) {
         $vue.loadGruposHorario($vue, seccion);
         });*/

    },
    methods: {
        isTipoMasivo() {
            return this.tipo == this.TIPO_ENUM.MASIVO;
        }, isTipoRegular() {
            return this.tipo == this.TIPO_ENUM.REGULAR;
        }, isTipoEspecial() {
            return this.tipo == this.TIPO_ENUM.ESPECIAL;
        }
    }
});

