Vue.component("historial-component", {
    template: "#historialComponent",
    props: {
        alumno: {},
        showTitle: true,
        showactions: {required: false, default: false}
    },
    data: function () {
        return {
            cursos: [],
            promedios: [],
            verInfo: 1,
            typeSearch: false,
            typeSearch2: false,
            typeSearch3: false,
            typeSearch4: false,
            cicloSelect: {},
            general: true,
        }
    },
    computed: {
        titulo() {
            return 'Historial Académico';
        }
    },
    beforeMount() {
    },
    mounted() {
    },
    watch: {
        alumno(newValue) {
            if (this.alumno != null && this.alumno.id != null) {
                if (this.alumno.id != newValue.id) {
                    this.cargaHistorial();
                }
            }
        }
    },
    methods: {
        generarReporteHistorial() {
            let $vue = this;

            let allAprobado = false;

            if ($vue.typeSearch) {
                allAprobado = true;
            }
            var tipo = "";
            if ($vue.typeSearch2) { // listado general de cursos
                tipo = 'LIST';
            } else if ($vue.typeSearch4) {
                tipo = 'PROM';
            } else {
                tipo = 'CICLO';
            }


            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/getToken'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        var token = response.data.token;
                        var url = response.data.url;

                        location.href = url + "/publico/historialPdf/" + $vue.alumno.id + "?notas=" + allAprobado + "&tipo=" + tipo + "&token=" + token;

                    }
                }
            });


        },
        colspanResumen(item) {
            if (item.carrera.codigo == item.carrera.facultad.codigo) {
                return 2;
            }
            return 3;
        },
        classResumen(item) {
            if (item.carrera.codigo == item.carrera.facultad.codigo) {
                return "col-md-3";
            }
            return "col-md-2";
        },
        verCarrera(item) {
            if (item.carrera.codigo == item.carrera.facultad.codigo) {
                return false;
            }
            return true;
        },
        tieneMerito(tipo, item) {
            if (tipo == "CICLO") {
                if (item.cuadroHonorCiclo == "" && item.quintoSuperiorCiclo == "" && item.tercioSuperiorCiclo == "") {
                    return false;
                }
                return true;
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultad == "" && item.quintoSuperiorFacultad == "" && item.tercioSuperiorFacultad == "") {
                    return false;
                }
                return true;
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarrera == "" && item.quintoSuperiorCarrera == "" && item.tercioSuperiorCarrera == "") {
                    return false;
                }
                return true;
            }
            return false;
        },
        tieneMeritoNivel(tipo, item) {
            if (tipo == "CICLO") {
                if (item.cuadroHonorCicloNivel == "" && item.quintoSuperiorCicloNivel == "" && item.tercioSuperiorCicloNivel == "") {
                    return false;
                }
                return true;
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultadNivel == "" && item.quintoSuperiorFacultadNivel == "" && item.tercioSuperiorFacultadNivel == "") {
                    return false;
                }
                return true;
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarreraNivel == "" && item.quintoSuperiorCarreraNivel == "" && item.tercioSuperiorCarreraNivel == "") {
                    return false;
                }
                return true;
            }
            return false;
        },
        getOrdenMerito(tipo, item) {
            let separator = "/";
            if (tipo == "CICLO") {
                if (item.cuadroHonorCiclo !== "") {
                    return item.cuadroHonorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                } else if (item.quintoSuperiorCiclo !== "") {
                    return item.quintoSuperiorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                } else if (item.tercioSuperiorCiclo !== "") {
                    return item.tercioSuperiorCiclo + separator + item.controlMeritoCiclo.alumnosComputados;
                }
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultad !== "") {
                    return item.cuadroHonorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                } else if (item.quintoSuperiorFacultad !== "") {
                    return item.quintoSuperiorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                } else if (item.tercioSuperiorFacultad !== "") {
                    return item.tercioSuperiorFacultad + separator + item.controlMeritoFacultad.alumnosComputados;
                }
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarrera !== "") {
                    return item.cuadroHonorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                } else if (item.quintoSuperiorCarrera !== "") {
                    return item.quintoSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                } else if (item.tercioSuperiorCarrera !== "") {
                    return item.tercioSuperiorCarrera + separator + item.controlMeritoCarrera.alumnosComputados;
                }
            }
            return "";
        },
        getOrdenMeritoNivel(tipo, item) {
            let $vue = this;
            let separator = "/";
            if (tipo == "CICLO") {
                if (item.cuadroHonorCicloNivel !== "") {
                    return item.cuadroHonorCiclo + separator + $vue.getComputados(tipo, item);
                } else if (item.quintoSuperiorCicloNivel !== "") {
                    return item.quintoSuperiorCicloNivel + separator + $vue.getComputados(tipo, item);
                } else if (item.tercioSuperiorCicloNivel !== "") {
                    return item.tercioSuperiorCicloNivel + separator + $vue.getComputados(tipo, item);
                }
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultadNivel !== "") {
                    return item.cuadroHonorFacultadNivel + separator + $vue.getComputados(tipo, item);
                } else if (item.quintoSuperiorFacultadNivel !== "") {
                    return item.quintoSuperiorFacultadNivel + separator + $vue.getComputados(tipo, item);
                } else if (item.tercioSuperiorFacultadNivel !== "") {
                    return item.tercioSuperiorFacultadNivel + separator + $vue.getComputados(tipo, item);
                }
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarreraNivel !== "") {
                    return item.cuadroHonorCarreraNivel + separator + $vue.getComputados(tipo, item);
                } else if (item.quintoSuperiorCarreraNivel !== "") {
                    return item.quintoSuperiorCarreraNivel + separator + $vue.getComputados(tipo, item);
                } else if (item.tercioSuperiorCarreraNivel !== "") {
                    return item.tercioSuperiorCarreraNivel + separator + $vue.getComputados(tipo, item);
                }
            }
            return "";
        },
        getComputados(tipo, item) {
            if (tipo == "CICLO") {
                if (item.nivel == 1) {
                    return item.controlMeritoCiclo.computadosNivel1;
                } else if (item.nivel == 2) {
                    return item.controlMeritoCiclo.computadosNivel2;
                } else if (item.nivel == 3) {
                    return item.controlMeritoCiclo.computadosNivel3;
                } else if (item.nivel == 4) {
                    return item.controlMeritoCiclo.computadosNivel4;
                } else if (item.nivel == 5) {
                    return item.controlMeritoCiclo.computadosNivel5;
                }
            } else if (tipo == "FAC") {
                if (item.nivel == 1) {
                    return item.controlMeritoFacultad.computadosNivel1;
                } else if (item.nivel == 2) {
                    return item.controlMeritoFacultad.computadosNivel2;
                } else if (item.nivel == 3) {
                    return item.controlMeritoFacultad.computadosNivel3;
                } else if (item.nivel == 4) {
                    return item.controlMeritoFacultad.computadosNivel4;
                } else if (item.nivel == 5) {
                    return item.controlMeritoFacultad.computadosNivel5;
                }
            } else if (tipo == "CARR") {
                if (item.nivel == 1) {
                    return item.controlMeritoCarrera.computadosNivel1;
                } else if (item.nivel == 2) {
                    return item.controlMeritoCarrera.computadosNivel2;
                } else if (item.nivel == 3) {
                    return item.controlMeritoCarrera.computadosNivel3;
                } else if (item.nivel == 4) {
                    return item.controlMeritoCarrera.computadosNivel4;
                } else if (item.nivel == 5) {
                    return item.controlMeritoCarrera.computadosNivel5;
                }
            }
            return "";
        },
        getMerito(tipo, item) {
            if (tipo == "CICLO") {
                if (item.cuadroHonorCiclo !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCiclo !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCiclo !== "") {
                    return "3cio.Super.";
                }
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultad !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorFacultad !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorFacultad !== "") {
                    return "3cio.Super.";
                }
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarrera !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCarrera !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCarrera !== "") {
                    return "3cio.Super.";
                }
            }
            return "";
        },
        getMeritoNivel(tipo, item) {
            if (tipo == "CICLO") {
                if (item.cuadroHonorCicloNivel !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCicloNivel !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCicloNivel !== "") {
                    return "3cio.Super.";
                }
            } else if (tipo == "FAC") {
                if (item.cuadroHonorFacultadNivel !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorFacultadNivel !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorFacultadNivel !== "") {
                    return "3cio.Super.";
                }
            } else if (tipo == "CARR") {
                if (item.cuadroHonorCarreraNivel !== "") {
                    return "C.Honor";
                } else if (item.quintoSuperiorCarreraNivel !== "") {
                    return "5to.Super.";
                } else if (item.tercioSuperiorCarreraNivel !== "") {
                    return "3cio.Super.";
                }
            }
            return "";
        },
        verCiclo(item) {
            let noVer = {NMAT: "NMAT", RCI: "RCI", ANCI: "ANCI", INH: "INH"};
            let estado = noVer[item.estadoEnum.name];
            if (estado === undefined) {
                return true;
            }
            return false;
        },
        verNota(notax) {
            return APP.verNota(notax);
        },
        classCiclo(item) {
            if (item.estadoEnum.name == 'NMAT') {
                return "text-warning";
            } else if (item.cicloAcademico.tipoEnum.name == 'REG') {
                return "bold";
            } else {
                return "text-muted";
            }
        },
        changeCiclo(item) {
            let url = location.href + "#" + item.id;
            location.href = "#" + item.id;
        },
        labelCiclo(item, id) {
            if (item.cicloAcademico == undefined) {
                return "";
            }
            return item.cicloAcademico.descripcion;
        },
        cargaHistorial() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.promedios = response.data.promedios;
                        $vue.cursos = response.data.cursos;
                        $vue.cicloSelect = {};
                        if ($vue.promedios.length > 0) {
                            $vue.cicloSelect = $vue.promedios[0];
                        }
                    }
                }
            });
        },
        classScrollable() {
            let $vue = this;
            if ($vue.typeSearch3) {
                return "";
            }
            return "pre-scrollable";
        },
        verificarCiclo(item) {
            let $vue = this;
            if ($vue.typeSearch3) {
                return (item.id == $vue.cicloSelect.id)
            }
            return true;
        },
        changeSearch() {
            let $vue = this;
            $vue.verificarShow();
        },
        changeSearch2() {
            let $vue = this;
            if (!$vue.typeSearch2) {
                $vue.general = true;
            } else {
                $vue.general = false;
                if ($vue.typeSearch3) {
                    $vue.typeSearch3 = false;
                }
            }

            $vue.verificarShow();
        },
        changeSearch3() {
            let $vue = this;
            $vue.verificarShow();
        },
        changeSearch4() {
            let $vue = this;
            $vue.verificarShow();
        },
        verificarShow() {
            let $vue = this;
            if ($vue.typeSearch4) {
                $vue.verInfo = 4;
            } else {
                if (!$vue.typeSearch2 && !$vue.typeSearch3) {
                    $vue.verInfo = 1;
                }
                if ($vue.typeSearch2 && !$vue.typeSearch3) {
                    $vue.verInfo = 3;
                }
                if (!$vue.typeSearch2 && $vue.typeSearch3) {
                    $vue.verInfo = 1;
                }
            }
        },
        styleNota(item) {
            if (item.estaAprobado == 1) {
                return "text-primary";
            } else {
                return "text-danger";
            }
        },
        validarNota(item, tipo) {
            if (!tipo) {
                return true;
            } else {
                return (item.estaAprobado == 1);
            }
        },
        calcularPromedio: function () {
            let vue = this;
            if (vue.alumno.id == null) {
                return;
            }

            bootbox.confirm({
                message: '¿Seguro que desea recalcular el promedio?',
                buttons: {
                    confirm: {label: 'Si, Calcular', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/alumno/calcularpromedio'),
                            data: {id: vue.alumno.id},
                            success: function (response) {
                                if (response.success) {
                                    vue.cargaHistorial();
                                    vue.reloadAlumno();
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                            }
                        });
                    }
                }
            });
        },
        reloadAlumno() {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/' + vue.alumno.id + '/data'),
                success: function (response) {
                    if (response.success) {
                        vue.alumno = response.data;
                        $global.$emit('update-alumno', vue.alumno);
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});