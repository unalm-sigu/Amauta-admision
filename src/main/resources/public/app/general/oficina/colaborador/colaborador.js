Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

new Vue({
    el: '#colaboradorVue',
    mixins: [VueLoader],
    data: {
        colaboradoreURL: APP.url(rutaModulo + '/listColaboradores'),
        cargosForm: JSON.parse(cargosJson),
        areasForm: JSON.parse(areasJson),
        estadosEmp: JSON.parse(estadosEmpJson),
        persona: {},
        colaborador: {},
        perfilCompania: {},
        dataModalFuncion: VUE_MODAL.structFormAjax({
            id: "idModalFuncion",
            form: "formFuncion",
            title: 'Nueva Función'
        }),
        dataModalCargo: VUE_MODAL.structFormAjax({
            id: "idModalCargo",
            form: "formCargo",
            title: 'Nuevo Cargo'
        }),
        dataCargosOficina: VUE_MODAL.structInfo({
            id: "idModalCargosOficina",
            header: true,
            title: 'titulos cargos oficina'
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "modalConfirmGeneric"
        }),
        dataModalDespedir: VUE_MODAL.structFormAjax({
            id: "modalDespidoEmpleado",
            form: "formDespidoEmpleado"
        }),
        dataModalActivar: VUE_MODAL.structFormAjax({
            id: "modalActivarEmpleado",
            form: "formActivarEmpleado"
        }),
        funcion: {},
        cargo: {},
        empleado: {},
        cargos: [],
        divElegido: 0,
        resumen: {
            activos: 0,
            vacaciones: 0,
            retirado: 0,
            descanso: 0,
            permiso: 0,
            despedido: 0
        },
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        }
    },
    computed: {

    },
    created() {
    },
    mounted: function () {
        let $vue = this;
        $vue.getResumen();
        $global.$on("showFuncionesColaborador", function (id) {
            $vue.showFuncionesColaborador(id);
        });

    },
    methods: {
        nuevoColaborador() {
            let $vue = this;
//            $vue.oficina
            location.href = APP.url(rutaModulo + "/nuevoColaborador") + $vue.getOrigenURL();

        },
        nuevoCargo() {
            let vue = this;
            vue.funcion = {id: null};
            vue.$refs.modalAddCargo.open();
        },
        saveCargo() {
            var vue = this;

            var form = $('#' + vue.dataModalCargo.form);
            if (!form.parsley().validate()) {
                return;
            }

            vue.$refs.modalAddCargo.beginProcessing();
            axios.post(APP.url(rutaModulo + '/savecargo'), vue.cargo)
                    .then(response => {
                        vue.$refs.modalAddCargo.confirmReaction(response.data.success);
                        if (response.data.success) {
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                vue.$refs.modalAddCargo.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });

        },
        nuevaFuncion() {
            let vue = this;
            vue.funcion = {id: null};
            vue.$refs.modalAddFuncion.open();
        },
        saveFuncion() {
            var vue = this;

            var form = $('#' + vue.dataModalFuncion.form);
            if (!form.parsley().validate()) {
                return;
            }

            vue.$refs.modalAddFuncion.beginProcessing();
            axios.post(APP.url(rutaModulo + '/savefuncion'), vue.funcion)
                    .then(response => {
                        vue.$refs.modalAddFuncion.confirmReaction(response.data.success);
                        if (response.data.success) {
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                vue.$refs.modalAddFuncion.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });

        },
        verCargo() {
            let vue = this;
            vue.dataCargosOficina.title = "Cargos de la oficina";
            vue.$refs.modalCargosOficina.open();

            axios.post(APP.url(rutaModulo + '/allCargosOficina'))
                    .then(response => {
                        if (response.data.success) {
                            vue.cargos = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                notify(GlobalMessages.errorComunicacion, "error");
            });
        },
        verFuncion() {
            let vue = this;
            vue.dataCargosOficina.title = "Funciones de la oficina";
            vue.$refs.modalCargosOficina.open();

            axios.post(APP.url(rutaModulo + '/allFuncionesOficina'))
                    .then(response => {
                        if (response.data.success) {
                            vue.cargos = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                notify(GlobalMessages.errorComunicacion, "error");
            });

        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        classEstado(item) {
            var color = {ACT: "success", VAC: "warning", RET: "default", DSC: "warning", PER: "warning", DESP: "danger"};
            return "label-" + color[item.estado];
        },
        classPanel(nroPanel) {
            let $vue = this;
            if ($vue.divElegido == nroPanel) {
                return 'bg-light';
            }
            return '';
        },
        verEmpleadosEstado(nroPanel, estado) {
            let $vue = this;
            console.log(nroPanel)
            console.log($vue.divElegido)
            $vue.$refs.raptorColaboran.querie = [];
            if ($vue.divElegido == nroPanel) {
                $vue.divElegido = 0;
            } else {
                $vue.divElegido = nroPanel;
                $vue.$refs.raptorColaboran.querie.push({name: "estado", value: estado});
            }
            $vue.$refs.raptorColaboran.loadRemoteData();
        },
        verChangeEstado(item, estado) {
            let $vue = this;

            $vue.empleado = Object.assign({}, item, {});
            $vue.empleado.estado = estado.name;

            var msg = "¿Seguro desea cambiar a <strong>" + item.persona.nombreCompleto
                    + "</strong> al estado <strong class='text-danger'>"
                    + estado.value + "</strong>?";
            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: msg,
                okbtn: "Si, cambiar",
                okaction: $vue.changeEstado
            });
            $vue.$refs.modalConfirmAction.open();
        },
        changeEstado() {
            let $vue = this;

            if ("/RET/DESP/".indexOf($vue.empleado.estado) > 0) {
                $vue.$refs.modalConfirmAction.confirmReaction(true);
                $vue.$refs.modalDespedirEmpleado.open();

                return;
            }
            if ("/ACT/".indexOf($vue.empleado.estado) > 0) {
                $vue.$refs.modalConfirmAction.confirmReaction(true);
                $vue.$refs.modalActivarEmpleado.open();
                return;
            }

            axios.post(APP.url(rutaModulo + '/updateEstado'), $vue.empleado)
                    .then(response => {
                        $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.getResumen();
                            $vue.$refs.raptorColaboran.loadRemoteData();
                            notify(response.data.message, "info");

                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });

        },
        despedirEmpleado() {
            let $vue = this;
            var form = $('#' + $vue.dataModalDespedir.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalDespedirEmpleado.beginProcessing();
            axios.post(APP.url(rutaModulo + '/updateEstado'), $vue.empleado)
                    .then(response => {
                        $vue.$refs.modalDespedirEmpleado.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.getResumen();
                            $vue.$refs.raptorColaboran.loadRemoteData();
                            notify(response.data.message, "info");

                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                $vue.$refs.modalDespedirEmpleado.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });
        },
        activarEmpleado() {
            let $vue = this;
            var form = $('#' + $vue.dataModalActivar.form);
            if (!form.parsley().validate()) {
                return;
            }
            
            $vue.$refs.modalActivarEmpleado.beginProcessing();
            axios.post(APP.url(rutaModulo + '/updateEstado'), $vue.empleado)
                    .then(response => {
                        $vue.$refs.modalActivarEmpleado.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.getResumen();
                            $vue.$refs.raptorColaboran.loadRemoteData();
                            notify(response.data.message, "info");

                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                $vue.$refs.modalActivarEmpleado.confirmReaction(false);
                notify(GlobalMessages.errorComunicacion, "error");
            });
        },
        getResumen() {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/resumen'),
                type: 'POST',
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                    }
                },
                error: function (error) {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        updateColaborador(item) {
            let $vue = this;
            location.href = APP.url(rutaModulo + "/updateColaborador/" + item.id) + $vue.getOrigenURL();
        },
        updatePersona(item) {
            let $vue = this;
            location = APP.url('general/persona/' + item.persona.id + '/edicion') + $vue.getOrigenURL();
        },
        showFuncionesColaborador(item) {
            let $vue = this;
            $vue.dataCargosOficina.title = "Funciones de " + item.persona.nombreCompleto;
            $vue.$refs.modalCargosOficina.open();

            axios.post(APP.url(rutaModulo + '/allFuncionesColaborador'), {id: item.id})
                    .then(response => {
                        if (response.data.success) {
                            $vue.cargos = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    }).catch(e => {
                notify(GlobalMessages.errorComunicacion, "error");
            });

        },
    }
});
