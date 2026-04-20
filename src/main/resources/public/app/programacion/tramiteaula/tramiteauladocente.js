new Vue({
    el: '#mainDocente',
    data: {
        urlfilter: APP.url("tramite/aula/listDocente"),
        reservaActiva: {},
        dataResumenModal: {id: 'iddataResumenModal', title: 'Resumen Reserva', header: true, modalsize: 'modal-lg', showaccept: false, cancelclass: 'btn-link'},
        tramiteresumen: {tramite: {}},
        dias: [],
        horas: []
    },
    mounted: function() {
    },
    methods: {
        editarReserva(item) {
            return APP.url("tramite/aula/docente/" + item.id + "/update");
        },
        verResumen(item) {
            let $vue = this;
            $vue.reservaActiva = item;
            $.ajax({
                method: 'POST',
                async: false,
                url: APP.url('tramite/aula/resumen'),
                data: {id: item.id},
                success: function(response) {
                    if (response.success) {
                        $vue.tramiteresumen = response.data.reservaAula;
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                        $vue.$refs.resumenModal.open();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify("Error al cargar el resumen", "error");
                }
            });
        },
        recargar() {
            this.$refs.raptor.refresh();
        }
    }
});
