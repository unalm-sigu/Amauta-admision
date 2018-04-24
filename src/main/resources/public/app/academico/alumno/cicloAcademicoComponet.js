Vue.component("ciclo-component", {
    template: "#cicloTemplate",
    props: {
        validarNota: {type: Function, default: () => {
            }},
        styleNota: {type: Function, default: () => {
            }},
        tab: {},
        typeSearch: false,
    },
    data: function() {
        return {
            tab: {},
        }
    },
    mounted() {
        let vue = this;
    }
});

