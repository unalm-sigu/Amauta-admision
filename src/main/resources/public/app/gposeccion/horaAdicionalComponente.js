Vue.component("hora-adicional-component", {
    template: "#modalHorasAdicionalesComponent",
    props: {
        seccion: {type: Object, default: {grupoHoras: {}}},
        gruposeccion: {type: Object, default: {}},
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    },
    methods: {

    }
});

