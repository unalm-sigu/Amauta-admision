Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#rolexamenesVUE',
    data: {
        rolexamenesURL: APP.url('rolexamen/rolexamenes/list')
    },
    mounted() {
        let $vue = this;
    },
    methods: {

    }
});
