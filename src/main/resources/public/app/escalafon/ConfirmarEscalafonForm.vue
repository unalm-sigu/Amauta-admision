<template>
    <modal-vik id="confirmEscalafonModal" ref="confirmEscalafonModal" v-bind:okaction="save">
        <template v-slot:body>
            <form id="form-validar-escalafon-confirm">
                <div class="form-group">
                    <label>Nota de Confirmación</label>
                    <textarea type="text" required="true" class="form-control" rows="2" v-model="escalafonConfirm.notaConfirm" ></textarea>
                </div> 
            </form>
        </template>
    </modal-vik>
</template>
<script>

    const ModalVik = httpVueLoader('/_vue/modules/ModalVik.vue');
    module.exports = {
        components: {ModalVik},
        data() {
            return{
                escalafonConfirm: {}
            };
        },
        computed: {
        },
        methods: {
            open(idInstancia, instTabla) {
                let $vue = this;
                $vue.escalafonConfirm = {instancia: idInstancia, tipo: instTabla};
                $vue.$refs.confirmEscalafonModal.open();
            },
            save() {
                let $vue = this;
                if (!$("#form-validar-escalafon-confirm").parsley().validate()) {
                    return;
                }
                axios.post("/escalafon/confirmarEscalafon", $vue.escalafonConfirm)
                        .then(function (response) {
                            if (response.data.success) {
                                notify(response.data.message, "success");
                                $vue.$refs.confirmEscalafonModal.close();
                                $vue.$parent.loadList();
                            } else {
                                notify(response.data.message, "warning");
                            }
                        })
                        .catch(function (error) {
                            notify(error.errorComunicacion, "error");
                        });
            }
        }
    };
</script>
