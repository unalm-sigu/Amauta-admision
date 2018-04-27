var app = new Vue({
    el: '#asistenciaAcademicaApp',
    data: {
        URL_LECCIONES: APP.url('academico/docente/asistenciaacademica/listLeccionesAcademicas'),
        seccion: null
    }, created: function () {
        this.seccion = JSON.parse(seccionJson);
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, controlarLeccion(seccion, e) {
            e.preventDefault();
            location.href = APP.url('academico/docente/asistenciaacademica/' + seccion.id + '/control');
        }
    }
})