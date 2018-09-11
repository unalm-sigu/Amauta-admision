Vue.component("avance-component", {
    template: "#avanceComponent",
    props: {
        alumno: {}
    },
    data: function () {
        return {
            planes: JSON.parse(planesJson),
            ident: true,
            ciclosCurricula: [],
            cursosCurricula: [],
            cantidadCursos: 0,
            showCiclo: 1,
            planTemp: {id: 0},
        }
    },
    computed: {
        titulo() {
            return 'Avance Curricular';
        }
    },
    beforeMount() {
    },
    mounted() {
        this.planTemp.id = this.alumno.planCurricular.id;
    },
    methods: {
        active(index) {
            let $vue = this;
            let tabSize = $vue.showCiclo - 1;
            if (index === tabSize) {
                return "active";
            }
        },
        styleNotaCurri(nota) {
            if (nota === "") {

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
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/avance'),
                contentType: "application/json",
                success: function (response) {
                    $vue.cursosCurricula = response.data.cursos;
                    $vue.ciclosCurricula = response.data.ciclos;
                    $vue.cantidadCursos = $vue.cursosCurricula.length;
                }
            });
        },
        generarAvance() {
            let $vue = this;
            if ($vue.planTemp.id == $vue.alumno.planCurricular.id && $vue.cursosCurricula.length > 0) {
                notify('Debe cambiar antes el plan curricular', 'error');
                return;
            }

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/' + $vue.planTemp.id + '/cambiarplan'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.cargaAvance();
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cicloSelecc: function (cicloSelecc) {
            let $vue = this;
            $vue.showCiclo = cicloSelecc;
        }
    }
});