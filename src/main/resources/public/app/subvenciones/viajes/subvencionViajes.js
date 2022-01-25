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
            let estilos = {'CREADO': 'warning', 'PENDIENTE': 'warning', 'DESAPROBADO': 'danger'};
            let estilosDocente = {'CREADO': 'warning', 'PENDIENTE': 'success', 'DESAPROBADO': 'danger'};
            let estilosJefeDpto = {'CREADO': 'default', 'PENDIENTE': 'warning', 'APROBADO': 'success', 'DESAPROBADO': 'danger'};

            let rpta = estilos[item.estadoViaje];
            if (item.esDocente) {
                rpta = estilosDocente[item.estadoViaje];
            } else if (item.esJefeDpto) {
                rpta = estilosJefeDpto[item.estadoViaje];
            }

            if (rpta === undefined) {
                return "label-primary";
            }
            return "label-" + rpta;
        },
        loadCursos() {
            let $vue = this;
            $vue.axios(VUE_AXIOS.structGetData({url: `/${rutaModulo}/allCursos`}))
                    .then(response => $vue.cursos = response.data.data)
                    .catch(error => console.log(error));
        },
        loadSecciones(curso) {
            let $vue = this;
            $vue.secciones = [];
            let cursoSend = {
                id: curso.id
            };

            $vue.axios(VUE_AXIOS.structGetData({url: `/${rutaModulo}/allSecciones`, body: cursoSend}))
                    .then(response => $vue.secciones = response.data.data)
                    .catch(error => console.log(error));
        },
        loadAlumnos(seccion) {
            let $vue = this;
            $vue.alumnos = [];
            let seccionSend = {
                id: seccion.id
            };

            $vue.axios(VUE_AXIOS.structGetData({url: `/${rutaModulo}/allAlumnos`, body: seccionSend}))
                    .then(response => $vue.alumnos = response.data.data)
                    .catch(error => console.log(error));
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

            $vue.axios(VUE_AXIOS.structModalClose({
                url: `/${rutaModulo}/saveViaje`,
                body: $vue.viajeCursoSelect,
                modal: $vue.$refs.modalAddCurso,
                raptor: $vue.$refs.raptorViajes
            }));
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

            $vue.axios(VUE_AXIOS.structModalConfirm({
                url: `/${rutaModulo}/solicitarAprobarViaje`,
                body: viaje,
                modal: $vue.$refs.modalConfirmAction,
                raptor: $vue.$refs.raptorViajes
            }));
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

            $vue.axios(VUE_AXIOS.structModalConfirm({
                url: `/${rutaModulo}/aprobarViaje`,
                body: viaje,
                modal: $vue.$refs.modalConfirmAction,
                raptor: $vue.$refs.raptorViajes
            }));
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

            $vue.axios(VUE_AXIOS.structModalConfirm({
                url: `/${rutaModulo}/aprobarViaje`,
                body: viaje,
                modal: $vue.$refs.modalConfirmAction,
                raptor: $vue.$refs.raptorViajes
            }));
        },
        puedeEditarse(item) {
            if (item.esDocente && item.estadoViaje === "CREADO") {
                return true;
            }
            return false;
        },
        puedeAprobarse(item) {
            if (!item.esDocente && item.esJefeDpto && item.estadoViaje === "PENDIENTE") {
                return true;
            }
            return false;
        },
        puedeDesaprobarse(item) {
            if (!item.esDocente && item.estadoViaje === "PENDIENTE") {
                return true;
            }
            return false;
        },
        verDetalle(item) {
            let $vue = this;
            location.href = APP.url(`${rutaModulo}/${item.id}/configurar`) + $vue.getOrigenURL();
        },
        axios(config) {
            return new Promise((resolve, reject) => {
                let sender = {};
                if (config.body) {
                    sender = config.body;
                }

                if (config.beginProcessing && config.modal) {
                    config.modal.beginProcessing();
                }

                axios.post(config.url, sender).then(response => {
                    if (config.modal && config.close) {
                        config.modal.confirmReaction(response.data.success);
                    } else if (config.modal && !config.close) {
                        config.modal.confirmReaction(false);
                    }

                    if (response.data.success) {
                        if (config.raptor) {
                            config.raptor.loadRemoteData();
                        }
                        if (config.accion) {
                            config.accion();
                        }
                        if (config.notificar) {
                            notify(response.data.message, "info");
                        }
                        resolve(response);

                    } else {
                        if (config.notificarError) {
                            notify(response.data.message, "warning");
                        }
                        reject(new Error(response.data.message));
                    }

                }).catch(e => {
                    if (config.modal) {
                        config.modal.confirmReaction(false);
                    }
                    if (config.notificarErrorCatch) {
                        notify(Messages.errorComunicacion, "error");
                    }
                    reject(new Error(Messages.errorComunicacion));
                });
            });
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







