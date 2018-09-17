Vue.component("historial-component", {
    template: "#historialComponent",
    props: {
        alumno: {},
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
        tieneMeritoCiclo(item) {
            if (item.cuadroHonorCiclo == "" && item.quintoSuperiorCiclo == "" && item.tercioSuperiorCiclo == "") {
                return false;
            }
            return true;
        },
        tieneMeritoFacultad(item) {
            if (item.cuadroHonorFacultad == "" && item.quintoSuperiorFacultad == "" && item.tercioSuperiorFacultad == "") {
                return false;
            }
            return true;
        },
        tieneMeritoCarrera(item) {
            if (item.cuadroHonorCarrera == "" && item.quintoSuperiorCarrera == "" && item.tercioSuperiorCarrera == "") {
                return false;
            }
            return true;
        },
        getMeritoCiclo(item) {
            if (item.cuadroHonorCiclo !== "") {
                return "C.Honor";
            } else if (item.quintoSuperiorCiclo !== "") {
                return "5to.Super.";
            } else if (item.tercioSuperiorCiclo !== "") {
                return "3cio.Super.";
            }
            return "";
        },
        getMeritoFacultad(item) {
            if (item.cuadroHonorFacultad !== "") {
                return "C.Honor";
            } else if (item.quintoSuperiorFacultad !== "") {
                return "5to.Super.";
            } else if (item.tercioSuperiorFacultad !== "") {
                return "3cio.Super.";
            }
            return "";
        },
        getMeritoCarrera(item) {
            if (item.cuadroHonorCarrera !== "") {
                return "C.Honor";
            } else if (item.quintoSuperiorCarrera !== "") {
                return "5to.Super.";
            } else if (item.tercioSuperiorCarrera !== "") {
                return "3cio.Super.";
            }
            return "";
        },
        verCiclo(item) {
            let noVer = {NMAT: "NMAT", RCI: "RCI", INH: "INH"};
            let estado = noVer[item.estadoEnum.name];
            if (estado === undefined) {
                return true;
            }
            return false;
        },
        verNota(nota) {
            let nn = nota.toFixed(2);
            if (nn.length == 4) {
                nn = "0" + nn;
            }
            return nn;
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
            var vue = this;
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
                                    notify(response.message, 'error');
                                } else {
                                    notify(response.message, 'error');
                                }
                                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                            }
                        });
                    }
                }
            });
        }
    }
});