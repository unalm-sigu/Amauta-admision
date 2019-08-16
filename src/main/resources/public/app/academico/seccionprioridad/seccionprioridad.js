Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#seccionprioridad',
    data: {
        alumno: "",
        seccion: {},
        secciones: [],
        matriculaSec: []
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        loadseccion(nombre) {
            let $vue = this;

            $.ajax({
                url: APP.url('academico/matriculaprioridad/findSeccion'),
                type: 'post',
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.secciones = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                }
            });

        },
        customLabel( { seccion }) {
         
            return `${seccion.grupoSeccion.curso.nombre} – ${seccion.codigo2}`;
        },
        color(index) {
            let $vue = this;
            if (index == $vue.matriculaSec.length - 1) {
                return "text-primary";
            }
            return;
        },
        buscar() {
            let $vue = this;
            $vue.matriculaSec = [];

            $.ajax({
                url: APP.url('academico/matriculaprioridad/findseccion'),
                type: 'post',
                data: {codigo: $vue.alumno, seccion: $vue.seccion.codigo2},
                success: function (response) {
                    if (response.success) {
                        $vue.matriculaSec = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                }
            });
        }
    }
})