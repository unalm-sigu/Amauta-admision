new Vue({
    el: '#infoAcademico',
    data: {
        alumno: JSON.parse(alumnoJson),
        allHoras: JSON.parse(horasJson),
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
        cursosMatriculados: [],
        creditosMatriculado: "",
        cursosMatriculado: "",

        coloresCurso: [],
        secciones: [],
        horas: [],
        classInit: 'curso size-1 ',
        tabla4: false,
        tabla8: false,
        tabla14: false,
        horarios: [],
        horaTmp: ''
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
    computed: {

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
            } else if ($vue.tabId == 5) {
                $vue.cargaHorario();
            }

        },
        cargaMatricula() {

            let $vue = this;

            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/cursoMatri'),
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
        },
        estadoMatricula(name) {

            if (name == 'MAT') {
                return "label label-success";
            }
            if (name == 'RET') {
                return "label label-danger";
            }
        },
        settingSeccionColor: function () {
            let vue = this;
            var horarios = vue.horarios.horarios;
            for (var i = 0; i < horarios.length; i++) {
                for (var j = 0; j < horarios[i].dias.length; j++) {
                    for (var m = 0; m < horarios[i].dias[j].secciones.length; m++) {
                        if (vue.secciones.indexOf(horarios[i].dias[j].secciones[m].seccion) === -1) {
                            let seccion = horarios[i].dias[j].secciones[m].seccion;
                            vue.secciones.push(seccion);
                        }
                    }
                }
            }

            vue.secciones.map(function (data, index) {
                let seccionColor = {
                    seccion: data,
                    color: 'curso color' + (index + 1)
                }
                vue.coloresCurso.push(seccionColor);
            });

        },
        getColorBySeccion(seccion) {
            let vue = this;
            let classDiv = "";

            vue.coloresCurso.map(function (data, index) {
                if (seccion === data.seccion) {
                    classDiv = data.color;
                }
            });
            return  vue.classInit + classDiv;
        },
        settingHoras() {
            let vue = this;
            var horarios = vue.horarios.horarios;

            for (var i = 0; i < horarios.length; i++) {
                if (vue.horas.indexOf(horarios[i].numeroHora) == -1) {
                    let numeroHora = horarios[i].numeroHora;
                    vue.horas.push(numeroHora);
                }
            }

        },
        validandoTabla() {
            let vue = this;
            let cantHoras = vue.horas.length;

            let horasTotal = [];//horas desde las 8 am
            vue.allHoras.map(function (data, index) {
                if (data.numero != 6 && data.numero != 7) {   //  menos 6 y 7
                    horasTotal.push(data.numero);
                }
            });
            //hora minima y maxima  del alumno
            var horaMin = Math.min.apply(null, horasTotal);
            var horaMax = Math.max.apply(null, horasTotal);
            //var longitudHoras = horasTotal.length;
            var posicionMayor = horasTotal.indexOf(horaMax);
            //eliminando las horas despues del mayor  
            for (var i = horasTotal.length - 1; i >= 0; i--) {
                if (horasTotal[i] > posicionMayor)
                    horasTotal.splice(horasTotal[i], 1);
            }

            //obtener index 
            let indexs = [];
            horasTotal.map(function (data, index) {
                indexs.push(horasTotal.indexOf(vue.horas[index]));
            });

            let horasRestante = [];
            //eliminando horas entre los rangos de horas minimo y maximo 
            for (var i = horasTotal.length - 1; i >= 0; i--) {
                if (indexs[i] > -1)
                    horasTotal.splice(indexs[i], 1);
            }

            horasRestante = horasTotal;

            if (cantHoras <= 4) {

                let horasLlenar = 4 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                vue.tabla4 = true;
            }
            if (cantHoras >= 5 && cantHoras <= 8) {
                let horasLlenar = 8 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                vue.tabla8 = true;
            }
            if (cantHoras >= 9 && cantHoras <= 15) {
                let horasLlenar = 15 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                if (horasLlenar == 4) {
                    vue.llenarSeccion(vue, 4, horasRestante);
                }
                if (horasLlenar == 5) {
                    vue.llenarSeccion(vue, 5, horasRestante);
                }
                if (horasLlenar == 6) {
                    vue.llenarSeccion(vue, 6, horasRestante);
                }
                vue.tabla14 = true;
            }

        },
        llenarSeccion(vue, index, horasRestante) {
            for (var i = 0; i < index; i++) {
                let horaAdd = horasRestante[i];
                if (horaAdd !== undefined) {
                    let dias = [];
                    vue.getDescripcionByNroHora(horaAdd);
                    vue.getDiasHorasVacias(dias, vue.horaTmp);

                    let itemAdd = {dias: dias, hora: vue.horaTmp, numeroHora: horaAdd};
                    vue.horarios.horarios.push(itemAdd)
                    vue.horarios.horarios.sort(function (a, b) {
                        if (a.numeroHora < b.numeroHora) {
                            return -1;
                        }
                        if (a.numeroHora > b.numeroHora) {
                            return 1;
                        }
                        return 0
                    });
                }
            }

        },
        getDiasHorasVacias(dias, hora) {
            let lunes = {dia: "Lunes", hora: hora, secciones: []};
            let martes = {dia: "Martes", hora: hora, secciones: []};
            let miercoles = {dia: "Miercoles", hora: hora, secciones: []};
            let jueves = {dia: "Jueves", hora: hora, secciones: []};
            let viernes = {dia: "Viernes", hora: hora, secciones: []};
            let sabado = {dia: "Sabado", hora: hora, secciones: []};
            let domingo = {dia: "Domingo", hora: hora, secciones: []};

            dias.push(lunes);
            dias.push(martes);
            dias.push(miercoles);
            dias.push(jueves);
            dias.push(viernes);
            dias.push(sabado);
            dias.push(domingo);
        },
        getDescripcionByNroHora(numero) {
            let vue = this;
            $.ajax({
                async: false,
                method: 'GET',
                url: APP.url('academico/alumno/' + numero + '/hora'),
                contentType: "application/json",
                success: function (response) {
                    vue.horaTmp = response.data.descripcion;
                }
            });

        },
        cargaHorario() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/horario'),
                contentType: "application/json",
                success: function (response) {
                    $vue.horarios = response.data;
                    $vue.settingSeccionColor();
                    $vue.settingHoras();
                    $vue.validandoTabla();
              }
            });
        }
    }

})