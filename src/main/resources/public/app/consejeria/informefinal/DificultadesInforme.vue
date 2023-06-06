<template>
    <div>
        <div class="col-md-6">
            <form v-bind:id="form">
                <h3 class="text-primary m-b-lg">Dificultades de la tutoría en el ciclo</h3>

                <template v-if="esConsejero && ['PEN','OBS'].includes(informe.estado) ">
                    <textarea v-model="dificultades" 
                              v-bind:id="textarea"
                              v-on:keyup="resize(4,textarea)" class="form-control" required="yes" rows="4"></textarea>
                </template>
                <template v-else="">
                    <div class="item-form-control item-form-gray text-primary">{{informe.dificultades}}</div>
                </template>

                <div class="pull-right m-b-sm m-t-sm">
                    <button v-if="esConsejero && ['PEN','OBS'].includes(informe.estado) "
                            v-on:click.prevent="save" class="btn btn-primary">
                        Guardar información
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
                dificultades: '',
                esConsejero: esConsejero,
                textarea: "id-textarea-dificultades-informe",
                form: "id-form-dificultades-informe",
                idModalConfirm: "id-modal-confirm-dificultades-informe"
            };
        },
        methods: {
            iniciar() {
                this.dificultades = this.informe.dificultades;
                setTimeout(() => this.resize(4, this.textarea), 200);
            },
            save() {
                let form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return;
                }

                let config = VUE_MODAL.structConfirm({
                    id: this.idModalConfirm,
                    message: `¿Seguro que desea guardar las <b>Dificultades</b> del informe?`,
                    okbtn: "Si, guardar",
                    okclass: "btn-success",
                    okaction: () => {
                        myUtils.axios(VUE_AXIOS.structModalClose({
                            url: `/${rutaModulo}/dificultadesInforme`,
                            modal: this.$refs.modalConfirm.getModal(),
                            body: {id: this.informe.id, dificultades: this.dificultades}
                        })).then(() => this.$parent.loadInforme());
                    }
                });

                this.$refs.modalConfirm.open(config);
            },
            validar() {
                const form = $("#" + this.form);
                if (!form.parsley().validate()) {
                    return false;
                }
                const ok = this.dificultades == this.informe.dificultades;
                if (!ok) {
                    notify("Tiene que guardar la información modificada", "error");
                }
                return ok;
            },
            resize(min, id) {
                const texta = $("#" + id);
                texta.prop("rows", min);

                const scroll = texta.prop("scrollHeight");
                const rows = Math.ceil(scroll / 23);

                let rowsFinal = rows;
                if (rows < min) {
                    rowsFinal = min;
                }

                texta.prop("rows", rowsFinal);
            },

            // metodos genericos
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };
</script>