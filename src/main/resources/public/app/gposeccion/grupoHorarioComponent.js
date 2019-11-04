Vue.component("grupohorario-component", {
    template: "#grupoHorarioComp",
    props: {

    },
    data: function () {
        return {
            seccionModal: null,
            errorsMessage: null,
            tabGrupoSelected: null,
            tabGrupos: {
                grupoHorarioSel: {},
                regulares: {
                    tipoGrupoHorasSeleccionado: null,
                    tblHorarios: null,
                    tipoGrupoHorasOpts: null
                }, zetas: {
                    tblHorarios: null
                }, especial: {
                    tblHorarios: null
                }
            }
        }
    },
    mounted: function () {

        let $vue = this;
        /*
         $global.$on("loadGrupoComponent", function (seccion) {
         $vue.loadGruposHorario($vue, seccion);
         });
         */
//        $global.$on("saveGrupoHorario", function () {
//            $vue.saveGrupoHorario($vue);
//        });
    },
    methods: {
        loadGruposHorario(seccion) {
            let $vue = this;
            $vue.tabGrupos = {
                grupoHorarioSel: {},
                regulares: {
                    tipoGrupoHorasSeleccionado: null,
                    tblHorarios: null,
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
            $vue.tabGrupos['regulares'].tblHorarios = null;
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
                        $vue.tabGrupos['regulares'].tipoGrupoHorasOpts = response.data.tiposGruposHorasOpt;

                        console.log($vue.tabGrupos.grupoHorarioSel)
                        if (response.data.grupoHorarioSel != null) {
                            $vue.tabGrupos.grupoHorarioSel = response.data.grupoHorarioSel;
                            console.log(response.data.grupoHorarioSel.tipoGrupoHoras)

                            if (response.data.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoRegular) {
                                console.log("isTipoGrupoRegular");
                                $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado = response.data.grupoHorarioSel.tipoGrupoHoras;
                                // $vue.tabGrupos.grupoHorarioSel["tabGrupo"] = "regulares";
                                $vue.tabGrupoSelected = "regulares";
                                $("[href='#grupo1']").click();
                                $vue.cambiarCboTipoGrupoHorReg();
                            } else if (response.data.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoZeta) {
                                console.log("isTipoGrupoZeta");
                                $vue.seleccionarGrupoZ($vue.tabGrupos.grupoHorarioSel.id);
                                $("[href='#grupo3']").click();
                                //  $vue.tabGrupos.grupoHorarioSel["tabGrupo"] = "zetas";
                                $vue.tabGrupoSelected = "zetas";
                            } else if (response.data.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoEspecial) {
                                console.log("isTipoGrupoEspecial");
                                $vue.seleccionarGrupoEsp($vue.tabGrupos.grupoHorarioSel.id);
                                $("[href='#grupo2']").click();
                                // $vue.tabGrupos.grupoHorarioSel["tabGrupo"] = "especiales";
                                $vue.tabGrupoSelected = "especiales";
                            }

                        } else {
                            //  $vue.tabGrupos.grupoHorarioSel["tabGrupo"] = "regulares";
                            $vue.tabGrupoSelected = "regulares";
                            $("[href='#grupo1']").click();
                            $vue.tabGrupos['zetas'].tblHorarios = null;
                        }
                        //$vue.tabGrupos.grupoHorarioSel["tabGrupo"] = "regulares";
                        console.log($vue.tabGrupos.grupoHorarioSel)
                    }
                }
            });

        },
        cambiarCboTipoGrupoHorReg() {
            console.log("cambiarCboTipoGrupoHorReg");
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horariosRegulares'),
                type: 'POST',
                async: false,
                data: {
                    tipoGrupoHorasId: $vue.tabGrupos['regulares'].tipoGrupoHorasSeleccionado.id,
                    seccionId: $vue.seccionModal.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabGrupos['regulares'].tblHorarios = response.data;
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos['regulares'].tblHorarios = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#tablaHorario").html('');
                }
            });

        },
        getClassGpoHorario(gpoHorario) {
            if (gpoHorario.seleccionado) {
                //this.seccionModal.aula.permiteCruceBool && 
                if (gpoHorario.tieneCruce) {
                    return "btn-danger";
                }
                return "btn-primary";
            } else {
                if (gpoHorario.tieneCruce) {
                    return "btn-warning";
                }
            }
            return "btn-default";
        },
        seleccionarGrupoZ(grupo) {
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
                        $global.$emit("clearAndSelectEsp");
                        $vue.tabGrupos['especial'].tblHorarios = null;
                        $vue.cleanDiasHorasGrupoDiferentGpoHorario($vue.tabGrupos.grupoHorarioSel);
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

        },
        seleccionarGrupoEsp(grupo) {

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
                        $global.$emit("clearAndSelectZeta");
                        $vue.tabGrupos['zetas'].tblHorarios = null;

                        //  $vue.cleanDiasHorasGrupoDiferentGpoHorario($vue.tabGrupos.grupoHorarioSel);

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

        },
        cambiarCboTipoGrupoHorZeta() {
            let $vue = this;
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

        },
        selectDiaHoraGrupo(diaHoraGrupo) {
            var seleccionado = !diaHoraGrupo.seleccionado;
            this.errorsMessage = null;
            //console.log(seleccionado)
            if (seleccionado) {
                let tabGrupo = this.tabGrupos.grupoHorarioSel["tabGrupo"];
                this.tabGrupos.grupoHorarioSel = Object.assign({}, diaHoraGrupo.grupoHorario);
                this.tabGrupos.grupoHorarioSel["tabGrupo"] = tabGrupo;

                if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoRegular) {
                    let jsonDiaHoraGrupoRegulares = this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo;
                    for (let key in jsonDiaHoraGrupoRegulares) {
                        let diaHoraAulaEach = this.tabGrupos['regulares'].tblHorarios.jsonHorarioAula[key];
                        let diaHoraGrupoEach = jsonDiaHoraGrupoRegulares[key];
                        if (diaHoraGrupoEach.grupoHorario.id != diaHoraGrupo.grupoHorario.id) {
                            diaHoraGrupoEach.seleccionado = false;
                            continue;
                        }
                        if (diaHoraGrupoEach.grupoHorario.id == diaHoraGrupo.grupoHorario.id
                                && diaHoraGrupo.dia.id == diaHoraGrupoEach.dia.id) {
                            //&& diaHoraAulaEach == undefined
                            if (this.seccionModal.aula != null) {
                                if (!this.seccionModal.aula.permiteCruceBool) {
                                    if (diaHoraGrupo.tieneCruce) {
                                        continue;
                                    }
                                }
                            }
                            diaHoraGrupoEach.seleccionado = true;
                        }
                    }
                    this.cleanDiasHorasGrupoDiferentGpoHorario(this.tabGrupos.grupoHorarioSel);

                } else if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoEspecial) {

                    for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                        let diaHoraAulaEach = this.tabGrupos['especial'].tblHorarios.jsonHorarioAula[key];
                        let diaHoraGrupoEach = this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key];
                        if (diaHoraGrupoEach.grupoHorario.id == this.tabGrupos.grupoHorarioSel.id
                                && diaHoraGrupo.dia.id == diaHoraGrupoEach.dia.id) {
                            //&& diaHoraAulaEach == undefined
                            if (this.seccionModal.aula != null) {
                                if (!this.seccionModal.aula.permiteCruceBool) {
                                    if (diaHoraGrupo.tieneCruce) {
                                        continue;
                                    }
                                }
                            }
                            diaHoraGrupoEach.seleccionado = seleccionado;
                        } else {
                            if (diaHoraGrupoEach.grupoHorario.id != diaHoraGrupo.grupoHorario.id) {
                                diaHoraGrupoEach.seleccionado = false;
                            }
                        }
                    }
                    this.cleanDiasHorasGrupoDiferentGpoHorario(this.tabGrupos.grupoHorarioSel);
                } else if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoZeta) {

                    if (!this.seleccionarGrupoValidate(this, diaHoraGrupo, this.tabGrupos['zetas'])) {
                        return;
                    }

                    diaHoraGrupo.seleccionado = seleccionado;
                    this.cleanDiasHorasGrupoDiferentGpoHorario(this.tabGrupos.grupoHorarioSel);
                }

            } else {
                if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoRegular) {
                    diaHoraGrupo.seleccionado = seleccionado;
                    //grupoHorario
                    if (this.tabGrupos['regulares'].tblHorarios != null) {
                        let cantSelecteds = 0;
                        for (let key in this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo) {
                            let diaHoraGrupoEach = this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo[key];

                            if (diaHoraGrupoEach.grupoHorario.id != diaHoraGrupo.grupoHorario.id) {
                                diaHoraGrupoEach.seleccionado = false;
                                continue;
                            }
                            if (diaHoraGrupo.dia.id == diaHoraGrupoEach.dia.id) {
                                diaHoraGrupoEach.seleccionado = false;
                            }
                            if (diaHoraGrupoEach.seleccionado) {
                                cantSelecteds++;
                            }
                        }
                        if (cantSelecteds == 0) {
                            //  console.log("zzz");
                            //     console.dir(this.tabGrupos);
                            //      console.dir(this.tabGrupos.);
                            this.tabGrupos.grupoHorarioSel.id = null;
                        }
                    }
                } else if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoEspecial) {
                    diaHoraGrupo.seleccionado = seleccionado;

                    if (this.tabGrupos['especial'].tblHorarios != null) {
                        let cantSelecteds = 0;
                        for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                            let diaHoraGrupoEach = this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key];
                            if (diaHoraGrupo.dia.id == diaHoraGrupoEach.dia.id) {
                                diaHoraGrupoEach.seleccionado = false;
                            }
                            if (diaHoraGrupoEach.seleccionado) {
                                cantSelecteds++;
                            }
                        }
                        if (cantSelecteds == 0) {
                            this.tabGrupos.grupoHorarioSel = null;
                        }
                    }
                } else if (diaHoraGrupo.grupoHorario.tipoGrupoHoras.isTipoGrupoZeta) {
                    if (this.deseleccionarGrupoValidate(this, diaHoraGrupo, this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo)) {
                        diaHoraGrupo.seleccionado = seleccionado;
                    }
                }
            }
        },
        seleccionarGrupoValidate($vue, diaHoraGrupo, tabGrupo) {

            let cantGruposSelec = 1;
            if (tabGrupo.tblHorarios != null) {
                for (let key in tabGrupo.tblHorarios.jsonDiaHoraGrupo) {
                    if (tabGrupo.tblHorarios.jsonDiaHoraGrupo[key].seleccionado) {
                        cantGruposSelec++;
                    }
                }
            }
            if (parseInt(cantGruposSelec) > parseInt(this.seccionModal.totalHorasSemanales)) {
                notify("No se puede asignar mas horas, verifique.", "error");
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
//                    if (stop) {
//                        return false;
//                    }
                }
            }
            return true;
        },
        deseleccionarGrupoValidate($vue, diaHoraGrupo, arg) {
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
        },
        saveGrupoHorario() {
            let $vue = this;
            if ($vue.tabGrupos.grupoHorarioSel == null) {
                notify("Seleccione un grupo horario.", "error");
                return;
            }

            let diasHorasGrupo = [];

            if ($vue.tabGrupos.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoRegular) {
                for (let key in this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo) {
                    let diaHoraGrupoEach = this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo[key];
                    if (diaHoraGrupoEach.seleccionado) {
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
                        console.log(diaHoraGrupoJson)
                    }
                }
            } else if (this.tabGrupos.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoZeta) {
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
            } else if (this.tabGrupos.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoEspecial) {

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
            console.log($vue.seccionModal.totalHorasSemanales)
            console.log(diasHorasGrupo.length)
            if ($vue.seccionModal.totalHorasSemanales != diasHorasGrupo.length) {
                errorCantHoras = true;
            }
            if ($vue.tabGrupos.grupoHorarioSel.permiteCeroHoras) {
                if (diasHorasGrupo.length == 0) {
                    errorCantHoras = false;
                }
            }


            if (errorCantHoras) {
                notify("Asignar la cantidad de horas requeridas para la sección.", "error");
                return;
            }

            $vue.tabGrupos.grupoHorarioSel.diaHoraGrupo = diasHorasGrupo;
            let gpoHoras = {
                id: $vue.tabGrupos.grupoHorarioSel.id,
                diaHoraGrupo: diasHorasGrupo
            };
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        axios.post("/academico/gposeccion/" + $vue.seccionModal.id + '/saveSeccionGrupo',
                                gpoHoras).then(function (response) {
                            if (response.data.success) {
                                MODAL.hideWait();
                                $global.$emit("afterSaveGrupo", response.data);
                            } else {
                                MODAL.hideWait();
                                notify(response.data.message, "error");
                            }
                        }).catch(function (error) {
                            MODAL.hideWait();
                            //$global.$emit("afterSaveGrupo", error);
                            notify(error.errorComunicacion, "error");
                        });

//                        $.ajax({
//                            url: APP.url('academico/gposeccion/' + $vue.seccionModal.id + '/saveSeccionGrupo'),
//                            dataType: "json",
//                            contentType: "application/json",
//                            type: 'POST',
//                            async: true,
//                            data: JSON.stringify($vue.tabGrupos.grupoHorarioSel),
//                            success: function (response) {
//                                if (response.success) {
//                                    MODAL.hideWait();
//                                    $global.$emit("afterSaveGrupo", response);
//                                } else {
//                                    MODAL.hideWait();
//                                    notify(response.message, "error");
//                                }
//                            },
//                            error: function (response) {
//                                MODAL.hideWait();
//                                $global.$emit("afterSaveGrupo", response);
//                                notify(MESSAGES.errorComunicacion, "error");
//                            }
//                        });


                    }
                }
            });
        },
        cleanDiasHorasGrupoDiferentGpoHorario(grupoHorario) {
            console.log("cleanDiasHorasGrupoDiferentGpoHorario")
            if (!grupoHorario.tipoGrupoHoras.isTipoGrupoZeta) {
                if (this.tabGrupos['zetas'].tblHorarios != null) {
                    console.log("isTipoGrupoZeta isTipoGrupoZeta isTipoGrupoZeta")
                    for (let key in this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo) {
                        this.tabGrupos['zetas'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                    }
                }
            }
            if (!grupoHorario.tipoGrupoHoras.isTipoGrupoRegular) {
                if (this.tabGrupos['regulares'].tblHorarios != null) {
                    console.log("isTipoGrupoRegular isTipoGrupoRegular isTipoGrupoRegular")
                    for (let key in this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo) {
                        this.tabGrupos['regulares'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                    }
                }
            }
            if (!grupoHorario.tipoGrupoHoras.isTipoGrupoEspecial) {
                if (this.tabGrupos['especial'].tblHorarios != null) {
                    console.log("isTipoGrupoEspecial isTipoGrupoEspecial isTipoGrupoEspecial")
                    for (let key in this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo) {
                        this.tabGrupos['especial'].tblHorarios.jsonDiaHoraGrupo[key].seleccionado = false;
                    }
                }
            }
        }
    }
});

