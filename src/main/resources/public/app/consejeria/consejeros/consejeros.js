Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    mixins: [VueLoader],
    data: {
        bgColorClass: {Habilitado: '', Inhabilitado: ''},
        consjerosURL: APP.url(rutaModulo + '/list'),
        configNuevoConsejero: {
            id: 'nuevoConsejeroModal',
            header: true,
            title: "Añadir Consejeros",
            okbtn: 'Agregar',
            showaccept: true
        },
        configAconsjadoNuevo: {
            id: 'nuevoAconsejadoModal',
            header: true,
            title: "Añadir Aconsejado",
            okbtn: 'ACEPTAR',
            showaccept: true
        },
        resumenCarrera: {
            consejerosActivos: 0,
            consejerosInactivos: 0,
            aconsejadosActivos: 0,
            aconsejadosInactivos: 0,
            sinconsejeroInactivos: 0,
            sinconsejeroActivos: 0,
            inhabilitados: 0
        },
        seleccionado: '',
        ciclo: [],
        carreras: [],
        btndissabled: '',
        docentes: [],
        listadoCarreras: [],
        carreraSelect: {},
        docenteSelect: {},
        departamentoDocente: {},
        isLoadingAlumnos: false,
        docenteResquest: {
            id: '',
            estado: '',
            idPersona: '',
            idDepart: '',
            idFacultad: '',
        },
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        isLoading: false,
        loadResumen: false,
        alumnoConsejero: null,
        alumnos: [],
        alumnoSeleccionado: {},
        alumnosSeleccionados: []
    },
    mounted: function () {
        let $vue = this;
        $vue.ciclo = JSON.parse(cicloJson);
        $vue.carreras = JSON.parse(carrerasJson);

        let carrera = $vue.$refs.raptorConsejero.getParameterByName('queries[carrera]');
        carrera = (carrera == null) ? '' : carrera;

        if ($vue.carreras.length > 0 && carrera == '') {
            $vue.carreraSelect = $vue.carreras[0];
        } else if (carrera != '') {
            for (var i = 0; i < $vue.carreras.length; i++) {
                if ($vue.carreras[i].id == carrera) {
                    $vue.carreraSelect = $vue.carreras[i];
                }
            }
        }

        if ($vue.carreraSelect.id != undefined) {
            $vue.cargaConsejeros();
        }

    },
    created: function () {
        let $vue = this;
        $vue.btndissabled = true;
    },
    methods: {
        nombreDocente(item) {
            if (item.persona == undefined) {
                return "";
            }
            return item.persona.nombreCompleto;
        },
        nuevoConsejero() {
            let $vue = this;
            if ($vue.btndissabled === false) {
                $vue.docenteSelect = {};
                $vue.departamentoDocente = {};
                $vue.docentes = [];
                $vue.$refs.nuevoConsejeroModal.open();
            } else {
                notify("Primero debe seleccionar una carrera", 'default');
            }
        },
        nuevoAconsejado(item) {
            let $vue = this;


            $vue.alumnos = [];
            $vue.alumnoSeleccionado = {};
            $vue.alumnosSeleccionados = [];

            $vue.alumnoConsejero = {};
            $vue.alumnoConsejero.consejero = item;
            $vue.$refs.agregarAconsejadoModal.open();
        },
        filtroConsejeros(estado) {
            let $vue = this;
            if ($vue.carreraSelect.id == undefined) {
                return;
            }
            if ($vue.carreraSelect.id == '') {
                return;
            }

            $vue.$refs.raptorConsejero.querie = [];
            $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: $vue.carreraSelect.id});

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[estado] = 'bg-light';
                $vue.seleccionado = estado;
                $vue.$refs.raptorConsejero.querie.push({name: "status", value: estado});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== estado) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[estado] = 'bg-light';
                $vue.seleccionado = estado;
                $vue.$refs.raptorConsejero.querie.push({name: "status", value: estado});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === estado) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.raptorConsejero.changeUrl('queries[status]', null);
            }
            $vue.$refs.raptorConsejero.loadRemoteData();
        },
        getDocentes(docente) {
            let $vue = this;
            let facultad = $vue.carreraSelect.facultad.id;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/listDocente"),
                data: {nombre: docente, idFacultad: facultad},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.docentes = response.data;
                $vue.isLoading = false;
            });
        },
        cargaDepartamento() {
            let $vue = this;
            $vue.departamentoDocente = $vue.docenteSelect.departamentoAcademico;
        },
        cargaConsejeros() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;

            if ($vue.seleccionado !== '') {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
            }

            $vue.resumenCarrera.consejerosActivos = 0;
            $vue.resumenCarrera.consejerosInactivos = 0;
            $vue.resumenCarrera.aconsejadosActivos = 0;
            $vue.resumenCarrera.aconsejadosInactivos = 0;
            $vue.resumenCarrera.sinconsejeroActivos = 0;
            $vue.resumenCarrera.sinconsejeroInactivos = 0;

            $vue.btndissabled = false;
            $vue.$refs.raptorConsejero.querie = [];
            $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: carrera});
            $vue.$refs.raptorConsejero.url = APP.url(rutaModulo + '/list/' + carrera);
            $vue.$refs.raptorConsejero.loadRemoteData();
            $vue.loadResumen = true;
        },
        getResumenCarrera(carrera) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/resumenCarrera"),
                data: {carrera: carrera},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.resumenCarrera = response.data;
                $vue.isLoading = false;
                $vue.loadResumen = false;
            });
        },
        loadResumenCarrera() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.loadResumen) {
                $vue.getResumenCarrera(carrera);
            }
        },
        cambiarEstado(item, estado) {
            let $vue = this;
            let consejero = {id: item.id, estado: estado};

            this.isLoading = true
            if (consejero.estado == 'ACT') {
                $.ajax({
                    method: 'POST',
                    url: APP.url(rutaModulo + "/cambiarEstado"),
                    data: JSON.stringify(consejero),
                    contentType: "application/json",
                }).then(response => {
                    notify(response.message, 'info');
                    $vue.loadResumen = true;
                    $vue.$refs.raptorConsejero.loadRemoteData();
                });

            } else {

                bootbox.confirm({
                    message: '¿Seguro que desea inhabilitar al tutor seleccionado? <br/> Al deshabilitar, todos los alumnos asociados a este quedarán sin tutor.',
                    buttons: {
                        confirm: {label: 'Si, inhabilitar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url(rutaModulo + "/cambiarEstado"),
                                data: JSON.stringify(consejero),
                                contentType: "application/json",
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.loadResumen = true;
                                        $vue.$refs.raptorConsejero.loadRemoteData();

                                    } else {
                                        notify(response.message, 'error');
                                    }
                                },
                                error: function () {
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
                message: '¿Seguro que desea añadir como Tutor el docente seleccionado?',
                buttons: {
                    confirm: {label: 'Si, Añadir', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        let docente = Object.assign({}, $vue.docenteSelect, {});
                        docente.carrera = $vue.carreraSelect;

                        $.ajax({
                            method: 'POST',
                            url: APP.url(rutaModulo + "/saveConsejero"),
                            data: JSON.stringify(docente),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.$refs.nuevoConsejeroModal.close();
                                    $vue.loadResumen = true;
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

            let sinConsejeros = $vue.resumenCarrera.sinconsejeroInactivos + $vue.resumenCarrera.sinconsejeroActivos;
            if (sinConsejeros == 0) {
                notify("No existe alumnos sin consejeros para esta carrera", 'error');
                return;
            }

            if ($vue.resumenCarrera.consejerosActivos == 0) {
                notify("No existe tutores activos para esta carrera", 'error');
                return;
            }

            let mm = bootbox.confirm({
                message: '¿Está seguro que desea asignar aleatoriamente tutores a los alumnos sin consejero?',
                buttons: {
                    confirm: {label: 'Si, asignar aleatoriamente', className: "btn-success btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        $.ajax({
                            method: 'POST',
                            url: APP.url(rutaModulo + "/asignarAlumno"),
                            data: {carrera: carrera},
                            dataType: 'json',
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.loadResumen = true;
                                    $vue.$refs.raptorConsejero.loadRemoteData();
                                    mm.modal("hide");

                                } else {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si, asignar aleatoriamente');
                                    notify(response.message, 'error');
                                }

                            }, error: function () {
                                $(".btn-modal").prop('disabled', false);
                                $(".btn-procesar").html('Si, asignar aleatoriamente');
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });

                        return false;
                    }
                }
            });


        },
        desasignarAlummnos() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.btndissabled === false) {
                $vue.isLoading = true;
                let mm = bootbox.confirm({
                    message: '¿Esta seguro que desea rertirar los tutores a todos los alumnos?',
                    buttons: {
                        confirm: {label: 'Si, retirar tutores', className: "btn-danger btn-modal btn-procesar"},
                        cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                    },
                    callback: function (result) {
                        if (result) {
                            $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                            $(".btn-modal").prop('disabled', true);

                            $.ajax({
                                method: 'POST',
                                url: APP.url(rutaModulo + "/desasignarAlumno"),
                                data: {carrera: carrera},
                                dataType: 'json',
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.loadResumen = true;
                                        $vue.$refs.raptorConsejero.loadRemoteData();
                                        mm.modal("hide");

                                    } else {
                                        $(".btn-modal").prop('disabled', false);
                                        $(".btn-procesar").html('Si, retirar tutores');
                                        notify(response.message, 'error');
                                    }
                                }, error: function () {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si, retirar tutores');
                                    notify(MESSAGES.errorComunicacion, "error");
                                }
                            });

                            return false;
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
            $vue.$refs.raptorConsejero.url = APP.url(rutaModulo + '/list/' + carrera);
            $vue.$refs.raptorConsejero.loadRemoteData();
        }, agregarAlumno() {
            let $vue = this;
            console.log("agregarAlumno");
            /*
             $.ajax({
             url: APP.url(rutaModulo + "/listDocente"),
             data: {nombre: docente, idFacultad: facultad},
             dataType: 'json',
             type: 'post',
             }).then(response => {
             $vue.docentes = response.data;
             $vue.isLoading = false;
             });*/

            $vue.alumnosSeleccionados.push(Object.assign({}, $vue.alumnoSeleccionado));
            $vue.alumnoeleccionado = {};
        }, reporte(item) {
            let $vue = this;
            let ruta = "/consejeria/consejeros/reporteAlumnos/" + $vue.carreraSelect.id;
            console.log(ruta);
            console.log(JSON.stringify({id: item.id}));
            $.fileDownload(ruta, {
                httpMethod: "POST",
                data: {consejero: item.id}
                ,
                successCallback: function (responseHtml, url) {
//                    console.log('aqui');
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(MESSAGES.errorComunicacion, 'error')
                }
            });
        }, searchAlumnos(search) {
            let $vue = this;
            $vue.isLoadingAlumnos = true;
            $.ajax({
                url: APP.url('consejeria/consejeros/searchAlumno'),
                type: 'POST',
                data: {nombre: search},
                success(response) {
                    $vue.isLoadingAlumnos = false;
                    if (response.success) {
                        $vue.alumnos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        labelAlumno(item) {
            if (item.id == undefined) {
                return "";
            }
            return item.codigo + " - " + item.persona.nombreCompleto;
        }, aceptarNuevosAconsejados() {
            let $vue = this;
            $vue.alumnoConsejero.consejero.alumno = $vue.alumnosSeleccionados;

            $.ajax({
                url: APP.url('consejeria/consejeros/saveAlumnoConjero'),
                type: 'POST',
                dataType: 'json',
                contentType: "application/json",
                data: JSON.stringify($vue.alumnoConsejero.consejero),
                success(response) {
                    if (response.success) {
                        //  $vue.$refs.raptor.repreload();
                        //  $vue.$refs.modalAmpliacionVacante.close();
                        notify(response.message, "info");
                        $vue.$refs.agregarAconsejadoModal.close();
                        $vue.$refs.raptorConsejero.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, consejerosPorEspecialidad() {
            let $vue = this;
            let ruta = "/consejeria/consejeros/consejerosPorEspecialidad";

            $.fileDownload(ruta, {
                httpMethod: "POST",
                data: {carrera: $vue.carreraSelect.id}
                ,
                successCallback: function (responseHtml, url) {
//                    console.log('aqui');
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(MESSAGES.errorComunicacion, 'error')
                }
            });
        }
    }
});








