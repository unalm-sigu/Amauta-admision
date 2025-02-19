Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#docentemodalidadVUE',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        url: '/academico/encuestaestudiantil/docentemodalidad',
        label: {'INA': 'label-warning', 'ACT': 'label-success', 'ANU': 'label-danger'},
        puntajeDocenteModalidad: [],
        ciclos: JSON.parse(CICLOS_ACADEMICOS),
        cicloAcademico: JSON.parse(CICLO_ACADEMICO),
        ciclo: [JSON.parse(CICLO_ACADEMICO)],
        facultades: JSON.parse(jFacultades),
        departamentos: JSON.parse(jDepartamentos),
        departamentosSelectos: [],
        facultad: null,
        departamento: null,
        cursoSinEncuesta: null,
        tipoGrado: {id: 'PRE', nombre: 'Pregrado'},
        grados: [{id: 'PRE', nombre: 'Pregrado'}, {id: 'EPG', nombre: 'Posgrado'}],
        docente: null,
        docentes: [],
        modalCursoSinEncuesta: VUE_MODAL.structFormAjax({
            id: 'modalCursoSinEncuesta',
            header: true,
            title: 'Historial Becado',
            cancelbtn: 'Aceptar',
            showaccept: false
        }),
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
        verReporteSinEncuesta(item) {
            const fullUrl = `${this.url}/${item.id}/sinEncuesta`;

            axios.post(fullUrl)
                .then(response => {
                    if (response.data.success) {
                        this.cursoSinEncuesta = response.data.data;
                        console.log(this.cursoSinEncuesta);
                    } else {
                        notify(response.data.message || 'Error al generar el reporte', 'error');
                    }
                })
                .catch(error => {
                    notify(Messages.errorComunicacion, 'error');
                    // console.error(error); // Log para mayor detalle del error
                })
                .finally(() => {
                    // Asegúrate de abrir el modal independientemente del resultado
                    this.$refs.modalCursoSinEncuesta.open();
                });
        },

        reporteGeneralShow() {
            this.$refs.reporteGeneralModal.open();
        },
        reporteGeneralSinCursosNoEncuestadosShow() {
            this.$refs.reporteGeneralSinCursosModal.open();
        },
        downloadReporteTotal() {
            let vue = this;
            let data = {
                cicloAcademicos: vue.ciclo,
                departamento: vue.departamento ? vue.departamento.id : '',
                tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                facultad: vue.facultad ? vue.facultad.id : '',
                docente: vue.docente ? vue.docente.id : '',
            };
            axios_blob.post("/academico/encuestaestudiantil/docentemodalidad/reporte/todos", data)
                    .then(response => {
                        UTIL_BLOB.save(response);
                        vue.$refs.reporteGeneralModal.close();
                    }, () => {
                        vue.$refs.reporteGeneralModal.stop();
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        downloadReporteSinCursoNoEncuestadosTotal() {
            let vue = this;
            let data = {
                cicloAcademicos: vue.ciclo,
                departamento: vue.departamento ? vue.departamento.id : '',
                tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                facultad: vue.facultad ? vue.facultad.id : ''
            };
            axios_blob.post("/academico/encuestaestudiantil/docentemodalidad/reporte/sincursosnoencuestados", data)
                    .then(response => {
                        UTIL_BLOB.save(response);
                        vue.$refs.reporteGeneralSinCursosModal.close();
                    }, () => {
                        vue.$refs.reporteGeneralSinCursosModal.stop();
                        notify(Messages.errorComunicacion, 'error');
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
        },
        searchDocente(nombre) {
            let $vue = this;
            if (!nombre) {
                return;
            }
            axios.get(APP.url("academico/profesor/searchDocente"),
                    {params: {nombre: nombre}})
                    .then(response => {
                        $vue.docentes = response.data;
                    });
        },
        base10(codigo) {
            return codigo <= 202210;
        },
        base5(codigo) {
            return codigo > 202210;
        }
    }
});







