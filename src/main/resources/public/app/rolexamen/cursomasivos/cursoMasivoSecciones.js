new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/cursomasivos'),
        cursoMasivoExamen: JSON.parse(jCursoMasivoExamen),
    },
    mounted() {

    },
    methods: {
        excluir(item, tipoAccion) {
            item.cursoMasivoExamen = {id: this.cursoMasivoExamen.id};

            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea excluir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/${tipoAccion}/excluir`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        console.dir(response.data.data);
                                        vue.cursoMasivoExamen = response.data.data;
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, findCursoMasivoExamen() {
            MODAL.showWait("Espere un momento por favor");
            let cursoMasivo = {
                id: this.rolExamen
            };
            AXIOS.post(`${this.URL}/findCursoMasivo`, cursoMasivo)
                    .then(response => {
                        if (response.data.success) {
                            this.cursoMasivoExamen = response.data.data;
                        }
                        MODAL.hideWait();
                    });
        }
    }
});
