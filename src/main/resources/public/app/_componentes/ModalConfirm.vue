<template>
    <modal-vik ref="modalConfirmAction"
               v-bind="configConfirmAction">
        <div slot="body">
            <table width="100%">
                <tr>
                    <td class="v-middle"><i class="fa fa-question-circle fa-4x text-primary"></i></td>
                    <td class="v-middle h4">
                        <div class="m-l" v-html="configConfirmAction.message"></div>
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
                configConfirmAction: VUE_MODAL.structConfirm({
                    message: "¿?"
                })
            };
        },
        methods: {
            open(configuration) {
                this.configConfirmAction = configuration;
                setTimeout(() => this.$refs.modalConfirmAction.open(), 100);
            },
            getModal() {
                return this.$refs.modalConfirmAction;
            }
        }
    };
</script>