Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#colaboradorVue',
    mixins: [VueLoader],
    data: {
        colaboradoreURL: APP.url(rutaModulo + '/listColaboradores'),
        listaCargos: JSON.parse(cargosJson),
        estadosEmp: JSON.parse(estadosEmpJson),
        persona: {},
        colaborador: {},
        perfilCompania: {},
        dataModalFuncion: {
            title: 'Nueva Función',
        },
        dataVerCargo: {
            title: 'Cargos',
            showaccept: false
        },
        funcion: {id: null},
        cargos: [],
        divElegido: 0,
        resumen: {
            activos: 0,
            vacaciones: 0,
            retirado: 0,
            descanso: 0,
            permiso: 0,
            despedido: 0
        }
    },
    computed: {

    },
    created() {
//        let $vue = this;
//        console.log($vue.listaCargos);
//        $vue.oficinas.forEach(function (elem) {
//            if ($vue.oficina.id == elem.id) {
//                $vue.oficina = elem;
//            }
//        })
    },
    mounted: function () {
        let $vue = this;
        $vue.getResumen();
        $global.$on("showFuncionesColaborador", function (id) {
            $vue.showFuncionesColaborador(id);
        });

    },
    methods: {

        addCargo: function () {
            let $vue = this;
            var flag = false;
            $vue.listaCargos.forEach(function (elem) {
                if (elem.nombre == $vue.perfilCompania.nombre) {
                    notify('El cargo ingresado ya existe', "error");
                    flag = true;
                }
            })
            if (flag) {
                return;
            }
            $vue.perfilCompania.oficinaContiene = $vue.oficina;
            $.ajax({
                url: APP.url('general/oficina/cargo'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.perfilCompania),
                success: function (response) {
                    notify(response.message, "info");
                    $global.$emit("reloadDyntable");
                    $("#myModal").modal('hide');
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        nuevoCargos: function () {
            $("#myModal").modal('show');
        },
        regresar: function () {
            location.href = APP.url("general/oficina");
        },

        nuevoColaborador: function () {
            let $vue = this;
//            $vue.oficina
            location.href = APP.url(rutaModulo + "/nuevoColaborador") + $vue.getOrigenURL();

        },
        oficinaSeleccionada: function () {
            let $vue = this;
            console.log($vue.oficina);
            $global.$emit("oficinaId", $vue.oficina);
        },
        updateEstado: function (id, value, estado) {
            let $vue = this;
            console.log(estado);
            if (estado == "DESP") {
                location.href = APP.url(rutaModulo + "/updateColaborador/" + $vue.oficina.id);
                return;
            }
            $vue.colaborador = {id: id, estado: value, oficina: $vue.oficina};
            bootbox.confirm({
                message: "¿Seguro desea cambiar de estado al colaborador?",
                buttons: {
                    confirm: {label: "Si, seguro", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (!result) {
                        return;
                    }
                    $.ajax({
                        url: APP.url('general/oficina/updateEstado'),
                        type: 'POST',
                        contentType: "application/json",
                        data: JSON.stringify($vue.colaborador),
                        success: function (response) {
                            notify(response.message, "info");
                            $global.$emit("reloadDyntable");
                        },
                        error: function (error) {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            });
        },
        nuevaFuncion: function () {
            let vue = this;
            vue.funcion = {id: null};
            vue.dataModalFuncion.title = "Nueva Función";
            vue.$refs.modaladdfuncion.open();
        },
        saveFuncion: function () {
            var vue = this;

            var valid = $('#formAddFuncion').parsley().validate();

            if (valid != true) {
                return;
            }

            vue.showLoader();

            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/savefuncion'),
                data: $('#formAddFuncion').serialize(),
                async: false,
                success: function (response) {

                    if (response.success) {

                        notify(response.message, 'info');
                        vue.$refs.modaladdfuncion.close();

                    } else {
                        notify(response.message, 'error');
                    }

                    vue.hideLoader();

                }, error: function () {

                    vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");

                }
            });

        },
        verCargo: function () {
            let vue = this;
            vue.dataVerCargo.title = "Cargos";
            vue.$refs.modalvercargo.open();

            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/vercargo'),
                data: {id: vue.oficina.id},
                success: function (response) {

                    if (response.success) {

                        vue.cargos = response.data;

                    } else {
                        notify(response.message, 'error');
                    }

                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        verFuncion: function () {
            let vue = this;
            vue.dataVerCargo.title = "Funciones";
            vue.$refs.modalvercargo.open();

            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/verfuncion'),
                data: {id: vue.oficina.id},
                success: function (response) {

                    if (response.success) {

                        vue.cargos = response.data;

                    } else {
                        notify(response.message, 'error');
                    }

                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        getRecord: function (id) {
            var id = parseInt(id);
            return $dynatable.settings.dataset.records.find(item => item.id === id);
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
        changeEstado(item, estado) {
            let $vue = this;
            console.log(estado);
            if (estado.name == "DESP") {
                location.href = APP.url(rutaModulo + "/updateColaborador/" + item.id) + $vue.getOrigenURL();
                return;
            }

            bootbox.confirm({
                message: "¿Seguro desea cambiar a <strong>" + item.persona.nombreCompleto
                        + "</strong> al estado <strong class='text-danger'>"
                        + estado.value + "</strong>?",
                buttons: {
                    confirm: {label: "Si, cambiar", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        let empleado = Object.assign({}, item, {});
                        empleado.estado = estado.name;

                        $.ajax({
                            url: APP.url(rutaModulo + '/updateEstado'),
                            type: 'POST',
                            contentType: "application/json",
                            data: JSON.stringify(empleado),
                            success: function (response) {
                                $vue.getResumen();
                                $vue.$refs.raptorColaboran.loadRemoteData();
                                notify(response.message, "info");
                            },
                            error: function (error) {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
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
                    notify(MESSAGES.errorComunicacion, "error");
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
            $vue.dataVerCargo.title = "Funciones de " + item.persona.nombreCompleto;
            $vue.$refs.modalvercargo.open();

            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/allFuncionesColaborador'),
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.cargos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }

                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
    }
});
