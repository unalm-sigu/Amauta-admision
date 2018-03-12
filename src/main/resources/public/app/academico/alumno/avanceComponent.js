Vue.component("avance-component", {
    template: "#avanceComponent",
    props: {
        alumno: {},
    },
    data: function () {
        return {
            ident: true,
            ciclosCurricula: [],
            cursosCurricula: [],
            cantidadCursos: 0,
            searchCiclo: 1
        }
    },
    computed: {
      titulo() {
          return 'Avance Curricular';
      }  
    },
    beforeMount() {
        this.cargaAvance();
    },
    methods: {
        active(index) {
            let $vue = this;
            let tabSize = $vue.searchCiclo - 1;
            if (index === tabSize) {
                return "active";
            }
        },
        styleNotaCurri(nota) {
            if (nota === null) {

            } else {
                return "estado-blue";
            }
        },
        styleEstadoCurr(nombre) {
            if (nombre === 'APR' || nombre === 'EQUIV') {
                return "text-success";
            } else if (nombre === 'SIM') {
                return "text-warning";
            } else if (nombre === 'NREQ') {
                return "text-secondary";
            } else if (nombre === 'HAB') {
                return "text-primary";
            }
        },
        cargaAvance() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/' + $vue.searchCiclo + '/avance'),
                contentType: "application/json",
                success: function (response) {
                    $vue.cursosCurricula = response.data.cursos;
                    if ($vue.searchCiclo == 1) {

                        $vue.ciclosCurricula = response.data.ciclos;
                        $vue.cantidadCursos = $vue.cursosCurricula.length;
                    }
                }
            });
        },
        generarAvance() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/generaravance'),
                contentType: "application/json",
                success: function (response) {
                    $vue.cargaAvance();
                    MODAL.hideWait();
                }
            });
        },
        cicloSelecc: function (cicloSelecc) {
            let $vue = this;
            $vue.searchCiclo = cicloSelecc;
            $vue.cargaAvance();
        }
    }
});