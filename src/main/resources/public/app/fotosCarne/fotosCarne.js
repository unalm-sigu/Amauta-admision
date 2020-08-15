new Vue({
    el: '#main',
    data: {
        info: {perAvance: 0},
        bloq: false
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        $vue.obtenerInfo();
    },
    methods: {
        descagarFoto() {
            let $vue = this;
            $vue.bloq = true;
            location.href = APP.url('fotos/carne/descargarFotos');
            setTimeout($vue.obtenerInfo, 3000);

        },
        obtenerInfo() {
            let $vue = this;
            $.ajax({
                url: APP.url('fotos/carne/info'),
                type: 'GET',
                async: true,
                success: function (response) {
                    if (response.success) {
                        $vue.info = response.data;
                        if ($vue.info.estado == 'ACT') {
                            $vue.bloq = true;
                            setTimeout($vue.obtenerInfo, 3000);
                        } else {
                            $vue.bloq = false;
                        }

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});
 