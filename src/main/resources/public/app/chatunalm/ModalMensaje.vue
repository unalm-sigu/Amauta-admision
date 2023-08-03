<template>
    <modal-base ref="modalMensajeBase"
                v-bind="config"
                v-bind:okaction="marcarMensaje">
        <div slot="body">
            <table width="100%">
                <tr>
                    <td class="v-middle"><i class="fa fa-info-circle fa-4x text-primary"></i></td>
                    <td class="v-middle h4">
                        <div class="m-l" v-html="config.message"></div>
                    </td>
                </tr>
            </table>
        </div>
    </modal-base>
</template>

<script>

    const ModalBase = httpVueLoader('/app/_componentes/ModalBase.vue');

    module.exports = {

        components: {
            ModalBase
        },

        data() {
            return {
                config: VUE_MODAL.structFormAjax({message: "¿?"}),
                rutaModulo: null,
                item: null,
                asuntos: [],
                index: 0
            };
        },
        methods: {
            open(config, item, asuntos, index, rutaModulo) {
                //let struct = VUE_MODAL.structFormAjax(config);
                this.item = item;
                this.index = index;
                this.asuntos = asuntos;
                this.rutaModulo = rutaModulo;
                this.config = VUE_MODAL.structFormAjax(config);
                setTimeout(() => this.$refs.modalMensajeBase.open(), 100);
            },
            marcarMensaje() {
                myUtils.axios(VUE_AXIOS.structModalClose({
                    url: `/${this.rutaModulo}/marcarMensaje`,
                    body: {id: this.item.mensajePrincipal.id},
                    modal: this.$refs.modalMensajeBase
                })).then(() => {

                    mySounds.playAudio('SINGLE');
                    if (this.item.mensajes.length == 1) {
                        this.asuntos.splice(this.index, 1);

                    } else {
                        this.item.mensajes.splice(0, 1);
                        this.item.mensajePrincipal = this.item.mensajes[0];
                    }
                });
            },
            getModal() {
                return this.$refs.modalInfo;
            }
        }
    };
</script>