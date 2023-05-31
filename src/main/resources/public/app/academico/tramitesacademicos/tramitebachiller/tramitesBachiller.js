Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramitesAcademicos',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitebachiller/list'),
        tramiteBachiller: {},
        alumnos: [],
        isLoading: false
    }, created: function () {

    }, mounted: function () {

    }, methods: {
        nuevo() {
            let $vue = this;
            $vue.tramiteBachiller = {};
            $vue.$refs.modalTramBachiller.open();
        },
        saveTramiteBachiller() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }

            axios_.post(APP.url('academico/tramiteacademico/tramitebachiller/save'), $vue.tramiteBachiller)
                    .then(response => {
                        notify(response.data, "success");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        $vue.$refs.modalTramBachiller.close();
                    }, () => {
                        $vue.$refs.modalTramBachiller.stop();
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

            AXIOS.get(APP.url("academico/tramitecondicional/allAlumnoByNombre"), {params: {nombre: nombre}})
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
        urlReporteBachiller(item) {

            axios_blob.get(APP.url('academico/tramiteacademico/tramitebachiller/' + item.tramite.id + '/reporte'))
                    .then(response => {
                        UTIL_BLOB.save(response);
                    }, (error) => {
                        notify(error.response.data.message, 'error')
                    });

        },
        anular(item) {
            let $vue = this;
            swal('¿Desea anular el tramite bachiller del alumno?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Sí, Anular", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                axios_.post(APP.url("academico/tramiteacademico/tramitebachiller/anular"), item)
                        .then(({data}) => {
                            $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                            return swal({text: data, icon: "success", button: false, timer: 1700});
                        }, () => {
                            return swal(APP.errorComunicacion, "error");
                        });
            }).catch(err => {
                if (err) {
                    swal(APP.errorComunicacion, "error");
                } else {
                    swal.stopLoading();
                    swal.close();
                }
            });

        },
        validarTramiteAnular(item) {
            if (item.tramite.estado == 'ANU' && item.usuarioAnulaTramite != null) {
                return true;
            }
            return false;
        }
    }
})