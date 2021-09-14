Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramiteTraslado',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteTraslado/list'),
        carreras: JSON.parse(carrerasJson),
        traslado: {},
        alumnos: [],
    },
    methods: {
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {
            return APP.url('academico/tramiteacademico/tramiteTraslado/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.traslado = {};
            $vue.$refs.modalTraslado.open();
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
        saveTraslado() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            axios_.post(APP.url('academico/tramiteacademico/tramiteTraslado/save'), $vue.traslado).
                    then(({data}) => {
                        notify(data, 'success');
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalTraslado.close();
                    }, () => {
                        $vue.$refs.modalTraslado.stop();
                    });
        },
        labelColor(item) {
            switch (item) {
                case  'SOL':
                    return "label label-default"
                    break;
                case  'ANU':
                    return "label label-danger"
                    break;
                default :
                    return "label label-primary"
                    break;
            }
        },
        anularTarmite(item) {
            let $vue = this;
            swal({
                title: "Seguro que desea anular el registro",
                icon: "warning",
                buttons: ["Cancelar", "Anular"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    axios_.get(APP.url('academico/tramiteacademico/tramiteTraslado/anular/' + item.id)).
                            then(({data}) => {
                                notify(data, 'info');
                                $vue.$refs.load.loadRemoteData();
                            }, () => {
                            });
                }
            });
        }
    }
})