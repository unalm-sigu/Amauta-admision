new Vue({
    el: '#main',
    data: {
        info: {perAvance: 0}
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
            $.ajax({
                url: APP.url('fotos/carne/descargarFotos'),
                type: 'GET',
                success: function (response) {
                    if (response.success) {
                        $vue.obtenerInfo();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
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
                        console.log($vue.info);
                        if ($vue.info.estado == 'ACT') {
                            setTimeout($vue.obtenerInfo, 3000);
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
 