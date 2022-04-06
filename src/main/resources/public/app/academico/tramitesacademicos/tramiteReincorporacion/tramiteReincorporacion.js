Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#retiroExcepcional',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteReincorporacion/list'),
        ciclos: JSON.parse(ciclosJson),
        reincorporacion: {},
        alumnos: [],
        isLoading: false
    },
    methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {
            return APP.url('academico/tramiteacademico/tramiteReincorporacion/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.reincorporacion = {};
            $vue.$refs.modalRincorporacion.open();
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
        saveRincorporacion() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            axios_.post(APP.url('academico/tramiteacademico/tramiteReincorporacion/save'), $vue.reincorporacion).
                    then(({data}) => {
                        notify(data, 'success');
                        $vue.$refs.modalRincorporacion.close();
                        $vue.$refs.load.loadRemoteData();
                    }, () => {
                        $vue.$refs.modalRincorporacion.stop();
                    });
        },
        labelColor(estado) {
            return "label " + APP.getEstadoClass(estado);
        }
    }
})