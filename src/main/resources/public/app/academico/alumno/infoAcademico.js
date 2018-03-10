let app = new Vue({
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
        cursos: [],
        coloresCurso: [],
        secciones: [],
        horas: [],
        classInit: 'curso size-1 ',
        tabla4: false,
        tabla8: false,
        tabla14: false,
        horarios: [],
        horaTmp: '',
        alumnoInfo: {}

    },
    created() {
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
        $vue.cargaInicio();
    },
    computed: {
        mostrarCreditos() {
            if (this.alumnoInfo.creditosCursados !== undefined && this.alumnoInfo.creditosCursados !== 0) {
                return true;
            }
            return false;
        },
        mostrarOrientacion() {
            if (this.alumno.planCurricular.orientacionCarrera !== undefined) {
                return true;
            }
            return false;
        },
    },
    beforeMount() {
        let $vue = this;

        $vue.cursos = JSON.parse(cursosJson);
    },
    mounted: function () {
        let $vue = this;
        $vue.alumnoCursoTemp = $vue.alumnoCurso;


        $vue.verMalla();
    },
    methods: {
        cicloSelecc: function (cicloSelecc) {
            let $vue = this;
            $vue.searchCiclo = cicloSelecc;
            $vue.cargaAvance();
        },
        updateTabs: function (tab) {

            let $vue = this;
            $vue.tabId = tab.id;
            if ($vue.tabId === 2) {
                this.cargaHistorial();
            } else if ($vue.tabId === 3) {
                this.cargaAvance();
            } else if ($vue.tabId === 4) {
                this.cargaMatricula();
            } else if ($vue.tabId === 5) {
                $vue.cargaHorario();
            } else if ($vue.tabId === 1) {
                this.cargaInicio();
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
        cargaInicio() {

            let $vue = this;

            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/alumno'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.alumnoInfo = response.data.alumno;
                    }

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
        },
        styleMenu(index) {
            let $vue = this;
            let id = $vue.tabId;
            if (index == id) {
                return "active";
            }
        },

        verMalla() {
            let $vue = this;
            var id = $vue.alumno.planCurricular.id;
            if (id === undefined)
                return;
            $.ajax({
                url: APP.url('academico/planCurricular/dataCurricula'),
                type: 'POST',
                async: true,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        $vue.buildMalla(response.data);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        buildMalla(ciclos) {
            let $vue = this;
            var ww = 170;
            var hh = 60;
            var padx = 30;
            var pady = 40;
            var pad = 60;
            var wwLine = 2;
            var wwBoldLine = 6;

            var colorBG = {GEN: "#F39C12", OBL: "#1E8449", ELC: "#AAB7B8", ELF: "#AAB7B8", ELE: "#AAB7B8"};
            var colorLetra = {GEN: "#fff", OBL: "#fff", ELC: "#fff", ELF: "#fff", ELE: "#fff"};
            var colorLine = "#E74C3C";
            var colorDot = "#34495E";
            var colorArrow = "#D7DBDD";

            var maxRows = 0;
            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    maxRows = (maxRows > cursos[row].numeroCurso) ? maxRows : cursos[row].numeroCurso;
                }
            }

            $("#divMalla").html("");

            var draw = SVG('divMalla').size((ww + 2 * padx) * ciclos.length, pad + (hh + pady) * maxRows);
            for (var col = 0; col < ciclos.length; col++) {
                var text = draw.text("Ciclo " + ciclos[col].numeroRomano).addClass("h4");
                text.move(((ww + 2 * padx) / 2 + (ww + 2 * padx) * col - 30) + 'px', '15px');
            }

            var lazos = {};
            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var x1 = padx + (ww + 2 * padx) * col;
                    var x2 = x1 + ww;
                    var yc = pad + (hh + pady) * (cursos[row].numeroCurso - 1) + hh / 2;

                    lazos[cursos[row].id] = {
                        "left-x": x1,
                        "right-x": x2,
                        "y": yc,
                        "requisitos": []
                    };
                }
            }

            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var req = cursos[row].requisitos;
                    for (var r = 0; r < req.length; r++) {
                        var x1 = lazos[cursos[row].id]["left-x"];
                        var y1 = lazos[cursos[row].id]["y"];
                        var x2 = lazos[req[r].idReq]["right-x"];
                        var y2 = lazos[req[r].idReq]["y"];

                        var linea = draw.line(x1, y1, x2, y2).stroke({color: colorLine, width: wwLine});
                    }
                }
            }

            for (var col = 0; col < ciclos.length; col++) {
                var cursos = ciclos[col].cursos;
                for (var row = 0; row < cursos.length; row++) {
                    var x1 = padx + (ww + 2 * padx) * col;
                    var x2 = x1 + ww;
                    var xc = x1 + ww / 2 - 60;
                    var tempXC = xc + 'px';
                    var y1 = pad + (hh + pady) * (cursos[row].numeroCurso - 1);
                    var y2 = y1 + hh;
                    var yc = y1 + hh / 2 - 15;
                    var tempYC = yc + 'px';

                    var polygon = draw.rect(ww, hh).radius(5).fill(colorBG[cursos[row]["tipo"]]).move(x1, y1).stroke({color: colorDot, width: 1});
                    var dot1 = draw.rect(10, 10).fill("#fff").move(x1 - 5, yc - 5).stroke({color: colorDot, width: 1});
                    var dot2 = draw.rect(10, 10).fill("#fff").move(x2 - 5, yc - 5).stroke({color: colorDot, width: 1});


                    var tncur = draw.text(cursos[row]["numeroCurso"] + "").move((x1 + 4) + 'px', (y2 - 26) + 'px').fill(colorLetra[cursos[row]["tipo"]]).style("font-size", "12px");
                    var group = draw.group();
                    group.add(polygon);
                    group.add(dot1);
                    group.add(dot2);
                    group.add(tncur);

                    var req = cursos[row].requisitos;
                    if (req.length > 0) {
                        var dot3 = draw.rect(10, 10).fill(colorDot).move(x1 - 5, yc - 5);
                        group.add(dot3);
                    }

                    for (var r = 0; r < req.length; r++) {
                        var x22 = lazos[req[r].idReq]["right-x"];
                        var y22 = lazos[req[r].idReq]["y"];
                        var dot4 = draw.rect(10, 10).fill(colorDot).move(x22 - 5, y22 - 5);
                        group.add(dot4);
                    }

                    var data = $vue.getConteCurso(cursos[row].curso, cursos[row].codigo, cursos[row].creditos);
                    if (data.length == 2) {
                        var y1 = yc - 8;
                        var y1 = y1 + 'px';
                        var y2 = yc + 8;
                        var y2 = y2 + 'px';

                        var t1 = draw.text(data[0]).move(tempXC, y1).fill(colorLetra[cursos[row]["tipo"]]);
                        var t2 = draw.text(data[1]).move(tempXC, y2).fill(colorLetra[cursos[row]["tipo"]]);
                        group.add(t1);
                        group.add(t2);

                    } else if (data.length == 3) {
                        var y1 = (yc - 17) + 'px';
                        var y2 = (yc - 1) + 'px';
                        var y3 = (yc + 15) + 'px';
                        var t1 = draw.text(data[0]).move(tempXC, y1).fill(colorLetra[cursos[row]["tipo"]]);
                        var t2 = draw.text(data[1]).move(tempXC, y2).fill(colorLetra[cursos[row]["tipo"]]);
                        var t3 = draw.text(data[2]).move(tempXC, y3).fill(colorLetra[cursos[row]["tipo"]]);
                        group.add(t1);
                        group.add(t2);
                        group.add(t3);
                    }

                    group.data({"idCurso": cursos[row].id});
                    group.style('cursor', 'pointer');
                    group.mouseover(function () {
                        var idCurso = this.data("idCurso");
                        var reqs = lazos[idCurso]["requisitos"];
                        for (var i = 0; i < reqs.length; i++) {
                            draw.get(reqs[i]).show();
                        }
                    });
                    group.mouseout(function () {
                        var idCurso = this.data("idCurso");
                        var reqs = lazos[idCurso]["requisitos"];
                        for (var i = 0; i < reqs.length; i++) {
                            draw.get(reqs[i]).hide();
                        }
                    });
                }
            }
        },
        getConteCurso(cur, cod, cred) {
            var data = [];
            if (cur.length <= 22) {
                data[0] = cur;
                data[1] = cod + " - " + cred + " crédito";
                data[1] += (cred == 1) ? "" : "s";
                return data;
            }

            var idx = 0;
            var partes = cur.split(" ");
            data[idx] = "";
            for (var i = 0; i < partes.length; i++) {
                if (data[idx].length + partes[i].length < 22) {
                    data[idx] += (data[idx].length == 0 ? "" : " ") + partes[i];
                } else if (idx < 1) {
                    idx++;
                    data[idx] = partes[i].substring(0, 22);
                } else if (idx == 1) {
                    data[idx] += (data[idx].length == 0 ? "" : " ") + partes[i];
                    data[idx] = data[idx].substring(0, 20) + "..";
                }
            }
            idx++;
            data[idx] = cod + " - " + cred + " crédito";
            data[idx] += (cred == 1) ? "" : "s";
            return data;
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
        },
        verDetalleCurso(id) {
            location.href = APP.url("academico/cursosmatriculados/" + id + "/curso");
        }
    }

});
