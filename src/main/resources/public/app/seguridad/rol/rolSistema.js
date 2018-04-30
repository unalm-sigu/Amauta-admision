Vue.component("multiselect", window.VueMultiselect.default);

Vue.component("aula-component", {
    template: "#modalAulaComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            tabAulas: {
                aulaSel: null,
                oera: {
                    id: 50,
                    nombre: "oera",
                    moduloSel: null,
                    aulaSel: null,
                    modulosCombo: [],
                    tblAulas: null
                },
                oficinas: {
                    oficinaSel: null,
                    aulaSel: null,
                    oficinasDisponibles: [],
                    tblAulas: null
                },
                especificas: {
                    aulasEspecificaSel: null,
                    aulasEspecificas: [],
                    errores: []
                }
            }
        }
    },
    mounted: function () {
        let $vue = this;
        $global.$on("loadAulaComponent", function (seccion) {
            $vue.loadAula($vue, seccion);
        });
    },
    methods: {
        loadAula($vue, seccion) {
            
        }
    }
});



new Vue({
    el: '#pageRolSistemaVUE',
    data: {
        rolesUrl: APP.url('seguridad/rol/list')
    },
    computed: {
    },
    mounted() {
    },
    methods: {
    }
});
