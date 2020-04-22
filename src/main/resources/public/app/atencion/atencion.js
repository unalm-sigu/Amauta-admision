new Vue({
    el: '#main',
    data: {
        seleccionado: '',
        cambiarOficinaData: {
            title: "Cambiar Oficina",
        },
        cambiarColaboradorData: {
            title: "Cambiar Colaborador",
        },
        oficinaDestino: {id: null},
        ticketAyuda: {id: null},
        colaboradores: [],
        colaboradorDestino: {id: null},
        motivo: '',
        pagination: {'total-items': 0, 'items-per-page': 10, 'max-size': 3, 'boundary-link-numbers': true},
    },
    created() {
    },
    mounted() {
        let vue = this;
        setTimeout(function () {
            vue.verEstado('ACTIVO');
        }, 2000);
    },
    methods: {
        responder(mensaje) {
            let vue = this;
            $(location).attr('href', APP.url('atencion/ticket/' + mensaje.ticketAyuda.id + '/respuesta'));
        },
        asignar(mensaje) {
            let vue = this;
        },
        verEstado(tipo) {
            let vue = this;
            if (vue.seleccionado == tipo) {
                vue.seleccionado = '';
                vue.$refs.raptorMensajes.querie = [];
                vue.$refs.raptorMensajes.loadRemoteData();
                return;
            }
            vue.seleccionado = tipo;
            vue.$refs.raptorMensajes.querie = [];
            vue.$refs.raptorMensajes.querie.push({name: 'tic.estado', value: tipo});
            vue.$refs.raptorMensajes.loadRemoteData();
        },
        trasladarOficina(msj) {
            let vue = this;
            vue.ticketAyuda.id = msj.ticketAyuda.id;
            vue.motivo = '';
            vue.oficinaDestino = {id: null};
            vue.$refs.cambiarOficina.open();
        },
        trasladarColaborador(msj) {
            let vue = this;
            vue.ticketAyuda.id = msj.ticketAyuda.id;
            vue.motivo = '';
            vue.colaboradorDestino = {id: null};
            vue.$refs.cambiarColaborador.open();
            vue.callColabradores();
        },
        saveCambiarOficina() {

            let vue = this;

            if ($("#formCambiarOficina").parsley().validate() != true) {
                return;
            }

            if (vue.oficinaDestino.id == null) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('atencion/ticket/trasladooficina'),
                data: $("#formCambiarOficina").serialize(),
                success: function (response) {
                    if (response.success) {

                        vue.$refs.cambiarOficina.close();
                        vue.$refs.raptorMensajes.loadRemoteData();

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });


        },
        saveCambiarColaborador() {

            let vue = this;
            if ($("#formCambiarColaborador").parsley().validate() != true) {
                return;
            }

            if (vue.colaboradorDestino.id == null) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('atencion/ticket/trasladocolaborador'),
                data: $("#formCambiarColaborador").serialize(),
                success: function (response) {
                    if (response.success) {

                        vue.$refs.cambiarColaborador.close();
                        vue.$refs.raptorMensajes.loadRemoteData();

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });



        },
        callColabradores() {

            let vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('atencion/ticket/allcolaborador'),
                data: {id: vue.ticketAyuda.id},
                success: function (response) {
                    if (response.success) {

                        vue.colaboradores = response.data.colaboradores;

                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });


        }
    }
})