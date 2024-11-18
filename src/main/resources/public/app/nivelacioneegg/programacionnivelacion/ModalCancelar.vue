<template>
    <modal-vik ref="modalCancelar"
               v-bind="modalCancelar"
               v-bind:okaction="saveCancelar">
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
                                Grupo horario
                                <br>
                                {{cursoNiv.grupoHoras.codigo}}
                            </span>
                        </div>
                    </div>

                    <div class="row m-t-md">
                        <div class="col-md-4">
                            <label>Vacantes</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.vacantes}}
                            </span>
                        </div>
                        <div class="col-md-4">
                            <label>Matriculados</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.matriculados}}
                            </span>
                        </div>
                        <div class="col-md-4">
                            <label>Disponibles</label>
                            <span class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.disponibles}}
                            </span>
                        </div>
                    </div>

                    <div class="row m-t">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Indique el motivo para cancelar esta sección</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" required="yes" rows="3"></textarea>
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
                aulas: [],
                hayCruceAula: false,
                mensajeCruceAula: "",
                ciclo: JSON.parse(cicloJson),
                form: "id-form-cancelar-seccion",
                title: "Cancelar Sección",
                modalCancelar: VUE_MODAL.structFormAjax({
                    id: "id-modal-cancelar-seccion",
                    okbtn: "Guardar cambio",
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

                this.hayCruceAula = false;
                this.mensajeCruceAula = null;

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalCancelar.open();
                myUtils.activarNumeric();
            },

            saveCancelar() {
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
                    url: `/${rutaModulo}/changeEstado/CAN`,
                    modal: this.$refs.modalCancelar,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalCancelar;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>