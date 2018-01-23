Vue.component("multiselect", window.VueMultiselect.default)
//Vue.component('pagination', Pagination);



$('#dynaTable').dynatable({});
$('#dynaTableEspecial').dynatable({});

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    // props: ["project", "dynatable"],
    props: {
        project: {required: false},
        dynatable: {required: false},
        onclick: {type: Function, default: () => {
            }}
    },
    mounted: function () {
        let $vue = this;
        $vue.createDynatable();

    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $vue.dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/gposeccion/listGrupoHorariosZetas'),
                    perPageDefault: 6,
                    ajaxData: {tipoGrupoHora: "ZETA"}

                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: 'div'},
                features: {
                    pushState: false,
                    search: false,
                    recordCount: false
                },
                inputs: {
                    processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
                }
            }).data('dynatable');

            $("body").delegate(".cls-grupos-sel", "click", function (e) {
                e.preventDefault();
                $vue.onclick($(this).attr("rel"));
            });



        },
        writter: function (rowIndex, record, columns, cellWriter) {
            var labelColor = {ACT: 'success', INA: 'danger'};
            var labelName = {ACT: 'Activo', INA: 'Inactivo'};
            record.colorEstado = labelColor[record.estado];
            record.nameEstado = labelName[record.estado];
            var html = $.templates("#dynatableRowTemplate").render(record);
            var outerHTML = $(html).prop('outerHTML');

            return outerHTML;
        },
        showModal() {
            // this.$refs.modalTest.open();

        }, clickGrupo() {

        }
    },
    watch: function () {
    }
});


Vue.component("dynatable-especial", {
    template: "#dynatableTemplateEspecial",
    // props: ["project", "dynatable"],
    props: {
        project: {required: false},
        dynatable: {required: false},
        onclick: {type: Function, default: () => {
            }}
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $vue.dynatable = $('#dynaTableEspecial').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/gposeccion/listGrupoHorariosByTipoEspecial'),
                    perPageDefault: 6,
                    ajaxData: {tipoGrupoHora: "ESPECIAL"}
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: 'div'},
                features: {
                    pushState: false,
                    //   search: false,
                    recordCount: false
                },
                inputs: {
                    processingText: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'
                }
            }).data('dynatable');

            $("body").delegate(".cls-grupos-sel-esp", "click", function (e) {
                e.preventDefault();
                $vue.onclick($(this).attr("rel"));
            });



        },
        writter: function (rowIndex, record, columns, cellWriter) {
            var labelColor = {ACT: 'success', INA: 'danger'};
            var labelName = {ACT: 'Activo', INA: 'Inactivo'};
            record.colorEstado = labelColor[record.estado];
            record.nameEstado = labelName[record.estado];
            var html = $.templates("#dynatableRowTemplateEsp").render(record);
            var outerHTML = $(html).prop('outerHTML');

            return outerHTML;
        },
        showModal() {
        }, clickGrupo() {

        }
    },
    created: function () {
        //  let $vue = this;
        // $vue.createDynatable();
    },
    mounted: function () {
        let $vue = this;
        $vue.createDynatable();
        $global.$on("reloadDynaEspecial", function (seccion) {
            //  $vue.dynatable.queries.add("seccion", seccion);
            //    $vue.dynatable.process();
        });

    }
});



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
    },
    watch: function () {

    }
});

