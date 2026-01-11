<template>
    <modal-vik ref="modalReabrirNotas"
               v-bind="modalReabrirNotas"
               v-bind:okaction="saveReabrir">
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
                                Sección: {{cursoNiv.codigo}}
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                {{cursoNiv.horasDictado}} horas
                                &nbsp;&nbsp; | &nbsp;&nbsp;
                                Del {{cursoNiv.fechaInicio}} al {{cursoNiv.fechaFin}}
                            </span>
                        </div>

                        <div class="col-md-3">
                            <span class="item-form-control item-form-gray text-primary">
                                Plantilla
                                <br>
                                {{cursoNiv.plantilla.codigo}}
                            </span>
                        </div>
                    </div>

                    <div class="row m-t-md m-b">
                        <div class="col-md-9">
                            <label>Docente</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.docente.persona.apellidosNombres}}
                            </span>
                        </div>

                        <div class="col-md-3">
                            <label>Fecha entregó notas</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.fechaEntregaNotas}}
                            </span>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Indique el motivo para reabrir el acta de notas</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" rows="3" required="yes"></textarea>
                            </div>
                        </div>
                    </div>


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
                docentes: [],
                ciclo: JSON.parse(cicloJson),
                form: "id-form-reabrir-notas",
                title: "Reabrir Acta de Notas",
                modalReabrirNotas: VUE_MODAL.structFormAjax({
                    id: "id-modal-reabrir-notas",
                    okbtn: "Reabrir acta de notas",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                })
            };
        },

        mounted() {},
        computed: {},
        created() {},

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalReabrirNotas.open();
                myUtils.activarNumeric();
            },

            saveReabrir() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                const payload = {
                    id: this.cursoNiv.id,
                    motivoCambio: this.cursoNiv.motivoCambio
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/reabrirNotas`,
                    modal: this.$refs.modalReabrirNotas,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalReabrirNotas;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>