Vue.component("multiselect", window.VueMultiselect.default);

$('#dynaTable').dynatable({});
$('#dynaTableEspecial').dynatable({});

Vue.component("autocomplete-doc", {
    template: `<input class="input-s-full" v-bind:rel="rel" type="hidden"  />`,
    props: {
        rel: {
            required: false
        },
        docseccion: {
            required: true
        }
    },
    mounted: function () {
        var vm = this

        $(this.$el).select2({
            containerCss: "width:400px !important;",
            containerCssClass: "buscarDocClass",
            minimumInputLength: 1,
            ajax: {
                url: APP.url("academico/gposeccion/buscarDocentes"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        nombre: term,
                        page: page
                    };
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                var conte = '<span class="bold block">' + info.apellidosNombres + '</span>';
                conte += '<small class="block">Dpto.Acad.: ' + info.departamento + '</small>';
                conte += '<span class="bold block">' + info.codigo + '</span>';

                return conte;
            },
            formatSelection: function (info) {
                return info.personaNombre + " " + info.personaPaterno + " " + info.personaMaterno;
            },
            initSelection: function (element, callback) {
                if (element.val() != "") {
                    callback({id: element.val(), apellidosNombres: element.attr("rel")});
                }
            },
            escapeMarkup: function (m) {
                return m;
            }

        }).on('select2-selecting', function (e) {
            vm.$emit('input', e.object.id);

            let docSeccion = vm.$options.propsData.docseccion;
            let docente = e.object.id;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarDocenteSeccion'),
                data: {
                    docSeccion: docSeccion,
                    docente: docente
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        vm.$emit('oncomplete');

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        });
    },
    destroyed: function () {
        $(this.$el).off().select2('destroy')
    },
    watch: function () {
    }
});

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

var app = new Vue({
    el: '#pageGpoSeccion',
    data: {
        SIN_RESTRICCION_TEXT: "Todos",
        ciclo: {},
        grupoSeccion: {},
        navega: {},
        btnNavega: {left: true, right: true},
        idxSeccion: 0,
        secciones: null,
        directEditSecciones: false,
        docentesSeccion: [],
        seccionSeleccionada: null,
        verDocentes: false,
        seccionModal: null,
        tabVisible: "DOCENTES",
        colorEstado: {CRE: "default", ACT: "success", ANU: "danger", INA: "danger", BLO: "warning", FUS: "warning"},
        colorEstadoAmpliacion: {PENDIENTE: "default", ACEPTADO: "success", RECHAZADO: "danger", ANULADA: "warning"},
        grupoModal: {
            id: 'modalGrupo',
            header: true,
            title: 'Buscar Grupo Disponible',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        aulaModal: {
            id: 'modalAula',
            header: true,
            title: 'Buscar Aula/Ambiente Disponible',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        aulaHorarioModal: {
            id: 'modalAulaHorario',
            header: true,
            title: 'Horario Aula',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones Modalidad / Facultad / Especialidad',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        tipoRepitenciaModal: {
            id: 'modalTipoRepitencia',
            header: true,
            title: 'Aplicar restricción repitencia / retirados / ingresantes',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
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
            modalsize: 'modal-md'
        },
        rechazarSolicitudIncrementoModal: {
            id: 'rechazarSolicitudIncrementoModalId',
            header: true,
            title: 'Rechazar ampliación de vacantes',
            okbtn: 'Rechazar',
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
        //isShowTabFusion: false,
        //cantidadTrasladados: 0,
        editaPrecio: false,
        guardaPrecio: false
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
        this.loadDataPantalla();

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
    },
    methods: {
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
            console.log("loadGpoSeccion-loadGpoSeccion-loadGpoSeccion")
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + idGpoSecc + '/get'),
                success(response) {
                    $vue.liberarBtn(dir);
                    if (response.success) {
                        $vue.grupoSeccion = response.data.grupoSeccion;
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
        loadGpoSeccionFlash() {
            console.log("loadGpoSeccionActual/loadGpoSeccionActual/loadGpoSeccionActual")
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
                        $vue.refreshDataFusion();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
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
                        //$vue.loadSecciones();
//                        $vue.loadGpoSeccion($vue.grupoSeccion.id, "");
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
                        //$vue.loadSecciones();
//                        $vue.loadGpoSeccion($vue.grupoSeccion.id, "");
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
//            $vue.verDocentes = false;
            $vue.idxSeccion = index;
            $vue.loadDataPantalla();
            //this.seccionSeleccionada = seccion;
        },
        cambiarDocPrincipal: function (docSeccion) {
            let $vue = this;
            //$vue.verDocentes = false;
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
                    //$vue.verDocentes = true;
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    //$vue.verDocentes = true;
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
                        porcentajeAvance: parseFloat(docSeccion.porcentajeCarga)
                    },
                    success: function (response) {
                        $vue.loadGpoSeccionEfecto($vue.grupoSeccion.id, "");
                        if (response.success) {
                            notify(response.message, "info");

                            //$vue.loadSecciones();
                            // $vue.docentesSeccion = [];
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
            bootbox.confirm({
                message: "¿Está seguro que desea elimar la seccón?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deleteSeccion'),
                            data: {
                                seccion: seccion.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                    $vue.docentesSeccion = [];
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
                }
            });
        },
        bloquearSeccion: function (seccion) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea bloquear la seccón?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/bloquearSeccion'),
                            data: {
                                seccion: seccion.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                    $vue.docentesSeccion = [];
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
                }
            });
        },
        activarSeccion: function (seccion) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea activar la seccón?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/activarSeccion'),
                            data: {
                                seccion: seccion.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                    $vue.docentesSeccion = [];
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
                }
            });
        },
        anularSeccion: function (seccion) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea anular la seccón?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/anularSeccion'),
                            data: {
                                seccion: seccion.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                    $vue.docentesSeccion = [];
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
                }
            });
        },
        deleteDocSeccion: function (docSeccion) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea elimar el docente?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deleteDocSeccion'),
                            data: {
                                docSeccion: docSeccion.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadGpoSeccionEfecto($vue.grupoSeccion.id, "");
                                    //$vue.loadSecciones();
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
//            let $vue = this;
//            $vue.secciones = $vue.grupoSeccion.secciones;
        },
        showModalGrupos(seccion) {
            var tabs = $("#tab-grupos");
            tabs.find("li").removeClass("active");
            tabs.find(".tab-pane").removeClass("active");

            let $vue = this;
            $global.$emit('loadGrupoComponent', seccion.id);
            this.$refs.modalGrupo.open();
        },
        saveGrupo() {
            $global.$emit('saveGrupoHorario');
        },
        afterSaveGrupo(response, $vue) {
            $vue.$refs.modalGrupo.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        },
        afterSaveRestriccion(response, $vue) {
            $vue.$refs.modalRestriccion.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        },
        afterSaveTipoRepRestriccion(response, $vue) {
            $vue.$refs.modalTipoRepitencia.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
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

                console.log("seccion.id:: " + seccion.id);
                console.log("seccion.vacantes:: " + seccion.vacantes);
                console.dir(seccion)

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
                        if (response.success) {
                            notify(response.message, "info");
                            $vue.loadSecciones();
                        } else {
                            $vue.loadSecciones();
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
            var tabs = $("#tab-aula");
            tabs.find("li").removeClass("active");
            tabs.find(".tab-pane").removeClass("active");

            this.$refs.aulaComponent.loadAula(seccion);
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
                $vue.loadSecciones();
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
            this.$refs.modalRestriccion.open();
        },
        saveRestriccion() {
            $global.$emit('saveRestriccion');
        },
        showModalTipoRepitencia(seccion) {
            let $vue = this;
            $global.$emit('loadRepitenciaComponent', seccion.id);
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
                        $vue.loadSecciones();
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
            console.dir(docSeccion);
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
                        $vue.loadSecciones();
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
                        $vue.loadSecciones();
                    } else {
                        target.parsley().addError('forcederror', {message: response.message, updateClass: true});
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
                        $vue.loadSecciones();
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

            console.log($vue.ampliacionVacante.vacantesFin);
            console.log($vue.seccionSeleccionada.matriculados);

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
            console.log('aceptarSolicitud');
            $vue.ampliacionVacante = Object.assign({}, ampliacion);
            $vue.$refs.aceptarSolicitudIncremento.open();

        },
        rechazarSolicitud: function (ampliacion) {

            let $vue = this;
            console.log('rechazarSolicitud');
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
                        console.log("aumento")
                        $vue.allSolicitarIncremento();
                        $vue.loadGpoSeccionFlash();
                        $vue.$refs.aceptarSolicitudIncremento.close();
                        notify(response.message, 'info');

                    } else {
                        console.log(" no aumento")
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

//                            $vue.allAlumnoBySeccion();
                            $vue.loadGpoSeccionFlash();
                            notify(response.message, "info");

                            //swal({text: response.message, icon: "success", button: false, timer: 1000});
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

            bootbox.confirm({
                message: '¿Está seguro que desea guarda el precio sección?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $vue.guardarPrecio(seccionSeleccionada);
                    } else {
                        $vue.guardaPrecio = false;
                    }
                }
            });
        },
        guardarPrecio(seccionSeleccionada) {
            this.guardaPrecio = true;
            let $vue = this;
            let precioSeccionSend = {};
            precioSeccionSend.id = seccionSeleccionada.id;
            precioSeccionSend.precio = seccionSeleccionada.precio;

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
                        $vue.loadGpoSeccionFlash();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $vue.editaPrecio = true;
                    $vue.guardaPrecio = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});

    
