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
            encuestasPosgrado: 0,
            encuestasPregrado: 0,
            encuestasModulares: 0,
            encuestasSemestrales: 0,
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
        curso: null,
        addCursoModal: VUE_MODAL.structInfo({
            id: 'modalAddCurso',
            waiting: true
        }),
        btnAgregar: false,
        seleccionado: '',
        modalidadSeleccionada: '',
        dictadoSeleccionado: '',
        bgColorClass: {activo: '', anulado: '', innecesario: '', sinperiodo: '', cerrado: '', encuestable: '', encuestado: ''},
        bgColorModalidadClass: {posgrados: '', pregrados: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},
        encuestaDocente: {},
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        }),
        cfgDesactivarEncu: VUE_MODAL.structFormAjax({
            id: "idModalDesactivarEncu",
            form: "formDesactivarEncu"
        }),
        facultad: null,
        departamento: null,
        facultades: JSON.parse(facultadesJson),
        departamentos: JSON.parse(departamentosJson),
        departamentosVer: JSON.parse(departamentosJson),
        processingCursos: false,
        cursosLista: [],
        pageCursos: {currentPage: 1},
        paginationCursos: {'total-items': 0, 'items-per-page': 10, 'max-size': 3, 'boundary-link-numbers': true},
        idCursoNew: -100,
        cursoDelete: null,
        cfgNoProcesados: VUE_MODAL.structInfo({
            id: 'modalNoProcesados',
            waiting: true
        }),
        processingNoProcesados: false,
        docentesSeccionesNoEncu: [],
        docentesSeccionesNoEncuLista: [],
        pageNoProcesados: {currentPage: 1},
        paginationNoProcesados: {'total-items': 0, 'items-per-page': 10, 'max-size': 3, 'boundary-link-numbers': true},
    },
    mounted: function () {
        let $vue = this;
        if ($vue.estadoVisor == 'INICIADO' || $vue.estadoVisor == 'OCUPADO') {
            setTimeout(function () {
                $vue.$refs.modalVerProgreso.open();
                $vue.refreshProgresoEncuesta();
            }, 1000);
        }

        let estadoEncu = $vue.getParameterQuery('estado');
        if (estadoEncu !== '') {
            $vue.bgColorClass[estadoEncu] = 'bg-light';
            $vue.seleccionado = estadoEncu;
        }

        let modalidadEncu = $vue.getParameterQuery('modalidad');
        if (modalidadEncu !== '') {
            $vue.bgColorModalidadClass[modalidadEncu] = 'bg-light';
            $vue.modalidadSeleccionada = modalidadEncu;
        }

        let dictadoEncu = $vue.getParameterQuery('dictado');
        if (dictadoEncu !== '') {
            $vue.bgColorDictadoClass[dictadoEncu] = 'bg-light';
            $vue.dictadoSeleccionado = dictadoEncu;
        }

        let fac = $vue.getParameterQuery('facultad');
        if (fac !== '') {
            for (var i = 0; i < $vue.facultades.length; i++) {
                if (fac == $vue.facultades[i].id) {
                    $vue.facultad = $vue.facultades[i];
                }
            }
        }
        let dep = $vue.getParameterQuery('departamento');
        if (dep !== '') {
            for (var i = 0; i < $vue.departamentos.length; i++) {
                if (dep == $vue.departamentos[i].id) {
                    $vue.departamento = $vue.departamentos[i];
                }
            }
        }

        $vue.loadRaptorAllParam();
        $vue.$refs.raptorEncu.repreload();
        $vue.loadResumen();
    },
    methods: {
        changePageCursos(idCursoNew) {
            let $vue = this;
            let page = $vue.pageCursos.currentPage;
            let pageSize = $vue.paginationCursos['items-per-page'];

            if (idCursoNew !== undefined) {
                console.log("idCursoNew !== undefined")
                var loop = 0;
                var existe = false;
                for (var i = 0; i < $vue.cursosNoEnc.length; i++) {
                    if (!existe) {
                        loop++;
                    }
                    if (idCursoNew === $vue.cursosNoEnc[i].id) {
                        existe = true;
                    }
                }
                console.log("existe=" + existe + " loop=" + loop)
                if (existe) {
                    var npage = (loop - loop % pageSize) / pageSize;
                    npage += (loop % pageSize === 0) ? 0 : 1;
                    $vue.pageCursos.currentPage = npage;
                    page = npage;
                    $vue.idCursoNew = idCursoNew;
                    setTimeout(function () {
                        $vue.idCursoNew = -100;
                    }, 3000);
                }
                console.log("page=" + page)
            } else {
                console.log("idCursoNew == undefined")
            }

            let ini = pageSize * (page - 1);
            let fin = pageSize * page - 1;

            if (ini >= $vue.cursosNoEnc.length) {
                $vue.pageCursos.currentPage = 1;
                page = $vue.pageCursos.currentPage;
                ini = pageSize * (page - 1);
                fin = pageSize * page - 1;
            }

            $vue.cursosLista = [];
            for (var i = 0; i < $vue.cursosNoEnc.length; i++) {
                if (i >= ini && i <= fin) {
                    $vue.cursosLista.push($vue.cursosNoEnc[i]);
                }
            }
        },
        getParameterQuery(param) {
            let $vue = this;
            let value = $vue.$refs.raptorEncu.getParameterByName('queries[' + param + ']');
            value = (value == null) ? '' : value;
            return value;
        },
        setParameterQuery(param, value) {
            let $vue = this;
            if (value !== '') {
                $vue.$refs.raptorEncu.querie.push({name: param, value: value});
            }
        },
        clearFacultad(qwe) {
            let $vue = this;
            console.log(qwe)
            $vue.facultad = null;
            $vue.departamentosVer = JSON.parse(JSON.stringify($vue.departamentos));

            $vue.loadRaptorAllParam();
            $vue.$refs.raptorEncu.loadRemoteData(true);
            $vue.loadResumen();
        },
        clearDepartamento(qwe) {
            let $vue = this;
            console.log(qwe)
            $vue.departamento = null;

            $vue.loadRaptorAllParam();
            $vue.$refs.raptorEncu.loadRemoteData(true);
            $vue.loadResumen();
        },
        loadEncuByFacultad(item) {
            let $vue = this;
            let existeDpto = $vue.departamento !== null;
            let existeDentroFac = false;
            $vue.departamentosVer = [];
            for (var i = 0; i < $vue.departamentos.length; i++) {
                if ($vue.departamentos[i].facultad.id === item.id) {
                    $vue.departamentosVer.push($vue.departamentos[i]);
                    if (existeDpto) {
                        if ($vue.departamento.id === $vue.departamentos[i].id) {
                            existeDentroFac = true;
                        }
                    }
                }
            }
            if (existeDpto && !existeDentroFac) {
                $vue.departamento = null;
            }

            $vue.loadRaptorAllParam();
            $vue.$refs.raptorEncu.loadRemoteData(true);
            $vue.loadResumen();

        },
        loadEncuByDepartamento(item) {
            let $vue = this;
            $vue.loadRaptorAllParam();
            $vue.$refs.raptorEncu.loadRemoteData(true);
            $vue.loadResumen();
        },
        loadRaptorAllParam() {
            let $vue = this;
            let estadoEncu = $vue.getParameterQuery('estado');
            let modalidadEncu = $vue.getParameterQuery('modalidad');
            let dictadoEncu = $vue.getParameterQuery('dictado');

            $vue.$refs.raptorEncu.querie = [];
            $vue.$refs.raptorEncu.changeUrl('queries[estado]', null);
            $vue.$refs.raptorEncu.changeUrl('queries[modalidad]', null);
            $vue.$refs.raptorEncu.changeUrl('queries[dictado]', null);
            $vue.$refs.raptorEncu.changeUrl('queries[facultad]', null);
            $vue.$refs.raptorEncu.changeUrl('queries[departamento]', null);

            $vue.setParameterQuery("estado", estadoEncu);
            $vue.setParameterQuery("modalidad", modalidadEncu);
            $vue.setParameterQuery("dictado", dictadoEncu);

            if ($vue.facultad !== null) {
                $vue.setParameterQuery("facultad", $vue.facultad.id);
            }
            if ($vue.departamento !== null) {
                $vue.setParameterQuery("departamento", $vue.departamento.id);
            }


        },
        loadResumen() {
            let $vue = this;

            axios.post(`/${rutaModulo}/resumen`).then(response => {
                if (response.data.success) {
                    $vue.encuesta = response.data.data;
                    $vue.configuraEncuesta = {};
                    if ($vue.encuesta.configuraEncuesta.length > 0) {
                        $vue.configuraEncuesta = $vue.encuesta.configuraEncuesta[0];
                    }
                }
            }).catch(function (error) {
                notify(Messages.errorComunicacion, "error");
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
            vue.loadResumen();
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

            axios.post(`/${rutaModulo}/saveConfigEncuesta`, vue.encuestaForm).then(response => {
                if (response.data.success) {
                    notify(response.data.message, 'info');
                    vue.$refs.modalEncuestaConfig.close();
                    vue.refreshEncuesta();
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(function (error) {
                console.log(error);
                notify(Messages.errorComunicacion, "error");
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
                                    notify(Messages.errorComunicacion, "error");
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
                                    notify(Messages.errorComunicacion, "error");
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
                        notify(Messages.errorComunicacion, "error");
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
                            notify(Messages.errorComunicacion, "error");
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
                        axios.post(`/${rutaModulo}/publicar`, {id: $vue.encuesta.id}).then(response => {
                            if (response.data.success) {
                                $vue.refreshEncuesta();
                                notify(response.data.message, "info");
                            } else {
                                notify(response.data.message, "error");
                            }

                        }).catch(function (error) {
                            console.log(error);
                            notify(Messages.errorComunicacion, "error");
                        });
                    }
                }
            });
        },
        sinEncuesta() {
            var $vue = this;
            $vue.addCursoModal = VUE_MODAL.structInfo({
                id: 'modalAddCurso',
                waiting: true
            });

            $vue.curso = null;
            $vue.processingCursos = true;
            $vue.pageCursos.currentPage = 1;
            $vue.$refs.modalAddCurso.open();

            axios.post(`/${rutaEditor}/allcursosinencuesta`, {'id': $vue.encuesta.id}).then(response => {
                if (response.data.success) {
                    $vue.cursosNoEnc = response.data.data;
                    $vue.paginationCursos['total-items'] = $vue.cursosNoEnc.length;
                    $vue.changePageCursos();
                    $vue.processingCursos = false;
                    $vue.addCursoModal.waiting = false;

                } else {
                    notify(response.data.message, 'error');
                }
            }).catch(function (error) {
                notify(Messages.errorComunicacion, "error");
            });

        },
        agregarCurso() {
            var $vue = this;
            if ($vue.curso == null) {
                notify("No hay curso seleccionado  para agregar", "error");
                return;
            }

            let idCursoNew = $vue.curso.id;
            $vue.$refs.modalAddCurso.beginProcessing();
            axios.post(`/${rutaEditor}/addcursosinencuesta`, {
                curso: {id: $vue.curso.id},
                encuestaEstudiantil: {id: $vue.encuesta.id}
            }).then(response => {
                $vue.$refs.modalAddCurso.confirmReaction(false);
                if (response.data.success) {

                    notify(response.data.message, 'info');
                    $vue.processingCursos = true;
                    setTimeout(function () {
                        axios.post(`/${rutaEditor}/allcursosinencuesta`, {'id': $vue.encuesta.id}).then(response => {
                            if (response.data.success) {
                                $vue.cursosNoEnc = response.data.data;
                                $vue.paginationCursos['total-items'] = $vue.cursosNoEnc.length;
                                $vue.changePageCursos(idCursoNew);
                                $vue.processingCursos = false;
                                $vue.refreshEncuesta();

                            } else {
                                notify(response.data.message, 'error');
                            }
                        }).catch(function (error) {
                            $vue.processingCursos = false;
                            notify(Messages.errorComunicacion, "error");
                        });
                    }, 1000);

                } else {
                    notify(response.data.message, 'error');
                }

            }).catch(function (error) {
                $vue.$refs.modalAddCurso.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });

            $vue.curso = null;
        },
        deleteCursoSinEncuesta(curso) {
            console.log("34535-34-53-4534534-534534")
            let $vue = this;

            axios.post(`/${rutaEditor}/removecursosinencuesta`, {
                curso: {id: $vue.cursoDelete.id},
                encuestaEstudiantil: {id: $vue.encuesta.id}
            }).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    notify(response.data.message, 'info');
                    $vue.processingCursos = true;
                    setTimeout(function () {
                        axios.post(`/${rutaEditor}/allcursosinencuesta`, {'id': $vue.encuesta.id}).then(response => {
                            if (response.data.success) {
                                $vue.cursosNoEnc = response.data.data;
                                $vue.paginationCursos['total-items'] = $vue.cursosNoEnc.length;
                                $vue.changePageCursos();
                                $vue.processingCursos = false;
                                $vue.refreshEncuesta();

                            } else {
                                notify(response.data.message, 'error');
                            }
                        }).catch(function (error) {
                            $vue.processingCursos = false;
                            notify(Messages.errorComunicacion, "error");
                        });
                    }, 1000);
                } else {
                    notify(response.message, 'error');
                }

            }).catch(function (error) {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        removeCurso(curso) {
            let $vue = this;
            $vue.cursoDelete = curso;

            $vue.configConfirmAction.okbtn = "Si, eliminar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.message = "¿Está seguro que desea eliminar el curso?";
            $vue.configConfirmAction.okaction = $vue.deleteCursoSinEncuesta;
            $vue.$refs.modalConfirmAction.open();
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
            });
        },
        verEstados(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'estado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'estado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.raptorEncu.changeUrl('queries[estado]', null);

                $vue.loadRaptorAllParam();
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();
            }
        },
        verModalidad(tipo) {
            let $vue = this;
            if ($vue.modalidadSeleccionada === '') {
                $vue.bgColorModalidadClass[tipo] = 'bg-light';
                $vue.modalidadSeleccionada = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'modalidad', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.modalidadSeleccionada !== '' && $vue.modalidadSeleccionada !== tipo) {
                $vue.bgColorModalidadClass[$vue.modalidadSeleccionada] = '';
                $vue.bgColorModalidadClass[tipo] = 'bg-light';
                $vue.modalidadSeleccionada = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'modalidad', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.modalidadSeleccionada !== '' && $vue.modalidadSeleccionada === tipo) {
                $vue.bgColorModalidadClass[$vue.modalidadSeleccionada] = '';
                $vue.modalidadSeleccionada = '';
                $vue.$refs.raptorEncu.changeUrl('queries[modalidad]', null);

                $vue.loadRaptorAllParam();
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();
            }
        },
        verDictado(tipo) {
            let $vue = this;
            if ($vue.dictadoSeleccionado === '') {
                $vue.bgColorDictadoClass[tipo] = 'bg-light';
                $vue.dictadoSeleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'dictado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.dictadoSeleccionado !== '' && $vue.dictadoSeleccionado !== tipo) {
                $vue.bgColorDictadoClass[$vue.dictadoSeleccionado] = '';
                $vue.bgColorDictadoClass[tipo] = 'bg-light';
                $vue.dictadoSeleccionado = tipo;

                $vue.$refs.raptorEncu.querie.push({name: 'dictado', value: tipo});
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();

            } else if ($vue.dictadoSeleccionado !== '' && $vue.dictadoSeleccionado === tipo) {
                $vue.bgColorDictadoClass[$vue.dictadoSeleccionado] = '';
                $vue.dictadoSeleccionado = '';
                $vue.$refs.raptorEncu.changeUrl('queries[dictado]', null);

                $vue.loadRaptorAllParam();
                $vue.$refs.raptorEncu.loadRemoteData(true);
                $vue.loadResumen();
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
                        notify(Messages.errorComunicacion, "error");
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
                notify(Messages.errorComunicacion, "error");
            });

        },
        noProcesados() {
            let $vue = this;
            $vue.cfgNoProcesados.waiting = true;
            $vue.$refs.modalNoProcesados.open();

            axios.post(`/${rutaModulo}/allNoProcesados`, {'id': $vue.encuesta.id}).then(response => {
                $vue.cfgNoProcesados.waiting = false;
                if (response.data.success) {
                    $vue.docentesSeccionesNoEncu = response.data.data;
                    $vue.paginationNoProcesados['total-items'] = $vue.docentesSeccionesNoEncu.length;
                    $vue.changePageNoProcesados();

                } else {
                    notify(response.data.message, 'error');
                }
            }).catch(function (error) {
                $vue.cfgNoProcesados.waiting = false;
                notify(Messages.errorComunicacion, "error");
            });
        },
        changePageNoProcesados() {
            let $vue = this;
            let page = $vue.pageNoProcesados.currentPage;
            let pageSize = $vue.paginationNoProcesados['items-per-page'];

            let ini = pageSize * (page - 1);
            let fin = pageSize * page - 1;

            if (ini >= $vue.docentesSeccionesNoEncu.length) {
                $vue.pageNoProcesados.currentPage = 1;
                page = $vue.pageNoProcesados.currentPage;
                ini = pageSize * (page - 1);
                fin = pageSize * page - 1;
            }

            $vue.docentesSeccionesNoEncuLista = [];
            for (var i = 0; i < $vue.docentesSeccionesNoEncu.length; i++) {
                if (i >= ini && i <= fin) {
                    $vue.docentesSeccionesNoEncuLista.push($vue.docentesSeccionesNoEncu[i]);
                }
            }
        },
        addNoProcesado(item) {
            let $vue = this;

            $vue.configConfirmAction.okbtn = "Si, agregar";
            $vue.configConfirmAction.okclass = "btn-success";
            $vue.configConfirmAction.message = '¿Está seguro que desea agregar este docente-sección a la encuesta?';
            $vue.configConfirmAction.okaction = function () {
                axios.post(`/${rutaModulo}/addNoProcesado`, item).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {

                        $vue.cfgNoProcesados.waiting = true;
                        axios.post(`/${rutaModulo}/allNoProcesados`, {'id': $vue.encuesta.id}).then(response => {
                            $vue.cfgNoProcesados.waiting = false;
                            if (response.data.success) {
                                $vue.docentesSeccionesNoEncu = response.data.data;
                                $vue.paginationNoProcesados['total-items'] = $vue.docentesSeccionesNoEncu.length;
                                $vue.changePageNoProcesados();

                            } else {
                                notify(response.data.message, 'error');
                            }
                        }).catch(function (error) {
                            $vue.cfgNoProcesados.waiting = false;
                            notify(Messages.errorComunicacion, "error");
                        });

                    } else {
                        notify(response.data.message, 'error');
                    }
                }).catch(function (error) {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            }

            $vue.$refs.modalConfirmAction.open();
        }

    }
});
