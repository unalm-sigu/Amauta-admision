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
//            location.href = APP.url();
            setTimeout($vue.obtenerInfo, 4000);

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
 