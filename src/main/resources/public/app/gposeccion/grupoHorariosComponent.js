Vue.component("grupohorarios-component", {
    template: "#grupoHorarioComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
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
        }
    },
    mounted: function () {

        let $vue = this;
        $global.$on("loadGrupoComponent", function (seccion) {
            $vue.loadGruposHorario($vue, seccion);
        });

        $global.$on("saveGrupoHorario", function () {
            $vue.saveGrupoHorario($vue);
        });
    },
    methods: {
        loadGruposHorario($vue, seccion) {
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

            $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado = null;
            $vue.tabGrupos['regulares'].tblHorarioRegular = null;
            $vue.tabGrupos['regulares'].grupoHorarioRegSel = null;
            $vue.tabGrupos['regulares'].tipoGrupoHorasOpts = null;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalGrupo'),
                data: {
                    seccion: seccion
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
                            } else if (response.data.grupoHorarioSel.esTipoGrupoEspecial) {
                                console.log("esTipoGrupoEspecial");
                                $vue.tabGrupos['especial'].grupoHorarioSel = response.data.grupoHorarioSel;
                                $vue.tabGrupos['especial'].tipoGrupoHorasSeleccionado = response.data.grupoHorarioSel.tipoGrupoHoras;
                                $vue.seleccionarGrupoEsp($vue.tabGrupos['especial'].grupoHorarioSel.id);
                            }

                        } else {
                            $vue.tabGrupos['zetas'].tblHorarios = null;
                        }


                    }
                }
            });

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

        }, getClassGpoHorario(gpoHorario) {
            if (gpoHorario.seleccionado) {
                return "btn-primary";
            }
            return "btn-default";
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

                        $global.$emit("seleccionarGrupoZeta", $vue.tabGrupos.grupoHorarioSel);
                        $global.$emit("reloadDynaEspecial", $vue.seccionModal.id);
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

        }, selectGrupoHoraHorario(diaHoraGrupo) {
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

                    if (!this.seleccionarGrupoValidate(diaHoraGrupo, this.tabGrupos['zetas'])) {
                        return;
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

                    if (!this.seleccionarGrupoValidate(diaHoraGrupo, this.tabGrupos['especial'])) {
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
                this.tabGrupos.grupoHorarioSel = null;

                if (diaHoraGrupo.grupoHorario.esTipoGrupoZeta) {
                    if (this.deseleccionarGrupoValidate(diaHoraGrupo, this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo)) {
                        diaHoraGrupo.seleccionado = seleccionado;
                    }
                } else if (diaHoraGrupo.grupoHorario.esTipoGrupoEspecial) {
                    if (this.deseleccionarGrupoValidate(diaHoraGrupo, this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo)) {
                        diaHoraGrupo.seleccionado = seleccionado;
                    }
                } else if (diaHoraGrupo.grupoHorario.esTipoGrupoRegular) {

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
        }, seleccionarGrupoValidate(diaHoraGrupo, tabGrupo) {

            let cantGruposSelec = 1;
            if (tabGrupo.tblHorarios != null) {
                for (let key in tabGrupo.tblHorarios.jsonDiaHoraGrupo) {
                    if (tabGrupo.tblHorarios.jsonDiaHoraGrupo[key].seleccionado) {
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


            for (let keyDia in tabGrupo.tblHorarios.dias) {
                let diaEach = tabGrupo.tblHorarios.dias[keyDia];

                let diaHoraGrupoAfter = null;
                let diaHoraGrupoBefore = null;
                let cantGruposSelDia = 1;
                for (let key in tabGrupo.tblHorarios.jsonDiaHoraGrupo) {
                    let diaHoraGrupoEach = tabGrupo.tblHorarios.jsonDiaHoraGrupo[key];
                    if (diaHoraGrupo.dia.numeroDia != diaHoraGrupoEach.dia.numeroDia) {
                        continue;
                    }
                    if (diaHoraGrupoEach.seleccionado) {
                        cantGruposSelDia++;
                    }
                }
                if (cantGruposSelDia > 1) {
                    for (let key in tabGrupo.tblHorarios.jsonDiaHoraGrupo) {
                        var diaHoraGrupoEach = tabGrupo.tblHorarios.jsonDiaHoraGrupo[key];
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
                    if (stop) {
                        return false;
                    }
                }
            }
            return true;
        }, deseleccionarGrupoValidate(diaHoraGrupo, arg) {
            let diaHoraGrupoAfter = null;
            let diaHoraGrupoBefore = null;
            let horaAfter = diaHoraGrupo.hora.numero + 1;
            let horaBefore = diaHoraGrupo.hora.numero - 1;
            for (let key in arg) {
                var diaHoraGrupoEach = arg[key];
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
                return false;
            }
            return true;
        }, saveGrupoHorario($vue) {
            let mensajeAsignarHoras = "Asignar la cantidad de horas requeridas para la sección.";

            if ($vue.tabGrupos.grupoHorarioSel == null) {
                alert("Seleccione un grupo horario");
                return;
            }

            let diasHorasGrupo = [];
            if ($vue.tabGrupos.grupoHorarioSel.esTipoGrupoRegular) {
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
            if ($vue.seccionModal.horasSemanales != diasHorasGrupo.length) {
                errorCantHoras = true;
            }
            if ($vue.tabGrupos.grupoHorarioSel.permiteCeroHoras) {
                if (diasHorasGrupo.length == 0) {
                    errorCantHoras = false;
                }
            }


            if (errorCantHoras) {
                alert(mensajeAsignarHoras);
                return
            }
            $vue.tabGrupos.grupoHorarioSel.diaHoraGrupo = diasHorasGrupo;


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
                                MODAL.hideWait();
                                $global.$emit("afterSaveGrupo", response);
                            },
                            error: function (response) {
                                MODAL.hideWait();
                                $global.$emit("afterSaveGrupo", response);
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        }
    }
});

