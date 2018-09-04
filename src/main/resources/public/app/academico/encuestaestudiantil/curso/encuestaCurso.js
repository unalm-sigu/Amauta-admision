Vue.component('date-picker', VueBootstrapDatetimePicker.default);
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
        cfgEncuestaConfig: {
            id: 'modalEncuestaConfig',
            modalsize: 'modal-lg',
            header: false
        },
        configuraEncuesta: {},
        periodosEncuesta: [],
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        }
    },
    mounted: function () {
        let vue = this;
        $global.$on("estado", function (encuestaDocente) {
            vue.estado(encuestaDocente);
        });
        if (vue.estadoVisor == 'INICIADO' || vue.estadoVisor == 'OCUPADO') {
            setTimeout(function () {
                vue.$refs.modalVerProgreso.open();
                vue.refreshProgresoEncuesta();
            }, 1000);
        }

        vue.refreshEncuesta();
    },
    methods: {
        verDocentes(seccion) {
            let vue = this;
            vue.docentesSecciones = seccion.docenteSeccion;
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
                message: '¿Está seguro que desea activar la encuesta de cursos para este ciclo?',
                buttons: {
                    confirm: {label: 'Si, activar encuesta'},
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
        estado: function (encuestaCurso) {
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
        changeEstado: function (encuestaDocente) {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/estado'),
                async: false,
                data: {'id': encuestaDocente.id},
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
        refreshProgresoEncuesta: function () {
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
        }
    }
});
