<template>
    <div class="block">
        <div v-if="esConsejero" class="col-md-10 m-t-lg">
            <h3 class="text-primary m-b-lg">Enviar informe a coordinación de tutoría</h3>

            <button v-if="esConsejero && ['PEN','OBS'].includes(informe.estado) "
                    v-on:click.prevent="enviarInforme" class="btn btn-danger">
                Enviar informe
            </button>
        </div>

        <div v-if="esCoordinador" class="col-md-6 m-t-lg">
            <form v-bind:id="form">
                <h3 class="text-primary m-b-lg">Aceptar u observar informe</h3>

                <div v-if="informe.observaciones" class="alert alert-danger">
                    <div class="h4 bold m-t-sm">Observación anterior</div>
                    <p>{{informe.observaciones}}</p>
                </div>

                <h4 class="bold">¿Está conforme con el informe?</h4>

                <div class="row">
                    <div class="col-sm-1"></div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label class="radio inline text-success">

                                <input  type="radio"
                                        required="true"
                                        name="aceptar-informe"
                                        v-on:click="verificarRpta"
                                        v-model="estado"
                                        id="inlineCheckbox1"
                                        value="ACP"
                                        /> Conforme
                            </label>

                        </div>
                    </div>

                    <div class="col-md-3 text-danger">
                        <div class="form-group">
                            <label class="radio inline">
                                <input  type="radio"
                                        required="true"
                                        name="aceptar-informe"
                                        v-on:click="verificarRpta"
                                        v-model="estado"
                                        id="inlineCheckbox2"
                                        value="OBS"
                                        /> No, observar
                            </label>

                        </div>
                    </div>
                </div>

                <div v-if="estado == 'OBS' " class="form-group">
                    <label>Observaciones del informe</label>
                    <textarea v-model="observaciones" class="form-control" required="yes" rows="4"></textarea>
                </div>

                <div class="pull-right m-b-sm m-t-sm">
                    <button v-if="['ACP','OBS'].includes(estado) "
                            v-on:click.prevent="aceptarInforme" class="btn btn-primary">
                        Enviar respuesta
                    </button>
                </div>
            </form>
        </div>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>
</template>

<script>
    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');

    module.exports = {

        components: {
            ModalConfirm, ModalInfo
        },

        props: {
            informe: {}
        },

        data() {
            return {
                estado: '',
                observaciones: '',
                esConsejero: esConsejero,
                esCoordinador: esCoordinador,
                form: "id-form-enviar-informe",
                idModalConfirm: "id-modal-confirm-enviar-informe"
            };
        },

        methods: {
            iniciar() {
                this.estado = 'AAA';
                if (this.esCoordinador) {
                    const form = $("#" + this.form);
                    form.parsley().reset();
                }
            },
            enviarInforme() {
                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea enviar el informe al coordinador de tutoría para su revisión? <br/><br/> <span class="text-danger">Una vez enviado, ya no podrá modificarlo.</span>`,
                    okbtn: "Si, enviar",
                    okclass: "btn-primary",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/enviarInforme`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.informe.id}
                        })).then(() => {
                            this.$parent.loadInforme();
                            this.$parent.choiseStep({id: 1});
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            aceptarInforme() {
                const form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                const aceptado = this.estado === 'ACP';
                const msg = aceptado ? 'aceptar' : 'observar';
                const btn = aceptado ? 'primary' : 'danger';

                const payload = {
                    id: this.informe.id,
                    estado: this.estado,
                    observaciones: this.observaciones
                };

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea ${msg} este informe?`,
                    okbtn: `Si, ${msg}`,
                    okclass: `btn-${btn}`,
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/aceptarInforme`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: payload
                        })).then(() => {
                            this.$parent.loadInforme();
                            this.$parent.choiseStep({id: 1});
                        });
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            validar() {
                return false;
            },
            verificarRpta() {

            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>