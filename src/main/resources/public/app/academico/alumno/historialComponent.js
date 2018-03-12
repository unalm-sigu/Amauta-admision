Vue.component("historial-component", {
    template: "#historialComponent",
    props: {
        alumno: {},
    },
    data: function () {
        return {
            cursos: [],
            alumnoCurso: [],
            listCiclos: [],
            typeSearch: false,
            typeSearch2: false,
            general: true,
        }
    },
    computed: {
        titulo() {
            return 'Historial Académico';
        }
    },
    beforeMount() {
        this.cargaHistorial();
    },
    methods: {
        cargaHistorial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.alumnoCurso = response.data;
                        var i = 1;
                        $vue.alumnoCurso.forEach(function (element) {
                            var obj = {id: 1, value: element.descripción};
                            $vue.listCiclos.push(obj);
                            i++;
                        })
                    }
                }
            });
        },
        changeSearch() {
            let $vue = this;
            $vue.alumnoCurso = this.alumnoCurso;
            $vue.alumnoCursoTemp = this.alumnoCursoTemp;
        },
        changeSearch2() {
            let $vue = this;
            if (!$vue.typeSearch2) {
                $vue.general = true;
            } else {
                $.ajax({
                    method: 'GET',
                    url: APP.url('academico/alumno/' + this.alumno.id + '/listHistorial'),
                    contentType: "application/json",
                    success: function (response) {
                        $vue.cursos = response.data.cursos;
                        $vue.general = false;
                    }
                });
            }
        },
        styleNota(nota) {
            if (nota < 11 || nota == 'DE') {
                return "text-danger";
            } else {
                return "text-primary";
            }
        },
        validarNota(curso, tipo) {
            if (!tipo) {
                return true;
            } else {
                if (curso.nota >= 11)
                    return true;
            }
        },
    }
});