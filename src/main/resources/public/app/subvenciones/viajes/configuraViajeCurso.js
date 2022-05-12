Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#configViajeVUE',
    data: {
        observacion: "",
        justificacion: {},
        alumnosViaje: JSON.parse(alumnosViaje),
        aprobable: aprobable,
        esDocenteCreador: esDocenteCreador,
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "id-modal-confirm"
        }),
        modalObservaJustifica: VUE_MODAL.structFormAjax({
            id: "id-modal-observa",
            header: true,
            title: 'Observar justificación',
            okbtn: 'Observar justificación',
            okclass: "btn-warning",
            form: "id-form-observa"
        }),
        modalAddObservacion: VUE_MODAL.structFormAjax({
            id: "id-modal-add-observacion",
            header: true,
            title: 'Agregar observación',
            okbtn: 'Agregar',
            okclass: "btn-warning",
            form: "id-form-add-observa"
        }),
        viajeCurso: {},
        viajeCursoTempo: {},
        objecion: {},
        objecionSelect: {},
        objecionesAll: [],
        objecionesPendientes: [],
        objecionesLevantadas: [],
        objecionesViaje: [],
        objecionesJustificacion: [],
        objecionesCronograma: [],
        objecionesProforma: [],
        tipoCantidad: [
            {name: 'TOTAL', value: 'Total'},
            {name: 'PARCIAL', value: 'Parcial'}
        ]
    },
    beforeMount() {
        let $vue = this;
        $vue.settingViaje(JSON.parse(viajeCurso));
        $vue.settingJustifica(JSON.parse(justificacion));
        $vue.settingObjeciones(JSON.parse(objeciones));
    },
    mounted() {
        let $vue = this;
        myUtils.activarNumeric();
    },
    computed: {
        verAddObjecionViaje() {
            return false;
        },
        verAddObjecionJustificacion() {
            let $vue = this;
            let estados = ['PENDIENTE', 'LEVANTADO'];
            let bloqueantes = $vue.objecionesJustificacion.filter(objecion => estados.includes(objecion.estado));
            if (bloqueantes.length > 0) {
                return false;
            }

            if (esDocenteCreador) {
                return $vue.viajeCurso.estadoViaje === 'JUSTIFICADO';
            }

            return false;
        },
        verAddObjecionCronograma() {
            return false;
        },
        verAddObjecionProforma() {
            return false;
        },
        enviarVoBoAdmin() {
            if (!(this.objecionesPendientes.length === 0 && this.objecionesLevantadas.length === 0)) {
                return false;
            }

            if (esDocenteCreador) {
                return this.viajeCurso.estadoViaje === 'JUSTIFICADO';
            }

            return false;
        },
        enviarObservaciones() {
            if (!(this.objecionesPendientes.length > 0 && this.objecionesLevantadas.length === 0)) {
                return false;
            }

            if (this.esDocenteCreador) {
                return this.viajeCurso.estadoViaje === 'JUSTIFICADO';
            }

            return false;
        }
    },
    methods: {
        settingViaje(viajeNuevo) {
            let $vue = this;
            let viajeTempo = JSON.parse(JSON.stringify(viajeNuevo));
            for (let i in viajeTempo.cronogramasViaje) {
                let item = JSON.parse(JSON.stringify(viajeTempo.cronogramasViaje[i]));
                item.modificar = false;
                viajeTempo.cronogramasViaje[i] = item;
            }

            for (let i in viajeTempo.proformasViaje) {
                let item = JSON.parse(JSON.stringify(viajeTempo.proformasViaje[i]));
                item.modificar = false;
                viajeTempo.proformasViaje[i] = item;
            }

            $vue.viajeCurso = JSON.parse(JSON.stringify(viajeTempo));
            $vue.viajeCursoTempo = JSON.parse(JSON.stringify(viajeTempo));
            $vue.aprobable = $vue.viajeCurso.aprobable;
        },
        settingJustifica(justiInput) {
            let $vue = this;
            let justifica = JSON.parse(JSON.stringify(justiInput));
            for (let i in justifica.itemsJustificacion) {
                let item = JSON.parse(JSON.stringify(justifica.itemsJustificacion[i]));
                item.modificar = false;

                for (let j in item.justificacionesAlumnos) {
                    let gastoAlu = JSON.parse(JSON.stringify(item.justificacionesAlumnos[j]));
                    gastoAlu.modificar = false;
                    item.justificacionesAlumnos[j] = gastoAlu;
                }

                justifica.itemsJustificacion[i] = item;
            }

            $vue.justificacion = JSON.parse(JSON.stringify(justifica));
        },
        settingObjeciones(objeciones) {
            this.objecionesAll = objeciones;

            this.objecionesPendientes = this.objecionesAll.filter(observa => observa.estado === 'PENDIENTE');
            this.objecionesLevantadas = this.objecionesAll.filter(observa => observa.estado === 'LEVANTADO');

            this.objecionesViaje = this.objecionesAll.filter(observa => observa.contexto === 'VIAJE');
            this.objecionesJustificacion = this.objecionesAll.filter(observa => observa.contexto === 'JUSTIFICACION');
            this.objecionesCronograma = this.objecionesAll.filter(observa => observa.contexto === 'CRONOGRAMA');
            this.objecionesProforma = this.objecionesAll.filter(observa => observa.contexto === 'PROFORMA');
        },
        verAprobarJustifica() {
            let $vue = this;

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                id: "id-modal-confirm",
                message: "¿Está seguro que desea dar por aprobado la justificación de gastos de este Viaje de Curso?",
                okbtn: "Si, aprobar",
                okclass: "btn-success",
                okaction: $vue.aprobarJustifica
            });

            $vue.$refs.modalConfirmAction.open();
        },
        aprobarJustifica() {
            let $vue = this;
            let viajeCursoSend = {
                id: $vue.viajeCurso.id
            };

            axios.post(`/${rutaModulo}/aprobarJustificacion`, viajeCursoSend).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.loadViaje();
                    $vue.loadJustificaciones();

                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verDesaprobarJustifica() {
            let $vue = this;
            $vue.observacion = "";
            $vue.$refs.modalObservaJustifica.open();
        },
        verAddObjecion(contexto) {
            let $vue = this;
            $vue.objecion = {};
            $vue.objecion.contexto = contexto;
            $vue.objecion.viajeCurso = {id: $vue.viajeCurso.id};
            $vue.modalAddObservacion.title = "Agregar Observación";
            $vue.modalAddObservacion.okbtn = "Agregar";
            $vue.$refs.modalAddObservacion.open();

        },
        saveObservacion() {
            let $vue = this;

            let form = $("#" + $vue.modalAddObservacion.form);
            if (!form.parsley().validate()) {
                return;
            }

            myUtils.axios(VUE_AXIOS.structModalClose({
                url: `/${rutaModulo}/addObjecion`,
                body: $vue.objecion,
                modal: $vue.$refs.modalAddObservacion
            })).then(response => {
                $vue.loadObjeciones();
            });

        },
        verBorrarObjecion(item) {
            let $vue = this;
            $vue.objecionSelect = JSON.parse(JSON.stringify(item));

            this.configConfirmAction = VUE_MODAL.structConfirm({
                id: "id-modal-confirm",
                message: "¿Está seguro que desea eliminar esta observación?",
                okbtn: "Si, eliminar observación",
                okclass: "btn-warning",
                okaction: this.borrarObjecion
            });

            $vue.$refs.modalConfirmAction.open();
        },
        borrarObjecion() {
            let $vue = this;

            myUtils.axios(VUE_AXIOS.structModalConfirm({
                url: `/${rutaModulo}/deleteObjecion`,
                body: {id: $vue.objecionSelect.id},
                modal: $vue.$refs.modalConfirmAction
            })).then(response => {
                $vue.loadObjeciones();
            });
        },
        loadObjeciones() {
            let $vue = this;
            let viajeCursoSend = {
                id: $vue.viajeCurso.id
            };

            myUtils.axios(VUE_AXIOS.structGetData({
                url: `/${rutaModulo}/allObjecionesViaje`,
                body: viajeCursoSend
            })).then(response => {
                this.settingObjeciones(response.data.data);
            });
        },
        puedeBorrarObjecion(item) {
            let $vue = this;
            if (item.estado === 'PENDIENTE' && $vue.viajeCurso.estadoViaje === 'JUSTIFICADO' && esDocenteCreador) {
                return true;
            }
            return false;
        },
        puedeReplicarObjecion(item) {
            let $vue = this;
            if (item.estado === 'LEVANTADO' && $vue.viajeCurso.estadoViaje === 'JUSTIFICADO' && esDocenteCreador) {
                return true;
            }
            return false;
        },
        replicarRptaObjecion(item, aceptar) {
            let $vue = this;
            $vue.objecionSelect = JSON.parse(JSON.stringify(item));

            if (aceptar) {
                $vue.configConfirmAction = VUE_MODAL.structConfirm({
                    id: "id-modal-confirm",
                    message: "¿Está seguro que desea aceptar esta respuesta?",
                    okbtn: "Si, aprobar",
                    okclass: "btn-success",
                    okaction: $vue.aprobarRptaObjecion
                });
                $vue.$refs.modalConfirmAction.open();

            } else {
                $vue.objecion = {};
                $vue.objecion.contexto = item.contexto;
                $vue.objecion.viajeCurso = {id: $vue.viajeCurso.id};
                $vue.objecion.objecionOrigen = $vue.objecionSelect;
                $vue.modalAddObservacion.title = "Rechazar respuesta a observación";
                $vue.modalAddObservacion.okbtn = "Rechazar respuesta";
                $vue.$refs.modalAddObservacion.open();
            }

        },
        aprobarRptaObjecion() {
            let $vue = this;

            myUtils.axios(VUE_AXIOS.structModalConfirm({
                url: `/${rutaModulo}/aprobarRespuestaObjecion`,
                body: {id: $vue.objecionSelect.id},
                modal: $vue.$refs.modalConfirmAction
            })).then(() => {
                $vue.loadObjeciones();
            });
        },
        verEnviarVoBoAdmin() {
            let $vue = this;

            if (!esDocenteCreador) {
                notify("Acción permitida solo para el docente del curso", "error");
                return;
            }

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                id: "id-modal-confirm",
                message: "¿Está seguro que desea dar el VºBº a la justificación de este Viaje de Curso?",
                okbtn: "Si, aprobar",
                okclass: "btn-success",
                okaction: () => {

                    myUtils.axios(VUE_AXIOS.structModalConfirm({
                        url: `/${rutaModulo}/aprobarJustificacion`,
                        body: {id: $vue.viajeCurso.id},
                        modal: $vue.$refs.modalConfirmAction
                    })).then(response => {
                        $vue.loadViaje();
                        $vue.loadJustificaciones();
                        $vue.loadObjeciones();
                    });
                }
            });

            $vue.$refs.modalConfirmAction.open();
        },
        verEnviarObjeciones() {
            let $vue = this;

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                id: "id-modal-confirm",
                message: "¿Está seguro que desea enviar las observaciones a esta subvención de Viaje de Curso?",
                okbtn: "Si, enviar",
                okclass: "btn-danger",
                okaction: () => {
                    myUtils.axios(VUE_AXIOS.structModalConfirm({
                        url: `/${rutaModulo}/enviarObservacion`,
                        body: {id: $vue.viajeCurso.id},
                        modal: $vue.$refs.modalConfirmAction
                    })).then(response => {
                        $vue.loadViaje();
                        $vue.loadJustificaciones();
                        $vue.loadObjeciones();
                    });
                }
            });

            $vue.$refs.modalConfirmAction.open();
        },
        classObjecion(item) {
            if (item.estado === 'PENDIENTE') {
                return 'label-warning';
            }
            if (item.estado === 'LEVANTADO') {
                return 'label-primary';
            }
            if (item.estado === 'RECHAZADO') {
                return 'label-danger';
            }
            if (item.estado === 'ACEPTADO') {
                return 'label-success';
            }
            return '';
        },
        classItemJustifica(item) {
            let $vue = this;
            if (item.estadoJustificacion === "PENDIENTE") {
                return "label-warning";
            } else if (item.estadoJustificacion === "ACEPTADA") {
                return "label-success";
            } else if (item.estadoJustificacion === "RECHAZADA") {
                return "label-danger";
            }
            return "label-default";
        },
        classImporte(item) {
            if (item.estadoJustificacion === 'ACEPTADA') {
                return "text-primary";
            } else if (item.estadoJustificacion === 'RECHAZADA') {
                return "text-danger tachado";
            }
            return "";
        },
        saveObservaJustifica() {
            let $vue = this;
            let viajeCursoSend = {
                id: $vue.viajeCurso.id,
                observacion: $vue.observacion
            };

            var form = $("#" + $vue.modalObservaJustifica.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalObservaJustifica.beginProcessing();
            axios.post(`/${rutaModulo}/observaJustificacion`, viajeCursoSend).then(response => {
                $vue.$refs.modalObservaJustifica.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.loadViaje();
                    $vue.loadJustificaciones();

                } else {
                    notify(response.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalObservaJustifica.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        loadViaje() {
            let $vue = this;
            let viajeCursoSend = {
                id: $vue.viajeCurso.id
            };

            axios.post(`/${rutaModulo}/findViaje`, viajeCursoSend).then(response => {
                if (response.data.success) {
                    $vue.settingViaje(response.data.data);

                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        loadJustificaciones() {
            let $vue = this;
            let viajeCursoSend = {
                id: $vue.viajeCurso.id
            };

            axios.post(`/${rutaModulo}/findJustificacion`, viajeCursoSend).then(response => {
                if (response.data.success) {
                    $vue.settingJustifica(response.data.data);

                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        verTemporal(bean) {
            let $vue = this;
            let ruta = "";

            if (bean.id) {
                ruta = bean.ruta;
            } else {
                ruta = APP.url("archivo/verArchivoTemporal/") + bean.nombre;
            }

            window.open(ruta, "_blank");
        },
        classBtnFactura(item) {
            let $vue = this;
            if (item.factura === undefined) {
                return "btn-gray";
            } else if (item.factura.ruta === undefined) {
                return "btn-gray";
            } else if (item.factura.ruta === "") {
                return "btn-gray";
            }
            return "btn-primary";
        },
        classEstadoAlumno(item) {
            let estilos = {
                'RENUNCIA': 'text-danger',
                'SEPARADO': 'text-danger',
                'SALUD_NO_OK': 'text-danger',
                'DENEGADO_EDAD': 'text-danger',
                'DENEGADO_CURRICULA': 'text-danger',
                'FICHA_SEC_OK': 'text-primary d-bold',
                'INSCRITO': 'text-primary d-bold'
            };
            let rpta = estilos[item.estado];
            if (rpta === undefined) {
                return "";
            }
            return rpta;
        },
        // metodos generales
        getObjectId(obj) {
            return myUtils.getObjectId(obj);
        },
        getObjectAttr(obj, attr) {
            return myUtils.getObjectAttr(obj, attr);
        },
        commas(n) {
            return myUtils.commas(n);
        }
    }
});
