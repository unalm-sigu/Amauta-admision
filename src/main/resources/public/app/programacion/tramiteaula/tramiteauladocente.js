new Vue({
    el: '#mainDocente',
    data: {
        urlfilter: APP.url("tramite/aula/listDocente"),
        reservaActiva: {}
    },
    mounted: function() {
        let $vue = this;

        $global.$on("close-resumen-modal", function() {
            $vue.recargar();
        });
    },
    methods: {
        editarReserva(item) {
            return APP.url("tramite/aula/docente/" + item.id + "/update");
        },
        verResumen(item) {
            let $vue = this;
            $vue.reservaActiva = item;

            let params = $.param({id: item.id});

            $.ajax({
                url: APP.url('tramite/aula/resumen'),
                type: 'POST',
                data: params,
                success: function(response) {
                    if (response.success) {
                        $global.$emit("show-resumen-modal", response.data);
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