/*
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
 */


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
        modulosCombo: {},
        tabAulas: {
            aulaSel: null,
            oera: {
                id: 50,
                nombre: "oera",
                moduloSel: null,
                aulaSel: null,
                modulosCombo: [],
                tblAulas: null
            },
            oficinas: {
                oficinaSel: null,
                aulaSel: null,
                oficinasDisponibles: [],
                tblAulas: null
            },
            especificas: {
                aulasEspecificaSel: null,
                aulasEspecificas: [],
                errores: []
            }
        },
        tabGrupos: {
            grupoHorarioSel: null,
            regulares: {
                tipoGrupoHorasSeleccionado: null,
                tblHorarioRegular: null,
                grupoHorarioRegSel: null,
                tipoGrupoHorasOpts: null
            }, zetas: {
                grupoHorarioSel: null,
                tblHorarios: null
            }, especial: {
                grupoHorarioSel: null,
                tblHorarios: null
            }
        }
    }, methods: {
        mounted: function () {
            let $vue = this;
            /*
             $global.$on("seleccionarGrupoEsp", function (id) {
             $vue.seleccionarGrupoEsp(id);
             });
             $global.$on("seleccionarGrupoZ", function (id) {
             $vue.seleccionarGrupoZ(id);
             });
             */
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
        asyncFindAulas(nombre) {
            //this.isLoading = true
            let $vue = this;
            $.ajax({
                url: APP.url("academico/gposeccion/asyncFindAulas"),
                dataType: 'json',
                type: 'post',
                data: {nombre: nombre},
            }).then(response => {
                // tabAulas especificas aulasEspecificaSel  aulasEspecificas
                $vue.tabAulas["especificas"].aulasEspecificas = response.data;
                //  this.isLoading = false
                if ($vue.tabAulas["especificas"].aulasEspecificas == null) {
                    $vue.tabAulas["especificas"].aulasEspecificas = [];
                }
            })
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

            this.tabGrupos['regulares'].tipoGrupoHorasSeleccionado = null;
            this.tabGrupos['regulares'].tblHorarioRegular = null;
            this.tabGrupos['regulares'].grupoHorarioRegSel = null;
            this.tabGrupos['regulares'].tipoGrupoHorasOpts = null;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalGrupo'),
                data: {
                    seccion: seccion.seccionId
                },
                success: function (response) {
                    if (response.success) {
                        $vue.seccionModal = response.data.seccion;
                        console.log($vue.seccionModal.id);
                        console.dir($global);
                        $global.$emit("reloadDynaEspecial", $vue.seccionModal.id);
                        //  $vue.tabGrupos['regulares'].grupoHorarioRegSel = response.data.grupoHorarioSel;
                        $vue.tabGrupos['regulares'].tipoGrupoHorasOpts = response.data.tiposGruposHorasOpt;

                        if (response.data.grupoHorarioSel != null) {
                            $vue.tabGrupos.grupoHorarioSel = response.data.grupoHorarioSel;
                            if (response.data.grupoHorarioSel.esTipoGrupoRegular) {
                                console.log("esTipoGrupoRegular");
                                $vue.tabGrupos['regulares'].grupoHorarioRegSel = response.data.grupoHorarioSel;
                                $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado = response.data.grupoHorarioSel.tipoGrupoHoras;
                                $vue.cambiarCboTipoGrupoHorReg();
                            } else if (response.data.grupoHorarioSel.esTipoGrupoZeta) {
                                console.log("esTipoGrupoZeta");
                                $vue.tabGrupos['zetas'].grupoHorarioSel = response.data.grupoHorarioSel;
                                $vue.seleccionarGrupoZ($vue.tabGrupos['zetas'].grupoHorarioSel.id);
                            } else if (response.data.grupoHorarioSel.isTipoGrupoEspecial) {
                                console.log("esTipoGrupoEspecial");
                            }
                        } else {
                            $vue.tabGrupos['zetas'].tblHorarios = null;
                        }

                        $vue.$refs.modalGrupo.open();
                    }
                }
            });


            /*
             $("#cboTipoGrupoHorasReg").select2({
             width: '100%'
             }).val(this.value).trigger('change').on('change', function () {
             $vue.$emit('input', this.value)
             });*/

        }, saveGrupo() {
            /*
             let grupoReg = this.tabGrupos['regulares'].grupoHorarioRegSel == null || this.tabGrupos['regulares'].grupoHorarioRegSel == "";
             let grupoZeta = this.tabGrupos['zetas'].grupoHorarioSel == null || this.tabGrupos['zetas'].grupoHorarioSel == "";
             let grupoEspecial = this.tabGrupos['especial'].grupoHorarioSel == null || this.tabGrupos['especial'].grupoHorarioSel == "";
             */

            let mensajeAsignarHoras = "Asignar la cantidad de horas requeridas para la sección.";

            if (this.tabGrupos.grupoHorarioSel == null) {
                alert("Seleccione un grupo horario");
                return;
            }
            console.dir(this.tabGrupos.grupoHorarioSel);
            let diasHorasGrupo = [];
            if (this.tabGrupos.grupoHorarioSel.esTipoGrupoRegular) {
                for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                    let diaHoraGrupoEach = this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key];
                    if (diaHoraGrupoEach.seleccionado) {
                        console.dir(diaHoraGrupoEach);
                        let diaHoraGrupo = diaHoraGrupoEach.id;
                        let grupoHorario = diaHoraGrupoEach.grupoHorario.id;
                        let dia = diaHoraGrupoEach.dia;
                        let hora = diaHoraGrupoEach.hora;
                        let diaHoraGrupoJson = {}
                        diaHoraGrupoJson["id"] = parseInt(diaHoraGrupo);
                        diaHoraGrupoJson["grupoHorario"] = {id: parseInt(grupoHorario)};
                        diaHoraGrupoJson["dia"] = {id: parseInt(dia.id)};
                        diaHoraGrupoJson["hora"] = {id: parseInt(hora.id)};
                        diasHorasGrupo.push(diaHoraGrupoJson);
                    }
                }
            } else if (this.tabGrupos.grupoHorarioSel.esTipoGrupoZeta) {
                for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                    let diaHoraGrupoEach = this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key];
                    if (diaHoraGrupoEach.seleccionado) {

                        let diaHoraGrupo = diaHoraGrupoEach.id;
                        let grupoHorario = diaHoraGrupoEach.grupoHorario
                        let dia = diaHoraGrupoEach.dia;
                        let hora = diaHoraGrupoEach.hora;
                        let diaHoraGrupoJson = {}

                        diaHoraGrupoJson["id"] = parseInt(diaHoraGrupo);
                        diaHoraGrupoJson["grupoHorario"] = {id: parseInt(grupoHorario.id)};
                        diaHoraGrupoJson["dia"] = {id: parseInt(dia.id)};
                        diaHoraGrupoJson["hora"] = {id: parseInt(hora.id)};
                        diasHorasGrupo.push(diaHoraGrupoJson);
                    }
                }
            } else if (this.tabGrupos.grupoHorarioSel.esTipoGrupoEspecial) {

                for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                    let diaHoraGrupoEach = this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key];
                    if (diaHoraGrupoEach.seleccionado) {

                        let diaHoraGrupo = diaHoraGrupoEach.id;
                        let grupoHorario = diaHoraGrupoEach.grupoHorario
                        let dia = diaHoraGrupoEach.dia;
                        let hora = diaHoraGrupoEach.hora;
                        let diaHoraGrupoJson = {}

                        diaHoraGrupoJson["id"] = parseInt(diaHoraGrupo);
                        diaHoraGrupoJson["grupoHorario"] = {id: parseInt(grupoHorario.id)};
                        diaHoraGrupoJson["dia"] = {id: parseInt(dia.id)};
                        diaHoraGrupoJson["hora"] = {id: parseInt(hora.id)};
                        diasHorasGrupo.push(diaHoraGrupoJson);
                    }
                }
            }

            if (this.seccionModal.horasSemanales != diasHorasGrupo.length) {
                /*
                 bootbox.alert({
                 message: mensajeAsignarHoras,
                 buttons: {
                 ok: {label: 'Cerrar', className: "btn-danger"},
                 },
                 callback: function (result) {
                 if (result) {
                 
                 }
                 }
                 });
                 */
                alert(mensajeAsignarHoras);
                return
            }

            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/gposeccion/' + $vue.seccionModal.id + '/saveSeccionGrupo'),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            async: true,
                            data:
                                    JSON.stringify(diasHorasGrupo)
                            ,
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
            /*
             let aulaEsp = this.tabAulas['especificas'].aulasEspecificaSel == null || this.tabAulas['especificas'].aulasEspecificaSel == "";
             let aulaOera = this.tabAulas['oera'].aulaSel == null || this.tabAulas['oera'].aulaSel == "";
             let aulaOfi = this.tabAulas['oficinas'].oficinaSel == null || this.tabAulas['oficinas'].oficinaSel == "";
             */
            let aulaSelArg = [];
            let aulaSeleccionada = this.tabAulas.aulaSel;

            if (aulaSeleccionada == null) {
                alert("Seleccione un aula");
                return;
            }
            /*
             if (aulaEsp && aulaOera && aulaOfi) {
             alert("Seleccione un aula");
             return;
             }*/
            /*
             if (this.tabAulas['especificas'].aulasEspecificaSel != null && this.tabAulas['especificas'].aulasEspecificaSel != "") {
             aulaSelArg.push(this.tabAulas['especificas'].aulasEspecificaSel);
             }
             if (this.tabAulas['oera'].aulaSel != null && this.tabAulas['oera'].aulaSel != "") {
             aulaSelArg.push(this.tabAulas['oera'].aulaSel);
             }
             if (this.tabAulas['oficinas'].aulaSel != null && this.tabAulas['oficinas'].aulaSel != "") {
             aulaSelArg.push(this.tabAulas['oficinas'].aulaSel);
             }
             
             if (aulaSelArg.length > 1 || aulaSelArg.length == 0) {
             alert("Error al seleccionar el aula.");
             return;
             }
             */
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveAula'),
                            type: 'POST',
                            async: true,
                            data: {
                                seccion: $vue.seccionModal.id,
                                aula: aulaSeleccionada.id
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
                    } else {

                    }
                }
            });
        }, selectGrupoHoraReg(diaHoraGrupo) {
            var seleccionado = !diaHoraGrupo.seleccionado;

            if (seleccionado) {
                this.tabGrupos.grupoHorarioSel = diaHoraGrupo.grupoHorario;

                if (diaHoraGrupo.grupoHorario.esTipoGrupoRegular) {
                    this.tabGrupos['regulares'].grupoHorarioRegSel = diaHoraGrupo;
                    this.tabGrupos['zetas'].grupoHorarioSel = null;
                    this.tabGrupos['especial'].grupoHorarioSel = null;

                    for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                        if (this.tabGrupos['regulares'].grupoHorarioRegSel != null &&
                                this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].grupoHorario.id == this.tabGrupos['regulares'].grupoHorarioRegSel.grupoHorario.id) {
                            this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = seleccionado;
                        } else {
                            this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                    if (this.tabGrupos['zetas'].tblHorarios != null) {
                        for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                            this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                    if (this.tabGrupos['especial'].tblHorarios != null) {

                        for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                            this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }
                } else if (diaHoraGrupo.grupoHorario.esTipoGrupoZeta) {

                    let cantGruposSelec = 1;
                    if (this.tabGrupos['zetas'].tblHorarios != null) {
                        for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                            if (this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado) {
                                cantGruposSelec++;
                            }
                        }
                    }
                    if (parseInt(cantGruposSelec) > parseInt(this.seccionModal.horasSemanales)) {
                        alert("No se puede asignar mas horas, verifique.");
                        return;
                    }
                    console.log("dia hora grupo seleccionado");
                    console.log(diaHoraGrupo.dia.numeroDia);
                    console.log(diaHoraGrupo.hora.numero);
                    console.log("--------------");

                    for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                        var diaHoraGrupoEach = this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key];

                        console.log("dia hora grupo each");
                        console.dir(diaHoraGrupoEach);
                        if (diaHoraGrupoEach.seleccionado) {
                            if (diaHoraGrupoEach.dia.numeroDia == diaHoraGrupo.dia.numeroDia) {
                                let horaAfter = diaHoraGrupo.hora.numero + 1;
                                let horaBefore = diaHoraGrupo.hora.numero - 1;

                                if (diaHoraGrupoEach.hora.numero != horaAfter && diaHoraGrupoEach.hora.numero != horaBefore) {
                                    diaHoraGrupo.seleccionado = false;
                                    return;
                                }
                            }
                        }
                    }

                    this.tabGrupos['zetas'].grupoHorarioSel = diaHoraGrupo;
                    this.tabGrupos['regulares'].grupoHorarioRegSel = null;
                    this.tabGrupos['especial'].grupoHorarioSel = null;
                    diaHoraGrupo.seleccionado = seleccionado;

                    if (this.tabGrupos['regulares'].tblHorarioRegular != null) {
                        for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                            this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                    if (this.tabGrupos['especial'].tblHorarios != null) {
                        for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                            this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                } else if (diaHoraGrupo.grupoHorario.esTipoGrupoEspecial) {

                    let cantGruposSelec = 1;

                    if (this.tabGrupos['especial'].tblHorarios != null) {
                        for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                            if (this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado) {
                                cantGruposSelec++;
                            }
                        }
                    }

                    if (parseInt(cantGruposSelec) > parseInt(this.seccionModal.horasSemanales)) {
                        /*
                         bootbox.alert({
                         message: "No se puede asignar mas horas, verifique.",
                         buttons: {
                         ok: {label: 'Cerrar', className: "btn-danger"},
                         },
                         callback: function (result) {
                         if (result) {
                         }
                         }
                         });
                         */
                        alert("No se puede asignar mas horas, verifique.");
                        return;
                    }

                    this.tabGrupos['zetas'].grupoHorarioSel = null;
                    this.tabGrupos['regulares'].grupoHorarioRegSel = null;
                    this.tabGrupos['especial'].grupoHorarioSel = diaHoraGrupo;
                    diaHoraGrupo.seleccionado = seleccionado;



                    if (this.tabGrupos['zetas'].tblHorarios != null) {
                        for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                            this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                    if (this.tabGrupos['regulares'].tblHorarioRegular != null) {
                        for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                            this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }
                }
            } else {



                diaHoraGrupo.seleccionado = seleccionado;
                this.tabGrupos['regulares'].grupoHorarioRegSel = null;
                /*  this.tabGrupos['especial'].grupoHorarioSel = null;
                 this.tabGrupos['zetas'].grupoHorarioSel = null;*/

                if (this.tabGrupos['regulares'].tblHorarioRegular != null) {
                    for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                        this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                    }
                }
                /*
                 if (this.tabGrupos['especial'].tblHorarios != null) {
                 for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                 this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                 }
                 }
                 
                 if (this.tabGrupos['zetas'].tblHorarios != null) {
                 for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                 this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                 }
                 }*/
            }

            // this.tblHorarioRegular = this.tblHorarioRegular;
        }, selectAula(aula) {
            let seleccionado = !aula.seleccionado;
            console.log("select aula");
            console.dir(aula);
            if (seleccionado) {
                this.tabAulas.aulaSel = aula;
                if (aula.esOera) {
                    /*
                     this.tabAulas['oera'].aulaSel = aula;
                     this.tabAulas['oficinas'].aulaSel = null;
                     this.tabAulas['especificas'].aulasEspecificaSel = null;
                     */
                    if (this.tabAulas['oficinas'].tblAulas != null) {
                        for (let key in this.tabAulas['oficinas'].tblAulas) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        }
                    }

                    for (let key in this.tabAulas['oera'].tblAulas) {
                        this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        if (this.tabAulas['oera'].tblAulas[key].id == aula.id) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = seleccionado;
                        }
                    }

                } else if (aula.esOficina) {
                    /*
                     this.tabAulas['oficinas'].aulaSel = aula;
                     this.tabAulas['oera'].aulaSel = null;
                     this.tabAulas['especificas'].aulasEspecificaSel = null;
                     */
                    if (this.tabAulas['oera'].tblAulas != null) {
                        for (let key in this.tabAulas['oera'].tblAulas) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        }
                    }
                    for (let key in this.tabAulas['oficinas'].tblAulas) {
                        this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        if (this.tabAulas['oficinas'].tblAulas[key].id == aula.id) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = seleccionado;
                        }
                    }

                } else if (aula.esEspecifica) {

                    if (this.tabAulas['oera'].tblAulas != null) {
                        for (let key in this.tabAulas['oera'].tblAulas) {
                            this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                        }
                    }

                    if (this.tabAulas['oficinas'].tblAulas != null) {
                        for (let key in this.tabAulas['oficinas'].tblAulas) {
                            this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                        }
                    }
                }
            } else {
                /*
                 this.tabAulas['oera'].aulaSel = null;
                 this.tabAulas['oficinas'].aulaSel = null;
                 */
                this.tabAulas.aulaSel = null;
                if (this.tabAulas['oficinas'].tblAulas != null) {
                    for (let key in this.tabAulas['oficinas'].tblAulas) {
                        this.tabAulas['oficinas'].tblAulas[key].seleccionado = false;
                    }
                }

                if (this.tabAulas['oera'].tblAulas != null) {
                    for (let key in this.tabAulas['oera'].tblAulas) {
                        this.tabAulas['oera'].tblAulas[key].seleccionado = false;
                    }
                }

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
            //$emit('update');
        }, cambiarCboTipoGrupoHorReg() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horario'),
                type: 'POST',
                async: false,
                data: {
                    tipoGrupoHorasId: $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado.id,
                    seccionId: $vue.seccionModal.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabGrupos['regulares'].tblHorarioRegular = response.data;
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

        }, seleccionarGrupoZ(grupo) {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horariosZeta'),
                type: 'POST',
                async: false,
                data: {
                    grupoHorario: grupo,
                    seccion: $vue.seccionModal.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabGrupos['zetas'].tblHorarios = response.data;
                        console.dir($vue.tabGrupos['zetas'].tblHorarios);
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos['zetas'].tblHorarios = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });

        }, seleccionarGrupoEsp(grupo) {

            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horariosEspeciales'),
                type: 'POST',
                async: false,
                data: {
                    grupoHorario: grupo,
                    seccion: $vue.seccionModal.id
                },
                success: function (response) {
                    console.dir(response);
                    if (response.success) {
                        $vue.tabGrupos['especial'].tblHorarios = response.data;
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos['especial'].tblHorarios = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tblHorarioEsp").html('');
                }
            });

            /*
             let $vue = this;
             $.ajax({
             url: APP.url('academico/gposeccion/horariosZeta'),
             type: 'POST',
             async: false,
             data: {
             grupoHorario: grupo,
             seccion: $vue.seccionModal.id
             },
             success: function (response) {
             if (response.success) {
             console.dir(response.data);
             $vue.tabGrupos['especial'].tblHorarios = response.data;
             } else {
             notify(response.message, "error");
             $vue.tabGrupos['especial'].tblHorarios = null;
             }
             },
             error: function () {
             notify(MESSAGES.errorComunicacion, "error");
             //  $("#tablaHorario").html('');
             }
             });
             */
        }, cambiarCboTipoGrupoHorZeta() {
            let $vue = this;
//tabGrupos['zetas'].   grupoHorarioSel tblHorarios
            $.ajax({
                url: APP.url('academico/gposeccion/horario'),
                type: 'POST',
                async: false,
                data: {
                    tipoGrupoHorasId: $vue.tabGrupos['zetas'].grupoHorarioSel.id,
                    seccionId: $vue.seccionModal.id
                },
                success: function (response) {
                    if (response.success) {

                        $vue.tabGrupos['zetas'].tblHorarios = response.data;
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos['zetas'].tblHorarios = null;
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
            /*
             if (this.tabGrupos['regulares'].grupoHorarioRegSel != null && this.tabGrupos['regulares'].grupoHorarioRegSel != "") {
             if (gpoHorario.id == this.tabGrupos['regulares'].grupoHorarioRegSel.id) {
             return "btn-primary";
             }
             }*/
            return "btn-default";
        }, getClassAula(aula) {
            if (aula.seleccionado) {
                return "btn-primary";
            }/*
             if ((this.tabAulas.aulaSel != null && this.tabAulas['oera'].aulaSel != "")
             && parseInt(aula.id) == parseInt(this.tabAulas['oera'].aulaSel.id)) {
             return "btn-primary";
             }*/
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
                        $vue.tabAulas.aulaSel = response.data.aulaSel;
                        $vue.seccionModal = response.data.seccion;
                        $vue.tabAulas['oera'].modulosCombo = response.data.modulosOera;
                        $vue.tabAulas['oficinas'].oficinasDisponibles = response.data.oficinasDisponibles;
                        // $vue.modulosCombo = response.data.modulosOera;

                        if ($vue.tabAulas.aulaSel != null) {
                            if ($vue.tabAulas.aulaSel.esOera) {
                                console.log("esOera");
                                console.dir(response.data.modulosOeraSel);
                                $vue.tabAulas['oera'].moduloSel = response.data.modulosOeraSel;
                                $vue.cambiarModulo();
                            } else if ($vue.tabAulas.aulaSel.esOficina) {
                                console.log("esOficina");
                                $vue.tabAulas['oficinas'].oficinaSel = response.data.oficinaSel;
                                $vue.cambiarOficina();
                            } else if ($vue.tabAulas.aulaSel.esEspecifica) {
                                console.log("esEspecifica");
                            }
                        }

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
            //  $vue.tabAulas.aulaSel = null;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aulas'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['oera'].moduloSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas['oera'].tblAulas = response.data.aulas;
                        /*
                         if (response.data.aulaSel != null && response.data.aulaSel != "") {
                         $vue.tabAulas['oera'].aulaSel = response.data.aulaSel;
                         }*/
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, cambiarOficina() {
            let $vue = this;
            //    $vue.tabAulas.aulaSel = null;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/aulas'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['oficinas'].oficinaSel.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabAulas['oficinas'].tblAulas = response.data.aulas;

                        if (response.data.aulaSel != null) {
                            $vue.tabAulas.aulaSel = response.data.aulaSel;
                        }

                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, seleccionarAulaEspecifica() {
            let $vue = this;
            if ($vue.tabAulas['especificas'].aulasEspecificaSel == null) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/seleccionarAula'),
                data: {
                    seccion: $vue.seccionModal.id,
                    aula: $vue.tabAulas['especificas'].aulasEspecificaSel.id
                },
                success: function (response) {
                    if (response.success) {
                        //  $vue.tabAulas['especificas'].aulasEspecificaSel = response.data;
                        //   $vue.tabAulas['especificas'].aulasEspecificaSel.seleccionado = true;

                        $vue.tabAulas.aulaSel = response.data;
                        $vue.selectAula($vue.tabAulas['especificas'].aulasEspecificaSel);
                        // $vue.tabAulas['especificas'].aulasEspecificaSel.seleccionado = true;
                        /*
                         this.tabAulas['oficinas'].aulaSel = null;
                         this.tabAulas['oera'].aulaSel = null;*/
                    } else {
                        if (response.total > 0) {
                            $vue.tabAulas['especificas'].errores = response.data;
                        } else {
                            $vue.tabAulas['especificas'].errores = [];
                        }
                        notify(response.message, "error");
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