Vue.component("multiselect", window.VueMultiselect.default);

$('#dynaTable').dynatable({});
$('#dynaTableEspecial').dynatable({});



Vue.component("seccion-det-component", {
    template: "#seccionDetComp",
    props: {
        seccion: null
    },
    watch: {
        seccion(newValue) {
        }
    }
});
Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#pageGpoSeccion',
    data: {
        SIN_RESTRICCION_TEXT: "Todos",
        ciclo: {},
        grupoSeccion: {},
        navega: {},
        oficinas: [],
        btnNavega: {left: true, right: true},
        idxSeccion: 0,
        secciones: null,
        directEditSecciones: false,
        docentesSeccion: [],
        docentes: [],
        seccionSeleccionada: null,
        verDocentes: false,
        habilDep: false,
        departamento: {},
        seccionModal: null,
        tabVisible: "DOCENTES",
        colorEstado: {CRE: "default", ACT: "success", ANU: "danger", CAN: "danger", INA: "danger", BLO: "warning", FUS: "warning"},
        colorEstadoAmpliacion: {PENDIENTE: "default", ACEPTADO: "success", RECHAZADO: "danger", ANULADA: "warning"},
        colorEstadoAulaGrupo: {PENDIENTE: "default", ACEPTADO: "success", RECHAZADO: "danger", ANULADA: "warning"},
        grupoModal: {
            id: 'modalGrupo',
            header: true,
            title: 'Buscar Grupo Disponible',
            okbtn: 'Aceptar',
            showaccept: true,
            modalsize: 'modal-lg'
        },
        aulaModal: VUE_MODAL.structFormAjax({
            id: 'modalAula',
            header: true,
            title: 'Buscar Aula/Ambiente Disponible',
            okbtn: 'Aceptar',
            showaccept: true,
            modalsize: 'modal-lg'
        }),
        aulaHorarioModal: {
            id: 'modalAulaHorario',
            header: true,
            title: 'Horario Aula',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        restriccionModal: VUE_MODAL.structFormAjax({
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            okbtn: 'Aceptar',
            showaccept: true,
            modalsize: 'modal-lg'
        }),
        tipoRepitenciaModal: VUE_MODAL.structFormAjax({
            id: 'modalTipoRepitencia',
            header: true,
            title: 'Aplicar restricción repitencia / retirados / ingresantes',
            okbtn: 'Aceptar',
            showaccept: true,
            modalsize: 'modal-lg'
        }),
        fecha: null,
        minFechaPeriodo: null,
        maxFechaPeriodo: null,
        modulosCombo: {},
        vecesClon: 1,
        clonacionModal: {
            id: 'modalClonacion',
            header: true,
            title: '',
            okbtn: 'Clonar',
            modalsize: 'modal-sm'
        },
        solicitarIncrementoModal: {
            id: 'modalSolicitarIncremento',
            header: true,
            title: 'Solicitud de ampliación de vacantes',
            okbtn: 'Solicitar',
            showaccept: true,
            modalsize: 'modal-md'
        },
        enviarCambioAulaGrupoModal: VUE_MODAL.structFormAjax({
            id: 'modalEnviarCambioAulaGrupo',
            header: true,
            title: 'Cambio Aula / Grupo',
            okbtn: 'Solicitar',
            showaccept: true,
            modalsize: 'modal-lg'
        }),
        cambioaulagrupos: [],
        changeAulaGpo: {
            oficina: {},
            aulaInicio: {},
            grupoInicio: {},
            grupoHorasFin: {},
            aulaFin: {},
            diahoragruposelects: []
        },
        aceptarCambioAulaGrupoModal: {
            id: 'aceptarCambioAulaGrupoModal',
            header: true,
            title: 'Aceptar cambio de aula / grupo',
            okbtn: 'Si, aceptar',
            modalsize: 'modal-md'
        },
        rechazarCambioAulaGrupoModal: {
            id: 'rechazarCambioAulaGrupoModal',
            header: true,
            title: 'Rechazar cambio de aula / grupo',
            okbtn: 'Rechazar',
            modalsize: 'modal-md'
        },
        ampliaciones: [],
        ampliacionVacante: {
            id: 'ampliacionVacanteId',
            incremento: null,
            colaborador: {id: null},
            oficina: {id: null},
            seccion: {id: null}
        },
        aceptarSolicitudIncrementoModal: {
            id: 'aceptarSolicitudIncrementoModalId',
            header: true,
            title: 'Aceptar ampliación de vacantes',
            okbtn: 'Si, aceptar',
            showaccept: true,
            modalsize: 'modal-md'
        },
        rechazarSolicitudIncrementoModal: {
            id: 'rechazarSolicitudIncrementoModalId',
            header: true,
            title: 'Rechazar ampliación de vacantes',
            okbtn: 'Rechazar',
            showaccept: true,
            modalsize: 'modal-md'
        },
        activarFusion: false,
        fusion: {
            todos: false,
            revisando: false,
            seccionDestino: {id: null, aula: {id: null}, grupoHoras: {id: null}},
            alumnosid: [],
            seccionOrigen: {id: null}
        },
        precioSeccion: {
            seccion: {id: null, aula: {id: null}, grupoSeccion: {id: null}},
            alumnosid: [],
            seccionOrigen: {id: null}
        },
        alumnos: [],
        seccionesDisponibles: [],
        editaPrecio: false,
        guardaPrecio: false,
        aulas: [],
        grupos: [],
        docenteSelect: {},
        dataModalAgregarHorasAdicionales: {
            id: 'idModalAgregarHorasAdicionales',
        },
        configConfirmAction: VUE_MODAL.structConfirm({}),
        seccionWorking: {}
    },
    watch: {
        seccionSeleccionada: function (val) {
            let $vue = this;
            $vue.loadDataFusion();
        },
        activarFusion: function (val) {
            let $vue = this;
            $vue.loadDataFusion();
        },
        "ampliacionVacante.incremento": function () {
            let $vue = this;
            if ($vue.ampliacionVacante.incremento == '') {
                $vue.ampliacionVacante.vacantesFin = parseInt($vue.ampliacionVacante.vacantesInicio);
                return;
            }
            $vue.ampliacionVacante.vacantesFin = parseInt($vue.ampliacionVacante.incremento) + parseInt($vue.ampliacionVacante.vacantesInicio);
        },
    },
    created: function () {
        this.grupoSeccion = JSON.parse(gpoSeccionJson);
        this.navega = JSON.parse(navigationJson);
        this.ciclo = JSON.parse(cicloJson);
        this.oficinas = JSON.parse(oficinasJson);
        this.loadDataPantalla();
        this.departamento = this.grupoSeccion.curso.departamentoAcademico;

        console.log(this.grupoSeccion);
    },
    mounted: function () {
        let $vue = this;
        $global.$on("afterSaveAula", function (response) {
            $vue.afterSaveAula(response, $vue);
        });
        $global.$on("afterSaveGrupo", function (response) {
            $vue.afterSaveGrupo(response, $vue);
        });
        $global.$on("afterSaveRestriccion", function (response) {
            $vue.afterSaveRestriccion(response, $vue);
        });
        $global.$on("afterSaveTipoRepRestriccion", function (response) {
            $vue.afterSaveTipoRepRestriccion(response, $vue);
        });
        $global.$on("selectGrupoHorarioChange", function (grupoHorario) {
            $vue.selectGrupoHorarioChange(grupoHorario, $vue);
        });
        $global.$on("cancelarSeccion", function (seccion) {
            $vue.cancelarSeccion(seccion);
        });
    },
    computed: {
        precioBaseFormatoCalculado: function () {
            let $vue = this;
            let total = $vue.seccionSeleccionada.minimoAlumnos * $vue.seccionSeleccionada.precio;
            return total.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
        }
    },
    methods: {

        custom() {

        },
        buscarDocente(name) {
            let $vue = this;
            if ($vue.habilDep) {
                var codigoDep = $vue.departamento.codigo;
            }
            $.ajax({
                method: 'POST',
                url: APP.url("academico/gposeccion/buscarDocentes"),
                data: {nombre: name, codigoDep: codigoDep},
                success(response) {
                    if (response.success) {
                        $vue.docentes = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                    $vue.liberarBtn(dir);
                }
            });
        },
        indexSelect(index) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarDocenteSeccion'),
                data: {
                    docSeccion: $vue.docentesSeccion[index].id,
                    docente: $vue.docenteSelect.id
                },
                success: function (response) {
                    console.log("indexSelect");
                    if (response.success) {
                        $vue.reloadProfes();
                        notify(response.message, "info");

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        docenteSelected(item) {
            let $vue = this;
            $vue.docenteSelect = item;
        },
        getClassTab(tabBuscar) {
            let $vue = this;
            if ($vue.tabVisible == tabBuscar) {
                return "active";
            }
            return "";
        },
        verTab(tabBuscar) {
            let $vue = this;
            $vue.tabVisible = tabBuscar;
        },
        reloadProfes() {
            let $vue = this;
            $vue.loadGpoSeccionEfecto($vue.grupoSeccion.id, "");
        },
        loadDataPantalla() {
            let $vue = this;
            $vue.verDocentes = false;
            $vue.secciones = this.grupoSeccion.secciones;
            if ($vue.idxSeccion >= $vue.secciones.length) {
                $vue.idxSeccion = 0;
            }
            $vue.seccionSeleccionada = $vue.secciones[$vue.idxSeccion];
            $vue.docentesSeccion = $vue.seccionSeleccionada.docenteSeccion;
            $vue.ampliaciones = $vue.seccionSeleccionada.ampliacionesVacantes;
            $vue.cambioaulagrupos = $vue.seccionSeleccionada.cambioAulaGrupos;
            $vue.refreshDataFusion();
            setTimeout(function () {
                $vue.verDocentes = true;
            }, 300);
        },
        avatarGpo() {
            let $vue = this;
            return $vue.secciones[0].codigo2.substring(0, 3);
        },
        loadGpoSeccionEfecto(idGpoSecc, dir) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + idGpoSecc + '/get'),
                success(response) {
                    $vue.liberarBtn(dir);
                    if (response.success) {
                        $vue.grupoSeccion = response.data.grupoSeccion;
                        $vue.departamento = $vue.grupoSeccion.curso.departamentoAcademico;
                        $vue.loadDataPantalla();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                    $vue.liberarBtn(dir);
                }
            });
        },
        loadGpoSeccionFlash(modalConfirm) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + $vue.navega.current + '/get'),
                success(response) {
                    if (response.success) {
                        $vue.grupoSeccion = response.data.grupoSeccion;
                        $vue.secciones = $vue.grupoSeccion.secciones;
                        if ($vue.idxSeccion >= $vue.secciones.length) {
                            $vue.idxSeccion = 0;
                        }
                        $vue.seccionSeleccionada = $vue.secciones[$vue.idxSeccion];
                        $vue.docentesSeccion = $vue.seccionSeleccionada.docenteSeccion;
                        $vue.cambioaulagrupos = $vue.seccionSeleccionada.cambioAulaGrupos;
                        $vue.refreshDataFusion();
                    } else {
                        notify(response.message, "error");
                    }
                    if (modalConfirm != undefined) {
                        modalConfirm.modal("hide");
                    }
                },
                error() {
                    if (modalConfirm != undefined) {
                        modalConfirm.modal("hide");
                    }
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        liberarBtn(dir) {
            let $vue = this;
            setTimeout(function () {
                if (dir == "left") {
                    $vue.btnNavega.left = true;
                } else {
                    $vue.btnNavega.right = true;
                }
            }, 200);
        },
        classBtnPrev() {
            let $vue = this;
            if ($vue.navega.position <= 0) {
                return "nav-gposec-gray";
            }
            return "nav-gposec-primary";
        },
        classBtnNext() {
            let $vue = this;
            if ($vue.navega.position + 1 >= $vue.navega.arrayGpoSecciones.length) {
                return "nav-gposec-gray";
            }
            return "nav-gposec-primary";
        },
        prevGpoSecc() {
            let $vue = this;
            if (!$vue.btnNavega.left) {
                return;
            }
            if ($vue.navega.position <= 0) {
                return;
            }
            $vue.btnNavega.left = false;

            let url = window.location.href;
            let oldGpoSecc = "/academico/gposeccion/" + $vue.navega.current + "/";
            let newGpoSecc = "/academico/gposeccion/" + $vue.navega.prev + "/";
            $vue.navega.position = $vue.navega.position - 1;

            $vue.navega.prev = null;
            $vue.navega.next = null;
            if ($vue.navega.position > 0) {
                $vue.navega.prev = $vue.navega.arrayGpoSecciones[$vue.navega.position - 1].id;
            }
            if ($vue.navega.position + 1 < $vue.navega.arrayGpoSecciones.length) {
                $vue.navega.next = $vue.navega.arrayGpoSecciones[$vue.navega.position + 1].id;
            }
            $vue.navega.current = $vue.navega.arrayGpoSecciones[$vue.navega.position].id;

            let newUrl = url.replace(oldGpoSecc, newGpoSecc);
            history.pushState(null, null, newUrl);
            $vue.loadGpoSeccionEfecto($vue.navega.current, "left");

        },
        nextGpoSecc() {
            let $vue = this;
            if (!$vue.btnNavega.right) {
                return;
            }
            if ($vue.navega.position + 1 >= $vue.navega.arrayGpoSecciones.length) {
                return;
            }
            $vue.btnNavega.right = false;

            let url = window.location.href;
            let oldGpoSecc = "/academico/gposeccion/" + $vue.navega.current + "/";
            let newGpoSecc = "/academico/gposeccion/" + $vue.navega.next + "/";
            $vue.navega.position = $vue.navega.position + 1;

            $vue.navega.prev = null;
            $vue.navega.next = null;
            if ($vue.navega.position > 0) {
                $vue.navega.prev = $vue.navega.arrayGpoSecciones[$vue.navega.position - 1].id;
            }
            if ($vue.navega.position + 1 < $vue.navega.arrayGpoSecciones.length) {
                $vue.navega.next = $vue.navega.arrayGpoSecciones[$vue.navega.position + 1].id;
            }
            $vue.navega.current = $vue.navega.arrayGpoSecciones[$vue.navega.position].id;

            let newUrl = url.replace(oldGpoSecc, newGpoSecc);
            history.pushState(null, null, newUrl);
            $vue.loadGpoSeccionEfecto($vue.navega.current, "right");

        },
        addSeccion: function () {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/addSeccion'),
                data: {
                    grupoSeccion: $vue.grupoSeccion.id
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addDocSeccion: function () {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/addDocSeccion'),
                data: {
                    seccion: $vue.seccionSeleccionada.id
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        seleccionarSeccion: function (index) {
            let $vue = this;
            $vue.idxSeccion = index;
            $vue.loadDataPantalla();
        },
        cambiarDocPrincipal: function (docSeccion) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarDocPrincipal'),
                data: {
                    docSeccion: docSeccion.id
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionEfecto($vue.grupoSeccion.id, "");

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cambiarPorcentajeCarga: function (docSeccion, event) {
            let $vue = this;
            if (event != null) {
                let target = $(event.target);
                target.parsley().destroy();
                target.parsley();
                if (target.parsley().validate() !== true) {
                    return;
                }

                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/gposeccion/cambiarPorcentajeAvance'),
                    data: {
                        docSeccion: docSeccion.id,
                        porcentajeAvanceFraccion: docSeccion.porcentajeCargaFraccion
                    },
                    success: function (response) {
                        $vue.loadGpoSeccionFlash();
                        if (response.success) {
                            notify(response.message, "info");
                            MODAL.hideWait();

                        } else {
                            notify(response.message, "error");
                            MODAL.hideWait();
                        }
                    }, error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                        MODAL.hideWait();
                    }
                });
            }
        },
        deleteSeccion: function (seccion) {
            let $vue = this;
            let mm = bootbox.confirm({
                message: "¿Está seguro que desea eliminar la sección?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deleteSeccion'),
                            data: {seccion: seccion.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadGpoSeccionFlash(mm);
                                } else {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si');
                                    notify(response.message, "error");
                                }
                            }, error: function () {
                                $(".btn-modal").prop('disabled', false);
                                $(".btn-procesar").html('Si');
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });

                        return false;
                    }
                }
            });
        },
        verBloquearSeccion(seccion) {
            let $vue = this;
            $vue.seccionWorking = Object.assign({}, seccion);

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Está seguro que desea bloquear la sección?",
                okbtn: "Si, bloquear",
                okclass: "btn-warning",
                okaction: $vue.bloquearSeccion
            });
            $vue.$refs.modalConfirmAction.open();
        },
        bloquearSeccion() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/bloquearSeccion'),
                data: {seccion: $vue.seccionWorking.id},
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verActivarSeccion(seccion) {
            let $vue = this;
            $vue.seccionWorking = Object.assign({}, seccion);

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Está seguro que desea activar la sección?",
                okbtn: "Si, activar",
                okaction: $vue.activarSeccion
            });

            $vue.$refs.modalConfirmAction.open();
        },
        activarSeccion() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/activarSeccion'),
                data: {seccion: $vue.seccionWorking.id},
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verAnularSeccion(seccion) {
            let $vue = this;
            $vue.seccionWorking = Object.assign({}, seccion);

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "¿Está seguro que desea anular la sección?",
                okbtn: "Si, anular",
                okclass: "btn-warning",
                okaction: $vue.previoAnularSeccion,
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Anulando...'
            });

            $vue.$refs.modalConfirmAction.open();
        },
        previoAnularSeccion() {
            let $vue = this;
            let minSecciones = 1;
            if ($vue.grupoSeccion.curso.tipoCursoTEOPRA) {
                minSecciones = 2;
            }
            if ($vue.grupoSeccion.secciones.length == minSecciones) {
                $vue.$refs.modalConfirmAction.close();

                setTimeout(function () {
                    $vue.configConfirmAction = VUE_MODAL.structConfirm({
                        message: "Al anular esta sección, se eliminará el grupo. ¿Desea continuar?",
                        okbtn: "Si, continuar",
                        okclass: "btn-warning",
                        okaction: $vue.anularSeccion,
                        okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Anulando...'
                    });
                    $vue.$refs.modalConfirmAction.open();
                }, 400);

            } else {
                $vue.anularSeccion();
            }

        },
        anularSeccion() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + '/anularSeccion'),
                data: {seccion: $vue.seccionWorking.id},
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        if (response.message == 'redirect') {
                            location.href = $vue.navega.origen;
                        } else {
                            notify(response.message, "info");
                            $vue.loadGpoSeccionFlash();
                        }
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        /*
         verCancelarSeccion(seccion) {
         let $vue = this;
         $vue.seccionWorking = Object.assign({}, seccion);
         
         let alus = $vue.seccionWorking.matriculados == 1
         ? "el alumno matriculado será retirado"
         : ("los " + $vue.seccionWorking.matriculados + " alumnos matriculados serán retirados");
         
         $vue.configConfirmAction = VUE_MODAL.structConfirm({
         message: "Al cancelar esta sección, " + alus + ".<br/><br/>¿Desea continuar?",
         okbtn: "Si, cancelar",
         okclass: "btn-danger",
         okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Cancelando...',
         okaction: $vue.cancelarSeccion
         });
         
         $vue.$refs.modalConfirmAction.open();
         }
         ,*/
        cancelarSeccion(seccionWorking) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/cancelarSeccion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify(seccionWorking),
                success: function (response) {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.success);
                    if (response.success) {
                        if (response.message == 'redirect') {
                            location.href = $vue.navega.origen;
                        } else {
                            notify(response.message, "info");
                            $vue.loadGpoSeccionFlash();
                        }
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        deleteDocSeccion: function (docSeccion) {
            let $vue = this;
            let mm = bootbox.confirm({
                message: "¿Está seguro que desea elimar el docente?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deleteDocSeccion'),
                            data: {docSeccion: docSeccion.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadGpoSeccionFlash(mm);

                                } else {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si');
                                    notify(response.message, "error");
                                }
                            }, error: function () {
                                $(".btn-modal").prop('disabled', false);
                                $(".btn-procesar").html('Si');
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                        return false;
                    }
                }
            });
        },
        getEstadoClass: function (estadoCode) {
            return "label-" + this.colorEstado[estadoCode];
        },
        getEstadoAmpliacionClass: function (estadoCode) {
            return "label-" + this.colorEstadoAmpliacion[estadoCode];
        },
        loadSecciones: function () {
        },
        showModalGrupos(seccion) {
            var tabs = $("#tab-grupos");
            tabs.find("li").removeClass("active");
            tabs.find(".tab-pane").removeClass("active");

            let $vue = this;
            $vue.$refs.grupoHorarioComponentRef.loadGruposHorario(seccion.id);
            $vue.grupoModal.showaccept = true;
            if (seccion.matriculados > 0) {
                $vue.grupoModal.showaccept = false;
            } else {
                $vue.grupoModal.showaccept = true;
            }
            this.$refs.modalGrupo.open();
        },
        saveGrupo() {
            this.$refs.grupoHorarioComponentRef.saveGrupoHorario();
        },
        afterSaveGrupo(response, $vue) {
            $vue.$refs.modalGrupo.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadGpoSeccionFlash();

            } else {
                notify(response.message, "error");
            }
        },
        afterSaveRestriccion(response, $vue) {
            $vue.$refs.modalRestriccion.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadGpoSeccionFlash();
            } else {
                notify(response.message, "error");
            }
        },
        afterSaveTipoRepRestriccion(response, $vue) {
            $vue.$refs.modalTipoRepitencia.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadGpoSeccionFlash();
            } else {
                notify(response.message, "error");
            }
        },
        changeVacantes(seccion, event) {

            seccion.editVacantes = false;
            let $vue = this;
            if (event != null) {
                let form = $(event.target);

                form.attr("data-parsley-type", "digits");
                if (seccion.aula.capacidadAula != "") {
                    form.attr("data-parsley-max", seccion.aula.capacidadAula);
                } else {
                    form.removeAttr("data-parsley-max");
                }

                form.parsley().destroy();
                form.parsley();

                if (form.parsley().validate() !== true) {
                    return;
                }

                $.ajax({
                    url: APP.url('academico/gposeccion/cambiarVacantesSeccion'),
                    type: 'POST',
                    async: false,
                    data: {
                        seccion: seccion.id,
                        vacantes: seccion.vacantes
                    },
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, "info");
                            $vue.loadGpoSeccionFlash();
                        } else {

                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        },
        changeRestriccionCapa(seccion, event) {

            seccion.editRestriccionCapa = false;
            let $vue = this;
            if (event != null) {
                let form = $(event.target);

                form.attr("data-parsley-type", "digits");
                if (seccion.aula != null) {
                    form.attr("data-parsley-max", 300);
                    form.attr("data-parsley-min", 1);
                } else {
                    form.removeAttr("data-parsley-max");
                    form.removeAttr("data-parsley-min");
                }

                form.parsley().destroy();
                form.parsley();

                if (form.parsley().validate() !== true) {
                    return;
                }

                $.ajax({
                    url: APP.url('academico/gposeccion/cambiarRestriccionCapa'),
                    type: 'POST',
                    async: false,
                    data: {
                        seccion: seccion.id,
                        capa: seccion.restriccionCapa
                    },
                    success: function (response) {
                        $vue.loadGpoSeccionFlash();
                        if (response.success) {
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        },
        directAulaChange(event) {
            let target = event.target.closest("table");
            $(target).find('[class*="parsley-errors"]').each(function () {
                this.remove();
            });
        },
        showModalAula(seccion) {
            let $vue = this;
            var tabs = $("#tab-aula");
            tabs.find("li").removeClass("active");
            tabs.find(".tab-pane").removeClass("active");

            this.$refs.aulaComponent.loadAula(seccion);
            $vue.aulaModal.showaccept = true;
//            if (seccion.matriculados > 0) {
//                $vue.aulaModal.showaccept = false;
//            } else {
//                $vue.aulaModal.showaccept = true;
//            }
            this.$refs.modalAula.open();


        },
        showModalAulaHorario(aula) {
            let $vue = this;
            $global.$emit('loadAulaHorarioComponent', aula);
            this.$refs.modalAulaHorario.open();
        },
        saveAula() {
            $global.$emit('saveAula');
        },
        closeAula() {
            $global.$emit('closeAula');
        },
        afterSaveAula(response, $vue) {
            $vue.$refs.modalAula.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadGpoSeccionFlash();
            } else {
                notify(response.message, "error");
            }
        },
        asyncModuloOera(nombre) {
            this.isLoading = true;
            let $vue = this;
            $.ajax({
                url: APP.url("comun/buscar/allDistritos"),
                data: {
                    nombre: nombre,
                    tipo: $vue.tabAulas['oera'].id
                },
                dataType: 'json',
                type: 'post',
            }).then(response => {
                this.ubigeos = response.data
                this.isLoading = false
            })
        },
        showModalRestriccion(seccion) {
            let $vue = this;
            $global.$emit('loadRestriccionComponent', seccion.id);
            $vue.restriccionModal.showaccept = true;
//            if (seccion.matriculados > 0) {
//                $vue.restriccionModal.showaccept = false;
//            } else {
//                $vue.restriccionModal.showaccept = true;
//            }
            this.$refs.modalRestriccion.open();
        },
        saveRestriccion() {
            $global.$emit('saveRestriccion');
        },
        showModalTipoRepitencia(seccion) {
            let $vue = this;
            this.$refs.repitenciaComp.loadComponent(seccion.id);
            $vue.tipoRepitenciaModal.showaccept = true;
//            if (seccion.matriculados > 0) {
//                $vue.tipoRepitenciaModal.showaccept = false;
//            } else {
//                $vue.tipoRepitenciaModal.showaccept = true;
//            }
            this.$refs.modalTipoRepitencia.open();
        },
        saveTipoRepRestriccion() {
            $global.$emit('saveTipoRepRestriccion');
        },
        cambiarFechaIniPeriodo(docSeccion) {
            let $vue = this;

            let docSeccionSend = {};
            docSeccionSend.id = docSeccion.id;
            docSeccionSend.fechaInicio = docSeccion.fechaInicio;
            MODAL.showWait("Espere un momento por favor");

            $.ajax({
                url: APP.url('academico/gposeccion/updateFechaInicio'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify(docSeccionSend),
                success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $vue.loadGpoSeccionFlash();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                        $vue.loadGpoSeccionFlash();
                        MODAL.hideWait();
                    }
                },
                error: function (response) {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        cambiarFechaFinPeriodo(docSeccion) {

            let $vue = this;
            let docSeccionSend = {};
            docSeccionSend.id = docSeccion.id;
            docSeccionSend.fechaFin = docSeccion.fechaFin;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url('academico/gposeccion/updateFechaFin'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data:
                        JSON.stringify(docSeccionSend)
                ,
                success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $vue.loadGpoSeccionFlash();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                        $vue.loadGpoSeccionFlash();
                        MODAL.hideWait();
                    }
                },
                error: function (response) {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        directEditAula(seccion, event) {
            let $vue = this;
            let target = $(event.target);
            target.parsley().destroy();
            target.parsley();
            if (target.parsley().validate() !== true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarAulaDirect'),
                data: {
                    seccion: seccion.id,
                    aula: target.val()
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();
                    } else {
                        target.parsley().addError('forcederror', {message: response.message, updateClass: true});
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        directEditGrupoHor(seccion, event) {
            let $vue = this;
            let target = $(event.target);
            target.parsley().destroy();
            target.parsley();
            if (target.parsley().validate() !== true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarGrupoHorDirect'),
                data: {
                    seccion: seccion.id,
                    grupoHor: target.val()
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadGpoSeccionFlash();
                    } else {
                        target.parsley().addError('forcederror', {message: response.message, updateClass: true});
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        upper(e) {
            e.target.value = e.target.value.toUpperCase()
        },
        openClonar() {
            this.clonacionModal.title = `Clonar grupo`;
            this.$refs.modalClonacion.open();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        clonar() {
            AXIOS.post(`${APP.url('academico/gposeccion')}/${this.grupoSeccion.id}/clonar/${this.vecesClon}`)
                    .then(response => {
                        if (response.data.success) {
                            let ids = response.data.data;
                            window.location.href = APP.url("academico/gposeccion/" + this.grupoSeccion.id + "/editar") + this.getOrigenURL() + `&ids=${Base64.encode(ids)}`;
                        }
                    })
        },
        solicitarIncremento() {

            let $vue = this;

            var tipo = $vue.seccionSeleccionada.tipoSeccion;
            if (tipo == 'TCUR') {
                return;
            }

            $vue.ampliacionVacante = {
                id: null,
                motivo: '',
                colaborador: {id: null},
                oficina: {id: null},
                seccion: $vue.seccionSeleccionada,
                vacantesInicio: $vue.seccionSeleccionada.vacantes,
                incremento: 0,
                vacantesFin: 0
            }

            $vue.$refs.modalSolicitarIncremento.open();
            var el = $vue.$refs.modalSolicitarIncremento.$el;
            $(el).find('[name="incremento"]').numeric();

        },
        solicitarIncrementoAceptar() {

            let $vue = this;

            if ($('#formAmpliarVacante').parsley().validate() !== true) {
                return;
            }

            if ($vue.ampliacionVacante.vacantesFin < $vue.seccionSeleccionada.matriculados) {
                swal({text: 'Ya existen alumnos matriculados', icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                return;
            }

            if ($vue.seccionSeleccionada.aula.capacidadAula > 0) {
                if ($vue.ampliacionVacante.total > $vue.seccionSeleccionada.aula.capacidadAula) {
                    swal({text: 'Ha sobrepasado la capacidad del aula', icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    return;
                }
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/saveampliacionvacante'),
                data: $('#formAmpliarVacante').serialize(),
                success: function (response) {
                    if (response.success) {

                        $vue.allSolicitarIncremento();
                        $vue.$refs.modalSolicitarIncremento.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        allSolicitarIncremento() {

            let $vue = this;

            if ($vue.seccionSeleccionada == null) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/allampliacionvacante'),
                data: {id: $vue.seccionSeleccionada.id},
                success: function (response) {
                    if (response.success) {
                        $vue.ampliaciones = response.data;
                        $vue.seccionSeleccionada.ampliacionesVacantes = $vue.ampliaciones;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        modificarSolicitud(ampliacion) {

            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/updateampliacionvacante'),
                data: {id: ampliacion.id},
                success: function (response) {
                    if (response.success) {

                        $vue.ampliacionVacante = response.data;
                        $vue.$refs.modalSolicitarIncremento.open();
                        var el = $vue.$refs.modalSolicitarIncremento.$el;
                        $(el).find('[name="incremento"]').numeric();

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        eliminarSolicitud(ampliacion) {

            let $vue = this;

            swal({
                title: "Anular solitud de ampliación",
                text: "¿Desea anular la solicitud de ampliación de vacante?",
                icon: "warning",
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Si, anular", closeModal: false}
                }
            }).then((value) => {

                if (value != true) {
                    return;
                }

                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/gposeccion/deleteampliacionvacante'),
                    async: false,
                    data: {id: ampliacion.id},
                    success: function (response) {
                        if (response.success) {
                            $vue.allSolicitarIncremento();
                            swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });

            }).catch(err => {
                swal(MESSAGES.errorComunicacion, "error");
            });
        },
        aceptarSolicitud: function (ampliacion) {

            let $vue = this;
            $vue.ampliacionVacante = Object.assign({}, ampliacion);
            $vue.$refs.aceptarSolicitudIncremento.open();

        },
        rechazarSolicitud: function (ampliacion) {

            let $vue = this;
            $vue.ampliacionVacante = Object.assign({}, ampliacion);
            $vue.$refs.rechazarSolicitudIncremento.open();

        },
        aceptarSolicitarIncrementoSave() {

            let $vue = this;

            if ($('#formAmpliarVacante').parsley().validate() !== true) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aceptarampliacionvacante'),
                data: $('#aceptarFormSolicitarIncremento').serialize(),
                success: function (response) {
                    if (response.success) {
                        $vue.allSolicitarIncremento();
                        $vue.loadGpoSeccionFlash();
                        $vue.$refs.aceptarSolicitudIncremento.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        rechazarSolicitarIncrementoSave() {

            let $vue = this;

            if ($('#formRechazaAmpliacion').parsley().validate() !== true) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/rechazarampliacionvacante'),
                data: $('#formRechazaAmpliacion').serialize(),
                success: function (response) {
                    if (response.success) {

                        $vue.allSolicitarIncremento();
                        $vue.$refs.rechazarSolicitudIncremento.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        trasladarAlumnos() {

            let $vue = this;

            if ($vue.fusion.seccionDestino.id == null) {
                swal({text: "Seleccione una sección", icon: "error", button: false, timer: 1000});
                return;
            }

            if ($vue.fusion.alumnosid.length < 1) {
                swal({text: "Seleccione algun alumno", icon: "error", button: false, timer: 1000});
                return;
            }

            swal({
                title: "Trasladar Alumnos",
                text: "¿Desea trasladar a los alumnos seleccionados?",
                icon: "warning",
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {

                if (value != true) {
                    return;
                }

                $vue.fusion.seccionOrigen.id = $vue.seccionSeleccionada.id;

                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/gposeccion/trasladar'),
                    contentType: "application/json",
                    data: JSON.stringify($vue.fusion),
                    success: function (response) {
                        if (response.success) {
                            $vue.loadGpoSeccionFlash();
                            notify(response.message, "info");
                            swal({text: response.message, icon: "success", button: false, timer: 1000});

                        } else {
                            swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });

            }).catch(err => {
                swal(MESSAGES.errorComunicacion, "error");
            });

        },
        allAlumnoBySeccion() {

            let $vue = this;
            if ($vue.seccionSeleccionada.tipoSeccion == 'TCUR') {
                $vue.alumnos = [];
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/allAlumno'),
                data: {id: $vue.seccionSeleccionada.id},
                success: function (response) {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allSeccionDisponible() {
            let $vue = this;
            if ($vue.seccionSeleccionada.tipoSeccion == 'TCUR') {
                $vue.seccionesDisponibles = [];
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/allSeccionDisponible'),
                data: {id: $vue.seccionSeleccionada.id},
                success: function (response) {
                    if (response.success) {
                        $vue.seccionesDisponibles = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verCruceAlumnos(destino) {
            let $vue = this;
            $vue.fusion.todos = false;
            $vue.fusion.revisando = true;
            $vue.fusion.alumnosid = [];

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/allAlumnoCruce'),
                data: {origen: $vue.seccionSeleccionada.id, destino: $vue.fusion.seccionDestino.id},
                success: function (response) {
                    $vue.fusion.revisando = false;
                    if (response.success) {
                        $vue.alumnos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $vue.fusion.revisando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        todosAlumnos() {
            let $vue = this;
            if ($vue.fusion.seccionDestino.id == null) {
                $vue.fusion.todos = false;
                notify("Primero debe seleccionar una sección destino", "error");
                return;
            }
            if ($vue.fusion.todos) {
                for (var i = 0; i < $vue.alumnos.length; i++) {
                    $vue.fusion.alumnosid.push($vue.alumnos[i].id);
                }
            } else {
                $vue.fusion.alumnosid = [];
            }
        },
        loadDataFusion() {
            let $vue = this;
            if ($vue.activarFusion) {
                $vue.allAlumnoBySeccion();
                $vue.allSeccionDisponible();
                $vue.fusion = {
                    todos: false,
                    revisando: false,
                    seccionDestino: {id: null, aula: {id: null}, grupoHoras: {id: null}},
                    alumnosid: [],
                    seccionOrigen: {id: null}
                }
            }
        },
        refreshDataFusion() {
            let $vue = this;
            if ($vue.activarFusion) {
                $vue.allAlumnoBySeccion();
                $vue.allSeccionDisponible();
            }
        },
        verGuardarPrecio(seccionSeleccionada) {

            let $vue = this;
            this.guardaPrecio = true;

            let mm = bootbox.confirm({
                message: '¿Está seguro que desea guardar el precio de la sección?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success btn-modal btn-procesar'},
                    cancel: {label: 'No', className: 'btn-link btn-modal'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $vue.guardarPrecio(seccionSeleccionada, mm);
                        return false;
                    } else {
                        $vue.guardaPrecio = false;
                    }
                }
            });
        },
        guardarPrecio(seccionSeleccionada, modalConfirm) {
            this.guardaPrecio = true;
            let $vue = this;
            let precioSeccionSend = {};
            precioSeccionSend.id = seccionSeleccionada.id;
            precioSeccionSend.precio = seccionSeleccionada.precio;
            precioSeccionSend.precioBase = $vue.seccionSeleccionada.minimoAlumnos * $vue.seccionSeleccionada.precio;

            $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
            $(".btn-modal").prop('disabled', true);

            $.ajax({
                url: APP.url("academico/gposeccion/saveprecioseccion"),
                contentType: "application/json",
                dataType: "json",
                type: 'POST',
                async: true,
                data: JSON.stringify(precioSeccionSend),
                success: function (response) {
                    if (response.success) {
                        $vue.editaPrecio = false;
                        $vue.guardaPrecio = false;
                        $vue.loadGpoSeccionFlash(modalConfirm);
                        notify(response.message, "info");
                    } else {
                        $(".btn-modal").prop('disabled', false);
                        $(".btn-procesar").html('Si');
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $vue.editaPrecio = true;
                    $vue.guardaPrecio = false;
                    $(".btn-modal").prop('disabled', false);
                    $(".btn-procesar").html('Si');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        enviarCambioAulaGrupo() {

            let $vue = this;

            $vue.changeAulaGpo = {
                oficina: {},
                grupoHorasFin: {},
                aulaFin: {}
            };

            $vue.seccionModal = $vue.seccionSeleccionada;


            $vue.changeAulaGpo.aulaFin = $vue.seccionSeleccionada.aula;
            $vue.changeAulaGpo.grupoHorasFin = $vue.seccionSeleccionada.grupoHoras;
            $vue.$refs.grupoRegularComponent.seccion = $vue.seccionSeleccionada;
            $vue.$refs.grupoRegularComponent.loadGrupoRegularAulaComponent();
            $vue.$refs.modalEnviarCambioAulaGrupo.open();

        },
        enviarCambioAulaGrupoAceptar() {

            let $vue = this;
            $vue.changeAulaGpo.seccion = {};
            $vue.changeAulaGpo.seccion.id = $vue.seccionSeleccionada.id;

            $global.$emit('preSaveGrupoHorario');

            if ($('#formAulaGrupo').parsley().validate() !== true) {
                return;
            }
            if (!$vue.changeAulaGpo.grupoHorasFin.diaHoraGrupo) {
                notify("Asignar la cantidad de horas requeridas para la sección.", "error");
                return;
            }
            if (!$vue.changeAulaGpo.grupoHorasFin.diaHoraGrupo.length) {
                notify("Asignar la cantidad de horas requeridas para la sección.", "error");
                return;
            }
            if ($vue.changeAulaGpo.grupoHorasFin.diaHoraGrupo.length < 1) {
                notify("Asignar la cantidad de horas requeridas para la sección.", "error");
                return;
            }

            $.ajax({
                url: APP.url('academico/gposeccion/savecambioaulagrupo'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.changeAulaGpo),
                success: function (response) {
                    if (response.success) {
                        $vue.allCambioAulaGrupo();
                        $vue.loadGpoSeccionFlash();
                        $vue.$refs.modalEnviarCambioAulaGrupo.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        asyncFindCambioAulas(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/gposeccion/asyncFindCambioAulas"),
                dataType: 'json',
                type: 'post',
                data: {
                    nombre: nombre
                },
            }).then(response => {
                $vue.aulas = response.data;
                if ($vue.aulas == null) {
                    $vue.aulas = [];
                }
            })
        },
        asyncFindCambioGrupos(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/gposeccion/asyncFindCambioGrupos"),
                dataType: 'json',
                type: 'post',
                data: {
                    nombre: nombre
                },
            }).then(response => {
                $vue.grupos = response.data;
                if ($vue.grupos == null) {
                    $vue.grupos = [];
                }
            })
        },
        aceptarCambioAulaGrupo() {

            let $vue = this;

            if ($('#formAulaGrupo').parsley().validate() !== true) {
                return;
            }

            $.ajax({
                url: APP.url('academico/gposeccion/aceptarcambioaulagrupo'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.changeAulaGpo),
                success: function (response) {
                    if (response.success) {

                        $vue.loadGpoSeccionFlash();
                        $vue.allCambioAulaGrupo();
                        $vue.$refs.aceptarCambioAulaGrupo.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        rechazarCambioAulaGrupo() {

            let $vue = this;

            if ($('#formAulaGrupo').parsley().validate() !== true) {
                return;
            }

            $.ajax({
                url: APP.url('academico/gposeccion/rechazarcambioaulagrupo'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.changeAulaGpo),
                success: function (response) {
                    if (response.success) {
                        $vue.loadGpoSeccionFlash();
                        $vue.allCambioAulaGrupo();
                        $vue.$refs.rechazarCambioAulaGrupo.close();
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        getEstadoAulaGrupoClass: function (estadoCode) {
            return "label-" + this.colorEstadoAulaGrupo[estadoCode];
        },
        eliminarSolicitudCambioAulaGrupo(cambioaulagrupo) {

            let $vue = this;

            let mm = bootbox.confirm({
                message: "¿Está seguro que desea eliminar el cambio aula/grupo?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deletecambioaulagrupo'),
                            data: {id: cambioaulagrupo.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadGpoSeccionFlash(mm);
                                    $vue.allCambioAulaGrupo();

                                } else {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si');
                                    notify(response.message, "error");
                                }
                            }, error: function () {
                                $(".btn-modal").prop('disabled', false);
                                $(".btn-procesar").html('Si');
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                        return false;
                    }
                }
            });
        },
        aceptarSolicitudCambioAulaGrupo: function (cambioaulagrupo) {

            let $vue = this;
            $vue.changeAulaGpo = Object.assign({}, cambioaulagrupo);
            $vue.$refs.aceptarCambioAulaGrupo.open();

        },
        rechazarSolicitudCambioAulaGrupo: function (cambioaulagrupo) {

            let $vue = this;
            $vue.changeAulaGpo = Object.assign({}, cambioaulagrupo);
            $vue.$refs.rechazarCambioAulaGrupo.open();

        },
        allCambioAulaGrupo() {

            let $vue = this;

            if ($vue.seccionSeleccionada == null) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/allcambioaulagrupo'),
                data: {id: $vue.seccionSeleccionada.id},
                success: function (response) {
                    if (response.success) {
                        $vue.cambioaulagrupos = response.data;
                        $vue.seccionSeleccionada.cambioAulaGrupos = $vue.cambioaulagrupos;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        selectGrupoHorarioChange(grupoHorario, $vue) {
            $vue.changeAulaGpo.grupoHorasFin = grupoHorario;
        },
        cambioAula() {

            let $vue = this;

            if ($vue.changeAulaGpo.aulaFin.id == $vue.seccionSeleccionada.aula.id) {

                $vue.changeAulaGpo.grupoHorasFin = $vue.seccionSeleccionada.grupoHoras;

            } else {

                $vue.changeAulaGpo.grupoHorasFin = {};

            }

            $vue.$refs.grupoRegularComponent.loadGrupoRegularAulaComponent();
        },
        generarPagoDocente(docSeccion) {

            let $vue = this;

            let mm = bootbox.confirm({
                message: "¿Está seguro que desea generar el pago docente ?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/generarpagodocente'),
                            data: {id: docSeccion.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadGpoSeccionFlash(mm);
                                    $vue.allCambioAulaGrupo();

                                } else {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si');
                                    notify(response.message, "error");
                                }
                            }, error: function () {
                                $(".btn-modal").prop('disabled', false);
                                $(".btn-procesar").html('Si');
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                        return false;
                    }
                }
            });

        },
        asignarHorasAdicionales(seccion) {
            let $vue = this;
            $vue.$refs.modalAgregarHorasAdicionales.open();
            $vue.$refs.modalHorasAdicionalesComponent.seccion = seccion;
            $vue.$refs.modalAgregarHorasAdicionales.showaccept = true;
//            if (seccion.grupoHoras.id) {
//                $vue.$refs.modalAgregarHorasAdicionales.showaccept = false;
//            } else {
//                $vue.$refs.modalAgregarHorasAdicionales.showaccept = true;
//            }
        },
        saveModalAgregarHorasAdicionales() {
            let $vue = this;
            let horasAsignadas = $vue.$refs.modalHorasAdicionalesComponent.seccion.horasAdicionales;
            if (horasAsignadas == '') {
                notify("Tiene que asignar un valor", "error");
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/asignarHorasAdicionales'),
                data: {id: $vue.$refs.modalHorasAdicionalesComponent.seccion.id,
                    horasAdicionales: $vue.$refs.modalHorasAdicionalesComponent.seccion.horasAdicionales
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.modalAgregarHorasAdicionales.close();
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        showHorasSemanales(seccion) {
            return seccion.horasAdicionales > 0;
        },
        cambiarFechaModular() {

            let $vue = this;

            if ($vue.grupoSeccion.fechaInicioModular == undefined || $vue.grupoSeccion.fechaFinModular == undefined) {
                return;
            }
            if ($vue.grupoSeccion.fechaInicioModular == '' || $vue.grupoSeccion.fechaFinModular == '') {
                return;
            }

            var message = ''
            if ($vue.grupoSeccion.tipoDictadoCheck) {
                message = "¿Está seguro que desea asignar el curso como modular?";
            } else {
                message = "¿Está seguro que desea desasignar el curso modular?";
            }

            let mm = bootbox.confirm({
                message: message,
                buttons: {
                    confirm: {label: 'Si, Aceptar', className: "btn-danger btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/asignarGrupoSeccionModular'),
                            data: {id: $vue.grupoSeccion.id,
                                fechaFinModular: $vue.grupoSeccion.fechaFinModular,
                                fechaInicioModular: $vue.grupoSeccion.fechaInicioModular,
                                tipoDictado: $vue.grupoSeccion.tipoDictado,
                                tipoDictadoCheck: $vue.grupoSeccion.tipoDictadoCheck
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                } else {
                                    mm.modal("hide");
                                    notify(response.message, "error");
                                }
                                $vue.loadGpoSeccionFlash(mm);
                            }, error: function () {
                                $vue.loadGpoSeccionFlash(mm);
                                mm.modal("hide");
                                notify(MESSAGES.errorComunicacion, "error");

                            }
                        });

                        return false;
                    } else {
                        $vue.loadGpoSeccionFlash();
                    }
                }
            });

        },
        getStyleDivGpoHoras(sec) {
            if (this.mostrarCuotasExedidas(sec)) {
                if (sec.estado == 'ACT' && sec.grupoHoras.tipoSeccion == 'TEO') {
                    if (sec.grupoHoras.cuotasGrupoHoras.utilizadasTeoria > sec.grupoHoras.cuotasGrupoHoras.cuotasTeoria) {
                        return 'border:1px solid; border-color: red;padding: 3px;border-radius: 5px;';
                    } else if (sec.grupoHoras.cuotasGrupoHoras.utilizadasTeoria == sec.grupoHoras.cuotasGrupoHoras.cuotasTeoria) {
                        return 'border:1px solid; border-color: red;padding: 3px;border-radius: 5px;';
                    } else {
                        return 'border:1px solid; border-color: orange;padding: 3px;border-radius: 5px;';
                    }
                } else if (sec.estado == 'ACT' && sec.grupoHoras.tipoSeccion == 'PRA') {
                    if (sec.grupoHoras.cuotasGrupoHoras.utilizadasPractica > sec.grupoHoras.cuotasGrupoHoras.cuotasPractica) {
                        return 'border:1px solid; border-color: red;padding: 3px;border-radius: 5px;';
                    } else if (sec.grupoHoras.cuotasGrupoHoras.utilizadasPractica == sec.grupoHoras.cuotasGrupoHoras.cuotasPractica) {
                        return 'border:1px solid; border-color: red;padding: 3px;border-radius: 5px;';
                    } else {
                        return 'border:1px solid; border-color: orange;padding: 3px;border-radius: 5px;';
                    }
                } else {
                    return 'border:1px solid; border-color: black;padding: 3px;border-radius: 5px;';
                }
            }
            return "";
        },
        mostrarCuotasExedidas(sec) {
            if (sec.grupoHoras.tipoSeccion == 'TEO' && sec.grupoHoras.cuotasGrupoHoras.utilizadasTeoria != null && sec.grupoHoras.cuotasGrupoHoras.utilizadasTeoria != '') {
                return true;
            }
            if (sec.grupoHoras.tipoSeccion == 'PRA' && sec.grupoHoras.cuotasGrupoHoras.utilizadasPractica != null && sec.grupoHoras.cuotasGrupoHoras.utilizadasPractica != '') {
                return true;
            }
            return false;
        },
        rowSeccionStyles(index, seccion) {
            let rows = $('#tblSecciones').find('> tbody > tr');
            let row = rows[index];
            if (row != null) {
                if (seccion.isEstadoActivo) {
                    $(row).find('a[class!="lnk-seccion"]').css('pointer-events', 'auto');
                } else {
                    $(row).find('a[class!="lnk-seccion"]').css('pointer-events', 'none');
                }
            }
            return "";
        }
    }
});


