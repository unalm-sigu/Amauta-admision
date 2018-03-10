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
            if (newVal === -1) {
                console.log('call play');
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
                async: true,
                data: {id: turno},
                success: function(response) {
                    if (response.success) {
                        vue.stop();
                    } else {
                        vue.stop();
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    vue.stop();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            vue.tracer();

        },
        tracer: function() {

            var socket = new SockJS('/wsconnect');

            stompClient = Stomp.over(socket);

            stompClient.debug = null;

            stompClient.connect({}, function(frame) {
                console.log(frame);
                stompClient.subscribe('/user/monitoreo/notify', function(notification) {
                    var notificacion = JSON.parse(notification.body);
                    console.log(notificacion);
//                    if (notificacion.tipo == 'NOTIFICACION') {
//                        notify(notificacion.message, "error");
//                    } else {
//                    }
                });
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

