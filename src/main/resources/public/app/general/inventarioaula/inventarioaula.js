Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        inventario: {},
        categorias: [],
        categoria: {id: null, productos: []},
        productos: [],
        producto: {id: null},
        aula: {id: idaula},
        inventarioURL: APP.url('general/aula/inventario/' + idaula + '/all'),
        nuevoproducto: {},
        dataNuevoProducto: {
            id: 'modalNuevoProducto',
            header: false,
        },
        isprocess: false
    },
    mounted: function () {
        let $vue = this;
        $vue.allProducto();
        $('[name="fechaVencimientoGarantia"]').datepicker();
        $('[name="anioFabricacion"]').datepicker();
    },
    methods: {
        allProducto() {
            let $vue = this;
            $.ajax({
                url: APP.url('general/aula/inventario/allProducto'),
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.categorias = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeCategoria(categoria) {
            console.log(categoria.nombre);
        },
        addNuevoProducto() {
            var vue = this;
            if (vue.categoria.id == null) {
                notify('Seleccione una categoria', 'info');
                return;
            }

            console.log(vue.categoria.id);
            console.log(vue.categoria.nombre);

            vue.$refs.nuevoProducto.open();

            var keys = Object.keys(vue.nuevoproducto);
            for (var key in keys) {
                vue.nuevoproducto['' + keys[key]] = null;
            }

            $('#formNuevoProducto').find('[name=tipo]').select2({minimumResultsForSearch: -1});
        },
        saveNuevoProducto() {
            var vue = this;
            if ($('#formNuevoProducto').parsley().validate() != true) {
                return;
            }
            vue.showLoader();
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/inventario/saveproducto'),
                data: $('#formNuevoProducto').serialize(),
                async: false,
                success: function (response) {
                    if (response.success) {
                        
                        vue.producto=response.data;
                        vue.$refs.nuevoProducto.close();
                        
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.hideLoader();

                }, error: function () {
                    vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
    }
});      