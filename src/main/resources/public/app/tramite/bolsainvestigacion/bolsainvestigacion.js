Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#bolsainvestigacionVUE',
    data: {
        alumnos: [],
        supervisores: [],
        labelType: {'PEND': 'label-warning', 'CER': 'label-primary', 'ENV': 'label-success'},
        labelText: {'PEND': 'Pendiente', 'CER': 'Cerrado', 'ENV': 'Activo'},
        labelAlumnoType: {'CRE': 'label-default', 'INVI': 'label-success'},
        labelAlumnoText: {'CRE': 'Creado', 'INVI': 'Invitado', },
        alumnoBolsa: {alumno: {}},
        alumnoError: {},
        errores: [],
        bolsaInvestigacion: {
            estado: 'PEND',
            becados: 0,
            postulantes: 0
        },
        modalError: {
            id: 'modalError',
            header: false,
            title: '',
            cancelbtn: 'Ok',
            cancelclass: 'btn btn-primary',
            showaccept: false
        },
        modalAlumnoBolsa: {
            id: 'modalAlumnoBolsa',
            header: true,
            title: '',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        },
        isLoading: false
    },
    mounted() {
        this.findInfoBolsa();
    },
    methods: {
        nuevoAlumnoBolsa() {
            this.alumnoBolsa = {};
            this.modalAlumnoBolsa.title = 'Nuevo postulante';
            this.$refs.modalAlumnoBolsa.open();
        },
        findInfoBolsa() {
            axios.get('/tramite/bolsainvestigacion/find')
                    .then(response => {
                        if (response.data.success) {
                            this.bolsaInvestigacion = response.data.data;
                        }
                    })
        },
        findAlumnoBolsa(id) {
            axios.get(`/tramite/bolsainvestigacion/alumnos/${id}/find`)
                    .then(response => {
                        if (response.data.success) {
                            this.modalAlumnoBolsa.title = 'Editar postulante';
                            this.alumnoBolsa = response.data.data;
                            this.$refs.modalAlumnoBolsa.open();
                        }
                    })
        },
        saveAlumnoBolsa() {
            AXIOS.post('/tramite/bolsainvestigacion/alumnos/save', this.alumnoBolsa)
                    .then(response => {
                        if (response.data.success) {
                            this.findInfoBolsa();
                            this.$refs.raptor.loadRemoteData();
                            this.$refs.modalAlumnoBolsa.close();
                        }
                    })
        },
        enviarInvitaciones() {
            bootbox.confirm({
                message: `¿Seguro que desea enviar las invitaciones?`,
                buttons: {
                    confirm: {label: 'Sí, enviar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post('/tramite/bolsainvestigacion/enviarinvitaciones')
                                .then(response => {
                                    if (response.data.success) {
                                        this.findInfoBolsa();
                                        this.$refs.raptor.loadRemoteData();
                                    }
                                })
                    }
                }
            });
        },
        checkAlumno(alumno) {
            if (!alumno) {
                return;
            }
            this.isLoading = true;
            axios.post(`/tramite/bolsainvestigacion/alumnos/${alumno.id}/checkear`)
                    .then(response => {
                        if (response.data.data.length > 0) {
                            this.alumnoError = alumno;
                            this.errores = response.data.data;
                            this.$refs.modalError.open();
                        }
                        this.isLoading = false;
                    })
        },
        searchAlumno(name) {
            if (!name)
                return;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/bolsainvestigacion/alumnos'),
                data: name,
                contentType: "application/json",
                success: (response) => {
                    if (response.success) {
                        this.alumnos = response.data;
                    }
                },
            });
        },
        searchSupervisor(name) {
            if (!name)
                return;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/bolsainvestigacion/supervisores'),
                data: name,
                contentType: "application/json",
                success: (response) => {
                    if (response.success) {
                        this.supervisores = response.data;
                    }
                },
            });
        },
        eliminarAlumnoBolsa(item) {
            bootbox.confirm({
                message: `¿Seguro que desea eliminar al postulante ${item.alumno.persona.nombreCompleto}?`,
                buttons: {
                    confirm: {label: 'Sí, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`/tramite/bolsainvestigacion/alumnos/${item.id}/eliminar`)
                                .then(response => {
                                    if (response.data.success) {
                                        this.$refs.raptor.loadRemoteData();
                                    }
                                })
                    }
                }
            });
        },
        getNombreAlumno(alumno) {
            if (alumno == null) {
                return "";
            }
            if (alumno.persona == null) {
                return "";
            }
            return "[" + alumno.codigo + "] " + alumno.persona.nombreCompleto;
        },
        getNombreSupervisor(supervisor) {
            if (supervisor == null) {
                return "";
            }
            if (supervisor.persona == null) {
                return "";
            }
            return supervisor.persona.nombreCompleto;
        }
    }
});







