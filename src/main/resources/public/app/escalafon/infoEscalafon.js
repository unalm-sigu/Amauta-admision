Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        escalafon: JSON.parse(escalafonJson),
        isPerfil: isPerfil
    },
    mounted: function () {
    },
    methods: {
        editar() {
            window.location.href = APP.url('escalafon/update/' + this.escalafon.id) + this.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        }
    }
});