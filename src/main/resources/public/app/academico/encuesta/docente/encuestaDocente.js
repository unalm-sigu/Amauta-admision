new Vue({
    el: '#main',
    data: {
        generando: false,
    },
    mounted: function() {
    },
    methods: {
        generarEncuesta: function() {
            let vue = this;
            vue.generando = true;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuesta/docente/generar'),
                async: false,
                success: function(response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.generando = false;
                }, error: function() {
                    vue.generando = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        }
    }
});
