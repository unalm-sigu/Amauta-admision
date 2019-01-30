Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/cursomasivos'),
        cursomasivosURL: APP.url('rolexamen/cursomasivos/list'),
        configAddAulasModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Asignar Aulas',
            cancelbtn: 'Cancelar',
            okbtn: 'Asignar',
            modalsize: 'modal-lg'
        },
        aulasAsignadasModal: {
            id: 'modalAulasAsignadas',
            header: true,
            title: 'Aulas Asignadas',
            showaccept: "false",
            modalsize: 'modal-lg'
        },
        seccionesModal: {
            id: 'modalSecciones',
            header: true,
            title: 'Secciones Asignadas',
            showaccept: "false"
        },
        rolExamenes: null,
        rolesExamenes: JSON.parse(jRolexamenes),
        curso: null,
        cursos: [],
        cursosMasivosByRolExamenes: [],
        modulo: {},
        modulos: [],
        cursoMasivoExamen: [],
        aulasModulo: [],
        aulas: [],
        aula: null,
        secciones: [],
        tipoAccion: {
            CURSO: "CURSO",
            SECCION: "SECCION",
            DOCENTE: "DOCENTE",
            ALUMNO: "ALUMNO"
        },
        semanasExamen: [],
        semanaExamenActiva: null,
        grupoActivo: null,
        rolExamenesLogger: null
    },
    mounted() {
        let $vue = this;
        $vue.loadModulos();
        if (jRolExamenes != null) {
            this.rolExamenes = JSON.parse(jRolExamenes);
            this.loadCursosMasivosByRoleExamen();
        }
    },
    computed: {
        agregarCursoDisponible() {
            return this.rolExamenes && this.rolExamenes.isEstadoConfigurando && (this.rolExamenes.isSituacionConfigurarHorario || this.rolExamenes.isSituacionConfigurarCursoMasivo)
        },
        modificarAulaHorarioDisponible() {
            return this.rolExamenes && this.rolExamenes.isEstadoConfigurando && this.rolExamenes.isSituacionConfigurarGrupoRegular;
        },
        verAulaHorarioDisponible() {
            return this.rolExamenes && ((this.rolExamenes.isEstadoConfigurando && this.rolExamenes.isSituacionConfigurarGrupoEspecial) || !this.rolExamenes.isEstadoConfigurando);
        },
        incluirExcluirMoverDisponible() {
            let situacionesValidas = this.rolExamenes && (this.rolExamenes.isSituacionConfigurarGrupoRegular || this.rolExamenes.isSituacionConfigurarCursoMasivo || this.rolExamenes.isSituacionConfigurarGrupoEspecial);
            return this.rolExamenes && (this.rolExamenes.isEstadoModificando || (this.rolExamenes.isEstadoConfigurando && situacionesValidas));
        }
    },
    methods: {
        loadCurso(nombre) {
            let $vue = this;

            $.ajax({
                method: "POST",
                url: APP.url("rolexamen/cursomasivos/" + $vue.rolExamenes.id + "/loadCurso"),
                data: {nombre: nombre}
            }).then(response => {
                if (response.success) {
                    $vue.cursos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        loadModulos() {
            let $vue = this;
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/allModulos")
            }).then(response => {
                if (response.success) {
                    $vue.modulos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        allAulasModulo(nombre) {
            let $vue = this;
            console.log(nombre);
            console.log("Buscar Módulos");
            $.ajax({
                url: APP.url("rolexamen/cursomasivos/allAulasModulo"),
                data: JSON.stringify(nombre),
                dataType: 'json',
                contentType: "application/json",
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        $vue.aulasModulo = response.data;
                    }
                }
            });
        },
        agregarCursoMasivo() {
            let $vue = this;
            let cursoMasivo = {
                curso: $vue.curso,
                rolExamenes: $vue.rolExamenes
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/save"),
                data: JSON.stringify(cursoMasivo)
            }).then(response => {
                if (response.success) {
                    $vue.loadCursosMasivosByRoleExamen();
                    $vue.curso = null;
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
                MODAL.hideWait();
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, eliminarGruposMasivos() {
            let vue = this;
            bootbox.confirm({
                message: "¿Si continua se perdera el avance de los cursos masivos, grupos regulares y especiales?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/eliminarCursosMasivos`, vue.rolExamenes)
                                .then(response => {
                                    if (response.data.success) {
                                        // notify(response.data.message, 'info');
                                        vue.loadCursosMasivosByRoleExamen();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });


        },
        loadCursosMasivosByRoleExamen() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("rolexamen/cursomasivos/list"),
                data: JSON.stringify($vue.rolExamenes)
            }).then(response => {
                if (response.success) {
                    $vue.cursosMasivosByRolExamenes = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verAsignarAulas(item) {
            let $vue = this;
            $vue.modulo = {};
            $vue.aulasModulo = [];
            $vue.cursoMasivoExamen = jQuery.extend(true, {}, item);
            $vue.aulas = $vue.cursoMasivoExamen.aulasCursosMasivos;
            $vue.$refs.addAulasModal.open();
        }, cursoMasivoSecciones(item) {
            location.href = `${this.URL}/secciones/${item.id}`;
        },
        verAulasAsignadas(item) {
            let $vue = this;
            $vue.cursoMasivoExamen = jQuery.extend(true, {}, item);
            $vue.aulas = $vue.cursoMasivoExamen.aulasCursosMasivos;
            $vue.$refs.modalAulasAsignadas.open();
        },
        verSeccionesAsignadas(item) {
            let $vue = this;
            $vue.cursoMasivoExamen = jQuery.extend(true, {}, item);
            $vue.secciones = $vue.cursoMasivoExamen.seccionesCursosMasivos;
            $vue.$refs.tblSeccionesCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
            $vue.$refs.tblSeccionesCursosMasivos.loadRemoteData();
            $vue.$refs.modalSecciones.open();
        },
        saveAulas() {
            var form = $("#formAulas");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea asignar estas aulas?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: "POST",
                            contentType: "application/json",
                            url: APP.url("rolexamen/cursomasivos/saveAulas"),
                            data: JSON.stringify($vue.cursoMasivoExamen)
                        }).then(response => {
                            if (response.success) {
                                $vue.$refs.addAulasModal.close();
                                $vue.loadCursosMasivosByRoleExamen();
                                $vue.curso = null;
                                notify(response.message, "info")
                            } else {
                                notify(response.message, 'error');
                            }
                            MODAL.hideWait();
                        }, error => {
                            notify(MESSAGES.errorComunicacion, 'error');
                        });
                    }
                }
            });
        },
        eliminar(item) {
            let $vue = this;
            var del = item;
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar el registro curso <b>' + item.curso.nombre + '</b>?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('rolexamen/cursomasivos/eliminar'),
                            type: 'POST',
                            async: true,
                            data: {id: del.id},
                            success: function (response) {
                                if (response.success) {
                                    $vue.loadCursosMasivosByRoleExamen();
                                    $vue.curso = null;
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                                MODAL.hideWait();
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }, excluir(item, tipoAccion) {
            console.dir(item);
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea excluir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/${tipoAccion}/excluir`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        /* obj.estadoEnum = {
                                         "name": "EXC",
                                         "value": "Excluido"
                                         };
                                         obj.estado = obj.estadoEnum.name;*/

                                        switch (tipoAccion) {
                                            case vue.tipoAccion.SECCION:
                                                vue.$refs.tblSeccionesCursosMasivos.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.tblAlumnoCursosMasivos.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.DOCENTE:
                                                vue.$refs.tblDocentesCursosMasivos.loadRemoteData();
                                                break;
                                        }
                                        vue.loadCursosMasivosByRoleExamen();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        }, incluir(item, tipoAccion) {
            let vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea incluir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/${tipoAccion}/incluir`, item)
                                .then(response => {
                                    if (response.data.success) {
                                        /* obj.estadoEnum = {
                                         "name": "EXC",
                                         "value": "Excluido"
                                         };
                                         obj.estado = obj.estadoEnum.name;*/

                                        switch (tipoAccion) {
                                            case vue.tipoAccion.SECCION:
                                                vue.$refs.tblSeccionesCursosMasivos.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.ALUMNO:
                                                vue.$refs.tblAlumnoCursosMasivos.loadRemoteData();
                                                break;
                                            case vue.tipoAccion.DOCENTE:
                                                vue.$refs.tblDocentesCursosMasivos.loadRemoteData();
                                                break;
                                        }
                                        vue.loadCursosMasivosByRoleExamen();
                                    }
                                    MODAL.hideWait();
                                });
                    }
                }
            });
        },
        noExiste(aula) {
            let $vue = this;
            for (var i = 0; i < $vue.aulas.length; i++) {
                if (aula.id == $vue.aulas[i].aula.id) {
                    return false;
                }
            }
            return true;
        },
        addAula(aula) {
            if (!this.modificarAulaHorarioDisponible) {
                return;
            }
            let $vue = this;
            $vue.aulas.push({id: '', aula: aula});
            $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas + 1;
            $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas + aula.capacidadAula;
        },
        removeAula(aula, idx) {
            if (!this.modificarAulaHorarioDisponible) {
                return;
            }
            let $vue = this;
            if (aula.id == '') {
                $vue.aulas.splice(idx, 1);
                $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas - 1;
                $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas - aula.aula.capacidadAula;

            } else {
                bootbox.confirm({
                    message: '¿Está seguro que desea eliminar esta aula?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                        cancel: {label: 'No', className: 'btn-link'}
                    },
                    callback: function (result) {
                        if (result) {
                            $vue.aulas.splice(idx, 1);
                            $vue.cursoMasivoExamen.aulas = $vue.cursoMasivoExamen.aulas - 1;
                            $vue.cursoMasivoExamen.capacidadAulas = $vue.cursoMasivoExamen.capacidadAulas - aula.aula.capacidadAula;
                        }
                    }
                });
            }

        },
        styleBgColor(aula) {
            if (aula.id == '') {
                return "background-color: #FFC300;";
            }
            return "background-color: #21B021;";
        },
        styleBgColorSeccion(seccion) {
            if (seccion.id == '') {
                return "background-color: #FFC300;";
            }
            return "background-color: #21B021;";
        },
        viewSecciones() {
            let $vue = this;
            $vue.$refs.modalSecciones.close();
        },
        viewAulas() {
            let $vue = this;
            $vue.$refs.modalAulasAsignadas.close();
        },
        verAsignarHorario(item) {
            let $vue = this;
            $vue.cursoMasivoExamen = jQuery.extend(true, {}, item);
            $vue.grupoActivo = $vue.cursoMasivoExamen.grupoHorasExamen;
            this.semanaExamenActiva = null;
            if ($vue.cursoMasivoExamen.hasOwnProperty("grupoHorasExamen.id")) {
                this.semanaExamenActiva = $vue.cursoMasivoExamen.grupoHorasExamen.semanaExamen;

                $("#semana" + $vue.semanaExamenActiva.numeroSemana).click();
                $("#semana" + $vue.semanaExamenActiva.numeroSemana).tab('show');

            }
            this.listarHorarioSemanal();
            $vue.$refs.modalHorarios.open();
        }, saveHorarioExamen() {
            let $vue = this;
            //   $vue.grupoActivo.semanaExamen = this.semanaExamenActiva;
            $vue.cursoMasivoExamen.grupoHorasExamen = {id: $vue.grupoActivo.id};
            AXIOS.post(`${this.URL}/saveHorarioExamen`, $vue.cursoMasivoExamen)
                    .then(response => {
                        if (response.data.success) {
                            this.loadCursosMasivosByRoleExamen();
                        } else {
                            if (response.data.data != null) {
                                this.rolExamenesLogger = response.data.data;
                                this.$refs.infoModal.title = this.rolExamenesLogger.message;
                                this.$refs.infoModal.open();
                            }
                        }
                        // MODAL.hideWait();
                    });
            $vue.$refs.modalHorarios.close();
        }, listarHorarioSemanal() {
            AXIOS.post(`${APP.url('rolexamen/plantillahorario')}/listarHorarioSemanal`, this.rolExamenes)
                    .then(response => {
                        if (response.data.success) {
                            this.semanasExamen = response.data.data;
                            if (this.semanaExamenActiva != null) {
                                this.seleccionarSemana(this.semanaExamenActiva);
                            } else {
                                console.log("###############");
                                console.dir(this.semanasExamen[0]);
                                this.seleccionarSemana(this.semanasExamen[0]);
                            }
                        }
                        // MODAL.hideWait();
                    });
        }, fechaGrupoHoraItem(fechaGrupoHora) {
            if (this.grupoActivo != null && fechaGrupoHora.grupoHorasExamen.id == this.grupoActivo.id) {
                return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
            }

            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        }, seleccionarSemana(semana) {
            console.dir(semana);
            let vue = this;
            this.semanasExamen.forEach(function (x) {
                if (x.id == semana.id) {
                    x.selected = true;
                    vue.semanaExamenActiva = x;
                } else {
                    x.selected = false;
                }
            });
        }, seleccionarGrupoHorasExamen(dia, hora, semExamen) {
            let fechaHoraGrupoExamen = semExamen.tblHorarioSeamanaExamen.fechasHorasGrupos[dia.id + '_' + hora.id];
            console.log("fecha hora grupo examen seleccionado");
            console.dir(fechaHoraGrupoExamen);
            console.log("semana examen");
            console.dir(semExamen);
            this.grupoActivo = fechaHoraGrupoExamen.grupoHorasExamen;
        }, verDocentes(cursoMasivo) {
            this.cursoMasivoExamen = cursoMasivo;
            this.$refs.tblDocentesCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
            this.$refs.tblDocentesCursosMasivos.loadRemoteData();
            this.$refs.docenteModal.open();
        }, verAlumnos(cursoMasivo) {
            this.cursoMasivoExamen = cursoMasivo;
            this.$refs.tblAlumnoCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
            this.$refs.tblAlumnoCursosMasivos.loadRemoteData();
            this.$refs.alumnoModal.open();
        }
    }
});
