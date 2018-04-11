new Vue({
    el: '#main',
    data: {
        generando: false,
    },
    mounted: function() {
        let vue = this;
        $global.$on("estado", function(encuestaDocente) {
            vue.estado(encuestaDocente);
        });
    },
    methods: {
        generarEncuesta: function() {
            let vue = this;
            vue.generando = true;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/generar'),
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


        },
        estado: function(encuestaDocente) {
            let vue = this;
            swal({
                text: "¿Está seguro que desea cambiar el estado a la encuesta del docente?",
                icon: "warning",
                type: "warning",
                dangerMode: true,
                showCancelButton: true,
                closeOnConfirm: false,
                buttons: {
                    cancel: "No",
                    confirm: "Si, estoy seguro"
                }
            }).then((willDelete) => {
                if (willDelete) {
                    vue.changeEstado(encuestaDocente);
                }
            });
        },
        changeEstado: function(encuestaDocente) {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/curso/estado'),
                async: false,
                data: {'id': encuestaDocente.id},
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
