new Vue({
    el: '#soporteVue',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_SOPORTE: APP.url('academico/soporte/list'),
        soporteForm: {},
        generandoReporte: false
    },
    mounted() {
    },
    methods: {
        modal(item) {
            let $vue = this;
            $vue.soporteForm = {...item};
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
            axios_.post(APP.url('academico/soporte/responder'), $vue.soporteForm)
                    .then(({data}) => {
                        $vue.$refs.listSoporte.loadRemoteData();
                        notify(data, "success");
                        $vue.$refs.modalSoporte.close();
                    }, () => {
                        $vue.$refs.modalSoporte.stop();
                    });
        },
        generarReporte() {
            let $vue = this;
            $vue.generandoReporte = true;
            axios_blob.get(APP.url('academico/soporte/reporte'))
                    .then(response => {
                        UTIL_BLOB.save(response);
                        $vue.generandoReporte = false;
                    }, () => {
                        $vue.generandoReporte = false;
                        notify(Messages.errorComunicacion, 'error')
                    });
        }
    }
});
