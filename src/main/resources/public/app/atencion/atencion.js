new Vue({
    el: '#main',
    data: {
        seleccionado: '',
    },
    created() {
    },
    mounted() {
        let $vue = this;
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

            let $vue = this;

            if ($vue.seleccionado == tipo) {

                $vue.seleccionado = '';

                $vue.$refs.raptorMensajes.querie = [];
                $vue.$refs.raptorMensajes.loadRemoteData();
                return;

            }


            $vue.seleccionado = tipo;
            $vue.$refs.raptorMensajes.querie = [];
            $vue.$refs.raptorMensajes.querie.push({name: 'tic.estado', value: tipo});
            $vue.$refs.raptorMensajes.loadRemoteData();

        }
    }
})