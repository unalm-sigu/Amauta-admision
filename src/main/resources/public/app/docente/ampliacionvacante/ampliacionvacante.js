Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        raptorurl: APP.url('docente/ampliacionvacante/list'),
        dataModalAmpliacionVacante: {
            id: 'idModalAmpliacionVacante',
            header: true,
            title: 'Ampliación Vacante',
            okbtn: 'Matricular',
        },
        seccion: {grupoHoras: {}, aula: {}, seccionSuperior: {grupoHoras: {}, aula: {}}},
        alumnos: [],
        alumnoeleccionado: {},
        alumnoeleccionados: [],
        isLoadingAlumnos: false
    },
    created: function () {
        let $vue = this;
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        changeVacante(seccion) {
            let $vue = this;
            $vue.$refs.modalAmpliacionVacante.open();
            $vue.seccion = seccion;
            $vue.alumnoeleccionado = {};
            $vue.alumnoeleccionados = [];
        },
        saveModalAmpliacionVacante() {
            let $vue = this;
            if ($vue.alumnoeleccionados.length < 1) {
                notify("Tiene que seleccionar un alumno", "error");
                return;
            }

            $vue.showLoader();
            
            $.ajax({
                url: APP.url('docente/ampliacionvacante/matricular'),
                type: 'POST',
                data: JSON.stringify($vue.alumnoeleccionados),
                contentType: "application/json",
                success(response) {
                    if (response.success) {
                        $vue.$refs.modalAmpliacionVacante.close();
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        agregarAlumno() {
            let $vue = this;
            $vue.alumnoeleccionados.push($vue.alumnoeleccionado);
            $vue.alumnoeleccionado = {};
        },
        labelAlumno(item) {
            if (item.id == undefined) {
                return "";
            }
            return item.codigo + " - " + item.persona.nombreCompleto;
        },
        searchAlumnos(search) {
            let $vue = this;
            $vue.isLoadingAlumnos = true;
            $.ajax({
                url: APP.url('docente/ampliacionvacante/allAlumno'),
                type: 'POST',
                data: {nombre: search},
                success(response) {
                    $vue.isLoadingAlumnos = false;
                    if (response.success) {
                        $vue.alumnos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});


