
var app = new Vue({
    el: '#generacionCuotasMain',
    data: {
        ALUMNOS_URL: APP.url('posgrado/cuotasalumno/list')
    },
    created: function () {
        console.log("creeeeeea");
        /* this.grupoSeccion = JSON.parse(gpoSeccionJson);
         this.navega = JSON.parse(navigationJson);
         this.loadDataPantalla();*/

    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verTipoCarrera(item) {
            return (item.carrera.tipo == "MAE" || item.carrera.tipo == "DOC");
        },
        verFacultad(item) {
            return (item.modalidadEstudio.codigo == "PRE" && item.carrera.codigo != item.carrera.facultad.codigo);
        }
    }
});
