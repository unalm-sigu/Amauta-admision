<template>
    <modal-vik ref="modalAddHorario"
               v-bind="modalAddHorario"
               v-bind:okaction="saveSetHorario">
        <div slot="body">

            <h3 class="text-primary block m-b m-t">{{title}} {{ciclo.descripcion}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row">
                        <div class="col-md-9">
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.cursoCiclo.curso.codigo}} -
                                {{cursoNiv.cursoCiclo.curso.nombre}}
                                <br>
                                {{cursoNiv.horasDictado}} horas dictado
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                Periodo del {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                            </span>
                        </div>

                        <div class="col-md-3">
                            <span class="item-form-control item-form-gray text-primary">
                                Grupo horario {{cursoNiv.grupoHoras.codigo}}
                                <br>
                                <span v-bind:class="classHoras()">{{horasTotales}} horas</span>
                            </span>
                        </div>
                    </div>

                    <table class="table table-hover table-condensed table-bordered m-t-sm">
                        <thead>
                            <tr>
                                <th class="v-middle text-right">
                                    <a v-on:click.prevent="addSemana(-1)" class="pointer text-primary">
                                        <i class="fa fa-chevron-circle-left fa-2x" aria-hidden="true"></i>
                                    </a>
                                </th>
                                <th colspan="3" class="v-middle text-center">
                                    <h4 v-if="semanaActiva && semanaActiva.fechaInicio"
                                        class="text-primary bold m-t-xs m-b-xs">
                                        Semana del {{ semanaActiva.fechaInicio }} al {{ semanaActiva.fechaFin }}
                                    </h4>
                                </th>
                                <th class="v-middle text-left">
                                    <a v-on:click.prevent="addSemana(1)" class="pointer text-primary">
                                        <i class="fa fa-chevron-circle-right fa-2x" aria-hidden="true"></i>
                                    </a>
                                </th>
                                <th colspan="3"></th>
                            </tr>
                            <tr>
                                <th class="v-middle text-center">Hora</th>
                                <th v-for="dia in dias" class="v-middle text-center wd-13">
                                    {{dia.simbolo}}
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="hora in horas">
                                <td class="v-middle text-center text-primary">
                                    {{hora.descripcion2}}
                                </td>
                                <td v-for="dia in dias" class="v-middle text-center">
                                    <div v-on:click.prevent="marcar(dia,hora)"
                                         v-bind:style="{backgroundColor: getBgColor(dia,hora),color: getTextColor(dia,hora)}"
                                         class="pointer">
                                        {{getCursoDiaHora(dia,hora)}}
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                </template>
            </form>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                visible: false,
                curso: null,
                cursoNiv: null,
                raptor: null,
                idSemana: 0,
                semanaActiva: null,
                dias: [],
                horas: [],
                semanas: [],
                horarios: [],
                ciclo: JSON.parse(cicloJson),
                form: "id-form-add-horario",
                title: "Configurar horario",
                modalAddHorario: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-horario",
                    okbtn: "Guardar configuración",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                }),
                coloresBg: [
                    "#f2f4f6", "#dff9fb", "#d1d8e0", "#ffcccc", "#fae3d9",
                    "#f3f4ed", "#ddf3f5", "#f2e9e4", "#fae8e0", "#f7d9d9",
                    "#ffedcc", "#fffbdf", "#e4f9f5", "#eaf6f6", "#ede7f6"
                ],
                coloresTexto: [
                    "#333333", "#1a1a1a", "#4d4d4d", "#5c5c5c", "#6f6f6f",
                    "#808080", "#999999", "#b3b3b3", "#666666", "#3a3a3a",
                    "#2e2e2e", "#1c1c1c", "#0f0f0f", "#000000", "#4a4a4a"
                ]
            };
        },

        mounted() {},
        computed: {
            horasTotales() {
                return this.horarios
                        .filter(hc => (hc.curso && hc.curso.id === this.curso.id))
                        .length;
            }
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.idSemana = 0;
                this.semanaActiva = null;
                this.semanas = [];
                this.horarios = [];
                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.curso = this.cursoNiv.cursoCiclo.curso;
                this.visible = true;
                this.$refs.modalAddHorario.open();
                myUtils.activarNumeric();

                this.loadDias();
                this.loadHoras();
                this.loadSemanas();
                this.loadHorarios();
            },

            loadDias() {
                if (this.dias.length > 0) {
                    return;
                }
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allDias`
                })).then((resp) => this.dias = resp.data.data);
            },
            loadHoras() {
                if (this.horas.length > 0) {
                    return;
                }
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allHoras`
                })).then((resp) => this.horas = resp.data.data);
            },
            loadSemanas() {
                const payload = {
                    id: this.cursoNiv.id
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allSemanas`,
                    body: payload
                })).then((resp) => {
                    this.semanas = resp.data.data;
                    this.semanaActiva = this.semanas[this.idSemana];
                });
            },
            loadHorarios() {
                const payload = {
                    id: this.cursoNiv.grupoHoras.id
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getHorarioGrupo`,
                    body: payload
                })).then((resp) => {
                    this.horarios = resp.data.data;
                });
            },
            reloadSemana(dir, incremento) {

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/addSemana/${dir}`,
                    body: this.semanas
                })).then((resp) => {
                    this.idSemana += incremento;
                    this.semanas = resp.data.data;
                    this.semanaActiva = this.semanas[this.idSemana];
                });
            },

            addSemana(idx) {
                if (idx === -1 && this.idSemana === 0) {
                    this.reloadSemana("antes", 0);

                } else if (idx === 1 && this.idSemana === (this.semanas.length - 1)) {
                    this.reloadSemana("despues", 1);

                } else {
                    this.idSemana += idx;
                    this.semanaActiva = this.semanas[this.idSemana];
                }
            },

            getCursoDiaHora(dia, hora) {
                if (!this.semanaActiva) {
                    return "...";
                }

                const hdia = this.horarios
                        .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                        .filter(hc => hc.dia.id === dia.id)
                        .find(hc => hc.hora.id === hora.id);

                if (hdia) {
                    return hdia.curso ? hdia.curso.codigo : "...";
                }

                return "...";
            },
            getClassDiaHora(dia, hora) {
                if (!this.semanaActiva) {
                    return "";
                }

                const hdia = this.horarios
                        .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                        .filter(hc => hc.dia.id === dia.id)
                        .find(hc => hc.hora.id === hora.id);

                if (hdia && hdia.curso) {
                    if (hdia.curso.id === this.curso.id) {
                        return "text-success bold";
                    }
                    return "text-warning";
                }

                return "";
            },
            marcar(dia, hora) {
                const horasCurso = this.horarios
                        .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                        .filter(hc => hc.curso && hc.curso.id === this.curso.id)
                        .filter(hc => hc.dia.id === dia.id)
                        .map(hc => hc.hora.numero);

                const hdia = this.horarios
                        .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                        .filter(hc => hc.dia.id === dia.id)
                        .find(hc => hc.hora.id === hora.id);

                if (hdia && hdia.curso) {
                    if (hdia.curso.id !== this.curso.id) {
                        notify("Esta hora está ocupada por otro curso", "error");
                        return;
                    }

                    const horasDia = this.horarios
                            .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                            .filter(hc => hc.curso && hc.curso.id === this.curso.id)
                            .filter(hc => hc.dia.id === dia.id)
                            .filter(hc => hc.hora.id !== hora.id)
                            .map(hc => hc.hora.numero);

                    const horasRestantes = horasDia.sort((a, b) => a - b);
                    for (let i = 1; i < horasRestantes.length; i++) {
                        if (horasRestantes[i] - horasRestantes[i - 1] !== 1) {
                            notify("Tienen que ser horas continuas", "error");
                            return;
                        }
                    }

                    hdia.curso = null;

                } else if (hdia && !hdia.curso) {
                    if (horasCurso.length > 0) {
                        if (!horasCurso.some(h => Math.abs(h - hora.numero) === 1)) {
                            notify("Tienen que ser horas continuas", "error");
                            return;
                        }
                    }
                    hdia.curso = this.curso;

                } else {
                    if (horasCurso.length > 0) {
                        if (!horasCurso.some(h => Math.abs(h - hora.numero) === 1)) {
                            notify("Tienen que ser horas continuas", "error");
                            return;
                        }
                    }

                    const hdiaNuevo = {
                        dia: dia,
                        hora: hora,
                        curso: this.curso,
                        semana: this.semanaActiva.fechaInicio
                    };
                    this.horarios.push(hdiaNuevo);
                }
            },

            saveSetHorario() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                if (this.horasTotales !== this.cursoNiv.horasDictado + 1) {
                    notify("Debe completar las horas de dictado y la hora del examen final", "error");
                    return;
                }

                const horariosCurso = this.horarios
                        .filter(hc => (hc.curso && hc.curso.id === this.curso.id));

                const payload = {
                    id: this.cursoNiv.cursoCiclo.id,
                    grupoHoras: {id: this.cursoNiv.grupoHoras.id},
                    horarios: horariosCurso
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/setHorario`,
                    modal: this.$refs.modalAddHorario,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            classHoras() {
                if (this.horasTotales === this.cursoNiv.horasDictado) {
                    return "text-warning bold";
                } else if (this.horasTotales === this.cursoNiv.horasDictado + 1) {
                    return "text-success bold";
                }
                return "text-danger";
            },
            getIdCurso(dia, hora) {
                if (!this.semanaActiva) {
                    return 900000;
                }

                const hdia = this.horarios
                        .filter(hc => hc.semana === this.semanaActiva.fechaInicio)
                        .filter(hc => hc.dia.id === dia.id)
                        .find(hc => hc.hora.id === hora.id);

                if (hdia && hdia.curso) {
                    return hdia.curso.id;
                }

                return 900000;
            },
            getBgColor(dia, hora) {
                const cursoId = this.getIdCurso(dia, hora);
                if (cursoId === this.curso.id) {
                    return "#28a745";
                }
                const index = cursoId % this.coloresBg.length;
                return this.coloresBg[index];
            },
            getTextColor(dia, hora) {
                const cursoId = this.getIdCurso(dia, hora);
                if (cursoId === this.curso.id) {
                    return "#ffffff";
                }
                const index = cursoId % this.coloresTexto.length;
                return this.coloresTexto[index];
            },

            getModal() {
                return this.$refs.modalAddHorario;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>