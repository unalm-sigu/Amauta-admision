Vue.component('date-picker', VueBootstrapDatetimePicker.default);
new Vue({
    el: '#main',
    data: {
        generando: false,
        encuestaURL: APP.url('academico/encuestaestudiantil/docente/list'),
        docentesSecciones: [],
        encuesta: JSON.parse(encuestaJson),
        cfgVerProgreso: {
            id: 'modalVerProgreso',
            header: false,
            footer: false,
            showaccept: false,
            modalsize: 'modal-lg',
            dataBackdrop: 'static',
            dataKeyboard: 'false'
        },
        cfgEncuestaConfig: {
            id: 'modalEncuestaConfig',
            modalsize: 'modal-lg',
            header: false
        },
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
        temas: []
    },
    mounted: function () {
        let vue = this;
        if (vue.estadoVisor == 'INICIADO' || vue.estadoVisor == 'OCUPADO') {
            setTimeout(function () {
                vue.$refs.modalVerProgreso.open();
                vue.refreshProgresoEncuesta();
            }, 1000);
        }
        vue.refreshEncuesta();
    },
    methods: {
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
            axios.post('/academico/encuestaestudiantil/docente/encuestaDocente')
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

            axios.post('/academico/encuestaestudiantil/docente/saveConfigEncuesta', vue.encuestaForm)
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
                        axios.post('/academico/encuestaestudiantil/docente/activar')
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
                message: '¿Está seguro que desea activar la encuesta de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, activar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/docente/generar')
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
        refreshProgresoEncuesta: function () {
            let vue = this;

            axios.post('/academico/encuestaestudiantil/docente/estadoGenerarEncuestas')
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
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/preguntas`)
                    .then(response => {
                        if (response.data.success) {
                            this.preguntas = response.data.data;
                            this.$refs.modalPreguntas.open();
                        }
                    })
        },
        findComentarios(item) {
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/comentarios`)
                    .then(response => {
                        if (response.data.success) {
                            this.comentarios = response.data.data;
                            this.$refs.modalComentarios.open();
                        }
                    })
        },
        findTemas(item) {
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/temas`)
                    .then(response => {
                        if (response.data.success) {
                            this.temas = response.data.data;
                            this.$refs.modalTemas.open();
                            this.generateChart(response.data.data);
                        }
                    })
        },
        generateChart(items) {
            var aData = [];
            for (var i = 0; i < items.length; i++) {
                let obj = {};
                obj.name = items[i].temaEncuesta.nombre;
                obj.y = items[i].puntaje;
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
                            format: '{point.y:.1f}%'
                        }
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
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
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar la encuesta de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, eliminar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/docente/delete', {id: $vue.encuesta.id})
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
                message: '¿Está seguro que desea publicar la encuesta de docentes para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, publicar encuesta'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        axios.post('/academico/encuestaestudiantil/docente/publicar', {id: $vue.encuesta.id})
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
        }
    }
});
