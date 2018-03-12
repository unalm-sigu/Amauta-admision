Vue.component("matricula-component", {
    template: "#matriculaComponent",
    props: {
        alumno: {}
    },
    data() {
        return {
            cursos: [],
            ciclo: {}
        }
    },
    computed: {
        titulo() {
            return 'Matrícula ' + this.ciclo.descripcion;
        },
        cantidadCreditos() {
            let $vue = this;
            return $vue.cursos.map(x => x.curso.creditos).reduce((a, b) => a + b);
        }
    },
    mounted() {
        this.obtenerDatos();
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
        },
        labeltext(item) {
            switch (item.estado) {
                case 'MAT':
                    return 'Matriculado';
                case 'PMAT':
                    return 'Prematriculado';
                case 'RCU':
                    return 'Retirado Curso';
                case 'RCI':
                    return 'Retirado Ciclo';
                case 'RET':
                    return 'Retirado';
            }
        }
    }
});