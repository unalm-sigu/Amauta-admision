Vue.component("multiselect", window.VueMultiselect.default);

Vue.component("rev-historial-component", {
    template: "#revisarHistorialComponent",
    props: {
        alumno: {},
        tramite: {}
    },
    data: function () {
        return {
            aluCicCursos: [],
            tab: null, //alumno ciclo
            cursos: [],
            promedios: [],
            ciclos: [],
            verInfo: 1,
            cicloSelect: {},
            general: true,
            typeSearch: false,
            typeSearch3: false
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
        if (this.alumno != null && this.alumno.id != null) {
            console.log("mounted revisarHistorialComponent");
            console.dir(this.alumno);
            this.cargaHistorial();
        }

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
        nuevoCurso(alumnoCicloCursos) {
            console.log("alumnoCicloCursos");
            console.dir(alumnoCicloCursos);
            let lastIndex = ((alumnoCicloCursos == null) ? 0 : alumnoCicloCursos.length - 1);
            console.log("lastIndex " + lastIndex);
            var newObject = jQuery.extend(true, {}, alumnoCicloCursos[lastIndex]);
            //    newObject.id = alumnoCicloCursos.length;
            newObject.id = alumnoCicloCursos.length * -1;
            newObject.curso = {};
            newObject.creditos = "";
            newObject.nota = "";
            newObject.estaActivo = false;
            alumnoCicloCursos.push(newObject);
            console.log("paso");
        }, removerCicloCurso(aluCicloCurso) {
            console.log("removerCicloCurso");
            var removeIndex = this.tab.alumnoCicloCurso.map(function (item) {
                return item.id;
            }).indexOf(aluCicloCurso.id);
            console.log("removeIndex " + removeIndex);
            this.tab.alumnoCicloCurso.splice(removeIndex, 1);
        }, grabarCicloCursos() {
            //    Modal.showWait("Espere un momento...");
            let $vue = this;
            $.ajax({
                url: APP.url('academico/tramiteacademico/' + $vue.tramite.id + '/saveRevAlumnoCiclo'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.tab),
                success: function (response) {
                    if (response.success) {
                        // MODAL.hideWait();
                        $vue.cargaHistorial();
                    } else {
                        // MODAL.hideWait();
                    }
                },
                error: function (response) {
                    // MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verificarCiclo(item) {
            let $vue = this;
            // if ($vue.typeSearch3) {
            return (item.id == $vue.cicloSelect.id)
            //  }
            //  return true;
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
        verCiclo(item) {
            let noVer = {NMAT: "NMAT", RCI: "RCI", INH: "INH"};
            let estado = noVer[item.estadoEnum.name];
            if (estado === undefined) {
                return true;
            }
            return false;
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
            console.log("changeCiclo");
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/tramiteacademico/' + $vue.tramite.id + '/' + item.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.dir(response.data.promedios);
                        if (response.data.promedios != null) {
                            $vue.tab = response.data.promedios;
                        }
                    }
                }
            });
        },
        changeCicloCurso(aluCicCurso) {
            this.tab.alumnoCicloCurso.forEach(function (x) {
                if (x.id == aluCicCurso.id) {
                    return;
                }
                if (x.curso.id == aluCicCurso.curso.id) {
                    notify("El curso ya se encuentra agregado", "error");
                    aluCicCurso.curso = {};
                    return;
                }
            });
        },
        labelCiclo(item, id) {
            if (item.cicloAcademico == undefined) {
                return "";
            }
            return item.cicloAcademico.descripcion;
        },
        labelCicloAcademico( {codigo, descripcion}) {
            if (codigo == undefined && descripcion == undefined) {
                return "";
            }
            return `${codigo} - ${descripcion}`;
        },
        labelCurso( {codigo, nombre}) {
            if (codigo == undefined && nombre == undefined) {
                return "";
            }
            return `${codigo} - ${nombre}`;
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
                        console.log("Promedios " + $vue.promedios.length);
                        console.dir($vue.promedios);
                        $vue.aluCicCursos = response.data.cursos;
                        $vue.cicloSelect = {};
                        if ($vue.promedios.length > 0) {
                            $vue.cicloSelect = $vue.promedios[0];
                            $vue.changeCiclo($vue.cicloSelect);
                        }
                    }
                }
            });
            /*
             $.ajax({
             method: 'GET',
             url: APP.url('academico/tramiteacademico/' + this.alumno.id + '/loadRevisarHistorialComponent'),
             contentType: "application/json",
             success: function (response) {
             if (response.success) {
             $vue.cursos = response.data.cursos;
             }
             }
             });*/
        },
        asyncFindCursos(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/tramiteacademico/asyncFindCursos"),
                dataType: 'json',
                type: 'post',
                data: {
                    nombreCurso: nombre
                }
            }).then(response => {
                $vue.cursos = response.data;
            });
        },
        asyncFindCiclos(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url("academico/tramiteacademico/asyncFindCiclosAcad"),
                dataType: 'json',
                type: 'post',
                data: {
                    nombreCiclo: nombre,
                    alumno: $vue.alumno.id
                }
            }).then(response => {
                console.log("asyncFindCiclos");
                console.log("size " + response.data.length);
                $vue.ciclos = response.data;
            });
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
        }, validarNota(item, tipo) {
            if (!tipo) {
                return true;
            } else {
                return (item.estaAprobado == 1);
            }
        },
        styleNota(item) {
            if (item.estaAprobado == 1) {
                return "text-primary";
            } else {
                return "text-danger";
            }
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
        revertirCambios(item) {
            var vue = this;
            console.log(vue.tab);
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/revertirCambioHistorial'),
                contentType: "application/json",
                data: JSON.stringify(vue.tab),
                success: function (response) {
                    if (response.success) {
                        vue.cargaHistorial();
                        notify(response.message, 'success');
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }
            });
        },
        deleteCicloCurso(item) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar el curso?',
                buttons: {
                    confirm: {label: 'Si, Eliminar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/tramiteacademico/' + vue.tramite.id+ '/deleteCicloCurso'),
                            contentType: "application/json",
                            data: JSON.stringify(item),
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