Vue.component("multiselect", window.VueMultiselect.default)
//Vue.component('pagination', Pagination);

$('#dynaTable').dynatable({});
$('#dynaTableEspecial').dynatable({});

Vue.component("autocomplete-doc", {
    template: "#autocomplete-doc",
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
            containerCssClass: "diegoSelect",
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
                //$.templates("#divBuscarCurso").render(info);
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


var app = new Vue({
    el: '#pageGpoSeccion',
    data: {
        grupoSeccion: {},
        secciones: [],
        docentesSeccion: [],
        seccionSeleccionada: null,
        seccionModal: null,
        colorEstado: {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"},
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
        aulOeraSel: null,
        tblAulas: null,
        modulosCombo: {}
    }, created: function () {
        this.grupoSeccion = JSON.parse(gpoSeccionJson);
        this.loadSecciones();
    }, mounted: function () {
        let $vue = this;
        $global.$on("afterSaveAula", function (response) {
            $vue.afterSaveAula(response, $vue);
        });
        $global.$on("afterSaveGrupo", function (response) {
            $vue.afterSaveGrupo(response, $vue);
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
                    seccion: $vue.seccionSeleccionada.seccionId
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
                    docSeccion: docSeccion.docSeccionId
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
        cambiarPorcentajeCarga: function (docSeccion) {
            let $vue = this;
            let form = $("#frmEditGpoSeccion");
            form.parsley().destroy();
            //  form.parsley();
            if (!form.parsley().validate("porcentaje-car")) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/cambiarPorcentajeAvance'),
                data: {
                    docSeccion: docSeccion.docSeccionId,
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
                                seccion: seccion.seccionId
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
                                docSeccion: docSeccion.docSeccionId
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
            //    MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/findDocentesSecciones'),
                data: {
                    seccion: $vue.seccionSeleccionada.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.docentesSeccion = response.data;
                        //   MODAL.hideWait();
                    }
                }
            });
        }, showModalGrupos(seccion) {
            let $vue = this;
            $global.$emit('loadGrupoComponent', seccion.seccionId);
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
        }, changeVacantes(seccion, event) {

            seccion.editVacantes = false;
            let $vue = this;
            if (event != null) {
                let form = $(event.target);

                form.attr("data-parsley-type", "digits");
                if (seccion.aula != "") {
                    form.attr("data-parsley-max", seccion.aula.aforo);
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
                        seccion: seccion.seccionId,
                        vacantes: seccion.vacantes
                    },
                    success: function (response) {
                        console.dir(response);
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
        }, showModalAula(seccion) {
            let $vue = this;
            $global.$emit('loadAulaComponent', seccion.seccionId);
            this.$refs.modalAula.open();
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
        }
    }
})