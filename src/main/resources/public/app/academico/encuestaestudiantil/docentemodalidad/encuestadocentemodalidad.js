Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#docentemodalidadVUE',
    data: {
        url: '/academico/encuestaestudiantil/docentemodalidad',
        label: {'INA': 'label-warning', 'ACT': 'label-success', 'ANU': 'label-danger'},
        labelText: {'INA': 'Inactivo', 'ACT': 'Activo', 'ANU': 'Anulado'},
        puntajeDocenteModalidad: [],
        facultades: JSON.parse(jFacultades),
        departamentos: JSON.parse(jDepartamentos),
        departamentosSelectos: [],
        facultad: null,
        departamento: null,
        tipoGrado: {id: 'PRE', nombre: 'Pregrado'},
        grados: [{id: 'PRE', nombre: 'Pregrado'}, {id: 'EPG', nombre: 'Posgrado'}]
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
        },
        verReporte(item) {
            location.href = `${this.url}/${item.id}/reporte`;
        },
        reporteGeneralShow() {
            this.$refs.reporteGeneralModal.open();
        },
        downloadReporteTotal() {
            let vue = this;
            let data = {params: {
                    departamento: vue.departamento ? vue.departamento.id : '',
                    tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                    facultad: vue.facultad ? vue.facultad.id : '',
                }};

            axios_blob.get("/academico/encuestaestudiantil/docentemodalidad/reporte/todos", data)
                    .then(response => {
                        UTIL_BLOB.save(response);
                        vue.$refs.reporteGeneralModal.close();
                    }, () => {
                        vue.$refs.reporteGeneralModal.stop();
                        notify(Messages.errorComunicacion, 'error')
                    });

        },
        changeFacultad() {
            let $vue = this;

            $vue.departamentosSelectos = [];
            $vue.departamento = null;

            if ($vue.facultad) {

                $vue.departamentos.map((x, i) => {
                    if (x.facultad.id == $vue.facultad.id) {
                        $vue.departamentosSelectos.push(x)
                    }
                });

                $vue.departamentosSelectos;

            }
        }
    }
});







