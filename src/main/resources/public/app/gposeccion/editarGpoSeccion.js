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
    }, created: function () {
        this.grupoSeccion = JSON.parse(gpoSeccionJson);
        this.loadSecciones();
    }, mounted: function () {
        let $vue = this;
        $global.$on("afterSaveAula", function (response) {
            $vue.afterSaveAula(response, $vue);
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

            $vue.tabGrupos = {
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

                        $global.$emit("reloadDynaZeta", $vue.seccionModal.id);
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
                                $vue.tabGrupos['especial'].grupoHorarioSel = response.data.grupoHorarioSel;
                                $vue.tabGrupos['especial'].tipoGrupoHorasSeleccionado = response.data.grupoHorarioSel.tipoGrupoHoras;
                                $vue.seleccionarGrupoEsp($vue.tabGrupos['especial'].grupoHorarioSel.id);
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

            let errorCantHoras = false;
            if (this.seccionModal.horasSemanales != diasHorasGrupo.length) {
                errorCantHoras = true;
            }
            if (this.tabGrupos.grupoHorarioSel.permiteCeroHoras) {
                if (diasHorasGrupo.length == 0) {
                    errorCantHoras = false;
                }
            }


            if (errorCantHoras) {
                alert(mensajeAsignarHoras);
                return
            }
            this.tabGrupos.grupoHorarioSel.diaHoraGrupo = diasHorasGrupo;
            /*
             if (this.tabGrupos.grupoHorarioSel.esTipoGrupoZeta && (
             this.tabGrupos.grupoHorarioSel.esTipoGrupoCodZeta || this.tabGrupos.grupoHorarioSel.esTipoGrupoCodZetaAsterisk
             )) {
             alert("valido bien");
             return;
             } else {
             if (this.seccionModal.horasSemanales != diasHorasGrupo.length) {
             alert(mensajeAsignarHoras);
             return
             }
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
                            url: APP.url('academico/gposeccion/' + $vue.seccionModal.id + '/saveSeccionGrupo'),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            async: true,
                            data:
                                    JSON.stringify($vue.tabGrupos.grupoHorarioSel)
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
            $global.$emit('saveAula');
        }, afterSaveAula(response, $vue) {

            if (response.success) {
                $vue.$refs.modalAula.close();
                notify(response.message, "info");
                $vue.loadSecciones();
            } else {
                $vue.$refs.modalAula.close();
                notify(response.message, "error");
            }
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



                    let horaAfter = diaHoraGrupo.hora.numero + 1;
                    let horaBefore = diaHoraGrupo.hora.numero - 1;


                    for (let keyDia in this.tabGrupos['zetas'].tblHorarios.dias) {
                        let diaEach = this.tabGrupos['zetas'].tblHorarios.dias[keyDia];

                        let diaHoraGrupoAfter = null;
                        let diaHoraGrupoBefore = null;
                        let cantGruposSelDia = 1;
                        for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                            let diaHoraGrupoEach = this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key];
                            if (diaHoraGrupo.dia.numeroDia != diaHoraGrupoEach.dia.numeroDia) {
                                continue;
                            }
                            if (diaHoraGrupoEach.seleccionado) {
                                cantGruposSelDia++;
                            }
                        }
                        if (cantGruposSelDia > 1) {
                            for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                                var diaHoraGrupoEach = this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key];
                                if (diaHoraGrupo.dia.numeroDia != diaEach.numeroDia) {
                                    continue;
                                }

                                if (diaHoraGrupoEach.dia.numeroDia == diaHoraGrupo.dia.numeroDia) {
                                    if (diaHoraGrupoEach.hora.numero == horaAfter) {
                                        diaHoraGrupoAfter = diaHoraGrupoEach;
                                    }
                                    if (diaHoraGrupoEach.hora.numero == horaBefore) {
                                        diaHoraGrupoBefore = diaHoraGrupoEach;
                                    }
                                }

                            }

                            let stop = false;
                            if ((diaHoraGrupoAfter != null && !diaHoraGrupoAfter.seleccionado) &&
                                    (diaHoraGrupoBefore != null && !diaHoraGrupoBefore.seleccionado)) {
                                stop = true;
                            }
                            if (diaHoraGrupoBefore == null &&
                                    (diaHoraGrupoAfter != null && !diaHoraGrupoAfter.seleccionado)) {
                                stop = true;
                            }
                            if (diaHoraGrupoAfter == null &&
                                    (diaHoraGrupoBefore != null && !diaHoraGrupoBefore.seleccionado)) {
                                stop = true;
                            }
                            /*
                             if (diaHoraGrupoAfter == null && diaHoraGrupoBefore == null) {
                             stop = false;
                             }*/
                            if (stop) {
                                return;
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

                if (diaHoraGrupo.grupoHorario.esTipoGrupoZeta) {
                    console.log("zeta");
                    console.dir(diaHoraGrupo);
                    let diaHoraGrupoAfter = null;
                    let diaHoraGrupoBefore = null;
                    let horaAfter = diaHoraGrupo.hora.numero + 1;
                    let horaBefore = diaHoraGrupo.hora.numero - 1;
                    for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                        var diaHoraGrupoEach = this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key];
                        if (diaHoraGrupoEach.dia.numeroDia == diaHoraGrupo.dia.numeroDia) {
                            if (diaHoraGrupoEach.hora.numero == horaAfter) {
                                diaHoraGrupoAfter = diaHoraGrupoEach;
                            }
                            if (diaHoraGrupoEach.hora.numero == horaBefore) {
                                diaHoraGrupoBefore = diaHoraGrupoEach;
                            }
                        }

                    }
                    if ((diaHoraGrupoBefore != null && diaHoraGrupoBefore.seleccionado)
                            && (diaHoraGrupoAfter != null && diaHoraGrupoAfter.seleccionado)) {
                        return;
                    }
                    diaHoraGrupo.seleccionado = seleccionado;
                } else if (diaHoraGrupo.esTipoGrupoRegular) {

                    this.tabGrupos['regulares'].grupoHorarioRegSel = null;
                    diaHoraGrupo.seleccionado = seleccionado;
                    if (this.tabGrupos['regulares'].tblHorarioRegular != null) {
                        for (let key in this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo) {
                            this.tabGrupos['regulares'].tblHorarioRegular.jsonDiaHoraGrupo[key].seleccionado = false;
                        }
                    }

                }


            }

            // this.tblHorarioRegular = this.tblHorarioRegular;
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

                        $vue.tabGrupos.grupoHorarioSel = response.data.grupoHorasSeleccionado;
                        console.dir($vue.tabGrupos.grupoHorarioSel);
                        $global.$emit("seleccionarGrupoZeta", $vue.tabGrupos.grupoHorarioSel);
                        $global.$emit("reloadDynaEspecial");
                        $vue.tabGrupos['especial'].tblHorarios = null;
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

                    if (response.success) {

                        $vue.tabGrupos['especial'].tblHorarios = response.data;
                        $vue.tabGrupos.grupoHorarioSel = response.data.grupoHorasSeleccionado;
                        $global.$emit("seleccionarGrupoEspecial", $vue.tabGrupos.grupoHorarioSel);
                        $global.$emit("reloadDynaZeta");
                        $vue.tabGrupos['zetas'].tblHorarios = null;
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
        }, showModalAula(seccion) {

            let $vue = this;
            $global.$emit('loadAulaComponent', seccion.seccionId);
            this.$refs.modalAula.open();
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