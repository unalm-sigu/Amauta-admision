new Vue({
    el: '#main',
    data: {
        myCountDown: null,
        initProceso: true,
        onProceso: false,
        intoProceso: false,
        updateProceso: true,
        alumnosPrematriculados: 0,
        seccionesPrematriculados: 0,
        countdown: 10,
        totalCurso: 0,
        logs: [],
        observaciones: [],
        limite: 10,
        minimo: 0,
        fydata: {
            currentCurso: 0,
            perCurso: 0,
            totalCurso: 0,
            currentSeccion: 0,
            perSeccion: 0,
            totalSeccion: 0,
        },
        blockMat: false,
        cicloAcademico: JSON.parse(cicloAcademicoJSON)
    },
    created() {
        let vue = this;
    },
    mounted() {
        let vue = this;
        vue.getInitData();
    },
    updated() {
        let vue = this;
    },
    watch: {
        countdown: function (newVal, oldVal) {
            let vue = this;
            if (newVal === -1) {
                vue.play();
                clearInterval(vue.myCountDown);
            }
        },
        logs: function () {
            let vue = this;
            if (vue.logs.length > 10) {
                vue.observaciones = vue.logs.slice(vue.minimo, vue.minimo + vue.limite);
            } else {
                vue.observaciones = vue.logs;
            }
        },
        minimo: function () {
            let vue = this;
            if (vue.logs.length > 10) {
                vue.observaciones = vue.logs.slice(vue.minimo, vue.minimo + vue.limite);
            } else {
                vue.observaciones = vue.logs;
            }
        }
    },
    methods: {
        block() {
            let vue = this;
            $.ajax({
                url: APP.url('academico/matricular/blequeoMatricula'),
                type: 'POST',
                async: true,
                data: {id: turno},
                success: function (response) {
                    if (response.success) {
                        vue.cicloAcademico = response.data;
                        notify("Se actualizó satisfactoriamente", "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        on: function () {
            let vue = this;
            swal({
                title: "¿Seguro que desea iniciar el proceso de asignación de vacantes?",
                icon: "warning",
                buttons: ['cancelar', 'aceptar'],
                dangerMode: true,
            }).then((resp) => {
                if (resp) {
                    vue.initProceso = false;
                    vue.onProceso = true;
                    vue.updateProceso = false;
                    vue.count();
                }
            });
        },
        stop: function () {
            let vue = this;
            vue.initProceso = true;
            vue.onProceso = false;
            vue.updateProceso = true;
            vue.countdown = 10;
            clearInterval(vue.myCountDown);
            vue.myCountDown = null;
        },
        play: function () {
            let vue = this;
            vue.initProceso = false;
            vue.onProceso = false;
            vue.intoProceso = true;
            $.ajax({
                url: APP.url('academico/matricular/iniciar'),
                type: 'POST',
                async: true,
                data: {id: turno},
                success: function (response) {
                    if (response.success) {
                        vue.updateProceso = true;
                        notify(response.message, "info");
                    } else {
                        vue.stop();
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    vue.stop();
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
            vue.tracer();
        },
        update: function () {
            let vue = this;
            vue.stop();
            vue.fydata = {
                currentCurso: 0,
                perCurso: 0,
                totalCurso: 0,
                currentSeccion: 0,
                perSeccion: 0,
                totalSeccion: 0
            };
            vue.getInitData();
        },
        tracer: function () {
            let vue = this;
            var socket = new SockJS('/wsconnect');
            stompClient = Stomp.over(socket);
            stompClient.debug = null;
            stompClient.connect({}, function (frame) {
                stompClient.subscribe('/user/monitoreo/notify', function (notification) {
                    var ntfy = JSON.parse(notification.body);
                    vue.fydata = ntfy;
                    if (!ntfy.state) {
                        vue.logs.push(ntfy.message);
                    }
                });
            });
        },
        count: function () {
            let vue = this;
            vue.myCountDown = null;
            vue.countdown = 10;
            vue.myCountDown = window.setInterval(function () {
                vue.countdown--;
            }, 1000);
        },
        getInitData: function () {
            let vue = this;
            $.ajax({
                url: APP.url('academico/matricular/detalle'),
                type: 'POST',
                async: true,
                data: {id: turno},
                success: function (response) {
                    if (response.success) {
                        vue.alumnosPrematriculados = response.data.alumnosPrematriculados;
                        vue.seccionesPrematriculados = response.data.seccionesPrematriculados;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        topLog: function () {
            console.log('topLog');
            let vue = this;
            vue.minimo;
            if (vue.minimo <= 0) {
                return;
            }
            vue.minimo--;
            console.log(vue.minimo);
        },
        downLog: function () {
            console.log('downLog');
            let vue = this;
            if (vue.observaciones <= 0) {
                return;
            }
            vue.minimo++;
            console.log(vue.minimo);
        }
    }
});

