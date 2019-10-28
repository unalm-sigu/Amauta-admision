
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#soporteVue',
    data: {
        URL_SOPORTE: APP.url('academico/soporte/list'),
        modalSoporte: {
            id: 'modalSoporte',
            header: true,
            title: 'Responder Observacion',
            okbtn: "Guardar",
            showaccept: true
        },
        soporteForm: {}
    },
    mounted() {
    },
    methods: {
        modal(item) {
            let $vue = this;
            $vue.soporteForm = Object.assign(item, {});
            $vue.$refs.modalSoporte.open();
        },
        responder() {
            let $vue = this;

            var form = $("#frmSoporteModal");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/soporte/responder'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.soporteForm),
                success: function (response) {
                    if (response.success) {
                        $vue.soporteForm = {};
                        $vue.$refs.listSoporte.loadRemoteData();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalSoporte.close();
        }
    }
});
