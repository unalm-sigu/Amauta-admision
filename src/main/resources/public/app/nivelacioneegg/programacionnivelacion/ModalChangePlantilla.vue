<template>
    <modal-vik ref="modalChangePlantilla"
               v-bind="modalChangePlantilla"
               v-bind:okaction="saveChangePlantilla">
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

                    <div class="row m-t-md">
                        <div class="col-md-4">
                            <label>Horas dictado</label>
                            <input v-if="horasEditables" v-model="cursoNiv.horasDictado" type="text" class="form-control numeric" required="yes" maxlength="3"/>
                            <span v-else="" class="item-form-control item-form-gray text-primary">
                                {{cursoNiv.horasDictado}}
                            </span>
                        </div>

                        <div class="col-md-4">
                            <div class="form-group">
                                <label>Plantilla</label>
                                <multiselect v-model="cursoNiv.plantilla"
                                             v-bind:options="plantillas"
                                             v-bind:allow-empty="false"
                                             v-on:input="selectPlantilla"
                                             track-by="id"
                                             placeholder="Seleccione una plantilla"
                                             v-bind:showNoOptions="true"
                                             v-bind:show-labels="false">

                                    <template slot="singleLabel" slot-scope="props">
                                        <span class="text-primary h4">{{ props.option.codigo }}</span>
                                    </template>

                                    <template slot="option" slot-scope="props">
                                        <span class="block bold">{{ props.option.codigo }} </span>
                                    </template>

                                    <template slot="noOptions">Lista vacía</template>
                                    <template slot="noResult">Sin resultados</template>

                                </multiselect>
                                <input v-bind:value="getObjectId(cursoNiv.plantilla)" required="true" type="text" class="hide"/>
                            </div>
                        </div>

                        <div class="col-md-4"
                             v-if="cursoNiv.plantilla">
                            <label>Horario configurado</label>
                            <template v-if="horarios.length == 0">
                                <span class="item-form-control item-form-gray text-danger">
                                    Falta configurar
                                </span>
                            </template>
                            <template v-else="">
                                <span class="item-form-control item-form-gray text-primary">
                                    {{horarios.length}} horas
                                </span>
                            </template>
                        </div>

                    </div>

                    <div class="row">
                        <div class="col-md-12">
                            <div class="form-group">
                                <label>Indique el motivo del cambio</label>
                                <textarea v-model="cursoNiv.motivoCambio" class="form-control" rows="3"></textarea>
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
                horarios: [],
                plantillas: JSON.parse(plantillasJson),
                ciclo: JSON.parse(cicloJson),
                form: "id-form-change-plantilla",
                title: "Cambio de plantilla",
                modalChangePlantilla: VUE_MODAL.structFormAjax({
                    id: "id-modal-change-plantilla",
                    okbtn: "Guardar cambio",
                    okclass: "btn-primary",
                    modalsize: "modal-lg"
                })
            };
        },

        mounted() {},
        computed: {
        },

        created() {
            this.searchAulaDebounce = debounce(this.searchAula, 800);
        },

        methods: {
            open(item, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.horarios = [];

                this.raptor = raptor;
                this.cursoNiv = JSON.parse(JSON.stringify(item));
                this.visible = true;
                this.$refs.modalChangePlantilla.open();

                myUtils.activarNumeric();
                this.selectPlantilla(this.cursoNiv.plantilla);
            },

            selectPlantilla(item) {
                let payload = {
                    cursoCiclo: {
                        curso: {id: this.cursoNiv.cursoCiclo.curso.id}
                    },
                    plantilla: {id: item.id}
                };

                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/getHorario`,
                    body: payload
                })).then((resp) => {
                    let data = resp.data.data;
                    this.horarios = data.horario;

                    let periodo = data.periodo;
                    if (periodo.fechaReferencia) {
                        this.cursoNiv.fechaReferencia = periodo.fechaReferencia;
                        this.cursoNiv.fechaInicio = periodo.fechaInicio;
                        this.cursoNiv.fechaFin = periodo.fechaFin;
                    }
                    myUtils.activarNumeric();
                });
            },

            saveChangePlantilla() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    notify("Debe completar los campos obligatorios", "error");
                    return;
                }

                const payload = {
                    id: this.cursoNiv.id,
                    plantilla: {id: this.cursoNiv.plantilla.id},
                    motivoCambio: this.cursoNiv.motivoCambio
                };

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/changePlantilla`,
                    modal: this.$refs.modalChangePlantilla,
                    raptor: this.raptor,
                    body: payload
                }));
            },

            getModal() {
                return this.$refs.modalChangePlantilla;
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>