Vue.component("multiselect", window.VueMultiselect.default)

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
            }, /*
             watch: {
             value: function (value) {
             // update value
             $(this.$el).select2('val', value)
             },
             options: function (options) {
             // update options
             $(this.$el).select2({data: options})
             }
             },*/
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
    }
});


Vue.component('select2', {
    props: {
        options: {required: false},
        value: {required: false},
        onchange: {type: Function, default: () => {
            }}
    },
    template: '#select2-template',
    mounted: function () {

        var vm = this;
        $(this.$el).select2({
            data: this.options
        }).on('change', function () {
            vm.$emit('input', this.value);
            vm.onchange();

        })
    }, watch: {
        value: function (value) {
            // update value
            $(this.$el).val(value)
        },
        options: function (options) {
            $(this.$el).empty().select2({data: options})
        }
    }, destroyed: function () {
        $(this.$el).off().select2('destroy')
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
            okbtn: 'Aceptar'
        },
        aulaModal: {
            id: 'modalAula',
            header: true,
            title: 'Buscar Aula/Ambiente Disponible',
            okbtn: 'Aceptar'
        },
        aulOeraSel: null,
        tblAulas: null,
        modulosCombo: {},
        tabAulas: {
            oera: {
                id: 50,
                nombre: "oera",
                moduloSel: null,
                aulaSel: null,
                modulosCombo: [],
                tblAulas: null
            }
        },
        tabGrupos: {
            regulares: {
                tipoGrupoHorasSeleccionado: null,
                tblHorarioRegular: null,
                grupoHorarioRegSel: null,
                tipoGrupoHorasOpts: null
            }
        }
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
            this.grupoSeccion = JSON.parse(gpoSeccionJson);
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + this.grupoSeccion.id + '/findSecciones'),
                success: function (response) {
                    if (response.success) {
                        $vue.secciones = response.data;
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
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/findDocentesSecciones'),
                data: {
                    seccion: $vue.seccionSeleccionada.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.docentesSeccion = response.data;
                    }
                }
            });
        }, showModalGrupos(seccion) {
            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalGrupo'),
                data: {
                    seccion: seccion.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.seccionModal = response.data.seccion;

                        $vue.tabGrupos['regulares'].grupoHorarioRegSel = response.data.grupoHorarioSel;
                        $vue.tabGrupos['regulares'].tipoGrupoHorasOpts = response.data.tiposGruposHorasOpt;

                        if ($vue.tabGrupos['regulares'].grupoHorarioRegSel != "") {
                            if ($vue.tabGrupos['regulares'].grupoHorarioRegSel.esTipoGrupoRegular) {
                                $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado = response.data.tipoGrupoHorasSeleccionado;
                                console.dir($vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado);
                                $vue.cambiarCboTipoGrupoHorReg();
                            }
                        }
                    }
                }
            });
            /*
             $.ajax({
             method: 'POST',
             url: APP.url('academico/gposeccion/findTiposGruposHoras'),
             data: {
             },
             success: function (response) {
             if (response.success) {
             
             //  $vue.tipoGrupoHorasOpts = response.data;
             let tiposGruposHoras = [];
             response.data.forEach(function (element) {
             var opt = {id: element.tipoGrupoHoraId, text: element.tipoGrupoHoraCodigo + " - " + element.tipoGrupoHoraDescripcion};
             tiposGruposHoras.push(opt);
             });
             $vue.tabGrupos['regulares'].tipoGrupoHorasOpts = tiposGruposHoras;
             }
             }
             });
             */
            this.$refs.modalGrupo.open();

            $("#cboTipoGrupoHorasReg").select2({
                width: '100%'
            }).val(this.value).trigger('change').on('change', function () {
                $vue.$emit('input', this.value)
            });

        }, saveGrupo() {
            if (this.tabGrupos['regulares'].grupoHorarioRegSel == null) {
                alert("Seleccione un grupo horario");
            }
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveSeccionGrupo'),
                            type: 'POST',
                            async: true,
                            data: {
                                seccion: $vue.seccionModal.seccionId,
                                grupoHorario: $vue.tabGrupos['regulares'].grupoHorarioRegSel.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    $vue.$refs.modalGrupo.close();
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                } else {
                                    MODAL.hideWait();
                                    $vue.$refs.modalGrupo.close();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        }, saveAula() {

            if (this.tabAulas['oera'].aulaSel == null) {
                alert("Seleccione un aula");
            }

            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveAula'),
                            type: 'POST',
                            async: true,
                            data: {
                                seccion: $vue.seccionModal.seccionId,
                                aula: $vue.tabAulas['oera'].aulaSel.id
                            },
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    $vue.$refs.modalAula.close();
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                } else {
                                    MODAL.hideWait();
                                    $vue.$refs.modalAula.close();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        }, selectGrupoHoraReg(grupoHora) {

            this.tabGrupos['regulares'].grupoHorarioRegSel = grupoHora;
            var seleccionado = !this.tabGrupos['regulares'].grupoHorarioRegSel.seleccionado;

            if (!seleccionado) {
                this.tabGrupos['regulares'].grupoHorarioRegSel = null;
            }
            for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                if (this.tabGrupos['regulares'].grupoHorarioRegSel != null &&
                        this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].id == this.tabGrupos['regulares'].grupoHorarioRegSel.id) {
                    this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = seleccionado;
                } else {
                    this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                }
            }
            // this.tblHorarioRegular = this.tblHorarioRegular;
        }, selectAula(aula) {

            this.tabAulas['oera'].aulaSel = aula;
            let seleccionado = !aula.seleccionado;
            if (!seleccionado) {
                this.tabAulas['oera'].aulaSel = null;
            }
            for (let key in this.tabAulas['oera'].tblAulas) {
                this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                if (this.tabAulas['oera'].tblAulas[key].id == aula.id) {
                    this.tabAulas['oera'].tblAulas[key].seleccionado = seleccionado;
                }
            }

        }, cambiarCboTipoGrupoHorReg() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horario'),
                type: 'POST',
                async: false,
                data: {
                    tipoGrupoHorasId: $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado.id,
                    seccionId: $vue.seccionModal.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabGrupos['regulares'].tblHorarioRegular = response.data;
                        console.dir($vue.tabGrupos['regulares'].tblHorarioRegular);
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos['regulares'].tblHorarioRegular = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });

        }, getClassGpoHorario(gpoHorario) {
            if (gpoHorario.seleccionado) {
                return "btn-primary";
            }
            if (this.tabGrupos['regulares'].grupoHorarioRegSel != null && this.tabGrupos['regulares'].grupoHorarioRegSel != "") {
                if (gpoHorario.id == this.tabGrupos['regulares'].grupoHorarioRegSel.id) {
                    return "btn-primary";
                }
            }
            return "btn-default";
        }, getClassAula(aula) {
            if (aula.seleccionado || parseInt(aula.id) == parseInt(this.tabAulas['oera'].aulaSel.id)) {
                return "btn-primary";
            }
            return "btn-default";
        }, showModalAula(seccion) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalAula'),
                data: {
                    seccion: seccion.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.seccionModal = response.data.seccion;
                        $vue.tabAulas['oera'].modulosCombo = response.data.modulosOera;
                        $vue.modulosCombo = response.data.modulosOera;

                        if (response.data.modulosOeraSel != null) {
                            $vue.tabAulas['oera'].moduloSel = response.data.modulosOeraSel;
                            $vue.tabAulas['oera'].aulaSel = response.data.aulaSel;
                            //  console.dir($vue.tabAulas['oera'].aulaSel);
                            $vue.cambiarModulo();
                        }

                        console.dir($vue.modulosCombo);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
            this.$refs.modalAula.open();
        }, cambiarModulo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aulas'),
                data: {
                    seccion: $vue.seccionModal.seccionId,
                    aula: $vue.tabAulas['oera'].moduloSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas['oera'].tblAulas = response.data;
                        console.dir($vue.tabAulas['oera'].tblAulas);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

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
    }, created: function () {
        this.grupoSeccion = JSON.parse(gpoSeccionJson);
        this.loadSecciones();
    }
})