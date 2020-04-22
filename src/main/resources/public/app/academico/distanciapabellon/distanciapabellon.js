new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        urlpabellon: APP.url('academico/distanciapabellon/list'),
        modalNuevaDistanciaPabellon: {
            id: 'idModalNuevaDistanciaPabellon',
            header: true,
            title: 'Distancia Pabellón'
        },
        distancias: [],
        departamentoSelecto: {},
    },
    mounted: function () {
        let $vue = this;
    },
    updated: function () {
        $(".numerico").numeric({negative: false});
    },
    methods: {
        configurarDistancia(departamento) {
            let $vue = this;
            $vue.showLoader();
            $vue.departamentoSelecto.id = departamento.id;
            $vue.departamentoSelecto.nombre = departamento.nombre;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/distanciapabellon/allDistancia'),
                data: {id: departamento.id},
                success: function (response) {
                    if (response.success) {
                        $vue.distancias = response.data;
                        $vue.$refs.modalNuevaDistanciaPabellon.open();
                    } else {
                        notify(response.message, 'error');
                    }
                    $vue.hideLoader();
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                    $vue.hideLoader();
                }
            });
        },
        saveDistanciaPabellon() {
            let $vue = this;
            $vue.showLoader();
            $vue.departamentoSelecto.distanciaPabellon = $vue.distancias;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/distanciapabellon/saveDistancia'),
                data: JSON.stringify($vue.departamentoSelecto),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.distancias = response.data;
                        $vue.$refs.modalNuevaDistanciaPabellon.close();
                        notify(response.message, 'success');
                    } else {
                        notify(response.message, 'error');
                    }
                    $vue.hideLoader();
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                    $vue.hideLoader();
                }
            });
        }
    }
});
