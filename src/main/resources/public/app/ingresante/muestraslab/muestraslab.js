
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        atendidoSelected: {},
        ingresantesURL: '',
        turnos: [],
        laboratorioActual: JSON.parse(laboratorioActual),
        verTodos: false,
        verTurno: true,
        verAtendidos: false,
    },
    mounted: function () {
        let $vue = this;
        // console.log("laboratorioActual", $vue.laboratorioActual);
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
                    $vue.selectTodos("turno");
                }
            })
        },
        cambiarTurno() {
            let $vue = this;
            console.log("turnosel", $vue.turnoSelected)
            $vue.selectTodos("turno");
        },
        cambiarTurnoAtendidos() {
            let $vue = this;
            console.log("atendidoSelected", $vue.atendidoSelected)
            $vue.selectTodos("atendidos");
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
        selectTodos(item) {
            console.log(item)
            let $vue = this;
            if (item == 'todos') {
                $vue.verTodos = true;
                $vue.verAtendidos = false;
                $vue.verTurno = false;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/todos');

            } else if (item == 'turno') {
                $vue.verTurno = true;
                $vue.verAtendidos = false;
                $vue.verTodos = false;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/turno/' + $vue.turnoSelected.id);

            } else if (item == 'atendidos') {
                $vue.verAtendidos = true;
                $vue.verTurno = false;
                $vue.verTodos = false;
                $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/atendidos/' + $vue.atendidoSelected.id);
            }

            setTimeout(function () {
                console.log("$vue.ingresantesURL == " + $vue.ingresantesURL)
                $vue.$refs.raptorML.loadRemoteData();
            }, 50);
        },
        verListaExcel() {
            console.log("ver excel")
            let $vue = this;
            if ($vue.verTodos) {
                console.log("excel todos")
                location.href = APP.url('ingresante/muestraslab/listaExcel')

            } else if ($vue.verTurno) {
                console.log("excel turno")
                let turno = $vue.turnoSelected.id
                console.log(turno)
                location.href = APP.url('ingresante/muestraslab/listaExcelTurno?turno=' + turno)

            } else if ($vue.verAtendidos) {
                console.log("excel atendidos")
                let turno = $vue.atendidoSelected.id
                console.log(turno)
                location.href = APP.url('ingresante/muestraslab/listaExcelAtendidos?turno=' + turno)
            }
        }, verMuestra(recorridoIngresante) {
            console.log("verMuestra");
            console.dir(recorridoIngresante);
            let actividadIngresante = recorridoIngresante.actividadIngresante.filter(opt => opt.tipoActividadIngresante.tipoRECEP && opt.estadoAct);
            if (actividadIngresante != null && actividadIngresante.length > 0) {
                console.log("found");
                console.dir(actividadIngresante);
                return true;
            }
            return false;
        }
    }
});