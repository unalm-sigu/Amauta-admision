Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ordenmeritocontrolVUE',
    data: {
        niveles: [{label: 'General', val: 0}, {label: 'Nivel 1', val: 1}, {label: 'Nivel 2', val: 2}, {label: 'Nivel 3', val: 3}, {label: 'Nivel 4', val: 4}],
        nivelEscogido: {label: 'General', val: 0},
        URL: APP.url('academico/ordenmerito'),
        control: JSON.parse(control),
    },
    computed: {
        url() {
            return `${this.URL}/${this.control.id}/control/${this.nivelEscogido.val}/alumnos`;
        }
    },
    watch: {
        url(old, neu) {
            this.$refs.raptor.url = old;
            this.$refs.raptor.page = {currentPage: 1};
            this.$refs.raptor.loadRemoteData();
        }
    },
    mounted() {

    },
    methods: {
    }
});