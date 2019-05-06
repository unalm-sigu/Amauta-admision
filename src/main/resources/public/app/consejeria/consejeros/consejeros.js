Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {activos: '', inactivos: ''},
        consjerosURL: APP.url(rutaModulo + '/list'),
        añadirConsejeroModal: {
            id: 'añadirConsejeroModal',
            header: 'true',
            title: "Añadir Consejeros",
            okbtn: 'Agregar',
            showaccept: true
        },
        cantidadActivoAlumno: 0,
        cantidadInactivoAlumno: 0,
        cantidadActivo: 0,
        cantidadInactivo: 0,
        cantidadConConnsejo: 0,
        cantidadSinConsejero: 0,
        cantidadConsejeroRetirado: 0,
        estadoConsejero: '',
        ciclo: [],
        carreras: [],
        btndissabled: '',
        listadoDocentes: [],
        listadoCarreras: [],
        carreraSelect: '',
        docenteSelect: '',
        departamento: '',
        docenteResquest: {
            id: '',
            estado: '',
            idPersona: '',
            idDepart: '',
            idFacultad: '',
        },
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        isLoading: false,
    },
    computed: {
        btnAñadir() {
            let $vue = this;
            return $vue.btndissabled;
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.ciclo = JSON.parse(cicloJson);
        $vue.carreras = JSON.parse(carrerasJson);

        let carrera = $vue.$refs.raptorConsejero.getParameterByName('queries[carrera]');
        carrera = (carrera == null) ? '' : carrera;

        if ($vue.carreras.length == 1 && carrera == '') {
            $vue.carreraSelect = $vue.carreras[0];
        } else if (carrera != '') {
            for (var i = 0; i < $vue.carreras.length; i++) {
                if ($vue.carreras[i].id == carrera) {
                    $vue.carreraSelect = $vue.carreras[i];
                }
            }
        }

        if ($vue.carreraSelect != '') {
            $vue.cargaConsejeros();
        }

    },
    created: function () {
        let $vue = this;
        $vue.btndissabled = true;
    },
    methods: {
        nombreforShow(item) {
            return item.persona.nombreCompleto;
        },
        nuevoConsejero() {
            let $vue = this;
            if ($vue.btndissabled === false) {
                $vue.$refs.añadirConsejeroModal.open();
            } else {
                notify("Primero debe seleccionar una carrera", 'default');
            }
        },
        filtroConsejeros(estado) {
            let $vue = this;
            $vue.isLoading = true;
            $vue.estadoConsejero = estado;
            $vue.$refs.raptorConsejero.querie.push({name: 'status', value: estado});
            $vue.actualizar();

            if (estado === 'Habilitado') {
                $vue.bgColorClass['activos'] = 'bg-light';
                $vue.bgColorClass['inactivos'] = '';
            } else {
                $vue.bgColorClass['activos'] = '';
                $vue.bgColorClass['inactivos'] = 'bg-light';
            }
        },
        getDocentes(docente) {
            /// listado de docente por carrera
            let $vue = this;
            let facultad = $vue.carreraSelect.facultad.id;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/listDocente"),
                data: {nombre: docente, idFacultad: facultad},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.listadoDocentes = response.data;
                $vue.isLoading = false;
            });
        },
        cargaDepartamento() {
            let $vue = this;
            if ($vue.docenteSelect === null) {
                $vue.departamento = "";
                $vue.docenteResquest = null;
            } else {
                $vue.departamento = $vue.docenteSelect.departamentoAcademico.nombre;
                $vue.docenteResquest.estado = $vue.docenteSelect.estado;
                $vue.docenteResquest.idDepartamento = $vue.docenteSelect.departamentoAcademico.id;
                $vue.docenteResquest.idFacultad = $vue.docenteSelect.departamentoAcademico.facultad.id;
                $vue.docenteResquest.idPersona = $vue.docenteSelect.persona.id;
            }
        },
        cargaConsejeros() {
            // listado de consejeros en dynatable
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            $vue.$refs.raptorConsejero.querie = [];
            $vue.bgColorClass['inactivos'] = '';
            $vue.bgColorClass['activos'] = '';
            $vue.listadoDocentes = '';
            $vue.docenteSelect = '';
            $vue.departamento = '';

            if ($vue.carreraSelect === null) {
                $vue.btndissabled = true;
            } else {
                $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: carrera});
                $vue.btndissabled = false;
                $vue.actualizar();
            }
            
            $vue.getCantidadEstado(carrera);
            $vue.getAconsejados(carrera);
        },
        getCantidadEstado(carrera) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/filtroEstado"),
                data: {carrera: carrera},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.cantidadActivo = response.data.activo === "" ? 0 : response.data.activo;
                $vue.cantidadInactivo = response.data.inactivo === "" ? 0 : response.data.inactivo;
                this.isLoading = false;
            });
        },
        getAconsejados(carrera) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/cantidadAconsejados"),
                data: {carrera: carrera},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.cantidadConConnsejo = response.data.conConsejados;
                $vue.cantidadConsejeroRetirado = response.data.consejeroRetirado;
                $vue.cantidadSinConsejero = response.data.sinConsejeros;
                this.isLoading = false;
            });
        },
        cambiarEstado(item, estado) {
            let $vue = this;
            let consejero = item;

            this.isLoading = true
//            alert(JSON.stringify(consejero));
            if (consejero.estado == 'INA') {
                $.ajax({
                    method: 'POST',
                    url: APP.url(rutaModulo + "/cambiarEstado"),
                    data: JSON.stringify({
                        id: consejero.id,
                        estado: estado
                    }),
                    contentType: "application/json",
                }).then(response => {
                    this.isLoading = false;
                    notify(response.message, 'info');
                    $vue.actualizar();
                });

            } else {

                bootbox.confirm({
                    message: '¿Seguro que desea inhabilitar el consejero seleccionado? Si inhabilita al consejero seleccionado, todos los alumnos asociados a este seran transladados al consejero externo.',
                    buttons: {
                        confirm: {label: 'Si, inhabilitar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url(rutaModulo + "/cambiarEstado"),
                                data: JSON.stringify({
                                    id: consejero.id,
                                    estado: estado
                                }),
                                contentType: "application/json",
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.actualizar();
                                        $vue.docenteSelect = '';
                                        $vue.departamento = '';
                                        $vue.$refs.añadirConsejeroModal.close();

                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }, error: function () {
                                    notify(MESSAGES.errorComunicacion, "error");
                                }
                            });
                        }
                    }
                });
            }
        },
        saveConsejero() {
            let $vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea añadir como Consejero el docente seleccionado?',
                buttons: {
                    confirm: {label: 'Si, Añadir', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(rutaModulo + "/saveConsejero"),
                            data: JSON.stringify({
                                estado: $vue.docenteResquest.estado,
                                persona: {id: $vue.docenteResquest.idPersona
                                },
                                departamentoAcademico: {
                                    id: $vue.docenteResquest.idDepartamento,
                                    facultad: {
                                        id: $vue.docenteResquest.id_facultad
                                    }
                                },
                                carrera: $vue.carreraSelect
                            }),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.$refs.añadirConsejeroModal.close();
                                    $vue.docenteSelect = '';
                                    $vue.departamento = '';
                                    $vue.actualizar();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        asignarAlummnos() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            $vue.isLoading = true;
            if (0 || $vue.cantidadActivo) {
                if ($vue.cantidadSinConsejero > 0 || $vue.cantidadConsejeroRetirado > 0) {
                    bootbox.confirm({
                        message: '¿Está seguro que desea asignar aleatoriamente tutores a los alumnos sin consejero?',
                        buttons: {
                            confirm: {label: 'Si, Asignar aleatoriamente', className: "btn-success"},
                            cancel: {label: 'Cancelar', className: "btn-link"}
                        },
                        callback: function (result) {
                            if (result) {
                                $.ajax({
                                    method: 'POST',
                                    url: APP.url(rutaModulo + "/asignarAlumno"),
                                    data: {carrera: carrera},
                                    dataType: 'json',
                                    success: function (response) {
                                        if (response.success) {
                                            notify(response.message, 'info');
                                            $vue.actualizar();
                                        } else {
                                            notify(response.message, 'error');
                                        }
                                    }, error: function () {
                                        notify(MESSAGES.errorComunicacion, "error");
                                    }
                                });
                            }
                        }
                    });
                    $vue.isLoading = false;
                } else {
                    notify("Actualmente no cuenta con alumnos disponibles", 'default');
                }
            } else {
                notify("Actualmente no cuenta con consejero en estado activo", 'default');
            }
        },
        desasignarAlummnos() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.btndissabled === false) {
                $vue.isLoading = true;
                bootbox.confirm({
                    message: '¿Esta seguro que desea desasignar todos los alumnos?',
                    buttons: {
                        confirm: {label: 'Si, Desasignar alumnos', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url(rutaModulo + "/desasignarAlumno"),
                                data: {carrera: carrera},
                                dataType: 'json',
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.actualizar();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }, error: function () {
                                    notify(MESSAGES.errorComunicacion, "error");
                                }
                            });
                        }
                    }
                });
                $vue.isLoading = false;
            } else {
                notify("Primero debe seleccionar una carrera", 'default');
            }
        },
        actualizar() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            $vue.getCantidadEstado(carrera);
            $vue.getAconsejados(carrera);
            $vue.$refs.raptorConsejero.url = APP.url(rutaModulo + '/list/' + carrera);
            $vue.$refs.raptorConsejero.loadRemoteData();
        }
    }
});








