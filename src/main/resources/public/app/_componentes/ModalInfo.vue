<template>
    <modal-vik ref="modalInfo"
               v-bind="config">
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
    </modal-vik>
</template>

<script>
    module.exports = {

        data() {
            return {
                config: VUE_MODAL.structInfo({
                    message: "¿?"
                })
            };
        },
        methods: {
            open(config) {
                this.config = config;
                setTimeout(() => this.$refs.modalInfo.open(), 100);
            },
            getModal() {
                return this.$refs.modalInfo;
            }
        }
    };
</script>