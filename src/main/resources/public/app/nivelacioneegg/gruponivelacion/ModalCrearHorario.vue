<template>
    <modal-vik ref="modalCrearHorario"
               v-bind="modalCrearHorario"
               v-bind:okaction="saveHorario">
        <div slot="body">

            <h3 class="text-primary block m-b m-t">{{title}}</h3>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="row">
                        <div class="col-md-9">
                            <span class="item-form-control item-form-gray text-primary">
                                Grupo: {{grupo.codigo}}
                            </span>
                        </div>
                        <div class="col-md-3">
                            <span class="item-form-control item-form-gray text-primary">
                                Horas seleccionadas: {{horasSeleccionadas}}
                            </span>
                        </div>
                    </div>

                    <table class="table table-hover table-condensed table-bordered m-t-sm">
                        <thead>
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
                                         v-bind:style="{backgroundColor: getBgColor(dia,hora), color: getTextColor(dia,hora)}"
                                         class="pointer">
                                        {{getTextoCelda(dia,hora)}}
                                    </div>
                                    <div v-on:click.prevent="marcar(dia,hora)"
                                         v-bind:style="{backgroundColor: getBgColorOtroGrupo(dia,hora)}"
                                         class="pointer">
                                        {{getTextoOtroGrupo(dia,hora)}}
                                    </div>
                                    <div v-if="validarCelda(dia,hora)"
                                         v-on:click.prevent="marcar(dia,hora)"
                                         class="pointer">
                                        &nbsp;
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
                grupo: {id: null, codigo: ''},
                raptor: null,
                dias: [],
                horas: [],
                horarios: [],
                otrosHorarios: [],
                form: "id-form-crear-horario",
                title: "Configurar Horario",
                modalCrearHorario: VUE_MODAL.structFormAjax({
                    id: "id-modal-crear-horario",
                    okbtn: "Guardar Horario",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                }),
                mapBgColor: {
                    'A': '#FFD1DC', // Rosa Pastel
                    'B': '#FFDFD3', // Durazno Suave
                    'C': '#FFFFD1', // Amarillo Crema
                    'D': '#E2F0CB', // Verde Lima Suave
                    'E': '#B5EAD7', // Menta
                    'F': '#C7CEEA', // Azul Periwinkle
                    'G': '#E0BBE4', // Lavanda
                    'H': '#F3E5F5', // Lila muy claro
                    'I': '#FFF9C4', // Amarillo pálido
                    'J': '#C8E6C9', // Verde hoja suave
                    'K': '#B3E5FC', // Azul cielo claro
                    'L': '#D1C4E9', // Violeta suave
                    'M': '#FFCCBC', // Naranja rojizo tenue
                    'N': '#CFD8DC', // Azul grisáceo
                    'O': '#F0F4C3', // Lima pálido
                    'P': '#B2DFDB', // Verde azulado (Teal) claro
                    'Q': '#FFE0B2', // Naranja claro
                    'R': '#FFCDD2', // Rojo pálido
                    'S': '#F5F5DC', // Beige
                    'X': '#E0E0E0'  // Gris (ideal para casos 'extra' o cancelados)
                }
            };
        },

        mounted() {},
        computed: {
            horasSeleccionadas() {
                return this.horarios.length;
            }
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                if (form.parsley()) form.parsley().destroy();

                this.horarios = [];
                this.raptor = raptor;
                this.grupo = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalCrearHorario.open();

                this.loadDias();
                this.loadHoras();
                this.loadHorarios();
                this.loadHorariosOtrosGrupos();
            },

            loadDias() {
                if (this.dias.length > 0) return;
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allDias`
                })).then((resp) => this.dias = resp.data.data);
            },
            loadHoras() {
                if (this.horas.length > 0) return;
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allHoras`
                })).then((resp) => this.horas = resp.data.data);
            },
            loadHorarios() {
                const payload = {
                    id: this.grupo.id
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getHorarioGrupo`,
                    body: payload
                })).then((resp) => {
                    this.horarios = resp.data.data.map(h => ({
                        dia: h.dia,
                        hora: h.hora,
                        id: h.id 
                    }));
                });
            },
            loadHorariosOtrosGrupos() {
                const payload = {
                    id: this.grupo.id
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getHorarioOtrosGrupos`,
                    body: payload
                })).then((resp) => {
                    this.otrosHorarios = resp.data.data.map(h => ({
                        grupoNivelacion: h.grupoNivelacion,
                        dia: h.dia,
                        hora: h.hora,
                        id: h.id
                    }));
                });
            },

            getTextoCelda(dia, hora) {
                const existe = this.horarios.some(h => h.dia.id === dia.id && h.hora.id === hora.id);
                return existe ? this.grupo.codigo : "";
            },
            
            getBgColor(dia, hora) {
                const existe = this.horarios.some(h => h.dia.id === dia.id && h.hora.id === hora.id);
                return existe ? "#28a745" : "";
            },
            
            getTextColor(dia, hora) {
                const existe = this.horarios.some(h => h.dia.id === dia.id && h.hora.id === hora.id);
                return existe ? "#ffffff" : "";
            },

            marcar(dia, hora) {
                console.log("horarios",this.horarios)
                const index = this.horarios.findIndex(h => h.dia.id === dia.id && h.hora.id === hora.id);
                console.log("index",index)
                if (index >= 0) {
                    this.horarios.splice(index, 1);
                } else {
                    this.horarios.push({
                        dia: dia,
                        hora: hora
                    });
                }
                console.log("horarios",this.horarios)
            },

            getTextoOtroGrupo(dia, hora) {
                const existe = this.otrosHorarios.find(h => h.dia.id === dia.id && h.hora.id === hora.id);
                return existe ? existe.grupoNivelacion.codigo : "";
            },
            getBgColorOtroGrupo(dia, hora) {
                const existe = this.otrosHorarios.find(h => h.dia.id === dia.id && h.hora.id === hora.id);
                const letra = existe ? existe.grupoNivelacion.codigo : "X";
                return this.mapBgColor[letra];
            },

            validarCelda(dia, hora) {
                const coincide = h => h.dia.id === dia.id && h.hora.id === hora.id;
                return !(this.horarios.some(coincide) || this.otrosHorarios.some(coincide));
            },

            saveHorario() {
                const payload = {
                    id: this.grupo.id,
                    horarios: this.horarios
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveHorarioGrupo`,
                    modal: this.$refs.modalCrearHorario,
                    raptor: this.raptor,
                    body: payload
                }));
            }
        }
    };
</script>