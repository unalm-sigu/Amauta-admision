Vue.component("multiselect", window.VueMultiselect.default);

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
