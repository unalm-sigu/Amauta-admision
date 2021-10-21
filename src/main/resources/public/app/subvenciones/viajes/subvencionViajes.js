Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#viajesVUE',
    data: {
        itemSelect: {},
        modalAddCurso: VUE_MODAL.structFormAjax({
            id: "id-modal-add-curso",
            header: true,
            title: 'Crear Viaje de Curso',
            okbtn: 'Crear viaje',
            okclass: "btn-primary",
            form: "id-form-crear-viaje"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "id-modal-confirm"
        }),
        cursos: [],
        secciones: [],
        alumnos: [],
        viajeCursoSelect: {}
    },
    mounted() {
        let $vue = this;
        $vue.loadCursos();
    },
    methods: {
        classEstado(item) {
            let estilos = {'PENDIENTE': 'danger', 'PAGADO': 'success', 'OBSERVA': 'warning', 'ANULADO': 'dark', 'VENCIDO': 'danger'};
            let rpta = estilos[item.estado];
            if (rpta === undefined) {
                return "label-primary";
            }
            return "label-" + rpta;
        },
        loadCursos() {
            let $vue = this;
            axios.post(`/${rutaModulo}/allCursos`).then(response => {
                if (response.data.success) {
                    $vue.cursos = response.data.data;
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        loadSecciones(curso) {
            let $vue = this;
            $vue.secciones = [];
            let cursoSend = {
                id: curso.id
            };

            axios.post(`/${rutaModulo}/allSecciones`, cursoSend).then(response => {
                if (response.data.success) {
                    $vue.secciones = response.data.data;
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        loadAlumnos(seccion) {
            let $vue = this;
            $vue.alumnos = [];
            let seccionSend = {
                id: seccion.id
            };

            axios.post(`/${rutaModulo}/allAlumnos`, seccionSend).then(response => {
                if (response.data.success) {
                    $vue.alumnos = response.data.data;
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        verViajeNuevo() {
            let $vue = this;

            $vue.viajeCursoSelect = {};
            $vue.secciones = [];
            $vue.alumnos = [];
            $vue.$refs.modalAddCurso.open();
        },
        verEditarViaje(item) {
            let $vue = this;

            $vue.viajeCursoSelect = JSON.parse(JSON.stringify(item));
            let nombreAlumno = item.alumnoDelegado.persona.apellidosNombres;
            $vue.viajeCursoSelect.alumnoDelegado.apellidosNombres = nombreAlumno;

            $vue.loadSecciones($vue.viajeCursoSelect.curso);
            $vue.loadAlumnos($vue.viajeCursoSelect.seccion);
            $vue.$refs.modalAddCurso.open();
        },
        saveViaje() {
            let $vue = this;

            var form = $("#" + $vue.modalAddCurso.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalAddCurso.beginProcessing();
            axios.post(`/${rutaModulo}/saveViaje`, $vue.viajeCursoSelect).then(response => {
                $vue.$refs.modalAddCurso.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorViajes.loadRemoteData();
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalAddCurso.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verSolicitarAprobacion(item) {
            let $vue = this;
            $vue.viajeCursoSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                id: "id-modal-confirm",
                message: "¿Está seguro que desea solicitar la aprobación de este viaje de curso al Departamento Académico?",
                okbtn: "Si, solicitar",
                okclass: "btn-success",
                okaction: $vue.solicitarAprobacion
            });

            $vue.$refs.modalConfirmAction.open();
        },
        solicitarAprobacion() {
            let $vue = this;
            let viaje = {
                id: $vue.viajeCursoSelect.id
            };

            axios.post(`/${rutaModulo}/solicitarAprobarViaje`, viaje).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorViajes.loadRemoteData();
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verAprobar(item) {
            let $vue = this;
            $vue.viajeCursoSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea aprobar esta solicitud de Viaje de Curso?";
            $vue.configConfirmAction.okbtn = "Si, aprobar";
            $vue.configConfirmAction.okclass = "btn-success";
            $vue.configConfirmAction.okaction = $vue.aprobarViaje;
            $vue.$refs.modalConfirmAction.open();
        },
        aprobarViaje() {
            let $vue = this;
            let viaje = {
                id: $vue.viajeCursoSelect.id,
                estadoViaje: "APROBADO"
            };

            axios.post(`/${rutaModulo}/aprobarViaje`, viaje).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorViajes.loadRemoteData();
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verDesaprobar(item) {
            let $vue = this;
            $vue.viajeCursoSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea desaprobar esta solicitud de Viaje de Curso?";
            $vue.configConfirmAction.okbtn = "Si, desaprobar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = $vue.desaprobarViaje;
            $vue.$refs.modalConfirmAction.open();
        },
        desaprobarViaje() {
            let $vue = this;
            let viaje = {
                id: $vue.viajeCursoSelect.id,
                estadoViaje: "DESAPROBADO"
            };

            axios.post(`/${rutaModulo}/aprobarViaje`, viaje).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorViajes.loadRemoteData();
                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        puedeEditarse(item) {
            if (item.esDocente && item.estadoViaje === "CREADO") {
                return true;
            }
            return false;
        },
        puedeAprobarse(item) {
            if (item.esJefeDpto && item.estadoViaje === "PENDIENTE") {
                return true;
            }
            return false;
        },
        puedeDesaprobarse(item) {
            if (item.esJefeDpto && item.estadoViaje === "PENDIENTE") {
                return true;
            }
            return false;
        },
        verDetalle(item) {
            let $vue = this;
            location.href = APP.url(`${rutaModulo}/${item.id}/configurar`) + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        activarNumeric() {
            setTimeout(function () {
                $('.numeric').numeric({negative: false});
            }, 800);
        },
        getObjectId(obj) {
            if (obj === undefined) {
                return "";
            }
            if (obj === null) {
                return "";
            }
            if (obj.id === undefined) {
                return "";
            }
            if (obj.id === null) {
                return "";
            }

            return obj.id;
        },
        commas(n) {
            var options = {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            };
            return Number(n).toLocaleString('en', options);
        }
    }
});







