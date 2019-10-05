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
            modalsize: 'modal-lg',
            showaccept: true
        },
        aulasAsignadasModal: {
            id: 'modalAulasAsignadas',
            header: true,
            title: 'Aulas Asignadas',
            modalsize: 'modal-lg'
        },
        seccionesModal: {
            id: 'modalSecciones',
            header: true,
            title: 'Secciones Asignadas'
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
        rolExamenesLogger: null,
        gposHorasExamen: [],
        gposFechas: [],
        conflictos: [],
        btnVerificar: true,
        btnTexto: "Verificar horarios",
        grupoHoras: [],
        cursoMasivoTempo: {},
        cursoMasivoSelect: {},
        configCambioAulasGpo: VUE_MODAL.structFormAjax({
            id: "idModalCambioAulasGpo",
            modalsize: "modal-lg"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        }),
        observaciones: {cantidad: 0, message: "", rows: 4}
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
        allowAsignarAula() {
            return this.rolExamenes && this.rolExamenes.isEstadoConfigurando
                    && (this.rolExamenes.isSituacionConfigurarGrupoRegular || this.rolExamenes.isSituacionAsignarHorarioCursosMasivos || this.rolExamenes.isSituacionConfigurarGrupoEspecial);
        },
        agregarCursoDisponible() {
            return this.rolExamenes && this.rolExamenes.isEstadoConfigurando && (this.rolExamenes.isSituacionHorarioConfirmado || this.rolExamenes.isSituacionConfigurarCursoMasivo)
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
            };
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
        },
        eliminarGruposMasivos() {
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
        },
        cursoMasivoSecciones(item) {
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
        },
        excluir(item, tipoAccion) {
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
        },
        incluir(item, tipoAccion) {
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
            if (!this.allowAsignarAula) {
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
        },
        saveHorarioExamen() {
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
        },
        listarHorarioSemanal() {
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
        },
        fechaGrupoHoraItem(fechaGrupoHora) {
            if (this.grupoActivo != null && fechaGrupoHora.grupoHorasExamen.id == this.grupoActivo.id) {
                return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
            }

            if (fechaGrupoHora.revisado == 'SI') {
                return "border-color:#600D63; background-color:#27AE60;color:#FFFFFF;";
            } else if (fechaGrupoHora.revisado == 'NO') {
                return "border-color:#600D63; background-color:#E74C3C;color:#FFFFFF;";
            }
            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;";
        },
        seleccionarSemana(semana) {
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
        },
        seleccionarGrupoHorasExamen(dia, hora, semExamen) {
            let $vue = this;
            let fechaHoraGrupoExamen = semExamen.tblHorarioSeamanaExamen.fechasHorasGrupos[dia.id + '_' + hora.id];
            if (fechaHoraGrupoExamen.revisado == "NO") {
                $vue.rolExamenesLogger = $vue.conflictos[dia.id + '_' + hora.id];
                $vue.$refs.infoModal.title = $vue.rolExamenesLogger.message;
                $vue.$refs.infoModal.open();
                notify("Este grupo ya fue descartado por contener conflictos", "error");
                return;
            }
            console.log("fecha hora grupo examen seleccionado");
            console.dir(fechaHoraGrupoExamen);
            console.log("semana examen");
            console.dir(semExamen);
            $vue.grupoActivo = fechaHoraGrupoExamen.grupoHorasExamen;
        },
        verDocentes(cursoMasivo) {
            this.cursoMasivoExamen = cursoMasivo;
            this.$refs.tblDocentesCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
            this.$refs.tblDocentesCursosMasivos.loadRemoteData();
            this.$refs.docenteModal.open();
        },
        verAlumnos(cursoMasivo) {
            this.cursoMasivoExamen = cursoMasivo;
            this.$refs.tblAlumnoCursosMasivos.ajaxdata = {cursoMasivo: this.cursoMasivoExamen.id};
            this.$refs.tblAlumnoCursosMasivos.loadRemoteData();
            this.$refs.alumnoModal.open();
        },
        verificarTodosGpos() {
            let $vue = this;
            let gposHorasExamen = [];
            $vue.gposFechas = [];
            $vue.conflictos = [];
            $vue.gposHorasExamen = [];
            $vue.btnVerificar = false;
            $vue.btnTexto = '<i class="fa fa-spinner fa-spin"></i> Verificando...';

            for (var i = 0; i < $vue.semanasExamen.length; i++) {
                let semExamen = $vue.semanasExamen[i];
                let dias = semExamen.tblHorarioSeamanaExamen.dias;
                let horas = semExamen.tblHorarioSeamanaExamen.horas;

                for (var j = 0; j < dias.length; j++) {
                    let dia = dias[j];
                    for (var k = 0; k < horas.length; k++) {
                        let hora = horas[k];
                        let fhg = semExamen.tblHorarioSeamanaExamen.fechasHorasGrupos[dia.id + '_' + hora.id];
                        if (fhg != undefined) {
                            gpo = fhg.grupoHorasExamen;
                            gposHorasExamen[gpo.id] = gpo;
                            let listaHdia = $vue.gposFechas[gpo.id];
                            if (listaHdia == undefined) {
                                listaHdia = [];
                            }
                            listaHdia.push({idDiaHora: dia.id + '_' + hora.id, semana: i});
                            $vue.gposFechas[gpo.id] = listaHdia;
                        }
                    }
                }
            }

            for (var key in gposHorasExamen) {
                let gpo = gposHorasExamen[key];
                $vue.gposHorasExamen.push(gpo);

                let listaHdia = $vue.gposFechas[gpo.id];
                for (var i = 0; i < listaHdia.length; i++) {
                    let idx = listaHdia[i];
                    let fhg = $vue.semanasExamen[idx.semana].tblHorarioSeamanaExamen.fechasHorasGrupos[idx.idDiaHora];
                    fhg.revisado = "";
                }
            }

            $vue.verificarGrupo(0);
        },
        verificarGrupo(idxGpo) {
            let $vue = this;
            if ($vue.gposHorasExamen.length <= idxGpo) {
                $vue.btnVerificar = true;
                $vue.btnTexto = 'Verificar horarios';
                notify("Se terminó de revisar todos los horarios", "info");
                return;
            }
            $vue.cursoMasivoExamen.grupoHorasExamen = {id: $vue.gposHorasExamen[idxGpo].id};
            axios.post(`${this.URL}/revisarGpoHorasExamenCursoMasivo`, $vue.cursoMasivoExamen)
                    .then(response => {
                        if (response.data.success) {
                            let rpta = response.data.data;
                            let gpo = $vue.gposHorasExamen[idxGpo];
                            let listaHdia = $vue.gposFechas[gpo.id];
                            for (var i = 0; i < listaHdia.length; i++) {
                                let idx = listaHdia[i];
                                let fhg = $vue.semanasExamen[idx.semana].tblHorarioSeamanaExamen.fechasHorasGrupos[idx.idDiaHora];
                                fhg.revisado = rpta.grupoHorasExamen.revisado;
                                $vue.conflictos[idx.idDiaHora] = rpta.conflictos;
                            }

                        }
                        $vue.verificarGrupo(idxGpo + 1);
                    });
        },
        styleAlumnoVsAulas(item) {
            if (item.capacidadAulas >= item.alumnosCount) {
                return "text-primary";
            }
            return "text-danger";
        },
        cambiarAulasGpo(item) {
            let $vue = this;
            $vue.cursoMasivoTempo = Object.assign({}, item, {});
            $vue.cursoMasivoSelect = Object.assign({}, item, {});
            $vue.loadGrupos();
            $vue.loadModulos();
            $vue.aulas = $vue.cursoMasivoTempo.aulasCursosMasivos;
            $vue.observaciones = {cantidad: 0, message: "", rows: 4};
            $vue.aulasModulo = [];
            $vue.modulo = {};

            $vue.$refs.modalCambioAulasGpo.open();
        },
        loadGrupos() {
            let $vue = this;
            AXIOS.post(`${$vue.URL}/allGrupoHE`, $vue.rolExamenes)
                    .then(response => {
                        if (response.data.success) {
                            $vue.grupoHoras = response.data.data;
                        }
                    });
        },
        loadModulosVerificados() {
            let $vue = this;
            AXIOS.post(`${$vue.URL}/allModulosVerificados`, $vue.cursoMasivoTempo)
                    .then(response => {
                        if (response.data.success) {
                            $vue.modulos = response.data.data;
                        }
                    });
        },
        cLabelGrupo(item) {
            if (item.grupoHoras) {
                return item.grupoHoras.codigo;
            }
        },
        buscarAulas() {
            let $vue = this;
            if ($vue.modulo.id == undefined) {
                return;
            }

            let moduloQuery = Object.assign({}, $vue.modulo, {});
            moduloQuery.cursoMasivo = $vue.cursoMasivoTempo;

            $vue.$refs.modalCambioAulasGpo.beginProcessing();
            axios.post(`${$vue.URL}/allAulasVerificadasByModulo`, moduloQuery)
                    .then(response => {
                        $vue.$refs.modalCambioAulasGpo.confirmReaction(false);
                        if (response.data.success) {
                            $vue.aulasModulo = response.data.data;
                        }
                    }).catch(e => {
                $vue.$refs.modalCambioAulasGpo.confirmReaction(false);
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        addAulaCM(aula) {
            let $vue = this;
            $vue.aulas.push({id: '', aula: aula});
            $vue.cursoMasivoTempo.aulas = $vue.cursoMasivoTempo.aulas + 1;
            $vue.cursoMasivoTempo.capacidadAulas = $vue.cursoMasivoTempo.capacidadAulas + aula.capacidadAula;
        },
        removeAulaCM(aula, idx) {
            let $vue = this;
            if (aula.id == '') {
                $vue.aulas.splice(idx, 1);
                $vue.cursoMasivoTempo.aulas = $vue.cursoMasivoTempo.aulas - 1;
                $vue.cursoMasivoTempo.capacidadAulas = $vue.cursoMasivoTempo.capacidadAulas - aula.aula.capacidadAula;

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
                            $vue.cursoMasivoTempo.aulas = $vue.cursoMasivoTempo.aulas - 1;
                            $vue.cursoMasivoTempo.capacidadAulas = $vue.cursoMasivoTempo.capacidadAulas - aula.aula.capacidadAula;
                        }
                    }
                });
            }

        },
        verSaveCambioAulasGpo() {

            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea asignar estas aulas?";
            $vue.configConfirmAction.okaction = $vue.saveCambioAulasGpo;
            $vue.$refs.modalConfirmAction.open();

            if (1 == 1) {
                return;
            }


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
            $vue.$refs.modalCambioAulasGpo.beginProcessing();
        },
        saveCambioAulasGpo() {
            let $vue = this;
            $vue.$refs.modalConfirmAction.close();

            $vue.$refs.modalCambioAulasGpo.beginProcessing();
            axios.post(`${$vue.URL}/cmbiarCambioAulasGrupo`, $vue.cursoMasivoTempo)
                    .then(response => {
                        $vue.$refs.modalCambioAulasGpo.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.loadCursosMasivosByRoleExamen();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                        let restricc = response.data.data;
                        if (restricc != null) {
                            $vue.observaciones.cantidad = restricc.length;
                            $vue.observaciones.rows = restricc.length > 7 ? 7 : restricc.length;
                            for (var i = 0; i < restricc.length; i++) {
                                $vue.observaciones.message += (i + 1) + ") " + restricc[i] + "\n";
                            }
                        }
                    }).catch(e => {
                $vue.$refs.modalCambioAulasGpo.confirmReaction(false);
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        styleAulaDisponible(aula) {
            if (aula.tieneCruces) {
                return "background-color: #E74C3C;";
            }
            return "background-color: #33ACFF;";
        }

    }
});
