
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        atendidoSelected: {},
        ingresantesURL: '',
        turnos: [],
        laboratorioActual: JSON.parse(laboratorioActual),
        verTurno: true,
        verAtendidos: false,
    },
    mounted: function () {
        let $vue = this;
        console.log("laboratorioActual", $vue.laboratorioActual);
        $vue.loadTurnos();
    },
    methods: {
        loadTurnos() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/muestraslab/turnos"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("turnos", response);
                $vue.turnos = response.data;
                if ($vue.turnos.length > 0) {
                    $vue.turnoSelected = $vue.turnos[0];
                    $vue.atendidoSelected = $vue.turnos[0];
                    $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);

                    setTimeout(function () {
                        $vue.$refs.raptorML.loadRemoteData();
                        console.log(" $vue.$refs.raptorML", $vue.$refs.raptorML._props);
                    }, 50);
                }
            })
        },
        cambiarTurno() {
            let $vue = this;
            console.log("turnosel", $vue.turnoSelected)
            $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
            setTimeout(function () {
                $vue.$refs.raptorML.loadRemoteData();
            }, 50);
        },
        cambiarTurnoAtendidos() {
            let $vue = this;
            console.log("atendidoSelected", $vue.atendidoSelected)
            $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/atendidos/' + $vue.atendidoSelected.id);
            setTimeout(function () {
                $vue.$refs.raptorML.loadRemoteData();
            }, 50);
        },
        asignarNumLab(item) {
            console.log("item selected", item)
            let $vue = this;
            if (item.laboratorio.historiaClinica.id === "") {
                swal('El alumno no tiene historia clínica', {
                    icon: "warning",
                    closeOnClickOutside: true,
                    closeOnEsc: true,
                    dangerMode: false,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: false},
                        confirm: {text: "OK", closeModal: true}
                    }
                });
            } else {
                $.ajax({
                    method: 'POST',
                    url: APP.url('ingresante/muestraslab/saveLaboratorio'),
                    data: JSON.stringify(item.laboratorio),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            console.log("response", response.data);
                            $vue.laboratorioActual.numero = response.data.numeroMuestra + 1;
                            $vue.$refs.raptorML.loadRemoteData();
                            notify(response.message, 'info');
                        } else {
                            notify(response.message, 'error');
                        }
                    }
                });
            }
        },

        borrarNumlab(item) {
            console.log("item selected", item)
            let $vue = this;

            swal('¿Seguro que desea eliminar el número de muestra?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                $.ajax({
                    method: 'POST',
                    url: APP.url('ingresante/muestraslab/borrarLaboratorio'),
                    data: JSON.stringify(item.laboratorio),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            console.log("response", response.data);
                            $vue.laboratorioActual.numero = response.data.numeroMuestra + 1;
                            $vue.$refs.raptorML.loadRemoteData();
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            notify(response.message, 'error');
                        }
                    }
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

        selectTurno() {
            let $vue = this;
            if ($vue.verTurno) {
                $vue.verAtendidos = false;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
                setTimeout(function () {
                    $vue.$refs.raptorML.loadRemoteData();
                }, 50);
            }
            if (!$vue.verTurno) {
                $vue.verAtendidos = true;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/atendidos/' + $vue.atendidoSelected.id);
                setTimeout(function () {
                    $vue.$refs.raptorML.loadRemoteData();
                    console.log(" $vue.$refs.raptorML", $vue.$refs.raptorML._props);
                }, 50);
            }
        },

        selectAtendido() {
            let $vue = this;
            if ($vue.verAtendidos) {
                $vue.verTurno = false;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/atendidos/' + $vue.atendidoSelected.id);
                setTimeout(function () {
                    $vue.$refs.raptorML.loadRemoteData();
                    console.log(" $vue.$refs.raptorML", $vue.$refs.raptorML._props);
                }, 50);
            }
            if (!$vue.verAtendidos) {
                $vue.verTurno = true;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
                setTimeout(function () {
                    $vue.$refs.raptorML.loadRemoteData();
                }, 50);
            }
        },
    }
});