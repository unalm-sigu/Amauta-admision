Vue.component("multiselect", window.VueMultiselect.default)
new Vue({
    el: '#main',
    data: {
        solicitud: JSON.parse(solicitudJson),
        mensajeerror: "",
        tramite: {},
        tabId: 1,
        alumno: {}

    },
    mounted: function () {
        let $vue = this;
        if ($vue.solicitud.tramite != null) {
            $vue.tramite = $vue.solicitud.tramite;
        }
    },
    methods: {

    }
});