new Vue({
    el: '#abonoVUE',
    data: {
        abonosURL: APP.url('abonoalumno/list'),
        modalReasignar: {
            id: 'modalReasignar',
            title: 'Extornos',
            modalsize: 'modal-lg',
            header: true,
            footer: true,
            showaccept: true,
            okbtn: 'Reasignar Extorno',
            cancelbtn: 'Cancelar'
        },
        modalAsignarPostulante: {
            id: 'modalAsignarPostulante',
            title: 'Asignar pago al postulante',
            modalsize: 'modal-md',
            header: true,
            footer: true,
            showaccept: true,
            okbtn: 'Asignar',
            cancelbtn: 'Cancelar'
        },
        rExItem: {},
        extornos: []
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        verReasignarExtorno(item) {
            let $vue = this;

            $vue.$refs.modalReasignar.open();
            $vue.rExItem = item;

            axios.post('/abonoalumno/extorno', {id: item.id, usuarioBanco: item.usuarioBanco, sucursal: item.sucursal, importe: item.importe})
                    .then(response => {
                        if (response.data.success) {
                            console.log(response.data.data);
                            $vue.extornos = response.data.data;
                        } else {
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
        },
        reasignarExtorno() {

        },
        verAsignarPostulante(item) {
            console.log(item);
        },
        save() {

        }
    }
});
