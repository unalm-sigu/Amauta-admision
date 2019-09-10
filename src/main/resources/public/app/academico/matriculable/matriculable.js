Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#matriculableVUE',
    data: {
        matriculaURL: APP.url('academico/matriculable/list'),
        ciclo: JSON.parse(cicloJson),
        resumen: JSON.parse(resumenJson),
        tiposCondicionales: JSON.parse(tipoCondicionalJson),
        configTurno: [],
        alumno: {},
        alumnos: [],
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
        url: null
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
            if (res == undefined) {
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
                url: APP.url('academico/matriculable/inhabilitar'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        modal() {
            let $vue = this;
            $vue.alumno = {};
            $vue.$refs.modalMatriculable.open();
        },
        generarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        eliminarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/eliminarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findCiclo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/ciclo'),
                success: function (response) {
                    $vue.ciclo = response.data;
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findConfiguraciones() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/configuracionesTurno'),
                success: function (response) {
                    if (response.data.length == 0) {
                        notify("No hay configuración de turnos", "error");
                    } else {
                        $vue.configTurno = response.data;
                        $vue.$refs.modalTurno.open();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        procesarTipoMatricula(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/procesarTipoMatricula'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        finalizarPrioridad(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/finalizarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        generarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generar'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        limpiarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/limpiarMatriculable'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        finalizarMatriculable() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/finalizarMatriculable'),
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
                    notify(MESSAGES.errorComunicacion, "error");
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
                url: APP.url('academico/matriculable/saveMatriculable/' + $vue.tipoCondicional.name),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/matriculable/allAlumnoByNombre"),
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
                url: APP.url('academico/matriculable/verificarAvance'),
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
                    notify(MESSAGES.errorComunicacion, "error");
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
                url: APP.url('academico/matriculable/verificarAlumnosNmat'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        beneficiar(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/beneficiar'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        asignarAporte(item) {
            let $vue = this;
            $vue.configConfirmAction.message = '¿Seguro que desea asignarle el aporte?';
            $vue.configConfirmAction.okaction = () => {
                $vue.actionAsignarAporte(item);
            };
            $vue.$refs.modalConfirmAction.open();
        },
        actionAsignarAporte(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/agregarAporteCarnet'),
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
                    notify(MESSAGES.errorComunicacion, "error");
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
                url: APP.url('academico/matriculable/quitarAporteCarnet'),
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
                    notify(MESSAGES.errorComunicacion, "error");
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                }
            });
        },
        urlGoMaipi(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.alumno.id + '/goMaipi') + $vue.getOrigenURL();
        },
        verAportes(item) {
            let $vue = this;
            $vue.resumenModal = {};
            $vue.$refs.modalAporteAlumno.open();
            $vue.$refs.modalAporteAlumno.showWait("Cargando aportes");

            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/getInfoAportes/' + item.id),
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
                    notify(MESSAGES.errorComunicacion, "error");
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
                url: APP.url('academico/matriculable/findBoleta/' + idMatriculaResumen),
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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        isPosgrado(modalidad) {
            return "/EPG/VIS/ESP/".indexOf(modalidad.codigo) >= 0;
        },
        urlHabilitarCursos(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/configcursos') + $vue.getOrigenURL();
        },
    }
});


