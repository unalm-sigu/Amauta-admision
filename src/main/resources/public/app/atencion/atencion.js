new Vue({
    el: '#main',
    data: {

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
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'tic.esatdo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'tic.estado', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.load.querie = [];
                $vue.$refs.load.changeUrl('queries[tic.estado]', null);
                $vue.$refs.load.loadRemoteData();
            }
        }
    }
})