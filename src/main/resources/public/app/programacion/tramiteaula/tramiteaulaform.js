Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

new Vue({
    el: '#main',
    data: {
        tramite: {alumno:{},empresa:{},docente:{}},
        urlfilter: APP.url("tramite/aula/filteraula"),
        institucion:{pais:{}},
        dataInstitucionModal: {
            id: 'idInstitucionModal',
            header: true,
            title: 'Agregar Institución',
            okbtn: 'Agregar Institución'
        },
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        changeSoloFecha() {
            let $vue = this;
        },
        changeRangoFecha() {
            let $vue = this;
        },
        addInstitucion() {
            let $vue = this;
            $vue.institucion={pais:{}};
            $vue.$refs.nuevaInstitucionModal.open();
        },
        saveInstitucionModal() {
            let $vue = this;
            let miform=$($vue.$refs.formInstitucionModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $.ajax({
                url: APP.url('tramite/aula/saveInstitucion'),
                type: 'POST',
                async: false,
                data: miform.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.tramite.empresa=response.data;
                        $vue.$refs.nuevaInstitucionModal.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});
