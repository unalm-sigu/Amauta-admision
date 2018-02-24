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
        searchCiclo: 1
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
        console.log($vue.alumno);
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
        console.log("tipo::: " + $vue.typeSearch)
    },
    methods: {

        updateTabs: function (tab) {
            let $vue = this;
            $vue.tabId = tab.id;
            this.carga();
        },
        carga() {
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
                    console.log($vue.listCiclos);
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
        }
    }

})