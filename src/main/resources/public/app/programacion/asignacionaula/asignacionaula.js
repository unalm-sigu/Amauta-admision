var app = new Vue({
    el: '#main',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/listTramites'),
        processing: false
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }
    }
})
