Vue.component('multiselect', {mixins: [window.VueMultiselect.default]});
new Vue({
    el: '#personaCtaBancoVUE',
    data: {
        persona: JSON.parse(personaJson),
        bancos: JSON.parse(bancos),
        cuentasBancarias: JSON.parse(cuentasBancarias),
        ctaBanco: {},
        modalAddCtaBanco: VUE_MODAL.structFormAjax({
            id: "id-modal-add-cta-banco",
            header: true,
            title: 'Agregar cuenta bancaria',
            okbtn: 'Agregar',
            okclass: "btn-danger"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "id-modal-confirm"
        }),
        itemSelect: {}
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        verAddCtaBanco() {
            let $vue = this;
            $vue.ctaBanco = {};
            $vue.$refs.modalAddCtaBanco.open();
        },
        saveCtaBanco() {
            let $vue = this;
            $vue.ctaBanco.persona = $vue.persona;

            $vue.$refs.modalAddCtaBanco.beginProcessing();
            axios.post(APP.url(`${rutaModulo}/saveCtaBanco`), $vue.ctaBanco).then(response => {
                $vue.$refs.modalAddCtaBanco.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadCtasBancarias();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalAddCtaBanco.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        reloadCtasBancarias() {
            let $vue = this;
            axios.post(APP.url(`${rutaModulo}/${$vue.persona.id}/allCuentasBancarias`)).then(response => {
                if (response.data.success) {
                    $vue.cuentasBancarias = response.data.data;
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        verEliminar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea eliminar esta cuenta bancaria?";
            $vue.configConfirmAction.okbtn = "Si, eliminar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = $vue.eliminar;
            $vue.$refs.modalConfirmAction.open();
        },
        verActivar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea activar esta cuenta bancaria como la principal?";
            $vue.configConfirmAction.okbtn = "Si, activar";
            $vue.configConfirmAction.okclass = "btn-primary";
            $vue.configConfirmAction.okaction = $vue.activar;
            $vue.$refs.modalConfirmAction.open();
        },
        eliminar() {
            let $vue = this;

            axios.post(APP.url(`${rutaModulo}/deleteCtaBanco`), $vue.itemSelect).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadCtasBancarias();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        activar() {
            let $vue = this;
            axios.post(APP.url(`${rutaModulo}/activarCtaBanco`), $vue.itemSelect).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadCtasBancarias();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        }
    }
});
