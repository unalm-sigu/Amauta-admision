Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cargaadicionaldocenteVUE',
    data: {
        url: APP.url('academico/cargaadicional/docente'),
        modalConfigura: {
            id: 'modalConfigura',
            title: 'Configuración',
            modalsize: 'modal-sm',
            header: true,
            footer: true,
            showaccept: true,
            cancelclass: 'btn-link'
        },
        configura: {

        }
    },
    computed: {
    },
    mounted: function () {
        this.findConfigura();
    },
    methods: {
        findConfigura() {
            AXIOS.get(`${this.url}/configuracion`)
                .then(response => {
                    if (response.data.success) {
                        this.configura = response.data.data;
                    }
                })
        },
        editConfigura() {
            AXIOS.get(`${this.url}/configuracion`)
                .then(response => {
                    if (response.data.success) {
                        this.configura = response.data.data;
                        this.$refs.modalConfigura.open();

                    }
                })
        },
        saveConfigura() {
            AXIOS.post(`${this.url}/configuracion/save`, this.configura)
                .then(response => {
                    if (response.data.success) {
                        this.$refs.modalConfigura.close();
                    }
                })
        },
        generarCarga() {
            bootbox.confirm({
                message: "¿Está seguro que desea generar la carga adicional?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/generar/carga`)
                            .then(response => {
                                if (response.data.success) {
                                    this.findConfigura();
                                    this.$refs.raptor.loadRemoteData();
                                }
                            })
                    }
                }
            });
        },
        generarMontos() {
            bootbox.confirm({
                message: "¿Está seguro que desea generar los montos?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/generar/montos`)
                            .then(response => {
                                if (response.data.success) {
                                    this.findConfigura();
                                    this.$refs.raptor.loadRemoteData();
                                }
                            })
                    }
                }
            });
        },
        eliminarCarga() {
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la carga adicional?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/eliminar/carga`)
                            .then(response => {
                                if (response.data.success) {
                                    this.findConfigura();
                                    this.$refs.raptor.loadRemoteData();
                                }
                            })
                    }
                }
            });
        },
        eliminarMontos() {
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar los montos generados?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/eliminar/montos`)
                            .then(response => {
                                if (response.data.success) {
                                    this.findConfigura();
                                    this.$refs.raptor.loadRemoteData();
                                }
                            })
                    }
                }
            });
        },
        cerrar() {
            bootbox.confirm({
                message: "¿Está seguro que desea cerrar?",
                buttons: {
                    confirm: {label: 'Sí, seguro', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`${this.url}/cerrar`)
                            .then(response => {
                                if (response.data.success) {
                                    this.findConfigura();
                                }
                            })
                    }
                }
            });
        }
    }
});







