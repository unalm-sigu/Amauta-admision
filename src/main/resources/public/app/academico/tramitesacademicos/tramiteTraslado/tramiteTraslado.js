Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramiteTraslado',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteTraslado/list'),
        carreras: JSON.parse(carrerasJson),
        traslado: {},
        alumnos: [],
        ciclos: JSON.parse(ciclosJson),
        ciclo: null
    },
    methods: {
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {

            /*AXIOS.get(APP.url('academico/tramiteacademico/tramiteTraslado/' + item.tramite.id + '/reporte'))
             .then(({data}) => {
             location.href = APP.url('academico/tramiteacademico/tramiteTraslado')
             });*/
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
        labelColor(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        anularTarmite(item) {
            let $vue = this;
            swal({
                text: "Seguro que desea anular el registro",
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
        },
        cambioFiltro($event) {
            let $vue = this;
            $vue.$refs.load.querie.push({name: 'ciclo', value: $event.id});
            $vue.$refs.load.loadRemoteData();
        },
        removeFiltro() {
            let $vue = this;
            $vue.$refs.load.querie.push({name: 'ciclo', value: null});
            $vue.$refs.load.loadRemoteData();
        },
    }
})