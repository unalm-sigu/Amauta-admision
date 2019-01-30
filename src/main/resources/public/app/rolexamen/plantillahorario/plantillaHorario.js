Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/plantillahorario'),
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: {
            semanasExamen: []
        },
        paginationGpo: {'total-items': 0, 'items-per-page': 30, 'max-size': 3, 'boundary-link-numbers': true},
        semanasExamen: [],
        semanaExamenActiva: null,
        grupoActivo: null,
    },
    computed: {
        generarDisponible() {
            return this.rolExamen && this.rolExamen.isEstadoCreado && this.rolExamen.isSituacionConfigurarRol;
        },
        modificarHorarioDisponible() {
            return this.rolExamen && this.rolExamen.isEstadoCreado && this.rolExamen.isSituacionConfigurarHorario;
        }
    },
    mounted() {
        if (jRolExamenes != null) {
            this.rolExamen = JSON.parse(jRolExamenes);
            this.changeRolExamen();
        }

    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        }, changeRolExamen() {
            AXIOS.post(`${this.URL}/changeRolExamen`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            this.rolExamen = response.data.data;
                            this.semanasExamen = [];
                            this.semanaExamenActiva = null;
                            this.grupoActivo = null;
                            this.listarGruposExamenByRolExamen();
                            this.listarHorarioSemanal();
                        }
                    });
        }, /*changeSemanaExamen() {
         AXIOS.post(`${this.URL}/changeSemanaExamen`, this.semanaExamen)
         .then(response => {
         if (response.data.success) {
         
         }
         // MODAL.hideWait();
         });
         },*/ calcularPlantillaHorario() {
            if (!this.generarDisponible) {
                return;
            }
            let vue = this;
            if (this.hasGruposHorasExamen()) {
                bootbox.confirm({
                    message: "Si continua se perdera el avance de su plantilla de horarios. Seguro que desea continuar?",
                    buttons: {
                        confirm: {label: 'Si', className: "btn-warning"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            MODAL.showWait("Espere un momento por favor");
                            AXIOS.post(`${vue.URL}/calcularPlantillaHorario`, vue.rolExamen)
                                    .then(response => {
                                        if (response.data.success) {
                                            vue.listarGruposExamenByRolExamen();
                                            vue.listarHorarioSemanal();
                                        }
                                        MODAL.hideWait();
                                    });
                        }
                    }
                });
            } else {
                MODAL.showWait("Espere un momento por favor");
                AXIOS.post(`${vue.URL}/calcularPlantillaHorario`, vue.rolExamen)
                        .then(response => {
                            if (response.data.success) {
                                vue.listarGruposExamenByRolExamen();
                                vue.listarHorarioSemanal();
                            }
                            MODAL.hideWait();
                        });
            }


        }, listarGruposExamenByRolExamen() {
            this.$refs.raptorGrupo.ajaxdata = {rolExamenes: this.rolExamen.id};
            this.$refs.raptorGrupo.loadRemoteData();

            /*
             AXIOS.post(`${this.URL}/listarGruposExamenByRolExamen`, this.semanaExamen)
             .then(response => {
             if (response.data.success) {
             response.data.data;
             }
             // MODAL.hideWait();
             });*/
        }, listarHorarioSemanal() {
            AXIOS.post(`${this.URL}/listarHorarioSemanal`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            this.semanasExamen = response.data.data;
                            if (this.semanaExamenActiva != null) {
                                this.seleccionarSemana(this.semanaExamenActiva);
                            } else {
                                this.seleccionarSemana(this.semanasExamen[0]);
                            }
                        }
                        // MODAL.hideWait();
                    });
        }, styleBorder(item) {
            let $vue = this;
            if ($vue.grupoActivo != null && item.id == $vue.grupoActivo.id) {
                return "background-color:gray;";
            }
            return "border-color:" + item.grupoHoras.color + ";";
        }, classHoras(item) {
            if (item.fechasHorasGruposExamen.length == 0 || item.fechasHorasGruposExamen.length != 2) {
                return "label-danger";
            }
            return "label-success";
        }, seleccionarHorario(grupoHora, event) {
            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
            event.preventDefault();
            this.grupoActivo = grupoHora;

            if (grupoHora.semanaExamen != null && grupoHora.semanaExamen.id != null) {
                this.semanaExamenActiva = grupoHora.semanaExamen;
                $("#semana" + this.semanaExamenActiva.numeroSemana).click();
            }

            /*  var self = $(e.currentTarget);
             var id = self.attr("rel");
             this.toggleActivo(self);
             Grupo.grupoActivo = id;
             
             this.getHorario(id);*/
            mibox.modal('hide');
        }, seleccionarSemana(semana) {
            let vue = this;
            this.semanasExamen.forEach(function (x) {
                if (x.id == semana.id) {
                    x.selected = true;
                    vue.semanaExamenActiva = x;
                } else {
                    x.selected = false;
                }
            });
        }, selectFechaHoraGrupo(dia, hora, semExamen) {
            if (!this.modificarHorarioDisponible) {
                return;
            }
            if (this.grupoActivo == null) {
                notify("Seleccione un grupo horas.", "error");
                return;
            }

            let fechaHoraGrupoExamen = semExamen.tblHorarioSeamanaExamen.fechasHorasGrupos[dia.id + '_' + hora.id];

            let fechaHoraGrupo = {
                grupoHorasExamen: this.grupoActivo,
                semanaExamen: {id: this.semanaExamenActiva.id},
                hora: hora,
                dia: dia
            };
            if (fechaHoraGrupoExamen == null) {
                if (this.grupoActivo.fechasHorasGruposExamen.length == 0) {
                    AXIOS.post(`${this.URL}/agregarFechaHoraGrupoExamen`, fechaHoraGrupo)
                            .then(response => {
                                if (response.data.success) {
                                    this.grupoActivo = response.data.data;
                                    this.actualizarGrupos(this.grupoActivo);
                                }
                            });
                } else {
                    this.grupoActivo.fechasHorasGruposExamen;
                    let diaSaved = this.grupoActivo.fechasHorasGruposExamen[0].dia;
                    if (dia.id != diaSaved.id) {
                        notify("Programe la hora en el mismo día.", "error");
                        return;
                    }
                    let minHoraSaved = 10000;
                    let maxHoraSaved = 0;
                    this.grupoActivo.fechasHorasGruposExamen.forEach(function (x) {
                        if (x.hora.numero > maxHoraSaved) {
                            maxHoraSaved = x.hora.numero;
                        }
                        if (x.hora.numero < minHoraSaved) {
                            minHoraSaved = x.hora.numero;
                        }
                    });
                    maxHoraSaved++;
                    minHoraSaved--;
                    if (hora.numero != minHoraSaved && hora.numero != maxHoraSaved) {
                        notify("Debe seleccionar horas consecutivas.", "error");
                        return;
                    }
                    AXIOS.post(`${this.URL}/agregarFechaHoraGrupoExamen`, fechaHoraGrupo)
                            .then(response => {
                                if (response.data.success) {
                                    this.grupoActivo = response.data.data;
                                    this.actualizarGrupos(this.grupoActivo);
                                }
                            });
                }
            } else {
                if (this.grupoActivo.id != fechaHoraGrupoExamen.grupoHorasExamen.id) {
                    return;
                }
                AXIOS.post(`${this.URL}/deleteFechaHoraGrupoExamen`, fechaHoraGrupoExamen)
                        .then(response => {
                            if (response.data.success) {
                                this.grupoActivo = response.data.data;
                                this.actualizarGrupos(this.grupoActivo);
                            }
                        });
            }
        }, actualizarGrupos(grupoActivo) {
            for (var j = 0; j < this.$refs.raptorGrupo.data.length; j++) {
                if (this.$refs.raptorGrupo.data[j].id == grupoActivo.id) {
                    this.$refs.raptorGrupo.data[j] = grupoActivo;
                }
            }
            this.listarHorarioSemanal();
        }, styleHdia(dia, hora) {
            let $vue = this;
            for (var i = 0; i < $vue.horarioGpo.length; i++) {
                if ($vue.horarioGpo[i].hora.id == hora.id && $vue.horarioGpo[i].dia.id == dia.id) {
                    return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
                }
            }
            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        }, fechaGrupoHoraItem(fechaGrupoHora) {
            if (this.grupoActivo != null && fechaGrupoHora.grupoHorasExamen.id == this.grupoActivo.id) {
                return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
            }

            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        }, hasGruposHorasExamen() {
            if (this.$refs.raptorGrupo == null) {
                return false;
            }
            if (this.$refs.raptorGrupo.data.length > 0) {
                return true;
            }
            return false;
        }
    }
});
