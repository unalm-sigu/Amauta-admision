Vue.component("malla-component", {
    template: "#mallaComponent",
    props: {
        alumno: {
            planCurricular: {
                id: 0
            }
        }
    },
    created() {
    },
    computed: {
        titulo() {
            if (this.alumno.planCurricular.id === undefined || this.alumno.planCurricular.id === null || this.alumno.planCurricular.id === '')
                return '';
            return 'Malla Curricular de ' + this.alumno.planCurricular.carrera.nombre + ': ' + this.alumno.planCurricular.cicloInicioVigencia.descripcion;
        },
        idPlan() {
            return this.alumno.planCurricular.id;
        }
    },
    watch: {
        idPlan(val) {
            let $vue = this;
            if (val !== undefined && val !== null) {
                $vue.verMalla();
            }
        }
    },
    methods: {
        verMalla() {
            let $vue = this;
            if (this.alumno.planCurricular === undefined || this.alumno.planCurricular === null)
                return;
            var id = this.alumno.planCurricular.id;
            $.ajax({
                url: APP.url('academico/alumno/dataCurricula'),
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
                        console.log("asdas");
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
        }
    }
});