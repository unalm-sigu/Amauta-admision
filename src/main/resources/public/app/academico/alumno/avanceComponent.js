Vue.component("avance-component", {
    template: "#avanceComponent",
    props: {
        showTitle: true,
        showActions: true,
        alumno: {}
    },
    data: function () {
        return {
            planes: [],
            ident: true,
            ciclosCurricula: [],
            cursosCurricula: [],
            resumenAlumno: [],
            resumenPlan: [],
            cantidadCursos: 0,
            showCursos: true,
            showCiclo: 1,
            planTemp: {id: 0},
        }
    },
    computed: {
        titulo() {
            let $vue = this;
            if ($vue.alumno.carrera)
                return 'Avance Curricular en ' + $vue.alumno.carrera.nombre;
            else
                return 'wwww';
        }
    },
    beforeMount() {
        let $vue = this;
        $vue.loadPlanes();
    },
    mounted() {
        let $vue = this;
        $vue.planTemp.id = this.alumno.planCurricular.id;
    },
    methods: {
        totalAlumno(tipo) {
            let $vue = this;
            let cred = 0;
            let cur = 0;

            for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                let res = $vue.resumenAlumno[i];
                if (res.tipoCursoCurricula.codigo == 'EEP') {
                    continue;
                }
                cred += res.creditos;
                cur += res.cursos;
            }
            return (tipo == 'CREDITOS') ? cred : cur;
        },
        totalPlan(tipo) {
            let $vue = this;
            let cred = 0;
            let cur = 0;

            for (var i = 0; i < $vue.resumenPlan.length; i++) {
                let res = $vue.resumenPlan[i];
                if (res.tipoCursoCurricula.codigo == 'EEP') {
                    continue;
                }
                cred += res.creditos;
                cur += res.cursos;
            }
            return (tipo == 'CREDITOS') ? cred : cur;
        },
        creditosAlumno(item) {
            let $vue = this;
            for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                let res = $vue.resumenAlumno[i];
                if (res.tipoCursoCurricula.codigo == item.tipoCursoCurricula.codigo) {
                    return res.creditos;
                }
            }
            return 0;

        },
        cursosAlumnos(item) {
            let $vue = this;
            for (var i = 0; i < $vue.resumenAlumno.length; i++) {
                let res = $vue.resumenAlumno[i];
                if (res.tipoCursoCurricula.codigo == item.tipoCursoCurricula.codigo) {
                    return res.cursos;
                }
            }
            return 0;
        },
        verResumen() {
            let $vue = this;
            $vue.showCiclo = 201;
            $vue.showCursos = false;
        },
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
            if (nombre === 'APR' || nombre === 'EQUIV' || nombre === 'CONV') {
                return "text-success";
            } else if (nombre === 'SIM') {
                return "text-warning";
            } else if (nombre === 'NREQ') {
                return "text-secondary";
            } else if (nombre === 'HAB') {
                return "text-primary";
            } else if (nombre === 'MAT') {
                return "text-primary bold";
            } else if (nombre === 'PEND') {
                return "text-warning ";
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
                    $vue.resumenAlumno = response.data.resumenAlumno;
                    $vue.resumenPlan = response.data.resumenPlan;
                    $vue.cantidadCursos = $vue.cursosCurricula.length;
                }
            });
        },
        generarAvance() {
            let $vue = this;
//            if ($vue.planTemp.id == $vue.alumno.planCurricular.id && $vue.cursosCurricula.length > 0) {
//                notify('Debe cambiar antes el plan curricular', 'error');
//                return;
//            }

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/' + $vue.planTemp.id + '/cambiarplan'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$emit("reload-plan-alumno");
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
            $vue.showCursos = true;
        },
        loadPlanes() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/planes'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.planes = response.data;
                        $vue.planTemp.id = $vue.alumno.planCurricular.id;
                    } else {
                        notify("No se pudo cargar la lista de planes curriculares disponibles para el alumno", "error");
                    }
                },
                error() {
                    notify("No se pudo cargar la lista de planes curriculares disponibles para el alumno", "error");
                }
            });
        }
    }
});