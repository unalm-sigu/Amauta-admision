Vue.component("grupo-regular-aula-component", {
    template: "#modalGrupoRegularAulaComp",
    props: {
    },
    data: function () {
        return {
            seccionModal: null,
            errorsMessage: null,
            tabGrupos: {
                grupoHorarioSel: {},
                regulares: {
                    tipoGrupoHorasSeleccionado: null,
                    tblHorarios: [],
                    tipoGrupoHorasOpts: null
                }
            }
        }
    },
    mounted: function () {

        let $vue = this;

        $global.$on("loadGrupoRegularAulaComponent", function (seccion) {
            $vue.loadGrupoRegularAulaComponent($vue, seccion);
        });

    },
    methods: {
        loadGrupoRegularAulaComponent($vue, seccion) {

            $vue.tabGrupos = {
                grupoHorarioSel: {tabGrupo: null},
                regulares: {
                    tipoGrupoHorasSeleccionado: null,
                    tblHorarios: [],
                    tipoGrupoHorasOpts: null
                }
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/loadModalGrupo'),
                data: {
                    seccion: seccion
                },
                success: function (response) {
                    if (response.success) {
                        
                        $vue.seccionModal = response.data.seccion;

                        $vue.tabGrupos.regulares.tipoGrupoHorasOpts = response.data.tiposGruposHorasOpt;

                        if (response.data.grupoHorarioSel != null) {

                            $vue.tabGrupos.grupoHorarioSel = response.data.grupoHorarioSel;

                            if (response.data.grupoHorarioSel.tipoGrupoHoras.isTipoGrupoRegular) {

                                $vue.tabGrupos.regulares.tipoGrupoHorasSeleccionado = response.data.grupoHorarioSel.tipoGrupoHoras;
                                $vue.tabGrupos.grupoHorarioSel.tabGrupo = "regulares";
                                $vue.cambiarCboTipoGrupoHorReg();
                            }

                        } 
                        
                        $vue.tabGrupos.grupoHorarioSel.tabGrupo = "regulares";

                    }
                }
            });
        },
        cambiarCboTipoGrupoHorReg() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/horariosRegulares'),
                type: 'POST',
                async: false,
                data: {
                    tipoGrupoHorasId: $vue.tabGrupos.regulares.tipoGrupoHorasSeleccionado.id,
                    seccionId: $vue.seccionModal.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.tabGrupos.regulares.tblHorarios = response.data;
                    } else {
                        notify(response.message, "error");
                        $vue.tabGrupos.regulares.tblHorarios = [];
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
                return "btn-primary";
            }
            return "btn-default";
        },
    }
});



