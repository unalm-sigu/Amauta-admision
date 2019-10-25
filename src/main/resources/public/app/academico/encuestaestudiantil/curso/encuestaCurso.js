Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        generando: false,
        encuestaURL: APP.url('academico/encuestaestudiantil/curso/list'),
        encuesta: JSON.parse(encuestaJson),
        cfgVerDocentes: {
            id: 'modalVerDocentes',
            header: false,
            showaccept: false,
            cancelbtn: 'Cerrar'
        },
        cfgVerProgreso: {
            id: 'modalVerProgreso',
            header: false,
            footer: false,
            showaccept: false,
            modalsize: 'modal-lg',
            dataBackdrop: 'static',
            dataKeyboard: 'false'
        },
        mensajeProgreso: "Calculado Información a procesar",
        porcentajeProgreso: 0,
        docentesSecciones: [],
        cursosNoEncuestar: [],
        cfgEncuestaConfig: VUE_MODAL.structFormAjax({
            id: 'modalEncuestaConfig',
            modalsize: 'modal-lg',
            header: false
        }),
        addCursoModal: {
            id: 'modalAddCurso',
            header: true,
            title: 'Cursos sin encuesta',
            showaccept: false,
            cancelbtn: 'Cerrar'
        },
        configuraEncuesta: {},
        periodosEncuesta: [],
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        },
        cursos: {},
        curso: null,
        btnAgregar: false,
        cursoss: [],
        seleccionado: '',
        bgColorClass: {activo: '', anulado: '', innecesario: '', sinperiodo: '', cerrado: '',
            encuestable: '', encuestado: ''}
    },
    mounted: function () {
        let $vue = this;
        if ($vue.estadoVisor == 'INICIADO' || $vue.estadoVisor == 'OCUPADO') {
            setTimeout(function () {
                $vue.$refs.modalVerProgreso.open();
                $vue.refreshProgresoEncuesta();
            }, 1000);
        }

        $vue.refreshEncuesta();

        let tipo = $vue.$refs.load.getParameterByName('queries[ec.estado]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'ec.estado', value: tipo});
        }
        $vue.$refs.load.repreload();
    },
    methods: {
        verDocentes(seccion) {
            let vue = this;
            vue.docentesSecciones = seccion.docenteSeccion;
            vue.$refs.modalVerDocentes.open();
        },
        verDocenteUnico(item) {
            let vue = this;
            vue.docentesSecciones = [];
            vue.docentesSecciones.push(item.encuestaDocente.docenteSeccion);
            vue.$refs.modalVerDocentes.open();
        },
        getDia(fecha) {
            if (fecha == "")
                return "";
            return fecha.split(" ")[0];
        },
        getHora(fecha) {
            if (fecha == "")
                return "";
            var time = fecha.split(" ")[1].split(":");
            var aamm = (parseInt(time[0]) > 11) ? "pm" : "am";
            var hh = (parseInt(time[0]) > 12) ? (parseInt(time[0]) - 12) : parseInt(time[0]);
            return (hh < 10 ? "0" : "") + hh + ":" + time[1] + " " + aamm;
        },
        generarEncuesta() {
            let vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea generar la encuesta de cursos para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, generar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/curso/generar')
                                .then(response => {
                                    if (response.data.success) {
                                        vue.$refs.modalVerProgreso.open();
                                        vue.refreshProgresoEncuesta();
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        estado(encuestaCurso) {
            let vue = this;
            swal({
                text: "¿Está seguro que desea cambiar el estado a la encuesta del curso?",
                icon: "warning",
                type: "warning",
                dangerMode: true,
                showCancelButton: true,
                closeOnConfirm: false,
                buttons: {
                    cancel: "No",
                    confirm: "Si, estoy seguro"
                }
            }).then((willDelete) => {
                if (willDelete) {
                    vue.changeEstado(encuestaCurso);
                }
            });
        },
        changeEstado(encuesta) {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/estado'),
                async: false,
                data: {'id': encuesta.id},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.generando = false;
                }, error: function () {
                    vue.generando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        activarEncuesta() {
            let vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea activar la encuesta de cursos para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, activar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/curso/activar')
                                .then(response => {
                                    if (response.data.success) {
                                        notify(response.data.message, 'info');
                                        vue.refreshEncuesta();
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        configurarEncuesta() {
            let vue = this;
            vue.$refs.modalEncuestaConfig.open();
        },
        saveConfiguracion() {
            var vue = this;
            var form = $("#formConfiguraEncuesta");
            if (!(form.parsley().validate() === true)) {
                return;
            }
            vue.encuestaForm = {
                periodosEncuesta: vue.periodosEncuesta,
                configuraEncuesta: []
            };
            vue.configuraEncuesta.encuestaTeoriaPractica = vue.configuraEncuesta.encuestaTeoriaPractica == true ? 1 : 0;
            vue.encuestaForm.configuraEncuesta.push(vue.configuraEncuesta);

            axios.post('/academico/encuestaestudiantil/curso/saveConfigEncuesta', vue.encuestaForm)
                    .then(response => {
                        console.log(response);
                        if (response.data.success) {
                            notify(response.data.message, 'info');
                            vue.$refs.modalEncuestaConfig.close();
                            vue.refreshEncuesta();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        cancelConfiguracion() {
            var vue = this;
            vue.refreshEncuesta();
        },
        addPeriodoEncuesta() {
            var vue = this;
            vue.periodosEncuesta.push({fechaInicio: "", fechaFin: ""});
        },
        refreshProgresoEncuesta() {
            let vue = this;

            axios.post('/academico/encuestaestudiantil/curso/estadoGenerarEncuestas')
                    .then(response => {
                        vue.porcentajeProgreso = response.data.data;
                        vue.mensajeProgreso = response.data.message;
                        if (response.data.success) {
                            setTimeout(function () {
                                vue.refreshProgresoEncuesta();
                            }, 1000);
                        } else {
                            vue.$refs.modalVerProgreso.close();
                            bootbox.alert({
                                message: "Finalizó la generación de encuesta de cursos",
                                buttons: {ok: {label: "Aceptar"}},
                                callback: function () {
                                    vue.$refs.load.loadRemoteData();
                                    vue.refreshEncuesta();
                                }
                            });
                        }
                    })
                    .catch(function (error) {
                        vue.generando = false;
                        notify(MESSAGES.errorComunicacion, "error");
                    });

        },
        refreshEncuesta() {
            let vue = this;
            axios.post('/academico/encuestaestudiantil/curso/encuestaCurso')
                    .then(response => {
                        if (response.data.success) {
                            vue.encuesta = response.data.data;
                            vue.periodosEncuesta = vue.encuesta.periodosEncuesta;
                            vue.cursosNoEncuestar = vue.encuesta.cursosNoEncuestar;
                            if (vue.encuesta.configuraEncuesta.length > 0) {
                                vue.configuraEncuesta = vue.encuesta.configuraEncuesta[0];
                            } else {
                                vue.configuraEncuesta = {};
                            }
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
        },
        eliminar() {
            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar la encuesta de cursos para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, eliminar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/curso/delete', {id: $vue.encuesta.id})
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.$refs.load.loadRemoteData();
                                        $vue.refreshEncuesta();
                                        notify(response.data.message, "info");
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        publicar() {
            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea publicar la encuesta de cursos para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, publicar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/curso/publicar', {id: $vue.encuesta.id})
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.refreshEncuesta();
                                        notify(response.data.message, "info");
                                    } else {
                                        notify(response.data.message, "error");
                                    }
                                })
                                .catch(function (error) {
                                    console.log(error);
                                    notify(MESSAGES.errorComunicacion, "error");
                                });
                    }
                }
            });
        },
        sinEncuesta() {
            var $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/editor/allcursosinencuesta'),
                data: {
                    'id': $vue.encuesta.id
                },
                async: false,
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        $vue.cursos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            $vue.curso = {};
            $vue.$refs.modalAddCurso.open();
        },
        agregarCurso() {
            var $vue = this;
            $vue.btnAgregar = true;
            if ($vue.curso.id == null) {
                notify("No hay curso seleccionado  para agregar", "error");
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/editor/addcursosinencuesta'),
                data: {
                    'curso.id': $vue.curso.id,
                    'encuestaEstudiantil.id': $vue.encuesta.id
                },
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.cursos.push($vue.curso);
                        $vue.refreshEncuesta();
                    } else {
                        notify(response.message, 'error');
                    }
                    $vue.btnAgregar = false;
                }, error: function () {
                    $vue.btnAgregar = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            $vue.curso = {};
        },
        deleteCursoSinEncuesta(curso) {
            var $vue = this;
            let idx = $vue.cursos.map(item => item.id).indexOf(curso.id);
            if (idx > -1) {
                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/encuestaestudiantil/editor/removecursosinencuesta'),
                    data: {
                        'curso.id': curso.id,
                        'encuestaEstudiantil.id': $vue.encuesta.id
                    },
                    async: false,
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, 'info');
                            $vue.cursos.splice(idx, 1);
                            $vue.refreshEncuesta();
                        } else {
                            notify(response.message, 'error');
                        }
                    }, error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        },
        removeCurso(curso) {
            var vue = this;

            swal({
                text: "¿Está seguro que desea eliminar el curso?",
                icon: "warning",
                type: "warning",
                dangerMode: true,
                showCancelButton: true,
                closeOnConfirm: false,
                buttons: {
                    cancel: "No",
                    confirm: "Si, estoy seguro"
                }
            }).then((willDelete) => {
                if (willDelete) {
                    vue.deleteCursoSinEncuesta(curso);
                }
            });

        },
        searchCurso(nombre) {
            this.isLoading = true
            $.ajax({
                url: APP.url("academico/encuestaestudiantil/editor/searchcurso"),
                dataType: 'json',
                type: 'post',
                data: {nombre: nombre}
            }).then(response => {
                console.log(response.data);
                this.cursoss = response.data
                this.isLoading = false
            })
        },
        removePeriodo(i) {
            var vue = this;
            vue.periodosEncuesta.splice(i, 1);
        },
        verEstados(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'ec.estado', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'ec.estado', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.load.querie = [];
                $vue.$refs.load.changeUrl('queries[ec.estado]', null);
                $vue.$refs.load.loadRemoteData();
            }
        }
    }
});
