var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitebachiller/list'),

    }, created: function () {

    }, mounted: function () {

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        urlReporteBachiller(item) {
            let $vue = this;
            return APP.url('academico/tramiteacademico/tramitebachiller/' + item.tramite.id + '/reporte');
        }
    }
})