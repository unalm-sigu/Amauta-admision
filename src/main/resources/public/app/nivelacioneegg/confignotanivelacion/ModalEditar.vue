<template>
    <modal-vik ref="modalEditar"
               v-bind="modalEditar"
               v-bind:okaction="saveConfig">
        <div slot="body">

            <h4 class="text-primary block m-b-lg">{{title}}</h4>

            <form v-bind:id="form">
                <template v-if='visible'>
                    <div class="form-group">
                        <span class="item-form-control item-form-gray text-primary">
                            <template v-if="configNueva.otrasModalidades">
                                Otras modalidades de ingreso
                            </template>
                            <template v-else="">
                                {{configNueva.modalidadIngreso.nombre}}
                            </template>
                        </span>
                    </div>

                    <div class="form-group">
                        <label>Tema del examen de admisión</label>
                        <span class="item-form-control item-form-gray text-primary">
                            {{configNueva.temaExamen.nombre}}
                        </span>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Puntajes mínimo/máximo</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    <template v-if="configNueva.otrasModalidades">
                                        {{puntaje(configNueva.temaCiclo.puntajeMinimo)}} /
                                        {{puntaje(configNueva.temaCiclo.puntajeMaximo)}}
                                    </template>
                                    <template v-else="">
                                        {{puntaje(configNueva.temaCiclo.puntajeCepreMinimo)}} /
                                        {{puntaje(configNueva.temaCiclo.puntajeCepreMaximo)}}
                                    </template>
                                </span>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Notas mínima/máxima</label>
                                <span class="item-form-control item-form-gray text-primary">
                                    <template v-if="configNueva.otrasModalidades">
                                        {{puntaje(configNueva.temaCiclo.notaMinima)}} /
                                        {{puntaje(configNueva.temaCiclo.notaMaxima)}}
                                    </template>
                                    <template v-else="">
                                        {{puntaje(configNueva.temaCiclo.notaCepreMinimo)}} /
                                        {{puntaje(configNueva.temaCiclo.notaCepreMaximo)}}
                                    </template>
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Cantidad de preguntas</label>
                                <div class="item-form-control item-form-gray text-primary">
                                    {{configNueva.temaCiclo.preguntas}}
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>Nota mínima aprobatoria</label>
                                <input v-model="configNueva.notaMinima" v-numeric-only="4" class="form-control" type="text" required=""/>
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
                configNueva: null,
                raptor: null,
                visible: false,
                form: "id-form-editar-configuracion-nota-aprobatoria",
                title: "Modificar nota mínima aprobatoria",
                modalEditar: VUE_MODAL.structFormAjax({
                    id: "id-modal-configuracion-nota",
                    okbtn: "Modificar nota aprobatoria",
                    okclass: "btn-primary"
                })
            };
        },
        methods: {
            open(config, raptor) {
                var form = $("#" + this.form);
                form.parsley().destroy();

                this.raptor = raptor;
                this.configNueva = JSON.parse(JSON.stringify(config));
                this.visible = true;
                this.$refs.modalEditar.open();
            },
            saveConfig() {
                var form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${rutaModulo}/saveConfig`,
                    modal: this.$refs.modalEditar,
                    raptor: this.raptor,
                    body: this.configNueva
                }));
            },
            puntaje(nota) {
                if (nota) {
                    return myUtils.commas(nota);
                }
                return "";
            },

            getModal() {
                return this.$refs.modalEditar;
            },

            // metodos genericos
            getListIds(list) {
                return list.map(item => item.id).join(',');
            },
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>