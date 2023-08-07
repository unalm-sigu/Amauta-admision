<template>
    <modal-vik ref="modalEncuesta"
               v-bind="modalEncuesta">
        <div slot="body">

            <h4 class="text-primary block">{{title}}</h4>

            <template v-if='visible'>
                <div class="block m-b">
                    <strong>Encuestados:</strong> {{encuestados}} &nbsp;&nbsp;&nbsp;
                    <strong>Desde:</strong> {{desde}} &nbsp;&nbsp;&nbsp;
                    <strong>hasta:</strong> {{hasta}}
                </div>

                <table class="table table-hover table-striped">
                    <thead>
                        <tr>
                            <th></th>
                            <th v-for="opc in opciones"
                                class="text-center v-middle wd-12">{{opc.contenido}}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="pgta in preguntas">
                            <td class="v-middle">{{pgta.texto}}</td>
                            <td v-for="opc in opciones" class="text-center">
                                <div v-bind:class="classPorcentaje(getRespuesta(pgta,opc.letra))">
                                    {{getRespuesta(pgta,opc.letra).toFixed(4)}}%
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>

            </template>
        </div>
    </modal-vik>
</template>

<script>

    module.exports = {

        data() {
            return {
                visible: false,
                consejero: null,
                encuesta: null,
                encuestados: 0,
                desde: "",
                hasta: "",
                preguntas: [],
                opciones: [],
                respuestas: [],
                title: "Encuesta de Satisfacción de Tutoría",
                modalEncuesta: VUE_MODAL.structInfo({
                    id: "id-modal-encuesta-tutor",
                    modalsize: "modal-lg"
                })
            };
        },
        methods: {
            open(consejero) {
                this.consejero = consejero;
                this.loadPreguntas();

                this.visible = true;
                this.$refs.modalEncuesta.open();
            },
            loadPreguntas() {
                this.encuestados = 0;
                this.desde = "";
                this.hasta = "";

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/allDataEncu`
                })).then((resp) => {
                    this.encuesta = resp.data.data.encuesta;
                    this.preguntas = resp.data.data.preguntas;
                    this.opciones = resp.data.data.opciones;
                    this.respuestas = resp.data.data.respuestas;
                    if (this.respuestas.length > 0) {
                        this.encuestados = this.respuestas[0].encuestados;
                        this.desde = this.respuestas[0].desde;
                        this.hasta = this.respuestas[0].hasta;
                    }
                });
            },
            getRespuesta(pgta, letra) {
                let rpta = this.respuestas.find(x => x.pregunta.id === pgta.id);
                let porc = rpta.puntajes.find(x => letra in x);
                return porc[letra];
            },
            classPorcentaje(num) {
                if (num == 0) {
                    return "";
                }
                return "text-primary bold";
            },
            getModal() {
                return this.$refs.modalEncuesta;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>