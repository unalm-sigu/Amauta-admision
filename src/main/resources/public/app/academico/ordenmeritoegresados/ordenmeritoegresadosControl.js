Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ordenmeritocontrolVUE',
    data: {
        URL: APP.url('academico/ordenmeritoegresados'),
        control: JSON.parse(control),
    },
    mounted() {

    },
    methods: {
    }
});