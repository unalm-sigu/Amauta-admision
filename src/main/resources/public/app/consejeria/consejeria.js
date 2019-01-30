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
        btnAñadir() {
            let $vue = this;
            return $vue.btndisabled;
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
            }
        },
        filtroConsejeros(estado) {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            $vue.isLoading = true;
            $vue.estadoConsejero = estado;
            $vue.$refs.raptorConsejero.querie.push({name: 'status', value: estado});
            $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);
            $vue.$refs.raptorConsejero.loadRemoteData();

//            let fondoColor = estado === 'ACT' ? 'activos' : 'inactivos';
            if (estado === 'Habilitado') {
                $vue.bgColorClass['activos'] = 'bg-light';
                $vue.bgColorClass['inactivos'] = '';
            } else {
                $vue.bgColorClass['activos'] = '';
                $vue.bgColorClass['inactivos'] = 'bg-light';
            }
        },
        getDocentes(nombreDoc) {
            /// listado de docente por carrera
            let $vue = this;
            let idfacultad = $vue.carreraSelect.facultad.id;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url("consejeria/consejeros/listDocente"),
                data: {nombre: nombreDoc, idFacultad: idfacultad},
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
            $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);

            if ($vue.carreraSelect === null) {
                $vue.btndisabled = true;
                //   $vue.$refs.raptorConsejero.loadRemoteData();
            } else {
                //let carrera = $vue.carreraSelect.nombre;
                /// pendiente
                $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: carrera});
                $vue.btndisabled = false;
                $vue.$refs.raptorConsejero.loadRemoteData();
            }
            $vue.cantidadEstado(carrera);
            $vue.getAconsejados(carrera);
        },
        cantidadEstado(carrera) {
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
//            $.ajax({
//                url: APP.url("consejeria/consejero/cantidadAconsejados"),
//                data: {carrera: carrera},
//                dataType: 'json',
//                type: 'post',
//            }).then(response => {
//                $vue.getAconsejados = response.data.activo;
//                $vue.cantidadSinAconsejados = response.data.inactivo;
//                this.isLoading = false;
//            });
        },
        cambiarEstado(item, estado) {
            let $vue = this;
            let consejero = item;
            let carrera = $vue.carreraSelect.id;

            this.isLoading = true
            //alert(JSON.stringify(item));
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
                $vue.cantidadEstado(carrera);
                $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);
                $vue.$refs.raptorConsejero.loadRemoteData();
            });
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
                                    $vue.cantidadEstado($vue.carreraSelect.id);
                                    $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + $vue.carreraSelect.id);
                                    $vue.$refs.raptorConsejero.loadRemoteData();
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
            bootbox.confirm({
                message: '¿Esta seguro que desea asignar alumnos de manera aleatoria?',
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
                                    $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);
                                    $vue.$refs.raptorConsejero.loadRemoteData();
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
        },
        desasignarAlummnos() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
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
                                    $vue.$refs.raptorConsejero.url = APP.url('consejeria/consejeros/list/' + carrera);
                                    $vue.$refs.raptorConsejero.loadRemoteData();
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
        }
    }
});







        