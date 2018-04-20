Vue.component("multiselect", window.VueMultiselect.default);

Vue.component('movimiento-raptor', {
    props: {
        verDetalles: {
            required: true
        }
    },
    data() {
        return {
            rolesUrl: APP.url('seguridad/rol/list')
        }
    },
    methods: {
        date2string(date) {
            return new Date(date).toLocaleDateString('la', {day: '2-digit', month: '2-digit', year: 'numeric'});
        },
        getNombre(cliente) {
            if (cliente.tipoContacto === 'PERSONA') {
                return cliente.persona.nombreCompleto;
            } else if (cliente.tipoContacto === 'EMPRESA') {
                return cliente.empresa.razonSocial;
            }
        },
        getDocumento(cliente) {
            let ente = {};
            if (cliente.tipoContacto === 'PERSONA') {
                ente = cliente.persona;
            } else if (cliente.tipoContacto === 'EMPRESA') {
                ente = cliente.empresa;
            }

            return ente.tipoDocumento.simbolo + ' ' + ente.numeroDocIdentidad;
        },
    }
});

new Vue({
    el: '#pageRolSistemaVUE',
    data: {
        movimiento: {tipoMovimiento: {}, contactoEmpresa: {persona: {tipoDocumento: {}}, empresa: {tipoDocumento: {}}}},
        movimientoalmacenModal: {
            id: 'movimientoalmacenModal',
            header: 'true',
            title: '',
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
            showaccept: false,
            modalSize: 'modal-lg'
        }
    },
    computed: {
    },
    mounted() {
    },
    methods: {
        verDetalles(mov) {

        }
    }
});
