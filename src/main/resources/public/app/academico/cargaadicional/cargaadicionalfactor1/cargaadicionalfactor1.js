Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cargaadicionalfactor1VUE',
    data: {
        url: APP.url('academico/cargaadicional/factor1'),
        factor: {},
        modal: {
            id: 'modal',
            title: '',
            modalsize: 'modal-md',
            header: true,
            footer: true,
            showaccept: true
        },
        categorias: JSON.parse(categoriasJson),
        situaciones: JSON.parse(situacionesJson)
    },
    computed: {
    },
    mounted: function () {
    },
    methods: {
        nuevo() {
            this.factor = {};
            this.modal.title = 'Nueva condición';
            this.$refs.modal.open();
        },
        find(item) {
            AXIOS.get(`${this.url}/find/${item.id}`)
                .then(response => {
                    this.factor = response.data.data;
                    this.modal.title = 'Editar condición';
                    this.$refs.modal.open();
                })
        },
        eliminar(item) {
            bootbox.confirm({
                message: "¿Está seguro que eliminar la condición seleccionada?",
                bbootboxuttons: {
                    confirm: {label: 'Si', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/delete/${item.id}`)
                            .then(response => {
                                this.$refs.raptor.loadRemoteData();
                            })
                    }
                }
            });

        },
        save() {
            AXIOS.post(`${this.url}/save`, this.factor)
                .then(response => {
                    if (response.data.success) {
                        this.$refs.modal.close();
                        this.$refs.raptor.loadRemoteData();
                    }
                })
        }
    }
});







