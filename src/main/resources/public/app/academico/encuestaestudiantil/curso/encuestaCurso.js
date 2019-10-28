Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        generando: false,
        encuestaURL: APP.url(`${rutaModulo}/list`),
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
        modalidadSeleccionada: '',
        dictadoSeleccionado: '',
        bgColorClass: {activo: '', anulado: '', innecesario: '', sinperiodo: '', cerrado: '', encuestable: '', encuestado: ''},
        bgColorModalidadClass: {posgrados: '', pregrados: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},
        facultad: null,
        departamento: null,
        facultades: JSON.parse(facultadesJson),
        departamentos: JSON.parse(departamentosJson),
        departamentosVer: JSON.parse(departamentosJson),
    },
    mounted: function () {
        let $vue = this;
        if ($vue.estadoVisor == 'INICIADO' || $vue.estadoVisor == 'OCUPADO') {
            setTimeout(function () {
                $vue.$refs.modalVerProgreso.open();
                $vue.refreshProgresoEncuesta();
            }, 1000);
        }

        //$vue.refreshEncuesta();

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
        
        $vue.$refs.raptorEncu.repreload();
        $vue.loadResumen();
    },
    methods: {
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
                }
            }).catch(function (error) {
                notify(MESSAGES.errorComunicacion, "error");
            });

        },
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
                        axios.post(`${rutaModulo}/generar`)
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
                url: APP.url(`${rutaModulo}/estado`),
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

            axios.post(`/${rutaModulo}/saveConfigEncuesta`, vue.encuestaForm)
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
                                message: "Finalizó la generación de encuesta de cursos",
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
        refreshEncuesta() {
            let vue = this;
            vue.loadResumen();
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
                        axios.post(`/${rutaModulo}/delete`, {id: $vue.encuesta.id})
                                .then(response => {
                                    if (response.data.success) {
                                        $vue.$refs.raptorEncu.loadRemoteData();
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
                url: APP.url(`${rutaEditor}/addcursosinencuesta`),
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
                    url: APP.url(`${rutaEditor}/removecursosinencuesta`),
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
                    },
                    error: function () {
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
        findPreguntas(item) {

        },
        findComentarios(item) {

        }

    }
});
