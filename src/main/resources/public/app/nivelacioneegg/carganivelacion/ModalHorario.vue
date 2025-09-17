<template>
    <modal-vik ref="modalHorario"
               v-bind="modalHorario">
        <div slot="body">

            <h3 class="text-primary block m-b m-t text-center">{{title}} {{ciclo.descripcion}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>

                    <table class="table table-hover table-condensed table-bordered m-t-sm">
                        <thead>
                            <tr>
                                <th class="v-middle text-right">
                                    <a v-on:click.prevent="addSemana(-1)" class="pointer text-primary">
                                        <i class="fa fa-chevron-circle-left fa-2x" aria-hidden="true"></i>
                                    </a>
                                </th>
                                <th colspan="4" class="v-middle text-center">
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
                                <th colspan="2"></th>
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
                                    <div v-bind:style="{backgroundColor: getBgColor(dia,hora),color: getTextColor(dia,hora)}"
                                         class="pointer"
                                         v-html='getCursoDiaHora(dia,hora)'>
                                        CONTENIDO-CELDA
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
                title: "Horario de clases de nivelación",
                modalHorario: VUE_MODAL.structFormAjax({
                    id: "id-modal-add-horario",
                    okclass: "btn-primary",
                    modalsize: "modal-lg",
                    showaccept: false
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
        computed: {},

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
                this.$refs.modalHorario.open();
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

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allSemanas`
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
                    url: `/${rutaModulo}/getHorario`,
                    body: payload
                })).then((resp) => {
                    this.horarios = resp.data.data;
                });
            },

            addSemana(idx) {
                if (idx === -1 && this.idSemana === 0) {
                    //this.reloadSemana("antes", 0);
                    notify("No hay más semanas programadas", "warning");

                } else if (idx === 1 && this.idSemana === (this.semanas.length - 1)) {
                    notify("No hay más semanas programadas", "warning");
                    //this.reloadSemana("despues", 1);

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

                if (hdia && hdia.curso) {
                    let dato = "";
                    if (hdia.aula) {
                        dato += "Aula " + hdia.aula.codigo + "<br>";
                    }
                    dato += "Sección " + hdia.cursoNivelacion.codigo + "<br>";
                    dato += hdia.curso.codigo;
                    return dato;
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
                        return "text-success d-bold";
                    }
                    return "text-warning";
                }

                return "";
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
                return this.$refs.modalHorario;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>