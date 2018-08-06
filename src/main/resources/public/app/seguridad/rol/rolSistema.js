Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    mixins: [Vudal],
    data: {
        rolesUrl: APP.url('seguridad/rol/list'),
        cargo: {id: null},
    },
    computed: {
    },
    mounted() {
    },
    methods: {
        relacionarCargo: function (rol) {
            let vue = this;
            vue.vudalOpen();
        },
        relacionarFuncion: function (rol) {
       
        },
        saveCargo: function () {
            let vue = this;
            vue.vudalClose();
        }
    }
});