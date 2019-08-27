Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#responsableAulaVUE',
    data: {
        responsablesURL: APP.url(rutaModulo + '/list'),
        aula: {},
        modalResponsableAula: {
            id: 'modalResponsableAula',
            header: true,
            okbtn: 'Guardar',
            showaccept: true,
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            form: "formRespAula"
        }
    },
    computed: {
    },
    mounted: function () {

    },
    methods: {
        nuevoResponsable() {
            this.$refs.modalResponsableAula.title = `Nuevo Responsable`;
            this.$refs.modalResponsableAula.open();
        }
    }
});
 