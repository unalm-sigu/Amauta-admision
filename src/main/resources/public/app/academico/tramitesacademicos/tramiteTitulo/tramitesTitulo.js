Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitetitulo/list'),
        tramiteTitulo: {},
        alumnos: [],
        isLoading: false
    },
    methods: {
        nuevo() {
            let $vue = this;
            $vue.tramiteTitulo = {};
            $vue.$refs.modalTramTitulo.open();
        },
        saveTramiteTitulo() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            axios_.post(APP.url('academico/tramiteacademico/tramitetitulo/save'), $vue.tramiteTitulo).
                    then(({data}) => {
                        notify(data, 'success');
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        $vue.$refs.modalTramTitulo.close();
                    }, () => {
                        $vue.$refs.modalTramTitulo.stop();
                    });
        },
        loadAlumno(nombre) {
            let $vue = this;

            if (!nombre) {
                return;
            }

            AXIOS.get(APP.url("academico/tramitecondicional/allAlumnoByNombre"), {params: {nombre: nombre}})
                    .then(({data}) => {
                        $vue.alumnos = data.data;
                    });

        },
        labelColor(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporteBachiller(item) {

            axios_blob.get(APP.url('academico/tramiteacademico/tramitetitulo/' + item.tramite.id + '/reporte'))
                    .then(response => {
                        UTIL_BLOB.save(response);
                    }, (error) => {
                        notify(error.response.data.message, 'error')
                    });

        },
        anular(item) {
            let $vue = this;
            swal({
                text: "¿Desea anular el tramite titulo del alumno?",
                icon: "warning",
                buttons: ["Cancelar", "Anular"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    axios_.post(APP.url('academico/tramiteacademico/tramitetitulo/anular/'), item).
                            then(({data}) => {
                                notify(data, 'info');
                                $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                            }, () => {
                            });
                }
            });
        }

    }
})