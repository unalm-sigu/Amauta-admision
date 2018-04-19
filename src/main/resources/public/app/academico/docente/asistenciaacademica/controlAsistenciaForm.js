var app = new Vue({
    el: '#controlAsistenciaApp',
    data: {
        URL_MATRICULAS_SECCION: APP.url("academico/docente/asistenciaacademica/listMatriculasSeccionDyna"),
        seccion: JSON.parse(seccionJson),
        matriculasSeccion: null
    }, created: function () {
        this.seccion = JSON.parse(seccionJson);
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }
    }
})