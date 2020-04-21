Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#matriculableVUE',
    data: {
        matriculaURL: APP.url(`${rutaModulo}/list`),
        ciclo: JSON.parse(cicloJson),
        resumen: JSON.parse(resumenJson),
        tiposCondicionales: JSON.parse(tipoCondicionalJson),
        configTurno: [],
        alumno: {},
        alumnos: [],
        processreporte: false,
        modalTurno: {
            id: 'modalTurno',
            header: true,
            title: 'Asignar Turno',
            okbtn: "Guardar",
            showaccept: false
        },
        modalCondicion: {
            id: 'modalCondicion',
            header: true,
            title: 'Agregar Alumno ',
            okbtn: "Guardar",
            showaccept: true
        },
        modalMatriculable: {
            id: 'modalMatriculable',
            header: true,
            title: 'Agregar Matriculable',
            okbtn: "Guardar",
            showaccept: true
        },
        modalInhabilitarMatriculable: {
            id: 'modalInhabilitarMatriculable',
            header: true,
            title: 'Inhabilitar Matriculable',
            okbtn: "Aceptar",
            showaccept: true
        },
        modalProcesos: {
            id: 'modalProcesos',
            styleModal: {'background-color': '#D8D8D8'},
            dataBackdrop: 'static',
            dataKeyboard: 'false',
            header: false,
            footer: false,
            cancelbtn: 'Cerrar'
        },
        matriculableSelected: {},
        tipoCondicional: {},
        messageAvance: 0,
        porcentajeAvance: 0,
        configConfirmAction: VUE_MODAL.structConfirm({}),
        procesando: false,
        resumenModal: {},
        confAporteAlumno: VUE_MODAL.structInfo({
            id: 'modalAporteAlumno',
            modalsize: 'modal-lg'
        }),
        modalBoletaAlumno: VUE_MODAL.structInfo({
            id: 'modalBoletaAlumno',
            title: 'Boletas del Alumno'
        }),
        url: null,
        seleccionado: '',
        bgColorClass: {pregrado: '', postgrado: '', visitante: '', especial: ''},
    },
    mounted: function () {

    },
    computed: {
        modalTitulo() {
            let $vue = this;
            return $vue.resumenModal.nombre;
        },
        modalSubtitulo() {
            let $vue = this;
            if ($vue.resumenModal.modalidadEstudio !== "Visitante" && $vue.resumenModal.modalidadEstudio !== "Especial") {
                return $vue.resumenModal.carrera + " - " + $vue.resumenModal.modalidadEstudio;
            } else {
                return $vue.resumenModal.carrera;
            }
        }
    },
    methods: {
        style(item) {
            var colorEstado = {MAT: 'success', PMAT: 'warning', NMAT: 'default'};
            var res = colorEstado[item];
            if (res === undefined) {
                return "label label-danger";
            }
            return "label label-" + res;
        },
        customConfig( {tipoEnum, eventoCicloAcademico}) {
            if (tipoEnum != null) {
                return tipoEnum.value - eventoCicloAcademico.eventoAcademico.nombre;
        }
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.alumno.id + '/infoacademico') + $vue.getOrigenURL();
        },
        inhabilitarModal(item) {
            let $vue = this;
            $vue.matriculableSelected = {};
            $vue.matriculableSelected = item;
            $vue.$refs.modalInhabilitarMatriculable.open();
        },
        inhabilitar() {
            let $vue = this;
            if (!$("#formInhabilitar").parsley().validate()) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/inhabilitar`),
                data: JSON.stringify($vue.matriculableSelected),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalInhabilitarMatriculable.close();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        verAddMatriculable() {
            let $vue = this;
            $vue.alumno = {};
            $vue.$refs.modalMatriculable.open();
        },
        generarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/generarPrioridad`),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        eliminarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/eliminarPrioridad`),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/ciclo`),
                success: function (response) {
                    $vue.ciclo = response.data;
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findConfiguraciones() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/configuracionesTurno`),
                success: function (response) {
                    if (response.data.length == 0) {
                        notify("No hay configuración de turnos", "error");
                    } else {
                        $vue.configTurno = response.data;
                        $vue.$refs.modalTurno.open();
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        procesarTipoMatricula(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/procesarTipoMatricula`),
                data: {
                    confTurnoAtencion: item.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalTurno.close();
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        finalizarPrioridad(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/finalizarPrioridad`),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        generarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/generar`),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        limpiarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/limpiarMatriculable`),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        finalizarMatriculable() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/finalizarMatriculable`),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        saveMatriculable() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
//            $('#formMatriculable').parsley().destroy();
            if (!$("#formMatriculable").parsley().validate()) {
                MODAL.hideWait();
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/saveMatriculable/${$vue.tipoCondicional.name}`),
                contentType: "application/json",
                data: JSON.stringify($vue.alumno),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalMatriculable.close();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    } else {
                        MODAL.hideWait();
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url(`${rutaModulo}/allAlumnoByNombre`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        urlMatricula(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.alumno.id + '/gomatricula') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        revisar() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/verificarAvance`),
                success: function (response) {
                    if (!response.success) {
                        MODAL.hideWait();
                        $vue.messageAvance = response.message;
                        $vue.porcentajeAvance = response.data;
                        $vue.verificarAlumnosNmat();
                        $vue.$refs.modalProcesos.open();
                        MODAL.hideWait();

                    } else {
                        let msg = "¿Está seguro que desea verificar los no matriculados?";
                        let btn = "Si, verificar";
                        $vue.configConfirmAction.message = msg;
                        $vue.configConfirmAction.okbtn = btn;
                        $vue.configConfirmAction.okaction = $vue.verificarAlumnosNmat;
                        $vue.styleProgress = 'width: ' + $vue.porcentProgress + '%';
                        $vue.$refs.modalConfirmAction.open();

                        $vue.procesando = true;
                        $vue.showProgress = true;
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        verificarAlumnosNmat(item) {
            let $vue = this;
            if ($vue.procesando) {
                $vue.$refs.modalConfirmAction.close();
                $vue.$refs.modalProcesos.open();
            }

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/verificarAlumnosNmat`),
                success: function (response) {
                    if (!response.success) {
                        $vue.messageAvance = response.message;
                        $vue.porcentajeAvance = response.data;
                        $vue.procesando = true;

                        setTimeout(function () {
                            $vue.verificarAlumnosNmat(1);
                        }, 1000);
                    } else {
                        $vue.showProgress = false;
                        $vue.procesando = true;
                        bootbox.alert({
                            message: 'Proceso finalizado',
                            callback: function () {
                                $vue.$refs.modalProcesos.close();
                            }
                        });
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        beneficiar(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/beneficiar`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        asignarAporte(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea asignarle el aporte <strong class="text-danger">Carné</strong>?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionAsignarAporte(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionAsignarAporte(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/agregarAporteCarnet`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        quitarAporte(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea remover el aporte?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionQuitarAporte(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionQuitarAporte(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/quitarAporteCarnet`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        asignarAporteDuplicado(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea asignarle el aporte <strong class="text-danger">Duplicado Carné</strong>?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionAsignarAporteDuplicado(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionAsignarAporteDuplicado(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/agregarAporteDuplicadoCarnet`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        quitarAporteDuplicado(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea remover el aporte duplicado carné?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionQuitarAporteDuplicado(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionQuitarAporteDuplicado(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/quitarAporteDuplicadoCarnet`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        asignarSegundaCarrera(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea asignarle el aporte <strong class="text-danger">Segunda Carrera</strong>?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionSegundaCarrera(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionSegundaCarrera(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/agregarAporteSegundaCarrera`),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        urlGoMaipi(item) {
            let $vue = this;
            return APP.url(`academico/alumno/${item.alumno.id}/goMaipi`) + $vue.getOrigenURL();
        },
        verAportes(item) {
            let $vue = this;
            $vue.resumenModal = {};
            $vue.$refs.modalAporteAlumno.open();
            $vue.$refs.modalAporteAlumno.showWait("Cargando aportes");

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/getInfoAportes/${item.id}`),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalAporteAlumno.hideWait();
                        $vue.resumenModal = response.data;
                    } else {
                        $vue.$refs.modalAporteAlumno.close();
                        notify(response.message, "error");
                    }

                },
                error() {
                    $vue.$refs.modalAporteAlumno.close();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        verBoletas(item) {
            let $vue = this;
            let idMatriculaResumen = item.id;

            $vue.resumenModal = {};
            $vue.url = null;
            $vue.$refs.modalBoletaAlumno.open();
            $vue.$refs.modalBoletaAlumno.showWait("Buscando boletas..");

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/findBoleta/${idMatriculaResumen}`),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalBoletaAlumno.hideWait();
                        if (response.data.boletas.length == 0) {
                            $vue.$refs.modalBoletaAlumno.close();
                            notify("No existen boletas generadas para este alumno", "warning");
                            return;
                        }
                        $vue.resumenModal = response.data;

                    } else {
                        $vue.$refs.modalBoletaAlumno.close();
                        notify(response.message, "error");
                    }
                },
                error() {
                    $vue.$refs.modalBoletaAlumno.close();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        isPosgrado(modalidad) {
            return "/EPG/VIS/ESP/".indexOf(modalidad.codigo) >= 0;
        },
        urlHabilitarCursos(item) {
            let $vue = this;
            return APP.url(`academico/alumno/${item.id}/configcursos`) + $vue.getOrigenURL();
        },
        generarReporte(param) {
            let $vue = this;
            let urll = '';
            $vue.processreporte = true;

            if (param === "candidatosAptPre" || param === "votantesAptPre") {
                urll = APP.url(`${rutaModulo}/aptosPregrado`);
            }

            axios({
                url: urll,
                method: 'POST',
                responseType: 'blob',
                params: {tipoReporte: param}
            }).then((response) => {
                var namee = response
                        .headers["content-disposition"]
                        .replace("attachment; filename=", "")
                        .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();
                $vue.processreporte = false;
            }).catch(error => {
                $vue.processreporte = false;
                notify(GlobalMessages.errorComunicacion, "error");
            });
        },
        verModalidades(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.load.querie = [];
                $vue.$refs.load.changeUrl('queries[moe.codigo]', null);
                $vue.$refs.load.loadRemoteData();
            }
        }
    }
});


