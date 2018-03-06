new Vue({
    el: '#infoAcademico',
    data: {
        alumno: JSON.parse(alumnoJson),
        tabId: 1,
        objtab: {},
        alumnoCurso: [],
        alumnoCursoTemp: [],
        typeSearch: false,
        typeSearch2: false,
        options: [{id: 1, value: 'Todas las notas'},
            {id: 2, value: 'Todas las notas aprobadas'}],
        options2: [{id: 1, value: 'Listado por semestre'},
            {id: 2, value: 'Listado general'}],
        listCiclos: [],
        isAprob: true,
        cursos: [],
        general: true,
        searchCiclo: 1,
        cursosCurricula: [],
        ciclosCurricula: [],
        cursosMatriculados:[],
        creditosMatriculado:"",
        cursosMatriculado:""
        
    },
    created() {
        //ajax  
        let $vue = this;
        $vue.flag = true;
        $vue.ident = true;
        $vue.facu = true;

        $vue.tabs = [{id: 1, name: "Inicio"},
            {id: 2, name: "Historial"},
            {id: 3, name: "Avance"},
            {id: 4, name: "Matricula"},
            {id: 5, name: "Horario"},
            {id: 6, name: "Malla"}];

        if ($vue.alumno.persona.numeroDocIdentidad == undefined) {
            $vue.ident = false;
        }
        if ($vue.alumno.modalidadEstudio.codigo == 'VIS' || $vue.alumno.modalidadEstudio.codigo == 'ESP') {
            $vue.flag = false;
            $vue.facu = false;
        }
        if ($vue.alumno.carrera.codigo == $vue.alumno.carrera.facultad.codigo) {
            $vue.facu = false;
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.alumnoCursoTemp = $vue.alumnoCurso;

    },
    methods: {
        cicloSelecc: function (cicloSelecc) {
            let $vue = this;
            $vue.searchCiclo = cicloSelecc;
            $vue.cargaAvance()
        },
        updateTabs: function (tab) {

            let $vue = this;
            $vue.tabId = tab.id;
            if ($vue.tabId == 2) {
                this.cargaHistorial();
            } else if ($vue.tabId == 3) {
                this.cargaAvance();
            } else if ($vue.tabId == 4) {
                this.cargaMatricula();
            }

        },
        cargaMatricula() {

            let $vue = this;

            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id +'/cursoMatri'),
                contentType: "application/json",
                success: function (response) {
                    
                    $vue.cursosMatriculados = response.data.cursosMatriculados;
                    $vue.creditosMatriculado = response.data.creditosMatriculado;
                    $vue.cursosMatriculado = response.data.cursosMatriculado;
                }
            });
        },
        cargaAvance() {

            let $vue = this;

            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/' + $vue.searchCiclo + '/avance'),
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
        cargaHistorial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    $vue.alumnoCurso = response.data;

                    var i = 1;
                    $vue.alumnoCurso.forEach(function (element) {
                        var obj = {id: 1, value: element.descripción};
                        $vue.listCiclos.push(obj);
                        i++;
                    })
                }
            });
        },
        styleNota(nota) {
            if (nota < 11 || nota == 'DE') {
                return "text-danger";
            } else {
                return "text-primary";
            }
        },
        styleNotaCurri(nota) {
            if (nota == null) {

            } else {
                return "estado-blue";
            }
        },
        styleEstadoCurr(nombre) {
            if (nombre == 'APR' || nombre == 'EQUIV') {
                return "text-success";
            } else if (nombre == 'SIM') {
                return "text-warning";
            } else if (nombre == 'NREQ') {
                return "text-secondary";
            } else if (nombre == 'HAB') {
                return "text-primary";
            }

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
        validarNota(curso, tipo) {
            if (!tipo) {
                return true;
            } else {
                if (curso.nota >= 11)
                    return true;
            }
        },
        active(index) {
            let $vue = this;
            let tabSize = $vue.searchCiclo - 1;
            if (index == tabSize) {
                return "active";
            }
        }
        ,
        estadoMatricula(name) {
       
            if (name == 'MAT') {
                return "label label-success";
            }if (name=='RET') {
                return "label label-danger";
            }
        }
    }

})