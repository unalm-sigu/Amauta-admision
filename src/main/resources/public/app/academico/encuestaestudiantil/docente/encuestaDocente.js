Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        generando: false,
        encuestaURL: APP.url(`${rutaModulo}/list`),
        docentesSecciones: [],
        encuesta: {
            encuestasActivas: 0,
            encuestasAnuladas: 0,
            encuestasInnecesarias: 0,
            encuestasSinPeriodo: 0,
            encuestasCerradas: 0,
            objetivosEncuesta: 0,
            objetivosEncuestados: 0,
            cursosNoEncuestar: 0,
            estado: "NCRE",
            estadoEnum: {value: "No creado"}
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
        cfgEncuestaConfig: VUE_MODAL.structFormAjax({
            id: 'modalEncuestaConfig',
            modalsize: 'modal-lg',
            header: false
        }),
        configuraEncuesta: {},
        periodosEncuesta: [],
        cursosNoEncuestar: [],
        estadoVisor: visor,
        porcentajeProgreso: 0,
        mensajeProgreso: "Calculado Información a procesar",
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        },
        modalPreguntas: {
            id: 'modalPreguntas',
            title: 'Resumen de preguntas',
            modalsize: 'modal-lg',
            header: true,
            footer: false,
            showaccept: false
        },
        preguntas: [],
        modalComentarios: {
            id: 'modalComentarios',
            title: 'Comentarios',
            modalsize: 'modal-md',
            header: true,
            footer: false,
            showaccept: false
        },
        comentarios: [],
        modalTemas: {
            id: 'modalTemas',
            title: 'Temas',
            modalsize: 'modal-lg',
            header: true,
            footer: false,
            showaccept: false
        },
        temas: [],
        cursos: [],
        cursosNoEnc: [],
        curso: {},
        addCursoModal: {
            id: 'modalAddCurso',
            header: true,
            title: 'Cursos sin encuesta',
            showaccept: false,
            cancelbtn: 'Cerrar'
        },
        btnAgregar: false,
        seleccionado: '',
        bgColorClass: {activo: '', anulado: '', innecesario: '', sinperiodo: '', cerrado: '',
            encuestable: '', encuestado: ''},
        encuestaDocente: {},
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        }),
        cfgDesactivarEncu: VUE_MODAL.structFormAjax({
            id: "idModalDesactivarEncu",
            form: "formDesactivarEncu"
        })
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

        let tipo = $vue.$refs.raptorEncu.getParameterByName('queries[ed.estado]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.raptorEncu.querie.push({name: 'ed.estado', value: tipo});
        }
        $vue.$refs.raptorEncu.repreload();
        $vue.loadResumen();
    },
    methods: {
        loadResumen() {
            let $vue = this;

            axios.post(`/${rutaModulo}/resumen`).then(response => {
                if (response.data.success) {
                    $vue.encuesta = response.data.data;
                }
            }).catch(function (error) {
                notify(MESSAGES.errorComunicacion, "error");
            });

        },
        removePeriodo(i) {
            var vue = this;
            vue.periodosEncuesta.splice(i, 1);
        },
        addPeriodoEncuesta() {
            var vue = this;
            vue.periodosEncuesta.push({fechaInicio: "", fechaFin: ""});
        },
        refreshEncuesta() {
            let vue = this;
            axios.post(`/${rutaModulo}/encuestaDocente`)
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
        configurarEncuesta() {
            let vue = this;
            vue.$refs.modalEncuestaConfig.open();
        },
        cancelConfiguracion() {
            var vue = this;
            vue.refreshEncuesta();
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
            vue.configuraEncuesta.encuestaTeoriaPractica = vue.configuraEncuesta.encuestaTeoriaPractica == true ? 1 : 0
            vue.encuestaForm.configuraEncuesta.push(vue.configuraEncuesta);

            axios.post(`/${rutaModulo}/saveConfigEncuesta`, vue.encuestaForm)
                    .then(response => {
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
        activarEncuesta() {
            let vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea activar la encuesta de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, activar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post(`/${rutaModulo}/activar`)
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
        generarEncuesta() {
            let vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea generar las encuestas de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, generar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post(`/${rutaModulo}/generar`)
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
        refreshProgresoEncuesta() {
            let vue = this;

            axios.post(`/${rutaModulo}/estadoGenerarEncuestas`)
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
                                message: "Finalizó la generación de encuesta de docentes",
                                buttons: {ok: {label: "Aceptar"}},
                                callback: function () {
                                    vue.$refs.raptorEncu.loadRemoteData();
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
        getDia(fecha) {
            if (fecha == "") {
                return "";
            }
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
        findPreguntas(item) {
            AXIOS.get(`/${rutaModulo}/${item.id}/resumen/preguntas`)
                    .then(response => {
                        if (response.data.success) {
                            this.preguntas = response.data.data;
                            this.$refs.modalPreguntas.open();
                        }
                    });
        },
        findComentarios(item) {
            AXIOS.get(`/${rutaModulo}/${item.id}/resumen/comentarios`)
                    .then(response => {
                        if (response.data.success) {
                            this.comentarios = response.data.data;
                            this.$refs.modalComentarios.open();
                        }
                    });
        },
        findTemas(item) {
            AXIOS.get(`/${rutaModulo}/${item.id}/resumen/temas`)
                    .then(response => {
                        if (response.data.success) {
                            this.temas = response.data.data;
                            this.$refs.modalTemas.open();
                            this.generateChart(response.data.data);
                        }
                    });
        },
        generateChart(items) {
            var aData = [];
            for (var i = 0; i < items.length; i++) {
                let obj = {};
                obj.name = items[i].temaEncuesta.nombre;
                obj.y = items[i].puntaje / 2;
                aData.push(obj);
            }

            Highcharts.chart('container', {
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Encuesta Estudiantil'
                },
                subtitle: {
                    text: '(Escala 1 - 5)'
                },
                xAxis: {
                    type: 'category'
                },
                yAxis: {
                    title: {
                        text: 'Puntaje'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.y:.1f} prom.'
                        }
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}</b> prom.<br/>'
                },
                "series": [
                    {
                        "name": "Tema",
                        "colorByPoint": true,
                        "data": aData
                    }
                ]
            });
        },
        eliminar() {
            let $vue = this;

            $vue.configConfirmAction.message = '¿Está seguro que desea eliminar la encuesta de los docentes de este ciclo?';
            $vue.configConfirmAction.okbtn = 'Si, eliminar encuesta de docentes';
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = function () {
                axios.post(`/${rutaModulo}/delete`, {id: $vue.encuesta.id})
                        .then(response => {
                            $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                            if (response.data.success) {
                                $vue.$refs.raptorEncu.loadRemoteData();
                                $vue.refreshEncuesta();
                                notify(response.data.message, "info");
                            } else {
                                notify(response.data.message, "error");
                            }
                        })
                        .catch(function (error) {
                            $vue.$refs.modalConfirmAction.confirmReaction(false);
                            console.log(error);
                            notify(MESSAGES.errorComunicacion, "error");
                        });
            };
            $vue.$refs.modalConfirmAction.open();
        },
        publicar() {
            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea publicar la encuesta de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, publicar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post(`/${rutaModulo}/publicar`, {id: $vue.encuesta.id})
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
                url: APP.url(`${rutaEditor}/allcursosinencuesta`),
                data: {
                    'id': $vue.encuesta.id
                },
                async: false,
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        $vue.cursosNoEnc = response.data;
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
                url: APP.url(`${rutaEditor}/addcursosinencuesta`),
                data: {
                    'curso.id': $vue.curso.id,
                    'encuestaEstudiantil.id': $vue.encuesta.id
                },
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.cursosNoEnc.push($vue.curso);
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
            let idx = $vue.cursosNoEnc.map(item => item.id).indexOf(curso.id);
            if (idx > -1) {
                $.ajax({
                    method: 'POST',
                    url: APP.url(`${rutaEditor}/removecursosinencuesta`),
                    data: {
                        'curso.id': curso.id,
                        'encuestaEstudiantil.id': $vue.encuesta.id
                    },
                    async: false,
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, 'info');
                            $vue.cursosNoEnc.splice(idx, 1);
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
                url: APP.url(`${rutaEditor}/searchcurso`),
                dataType: 'json',
                type: 'post',
                data: {nombre: nombre}
            }).then(response => {
                console.log(response.data);
                this.cursos = response.data
                this.isLoading = false
            })
        },
        verEstados(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'ed.estado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'ed.estado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.raptorEncu.querie = [];
                $vue.$refs.raptorEncu.changeUrl('queries[ed.estado]', null);
                $vue.$refs.raptorEncu.loadRemoteData();
            }
        },
        verResultados(item) {
            let $vue = this;
            if (item.estado != "ACT") {
                return false;
            }

            if (item.modalidadEstudio.codigo == "PRE") {
                return item.alumnosEncuestados >= $vue.configuraEncuesta.cantidadMinimaAlumnosPregrado;
            }
            if (item.modalidadEstudio.codigo == "EPG") {
                return item.alumnosEncuestados >= $vue.configuraEncuesta.cantidadMinimaAlumnosPosgrado;
            }
            return false;

        },
        verchangeEstado(item) {
            let $vue = this;
            let btn = "Si, " + (item.estado == 'ACT' ? "desactivar" : "activar");
            let okclass = (item.estado == 'ACT') ? "btn-danger" : "btn-success";

            $vue.configConfirmAction.okbtn = btn;
            $vue.configConfirmAction.okclass = okclass;

            if (item.estado == "ACT") {
                let msg = '¿Está seguro que desea <span class="text-danger" bold">desactivar</span> esta encuesta del docente <strong>';
                msg += item.docenteSeccion.docente.persona.apellidosNombres.replace(/,/g, "") + "</strong>?";

                $vue.encuestaDocente = JSON.parse(JSON.stringify(item));
                $vue.configConfirmAction.message = msg;
                $vue.configConfirmAction.okaction = function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(true);
                    $vue.$refs.modalDesactivarEncu.open();
                };

            } else {
                let msg = '<div class="h4 text-dark m-t-xxs">¿Está seguro que desea <span class="text-primary bold">activar</span> la siguiente encuesta:</div>';
                msg += '<div class="text-primary block">Docente: ' + item.docenteSeccion.docente.persona.apellidosNombres.replace(/,/g, "") + "</div>";
                msg += '<div class="text-primary block">Curso: ' + item.docenteSeccion.seccion.grupoSeccion.curso.codigo + " ";
                msg += '<i class="fa fa-bookmark-o"></i> ' + item.docenteSeccion.seccion.grupoSeccion.curso.tpc + " | ";
                msg += item.docenteSeccion.seccion.grupoSeccion.curso.nombre + "</div>";
                msg += '<div class="text-primary block">Sección: ' + item.docenteSeccion.seccion.codigo2 + "</div>";
                $vue.configConfirmAction.message = msg;

                $vue.configConfirmAction.okaction = function () {
                    let data = {id: item.id, estado: item.estado};
                    axios.post(`/${rutaModulo}/changeEstado`, data).then(response => {
                        $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.loadResumen();
                            $vue.$refs.raptorEncu.loadRemoteData();
                            notify(response.data.message, 'info');
                        } else {
                            notify(response.data.message, 'error');
                        }
                    }).catch(function (error) {
                        $vue.$refs.modalConfirmAction.confirmReaction(false);
                        notify(MESSAGES.errorComunicacion, "error");
                    });
                };
            }
            $vue.$refs.modalConfirmAction.open();

        },
        changeEstado() {
            let $vue = this;
            let form = $("#" + $vue.cfgDesactivarEncu.form);
            if (!(form.parsley().validate() === true)) {
                return;
            }

            $vue.$refs.modalDesactivarEncu.beginProcessing();
            let data = {
                id: $vue.encuestaDocente.id,
                estado: $vue.encuestaDocente.estado,
                descripcion: $vue.encuestaDocente.descripcion
            };

            axios.post(`/${rutaModulo}/changeEstado`, data).then(response => {
                $vue.$refs.modalDesactivarEncu.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.loadResumen();
                    $vue.$refs.raptorEncu.loadRemoteData();
                    notify(response.data.message, 'info');
                } else {
                    notify(response.data.message, 'error');
                }
            }).catch(function (error) {
                $vue.$refs.modalDesactivarEncu.confirmReaction(false);
                notify(MESSAGES.errorComunicacion, "error");
            });

        }
    }
});
