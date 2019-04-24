var app = new Vue({
    el: '#asistenciaAcademicaApp',
    data: {
        URL_GRUPOS_SECCION: APP.url(rutaModulo + '/listGruposSecciones'),
        hola: "chau"
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, controlarSeccion(seccion, e) {
            console.log(seccion);
            e.preventDefault();
            location.href = APP.url(rutaModulo + '/' + seccion.id + '/lecciones');
        }
    }
})