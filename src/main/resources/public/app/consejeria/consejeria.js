Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {activos: '', inactivos: ''},
        consjerosURL: APP.url('consejeria/consejeros/list'),
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
        cantidadAconsejados: 0,
        cantidadSinAconsejados: 0,
        estadoConsejero: '',
        ciclo: [],
        carreras: [],
        btndisabled: '',
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
        btndissabled() {
            let $vue = this;
            return $vue.btndisabled;
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.ciclo = JSON.parse(cicloJson);
        $vue.carreras = JSON.parse(carrerasJson);

        let carrera = $vue.$refs.raptorConsejero.getParameterByName('queries[carrera]');
        carrera = (carrera === null) ? '' : carrera;

        if ($vue.carreras.length === 1 && carrera === '') {
            $vue.carreraSelect = $vue.carreras[0];
        } else if (carrera !== '') {
            for (var i = 0; i < $vue.carreras.length; i++) {
                if ($vue.carreras[i].id === carrera) {
                    $vue.carreraSelect = $vue.carreras[i];
                }
            }
        }

        if ($vue.carreraSelect !== '') {
            $vue.cargaConsejeros();
        }
    },
    created: function () {
        let $vue = this;
        $vue.btndisabled = true;
    },
    methods: {
        nombreforShow(item) {
            return item.persona.nombreCompleto;
        },
        nuevoConsejero() {
            let $vue = this;
            if ($vue.btndisabled === false) {
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
                url: APP.url("consejeria/consejeros/listDocente"),
                data: {nombre: docente, idFacultad: facultad},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.listadoDocentes = response.data;
                $vue.isLoading = false;
            });
        },
        CargaDepartamento() {
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
                $vue.btndisabled = true;
            } else {
                $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: carrera});
                $vue.btndisabled = false;
                $vue.actualizar();
            }
            $vue.getCantidadEstado(carrera);
            //$vue.getAconsejados(carrera);
        },
        getCantidadEstado(carrera) {
            let $vue = this;
            $.ajax({
                url: APP.url("consejeria/consejeros/filtroEstado"),
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
                url: APP.url("consejeria/consejeros/cantidadAconsejados"),
                data: {carrera: carrera},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.cantidadAconsejados = response.data.aconsejados;
                $vue.cantidadSinAconsejados = response.data.sinConsejeros;
                this.isLoading = false;
            });
        },
        cambiarEstado(item, estado) {
            let $vue = this;
            let consejero = item;
            let carrera = $vue.carreraSelect.id;

            this.isLoading = true;
//            alert(JSON.stringify(consejero));
//            if (consejero.estado == 'INA') {

            $.ajax({
                method: 'POST',
                url: APP.url("consejeria/consejeros/cambiarEstado"),
                data: JSON.stringify({
                    id: consejero.id,
                    estado: estado
                }),
                contentType: "application/json",
            }).then(response => {
                this.isLoading = false;
                notify(response.message, 'info');
                $vue.getCantidadEstado(carrera);
                $vue.actualizar();
            });

//                bootbox.confirm({
//                    message: '¿Seguro que desea inhabilitar el consejero seleccionado? Si inhabilita el consejero seleccionado, todos los alumnos asociados a este seran movidos al consejero externo.',
//                    buttons: {
//                        confirm: {label: 'Si, inhabilitar', className: "btn-danger"},
//                        cancel: {label: 'Cancelar', className: "btn-link"}
//                    },
//                    callback: function (result) {
//                        if (result) {
//                            $.ajax({
//                                method: 'POST',
//                                url: APP.url("consejeria/consejeros/cambiarEstado"),
//                    data: JSON.stringify({
//                        id: consejero.id,
//                        estado: estado
//                    }),
//                                 contentType: "application/json",
//                                success: function (response) {
//                                    if (response.success) {
//                                        notify(response.message, 'info');
//                                        $vue.$refs.añadirConsejeroModal.close();
//                                        $vue.docenteSelect = '';
//                                        $vue.departamento = '';
//                                        $vue.getcantidadEstado($vue.carreraSelect.id);
//                                        $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + $vue.carreraSelect.id);
//                                        $vue.$refs.raptorConsejero.loadRemoteData();
//                                    } else {
//                                        notify(response.message, 'error');
//                                    }
//                                }, error: function () {
//                                    notify(MESSAGES.errorComunicacion, "error");
//                                }
//                            });
//                        }
//                    }
//                });

//            }
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
                            url: APP.url("consejeria/consejeros/saveConsejero"),
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
                                    $vue.getCantidadEstado($vue.carreraSelect.id);
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
            if ($vue.btndisabled === false) {
                let carrera = $vue.carreraSelect.id;
                $vue.isLoading = true;
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
                                url: APP.url("consejeria/consejeros/asignarAlumno"),
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
        desasignarAlummnos() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.btndisabled === false) {
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
                                url: APP.url("consejeria/consejeros/desasignarAlumno"),
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
            $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);
            $vue.$refs.raptorConsejero.loadRemoteData();
        }
    }
});








