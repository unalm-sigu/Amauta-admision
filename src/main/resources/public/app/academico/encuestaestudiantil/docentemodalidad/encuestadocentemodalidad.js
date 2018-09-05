
new Vue({
    el: '#docentemodalidadVUE',
    data: {
        url: '/academico/encuestaestudiantil/docentemodalidad/',
        label: {'INA': 'label-warning', 'ACT': 'label-success'},
        labelText: {'INA': 'Inactivo', 'ACT': 'Activo'},
        puntajeDocenteModalidad: [],
        modalTemas: {
            id: 'modalTemas',
            title: 'Temas',
            modalsize: 'modal-md',
            header: true,
            footer: false,
            showaccept: false
        },
    },
    methods: {
        findTemas(item) {
            AXIOS.get(`${this.url}/${item.id}/resumen/temas`)
                .then(response => {
                    if (response.data.success) {
                        this.puntajeDocenteModalidad = response.data.data;
                        this.$refs.modalTemas.open();
                    }
                })
        }
    }
});







