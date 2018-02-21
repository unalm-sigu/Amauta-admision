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
            minimumInputLength: 3,
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
                return info.apellidosNombres;
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
        }

        ).on('select2-selecting', function (e) {
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
    }, watch: {
        seccion(newValue) {

        }
    }
});


var app = new Vue({
    el: '#pageGpoSeccion',
    data: {
        grupoSeccion: {},
        secciones: null,
        directEditSecciones: false,
        docentesSeccion: [],
        seccionSeleccionada: null,
        seccionModal: null,
        colorEstado: {CRE: "default", ACT: "success", INA: "danger", ANU: "danger", BLO: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"},
        grupoModal: {
            id: 'modalGrupo',
            header: true,
            title: 'Buscar Grupo Disponible',
            okbtn: 'Aceptar',
            modalSize: 'modal-lg'
        },
        aulaModal: {
            id: 'modalAula',
            header: true,
            title: 'Buscar Aula/Ambiente Disponible',
            okbtn: 'Aceptar',
            modalSize: 'modal-lg'
        },
        aulaHorarioModal: {
            id: 'modalAulaHorario',
            header: true,
            title: 'Horario Aula',
            okbtn: 'Aceptar',
            modalSize: 'modal-lg'
        },
        restriccionModal: {
            id: 'modalRestriccion',
            header: true,
            title: 'Restricciones',
            okbtn: 'Aceptar',
            modalSize: 'modal-lg'
        },
        tipoRepitenciaModal: {
            id: 'modalTipoRepitencia',
            header: true,
            title: 'Tipo Repitencia',
            okbtn: 'Aceptar',
            modalSize: 'modal-lg'
        },
        fecha: null,
        minFechaPeriodo: null,
        maxFechaPeriodo: null,
        modulosCombo: {}
    }, created: function () {
        this.grupoSeccion = JSON.parse(gpoSeccionJson);
        this.loadGpoSeccionForm();
        this.loadSecciones();
    }, mounted: function () {
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

    }, methods: {
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
                        $vue.loadSecciones();
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
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
                        $vue.loadDocentesSec();
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        seleccionarSeccion: function (seccion) {
            this.seccionSeleccionada = seccion;
            this.loadDocentesSec();
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
                        $vue.loadDocentesSec();
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
                        porcentajeAvance: parseFloat(docSeccion.porcentajeCarga)
                    },
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, "info");
                            $vue.loadSecciones();
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
                                    $vue.loadDocentesSec();
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
        }, loadGpoSeccionForm: function () {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + this.grupoSeccion.id + '/loadGpoSeccionForm'),
                success: function (response) {
                    if (response.success) {

                        $vue.minFechaPeriodo = response.data.minFechaPeriodo;
                        $vue.maxFechaPeriodo = response.data.maxFechaPeriodo;
                    }
                }
            });
        }, loadSecciones: function () {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + this.grupoSeccion.id + '/findSecciones'),
                success: function (response) {
                    if (response.success) {
                        $vue.secciones = response.data;
                    }
                }
            });
        }, loadDocentesSec: function () {
            let $vue = this;
            $vue.docentesSeccion = null;
            /*
             for (let idx in this.$refs.datePicker) {
             let element = this.$refs.datePicker[idx];
             console.dir(element);
             element.$destroy();
             }
             for (let idx in this.$refs.datePicker) {
             let element = this.$refs.datePicker[idx];
             element.$mount('#elementidhere')
             }*/
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/findDocentesSecciones'),
                data: {
                    seccion: $vue.seccionSeleccionada.id
                },
                success: function (response) {
                    if (response.success) {

                        $vue.docentesSeccion = response.data;
                        MODAL.hideWait();
                    }
                }
            });
        }, showModalGrupos(seccion) {
            let $vue = this;
            $global.$emit('loadGrupoComponent', seccion.id);
            this.$refs.modalGrupo.open();

        }, saveGrupo() {
            $global.$emit('saveGrupoHorario');
        }, afterSaveGrupo(response, $vue) {
            $vue.$refs.modalGrupo.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        }, afterSaveRestriccion(response, $vue) {
            $vue.$refs.modalRestriccion.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        }, afterSaveTipoRepRestriccion(response, $vue) {
            $vue.$refs.modalTipoRepitencia.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        }, changeVacantes(seccion, event) {

            seccion.editVacantes = false;
            let $vue = this;
            if (event != null) {
                let form = $(event.target);

                form.attr("data-parsley-type", "digits");
                if (seccion.aula != null) {
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
        }, directAula() {
            alert("1");
        }
        , showModalAula(seccion) {
            let $vue = this;
            $global.$emit('loadAulaComponent', seccion.id);
            this.$refs.modalAula.open();
        }, showModalAulaHorario(aula) {
            let $vue = this;
            $global.$emit('loadAulaHorarioComponent', aula);
            this.$refs.modalAulaHorario.open();
        }, saveAula() {
            $global.$emit('saveAula');
        }, afterSaveAula(response, $vue) {
            $vue.$refs.modalAula.close();
            if (response.success) {
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                notify(response.message, "error");
            }
        }, asyncModuloOera(nombre) {
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
        }, showModalRestriccion(seccion) {
            let $vue = this;
            $global.$emit('loadRestriccionComponent', seccion.id);
            this.$refs.modalRestriccion.open();
        }, saveRestriccion() {
            $global.$emit('saveRestriccion');
        }, showModalTipoRepitencia(seccion) {
            let $vue = this;
            $global.$emit('loadRepitenciaComponent', seccion.id);
            this.$refs.modalTipoRepitencia.open();
        }, saveTipoRepRestriccion() {
            $global.$emit('saveTipoRepRestriccion');
        }, cambiarFechaIniPeriodo(docSeccion) {
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
                data:
                        JSON.stringify(docSeccionSend)
                ,
                success: function (response) {
                    if (response.success) {
                        MODAL.hideWait();
                        $vue.loadSecciones();
                        //     $vue.loadDocentesSec();
                        notify(response.message, "info");
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function (response) {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, cambiarFechaFinPeriodo(docSeccion) {

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
                        // $vue.loadDocentesSec();
                        notify(response.message, "info");
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function (response) {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, directEditAula(seccion, event) {
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
        }, directEditGrupoHor(seccion, event) {
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
                        alert("error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        }
    }
})