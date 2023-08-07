Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {sinConsejero: '', conConsejero: '', inhabilitado: ''},
        ciclo: {},
        carreras: [],
        resumenCarrera: {
            consejerosActivos: 0,
            consejerosInactivos: 0,
            aconsejadosActivos: 0,
            aconsejadosInactivos: 0,
            sinconsejeroInactivos: 0,
            sinconsejeroActivos: 0,
            inhabilitados: 0
        },
        isLoading: false,
        consejeroModal: {
            id: 'consejeroModal',
            header: true,
            title: "Tutores",
            okbtn: 'Aceptar',
            showaccept: true
        },
        carreraSelect: {},
        consejeros: [],
        seleccionado: '',
        alumnoConsejeroForm: {},
        count: {activos: 0, sinConsejero: 0, sinAsignar: 0},
        loadResumen: false,
        axios: moduleAxios(RUTA_MODULO)
    },
    mounted: function () {
        let $vue = this;
        $vue.ciclo = JSON.parse(cicloJson);
        $vue.carreras = JSON.parse(carrerasJson);

        let carrera = $vue.$refs.raptorAconsejados.getParameterByName('queries[carrera]');
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
            $vue.cargaAconsejados($vue.carreraSelect);
        }
    },
    methods: {
        findAconsejado(tipo) {
            let $vue = this;
            if ($vue.carreraSelect.id == undefined) {
                return;
            }
            if ($vue.carreraSelect.id == '') {
                return;
            }

            $vue.$refs.raptorAconsejados.querie = [];
            $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: $vue.carreraSelect.id});

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: "estado", value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: "estado", value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.raptorAconsejados.changeUrl('queries[estado]', null);
            }
            $vue.$refs.raptorAconsejados.loadRemoteData();
        },
        customLabel( { colaborador }) {
            return `${colaborador.persona.nombreCompleto}`;
        },
        getDocentes(nombreDoc) {
            let $vue = this;
            $vue.isLoading = true;
            $vue.axios.get("/listConsejero", {params: {idCarrera: $vue.carreraSelect.id, nombre: nombreDoc}})
                    .then(({data}) => {
                        $vue.consejeros = data;
                        $vue.isLoading = false;
                    }, () => {
                    });
        },
        cargaAconsejados() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;

            if ($vue.seleccionado !== '') {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
            }

            $vue.resumenCarrera.aconsejadosActivos = 0;
            $vue.resumenCarrera.aconsejadosInactivos = 0;
            $vue.resumenCarrera.sinconsejeroActivos = 0;
            $vue.resumenCarrera.sinconsejeroInactivos = 0;

            $vue.$refs.raptorAconsejados.querie = [];
            $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: carrera});
            $vue.$refs.raptorAconsejados.url = APP.url(RUTA_MODULO + '/list/' + carrera);
            $vue.$refs.raptorAconsejados.loadRemoteData();
            $vue.loadResumen = true;

        },
        getResumenCarrera(carrera) {
            let $vue = this;
            $vue.axios.get("/resumenCarrera/" + carrera)
                    .then(({data}) => {
                        $vue.resumenCarrera = data;
                        $vue.isLoading = false;
                        $vue.loadResumen = false;
                    }, () => {
                    });
        },
        loadResumenCarrera() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.loadResumen) {
                $vue.getResumenCarrera(carrera);
            }
        },
        model(item) {
            let $vue = this;
            $vue.alumnoConsejeroForm = Object.assign({}, item);
            $vue.$refs.consejeroModal.open();
        },
        cambiarConsejero() {
            let $vue = this;
            $vue.axios.post("/update", $vue.alumnoConsejeroForm)
                    .then(({data}) => {
                        $vue.$refs.raptorAconsejados.loadRemoteData();
                        notify(data, "success");
                        $vue.$refs.consejeroModal.close();
                    }, () => {
                    });
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        solicitudBeneficio(item) {
            let $vue = this;

            var sexo = item.alumno.persona.sexo == 'M' ? 'al alumno ' : 'a la alumna ';
            var alumno = sexo + item.alumno.persona.apellidosNombres;
            var ciclo = item.cicloAcademico.descripcion;

            swal('¿Esta seguro que desea asignar el beneficio de último ciclo ' + alumno + ' en el ciclo ' + ciclo + ' ?', {
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
                $vue.axios.post("/solicitudBeneficio", item)
                        .then(({data}) => {
                            $vue.$refs.raptorAconsejados.loadRemoteData();
                            return  swal({text: data, icon: "success", button: false, timer: 1000});
                        }, () => {
                            return  swal({text: Messages.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
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
        eliminarTutorado(item) {

            let $vue = this;

            swal('¿Seguro que desea eliminar el tutorado ?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Sí, Eliminar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                $vue.axios.get("/eliminar/" + item.id)
                        .then(({data}) => {
                            notify(data, 'info');
                            $vue.$refs.raptorAconsejados.loadRemoteData();
                            return swal({text: data, icon: "success", button: false, timer: 1000});
                        }, () => {
                            return swal(APP.errorComunicacion, "error");
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
        quitarTutorado(item) {

            let $vue = this;

            swal('¿Seguro que desea quitar el tutor?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Sí, Remover", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                $vue.axios.get("/quitar/tutor/" + item.id)
                        .then(({data}) => {
                            notify(data, 'info');
                            $vue.$refs.raptorAconsejados.loadRemoteData();
                            return swal({text: data, icon: "success", button: false, timer: 1000});
                        }, () => {
                            return swal(APP.errorComunicacion, "error");
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
        planificar(item) {
            return `/${rutaModuloTutor}/${item.alumno.id}/planificacion${myUtils.getOrigenURL()}`;
        },
        agendar(item) {
            return `/${rutaModuloTutor}/${item.alumno.id}/agendarTutorado${myUtils.getOrigenURL()}`;
        },
        derivar(item) {
            return `/${rutaModuloTutor}/${item.alumno.id}/derivarTutorado${myUtils.getOrigenURL()}`;
        }
    }
});







        