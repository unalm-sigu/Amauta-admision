Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#aulaVUE',
    data: {
        inventario: {imagen: APP.url('/phobos/images/img.svg')},
        rutaImagen:'',
        aulasURL: APP.url(rutaModulo + '/list'),
        traslados:{},
        id:'',
        modalImagenProducto:{
            id: 'modalImagenProducto',
            // cancelbtn: 'Cerrar',
        },
        modalTraslados: {
            id: 'modalTraslados',
            header: true,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
        }
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        let tipo = $vue.$refs.raptorAulas.getParameterByName('queries[tipo-aula]');
        if (tipo != null) {
            $vue.tipoAula = {codigo: tipo};
            $vue.$refs.raptorAulas.querie.push({name: 'tipo-aula', value: tipo});
        }
        $vue.$refs.raptorAulas.loadRemoteData();
    },
    methods: {
        verTraslados(item) {
            let $vue = this;
            $vue.traslados = Object.assign({}, item);
            $vue.allProducto(item.id);
            $vue.modalTraslados.title = "Traslados de producto";
            $vue.modalTraslados.modalsize="modal-lg"
            $vue.$refs.modalTraslados.open();
        },
        allProducto(idInventario) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/aula/producto/traslados'),
                data:{id: idInventario},
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.traslados = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verImagen(item){
            let $vue = this;
            $vue.rutaImagen=item.imagen;
            $vue.modalImagenProducto.title = "Traslados de producto";
            $vue.modalImagenProducto.modalsize="modal-lg"
            $vue.$refs.modalImagenProducto.open();
        }
    }
});
 