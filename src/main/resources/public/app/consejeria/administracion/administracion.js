Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker);
Vue.component('file-upload', VueUploadComponent);

const HistorialConsejeriaList = httpVueLoader('/app/consejeria/administracion/HistorialConsejeriaList.vue');
const HistorialConsejeriaClonar = httpVueLoader('/app/consejeria/administracion/HistorialConsejeriaClonar.vue');

var root = new Vue({
    el: '#main',
    components: {
        historialConsejeriaList: HistorialConsejeriaList,
        historialConsejeriaClonar: HistorialConsejeriaClonar,
    },
    methods: {
        clonarConsejeros() {
            let $vue = this;
            $vue.$refs.clonar.open();
        },
        agendaConsejerosURL() {
            location.href=APP.url('consejeria/administracion/agendaconsejero/') + URL_UTIL.getOrigenURL();
        },
        reloadList() {
            let $vue = this;
            $vue.$refs.historial.reloadList();
        }
    }
});