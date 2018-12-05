new Vue({
    el: '#preciocursoestructuraVUE',
    data: {
        URL: APP.url('academico/preciocursoestructura'),
        estructuras: [],
        editar: false,
        precioCredito: 0,
        guardando: false
    },
    mounted() {
        this.list();
    },
    methods: {
        list() {
            axios.get(`${this.URL}/list`)
                    .then(response => this.estructuras = response.data.data);

        },
        changePrecio() {
            let $vue = this;
            for (var i = 0; i < $vue.estructuras.length; i++) {
                $vue.estructuras[i].precio = $vue.estructuras[i].creditos * $vue.precioCredito;
            }
        },
        guardar() {
            let $vue = this;
            let mm = bootbox.confirm({
                message: "¿Está seguro que desea modificar los precios según la estructura?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                        $(".btn-modal").prop('disabled', true);

                        AXIOS.post(`${$vue.URL}/save`, $vue.estructuras)
                                .then(response => {
                                    if (response.data.success) {
                                        mm.modal("hide");
                                        $vue.list();
                                        $vue.editar = false;
                                        $vue.guardando = false;
                                        
                                    } else {
                                        $(".btn-modal").prop('disabled', false);
                                        $(".btn-procesar").html('Si');
                                        notify(response.message, "error");
                                    }
                                });

                        return false;
                    }
                }
            });

        }
    }
});
