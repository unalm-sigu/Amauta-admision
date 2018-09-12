Vue.component("matricula-component", {
    template: "#matriculaComponent",
    props: {
        alumno: {}
    },
    data() {
        return {
            cursos: [],
            ciclo: {},
            resumen: {}
        }
    },
    computed: {
        titulo() {
            return 'Cursos Matriculados ' + this.ciclo.descripcion;
        }
    },
    mounted() {
    },
    methods: {
        obtenerDatos() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/cursosmatriculados'),
                contentType: "application/json",
                success: function (response) {
                    $vue.cursos = response.data.cursos;
                    $vue.ciclo = response.data.ciclo;
                    $vue.resumen = response.data.resumen;
                }
            });
        },
        colornota(nota) {
            let $vue = this;
            if ($vue.alumno.modalidadEstudio.nombre === 'Posgrado') {
                return {
                    'text-danger': nota < 13
                };
            } else {
                return {
                    'text-danger': nota < 11
                };
            }
        },
        displayCreditos(item) {
            if (item.curso.creditos === 1) {
                return "1 crédito";
            } else {
                return item.curso.creditos + " créditos";
            }
        },
        labelclass(item) {
            return {
                'label-success': item.estado === 'MAT',
                'label-warning': item.estado === 'PMAT',
                'label-danger': item.estado === 'RCU' || item.estado === 'RET' || item.estado === 'RCI'
            };
        }
    }
});