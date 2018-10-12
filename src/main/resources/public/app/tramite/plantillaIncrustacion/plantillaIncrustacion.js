Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#plantillaVUE',
    data: {
        tipos: JSON.parse(tiposJson),
        plantillaURL: APP.url('tramite/plantillainscrustacion/list')
    },
    computed: {

    },
    created() {
    },
    mounted: function () {

    },
    methods: {
        update() {

        }
    }
});
