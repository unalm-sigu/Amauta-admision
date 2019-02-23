Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    data: {
        ciclo: {},
        carpetaUrl:APP.url("general/tipocarpeta/allTipoCarpeta"),
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        custom() {
            let $vue = this;
        },
    }
});


