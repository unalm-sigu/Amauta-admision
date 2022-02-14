Vue.component("multiselect", window.VueMultiselect.default)
Vue.component('date-picker', VueBootstrapDatetimePicker);
new Vue({
    el: '#main',
    data: {
        carreras: [],
        coordinadoresURL: APP.url('consejeria/administracion/coordinadores/all'),
        alumnos: [],
        consejeros: [],
        origen: APP.url('consejeria/administracion'),
        filtro: {}
    },
    mounted: function () {
        let $vue = this;
        $vue.setOrigin();
    },
    methods: {
        styleColor(item) {
            switch (item) {
                case "ACT":
                    return "label label-primary";
                case "INA" :
                    return "label label-danger";
            }
        },
        setOrigin() {
            let $vue = this;
            $vue.origen = URL_UTIL.getOrigenDecodeURL();
        },
    }
});







        