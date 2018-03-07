new Vue({
    el: '#main',
    data: {
        myCountDown: null,
        initProceso: true,
        onProceso: false,
        intoProceso: false,
        countdown: 10
    },
    created() {
        let vue = this;
    },
    mounted() {
        let vue = this;
    },
    updated() {
        let vue = this;
    },
    watch: {
        countdown: function(newVal, oldVal) {
            let vue = this;
            if (newVal < 0) {
                vue.play();
                clearInterval(vue.myCountDown);
            }
        }
    },
    methods: {
        on: function() {
            let vue = this;
            swal({
                title: "¿Seguro que desea iniciar la matricula?",
                icon: "warning",
                buttons: ['cancelar', 'aceptar'],
                dangerMode: true,
            }).then((resp) => {
                if (resp) {

                    vue.initProceso = false;
                    vue.onProceso = true;
                    vue.count();

                }
            });
        },
        stop: function() {
            let vue = this;
            vue.initProceso = true;
            vue.onProceso = false;
            vue.countdown = 10;
            clearInterval(vue.myCountDown);
            vue.myCountDown = null;
        },
        play: function() {
            let vue = this;
            vue.initProceso = false;
            vue.onProceso = false;
            vue.intoProceso = true;

            $.ajax({
                url: APP.url('academico/matricular/iniciar'),
                type: 'POST',
                async: false,
                data: {idAlumno: alumno},
                success: function(response) {
                    if (response.success) {

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        count: function() {
            let vue = this;
            vue.myCountDown = null;
            vue.countdown = 10;
            vue.myCountDown = window.setInterval(function() {
                vue.countdown--;
            }, 1000);
        }
    }
});

