Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramitesAcademicos',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitealumnorenuncia/list'),
        tramiteAlumnoRenuncia: {},
        alumnos: [],
        isLoading: false,
        tiposTramite: JSON.parse(tipoTramiteJson),
    }, created: function () {

    }, mounted: function () {

    }, methods: {
        nuevo() {
            let $vue = this;
            $vue.tramiteAlumnoRenuncia = {};
            $vue.$refs.modalTramRenuncia.open();
        },
        saveTramiteAlumnoRenuncia() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            const tipoTramite = $vue.tramiteAlumnoRenuncia.tipoTramite.codigo;

        axios_.post(APP.url('academico/tramiteacademico/tramitealumnorenuncia/save'),
               $vue.tramiteAlumnoRenuncia,{
                   params: {
        tipoTramite: tipoTramite
    }
               })
                    .then(response => {
                        notify(response.data, "success");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        $vue.$refs.modalTramRenuncia.close();
                    }, () => {
                        $vue.$refs.modalTramRenuncia.stop();
                    });

        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        loadAlumno(nombre) {
            let $vue = this;

            if (!nombre) {
                return;
            }

            AXIOS.get(APP.url("academico/tramitecondicional/allAlumnoByNombrePregrado"), {params: {nombre: nombre}})
                    .then(({data}) => {
                        $vue.alumnos = data.data;
                    });
        },
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        validarTramiteAnular(item) {
            if (item.tramite.estado == 'ANU' && item.usuarioAnulaTramite != null) {
                return true;
            }
            return false;
        }
    }
})