var app = new Vue({
    el: '#asistenciaAcademicaApp',
    data: {
        URL_GRUPOS_SECCION: APP.url('academico/docente/asistenciaacademica/listGruposSecciones'),
        hola: "chau"
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, controlarSeccion(seccion, e) {
            e.preventDefault();
            location.href = APP.url('academico/docente/asistenciaacademica/' + seccion.id + '/control');
        }
    }
})