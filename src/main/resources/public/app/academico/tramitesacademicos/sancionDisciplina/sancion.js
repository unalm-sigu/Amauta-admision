Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/suspendidoDisciplina/list'),
        ciclos: JSON.parse(ciclosJson),
        retiroExcepcional: {},
        sancionDisciplina: {},
        alumnos: [],
        isLoading: false,
        isEditing: false,
        editingItemId: null
    },
    methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.idAlumno + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {
            return APP.url('academico/tramiteacademico/tramiteRetiroExcepcional/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.isEditing = false;
            $vue.editingItemId = null;
            $vue.sancionDisciplina = {};
            $vue.alumnos = [];
            $vue.$refs.modalNuevo.open();
        },

        customLabel(option) {
            // if (option) {
            //     return option.nombreCompleto + " - " + option.numeroDocumento;
            // }
            // return "";
            if (!option) return ''
            const nombre = option.nombreCompleto || option.persona?.nombreCompleto || ''
            const doc = option.numeroDocumento || option.persona?.numeroDocIdentidad || ''
            return `${nombre} (${doc})`
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true;

            if (nombre) {

                $.ajax({
                    url: APP.url("academico/tramitecondicional/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                });

            }

        },
        saveSancion() {
            let $vue = this;

            const url = $vue.isEditing
                ? "/academico/tramiteacademico/suspendidoDisciplina/update"
                : "/academico/tramiteacademico/suspendidoDisciplina/save";

            axios_.post(url, $vue.sancionDisciplina)
                .then(response => {
                    if (response.data.success) {
                        notify(response.data.message, "success");
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalNuevo.close();

                        $vue.isEditing = false;
                        $vue.editingItemId = null;
                    } else {
                        notify(response.data.message, "error");
                        $vue.$refs.modalNuevo.stop();

                    }
                }, (error) => {
                    console.error('Error al guardar:', error);
                    $vue.$refs.modalNuevo.stop();

                });
        },

        editar(item) {
            console.log('Item completo:', item);
            console.log('¿Hay sancionDisciplina?:', item.sancionDisciplina);
            console.log('ID del trámite:', item.tramite.id);
            let $vue = this;
            $vue.isEditing = true;

            const sancionId = item.tramite.id;
            $vue.editingItemId = sancionId;

            axios_.get(APP.url('academico/tramiteacademico/suspendidoDisciplina/get/' + sancionId))
                .then(response => {
                    if (response.data.success) {
                        const sancionData = response.data.data;

                        if (!['SOL', 'ACEP'].includes(item.tramite.estadoSancion)) {
                            notify('No se puede editar una sanción que no está en estado "Solicitado" o "Aceptado"', 'error');
                            $vue.isEditing = false;
                            return;
                        }

                        $vue.sancionDisciplina = {
                            id: sancionData.id,
                            alumno: sancionData.alumno,
                            cicloAcademico: sancionData.cicloAcademico || [],
                            motivo: sancionData.motivo || ''
                        };

                        if (sancionData.alumno) {
                            $vue.alumnos = [sancionData.alumno];
                        }

                        $vue.$refs.modalNuevo.open();
                    } else {
                        notify('Error al cargar los datos de la sanción', 'error');
                        $vue.isEditing = false;
                    }
                })
                .catch(error => {
                    console.error('Error al obtener los datos:', error);
                    notify('Error al cargar los datos de la sanción', 'error');
                    $vue.isEditing = false;
                });
        },

        labelColor(item) {
            switch (item) {
                case  "SOL":
                    return "label label-success"
                    break;
                case  "ANU":
                    return "label label-danger"
                    break;
                case  "RCHR":
                    return "label label-warning"
                    break;
                default :
                    return "label label-primary"
                    break;
            }
        },
        anularSancion(item) {
            let $vue = this;
            swal({
                text: "Seguro que desea anular el registro",
                icon: "warning",
                buttons: ["Cancelar", "Anular"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    axios_.get(APP.url('academico/tramiteacademico/suspendidoDisciplina/anular/' + item.tramite.id)).
                    then(({data}) => {
                        notify(data, 'info');
                        $vue.$refs.load.loadRemoteData();
                    }, () => {
                    });
                }
            });
        },
        anular(item) {
            let $vue = this;
            $vue.retiroExcepcional = {...item};
            $vue.$refs.modalEliminarTramite.open();
        },
        anularActionHandler() {
            let $vue = this;
            axios_.post("/academico/tramiteacademico/tramiteRetiroExcepcional/anular", $vue.retiroExcepcional)
                .then(response => {
                    $vue.$refs.modalEliminarTramite.close();
                    $vue.$refs.load.loadRemoteData();
                    notify(response.data, "success");
                }, () => {
                    $vue.$refs.modalEliminarTramite.stop();
                });
        }
    }
})