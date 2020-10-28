
new Vue({
    el: '#docentemodalidadVUE',
    data: {
        url: '/docente/encuesta/resumen',
        label: {'INA': 'label-warning', 'ACT': 'label-success', 'ANU': 'label-danger'},
        labelText: {'INA': 'Inactivo', 'ACT': 'Activo', 'ANU': 'Anulado'},
        puntajeDocenteModalidad: [],
        modalTemas: {
            id: 'modalTemas',
            title: 'Temas',
            modalsize: 'modal-md',
            header: true,
            footer: false,
            showaccept: false
        },
        indicadorClave: 0
    },
    methods: {
        findTemas(item) {
            AXIOS.get(`${this.url}/${item.id}/resumen/temas`)
                    .then(response => {
                        if (response.data.success) {
                            this.puntajeDocenteModalidad = response.data.data;
                            this.indicadorClave = this.puntajeDocenteModalidad[0].encuestaDocente.puntajeBase10;
                            this.$refs.modalTemas.open();
                        }
                    })
        }
    }
});







