Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#configViajeVUE',
    data: {
        observacion: "",
        justificacion: {},
        alumnosViaje: JSON.parse(alumnosViaje),
        aprobable: aprobable,
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
        viajeCurso: {},
        viajeCursoTempo: {},
        tipoCantidad: [
            {name: 'TOTAL', value: 'Total'},
            {name: 'PARCIAL', value: 'Parcial'}
        ]
    },
    beforeMount() {
        let $vue = this;
        $vue.settingViaje(JSON.parse(viajeCurso));
        $vue.settingJustifica(JSON.parse(justificacion));

    },
    mounted() {
        let $vue = this;
        $vue.activarNumeric();
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
        activarNumeric() {
            setTimeout(function () {
                $('.numeric').numeric({negative: false});
            }, 800);
        },
        classEstadoAlumno(item) {
            let estilos = {
                'RENUNCIA': 'text-danger',
                'SALUD_NO_OK': 'text-danger',
                'DENEGADO_EDAD': 'text-danger',
                'DENEGADO_CURRICULA': 'text-danger',
                'FICHA_SEC_OK': 'text-primary d-bold'};
            let rpta = estilos[item.estado];
            if (rpta === undefined) {
                return "";
            }
            return rpta;
        },
        // metodos generales
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
        getObjectAttr(obj, attr) {
            if (obj === undefined) {
                return "";
            }
            if (obj === null) {
                return "";
            }
            if (obj[attr] === undefined) {
                return "";
            }
            if (obj[attr] === null) {
                return "";
            }

            return obj[attr];
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







