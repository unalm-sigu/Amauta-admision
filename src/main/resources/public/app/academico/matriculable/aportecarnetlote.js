Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        ciclo: JSON.parse(cicloJson),
    },
    mounted: function () {

    },
    methods: {
        generar() {
            let $vue = this;
            $vue.showLoader();
            axios.post("/academico/matriculable/executeaportecarnetlote")
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, "info");
                        } else {
                            notify(Messages.errorComunicacion, "error");
                        }
                        $vue.hideLoader();
                    }).catch(e => {
                $vue.hideLoader();
                notify(Messages.errorComunicacion, "error");
            });
        },
        eliminar() {
            let $vue = this;
            $vue.showLoader();
            axios.post("/academico/matriculable/eliminaraportecarnetlote")
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                        $vue.hideLoader();
                    }).catch(e => {
                $vue.hideLoader();
                notify(Messages.errorComunicacion, "error");
            });
        }
    }
});

