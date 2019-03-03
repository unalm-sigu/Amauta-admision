$(function () {
    $(".numerico").numeric({negative: false});
});

Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#pageCursoVUE',
    data: {
        curso: JSON.parse(cursoJson),
        modalidades: JSON.parse(modalidadesJson),
        tiposCurso: JSON.parse(tiposCursoJson),
        tiposCurricula: JSON.parse(tiposCurriculaJson),
        tiposCarpeta: JSON.parse(tiposCarpetaJson),
        idiomas: JSON.parse(idiomasJson),
        departamentos: JSON.parse(departamentosJson),
        carreras: JSON.parse(carrerasJson),
        nivelCurso: {id: ""},
        tipoCursoEnum: {},
        tipoCurriculaEnum: {},
        nivelesCurso: [],
        nivelesPregrado: [],
        nivelesPosgrado: [],
        modalidadCurso: {id: ""},
        dptoCurso: {id: ""},
        carreraCurso: {id: ""},
        docentes: [],
        idiomaCurso: [],
        isLoadingDocentes: false,
        siEncuestar: true,
        siCargaAdicional: true,
        tipoCarpetaTeoria: {},
        tipoCarpetaPractica: {}
    },
    created: function () {
        let $vue = this;
        for (var i = 1; i < 7; i++) {
            $vue.nivelesPregrado.push({id: i});
        }
        for (var i = 6; i < 10; i++) {
            $vue.nivelesPosgrado.push({id: i});
        }

        $vue.tipoCarpetaTeoria = $vue.curso.tipoCarpetaTeoria;
        $vue.tipoCarpetaPractica = $vue.curso.tipoCarpetaPractica;
        $vue.modalidadCurso = $vue.curso.modalidadEstudio;
        $vue.dptoCurso = $vue.curso.departamentoAcademico;
        $vue.tipoCursoEnum = $vue.curso.tipoCursoEnum;
        $vue.nivelCurso = {id: $vue.curso.nivel};
        $vue.siEncuestar = !$vue.curso.noEncuestar;
        $vue.siCargaAdicional = !$vue.curso.noCargaAdicional;
        $vue.idiomaCurso = $vue.curso.nombreCurso;


        if ($vue.curso.tipoCurricula == '') {
            $vue.curso.tipoCurriculaEnum = {};
        }

        for (var i = 0; i < $vue.curso.nombreCurso.length; i++) {
            $vue.curso.nombreCurso[i].locked = true;
        }

        $vue.tipoCurriculaEnum = $vue.curso.tipoCurriculaEnum;
        $vue.setTipoCredito();
    },
    mounted: function () {

    },
    methods: {
        desbloquearIdioma(index) {
            let $vue = this;
            let nuevo = $vue.idiomaCurso[index];
            nuevo.locked = false;
            $vue.$set($vue.idiomaCurso, index, nuevo);
        },
        bloquearIdioma(index) {
            let $vue = this;
            let nuevo = $vue.idiomaCurso[index];
            nuevo.locked = true;
            $vue.$set($vue.idiomaCurso, index, nuevo);
        },
        addIdioma() {
            let $vue = this;
            let nuevo = {
                id: "",
                nombre: "",
                idioma: {id: "", nombre: ""},
                curso: {id: $vue.curso.id},
            };
            $vue.curso.nombreCurso.push(nuevo);
        },
        removeIdioma(index) {
            let $vue = this;
            $vue.idiomaCurso.splice(index, 1);
        },
        setTipoCredito() {
            let $vue = this;
            if ($vue.curso.tipoCredito == 'FIJO' && $vue.tipoCursoEnum.name == 'TEO') {
                if ($vue.curso.creditosPractica == '') {
                    $vue.curso.creditosPractica = "0";
                }
                if ($vue.curso.horasPractica == '') {
                    $vue.curso.horasPractica = "0";
                }
                if ($vue.curso.horasPracticaVerano == '') {
                    $vue.curso.horasPracticaVerano = "0";
                }
            }
            if ($vue.curso.tipoCredito == 'FIJO' && $vue.tipoCursoEnum.name == 'PRA') {
                if ($vue.curso.creditosTeoria == '') {
                    $vue.curso.creditosTeoria = "0";
                }
                if ($vue.curso.horasTeoria == '') {
                    $vue.curso.horasTeoria = "0";
                }
                if ($vue.curso.horasTeoriaVerano == '') {
                    $vue.curso.horasTeoriaVerano = "0";
                }
            }
            if ($vue.curso.tipoCredito == 'VAR') {
                if ($vue.curso.creditosPractica == '') {
                    $vue.curso.creditosPractica = "0";
                }
                if ($vue.curso.horasPractica == '') {
                    $vue.curso.horasPractica = "0";
                }
                if ($vue.curso.horasPracticaVerano == '') {
                    $vue.curso.horasPracticaVerano = "0";
                }
                if ($vue.curso.creditosTeoria == '') {
                    $vue.curso.creditosTeoria = "0";
                }
                if ($vue.curso.horasTeoria == '') {
                    $vue.curso.horasTeoria = "0";
                }
                if ($vue.curso.horasTeoriaVerano == '') {
                    $vue.curso.horasTeoriaVerano = "0";
                }
            }
        },
        setHorasTeoria() {
            let $vue = this;
            if ($vue.curso.horasTeoria == "") {
                return;
            }
            if ($vue.curso.creditosTeoria == "") {
                $vue.curso.creditosTeoria = parseInt($vue.curso.horasTeoria);
            }
            $vue.setCreditos();
        },
        setHorasPractica() {
            let $vue = this;
            if ($vue.curso.horasPractica == "") {
                return;
            }
            if ($vue.curso.creditosPractica == "") {
                $vue.curso.creditosPractica = parseInt(parseInt($vue.curso.horasPractica) / 2);
            }
            $vue.setCreditos();
        },
        isCreditosTeoria() {
            let $vue = this;
            if ($vue.tipoCurriculaEnum.name != 'REG') {
                return false;
            }
            return $vue.isTeoria();
        },
        isCreditosPractica() {
            let $vue = this;
            if ($vue.tipoCurriculaEnum.name != 'REG') {
                return false;
            }
            return $vue.isPractica();
        },
        isTeoria() {
            let $vue = this;
            if ($vue.tipoCursoEnum.name == 'TEO') {
                return true;
            } else if ($vue.tipoCursoEnum.name == 'TEOPRA') {
                return true;
            }
            return false;
        },
        isPractica() {
            let $vue = this;
            if ($vue.tipoCursoEnum.name == 'PRA') {
                return true;
            } else if ($vue.tipoCursoEnum.name == 'TEOPRA') {
                return true;
            }
            return false;
        },
        searchDocentes(search) {
            let $vue = this;
            $vue.isLoadingDocentes = true;
            $.ajax({
                url: APP.url('academico/curso/' + $vue.dptoCurso.id + '/allDocentes'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    $vue.isLoadingDocentes = false;
                    if (response.success) {
                        $vue.docentes = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        isComodinAdicionalExterno(tipo) {
            if (tipo == 'ADIE') {
                return true
            } else if (tipo == 'COMD') {
                return true
            }
            return false;
        },
        setCreditos() {
            let $vue = this;
            let teo = isNaN(parseInt($vue.curso.creditosTeoria)) ? 0 : parseInt($vue.curso.creditosTeoria);
            let pra = isNaN(parseInt($vue.curso.creditosPractica)) ? 0 : parseInt($vue.curso.creditosPractica);
            $vue.curso.creditos = teo + pra;
        },
        setTipoCurricula(item) {
            let $vue = this;
            if ($vue.curso.tipoCurricula != 'REG') {
                $vue.curso.creditosTeoria = "";
                $vue.curso.creditosPractica = "";
                $vue.curso.creditos = 0;
                $vue.curso.creditosVariables = 0;
            }
        },
        setTipoCurso(item) {
            let $vue = this;

            if ($vue.isTeoria() && $vue.isPractica()) {
            } else if ($vue.isTeoria() && !$vue.isPractica()) {
                $vue.curso.creditosPractica = 0;
                $vue.curso.horasPractica = 0;
                $vue.curso.horasPracticaVerano = 0;
            } else if (!$vue.isTeoria() && $vue.isPractica()) {
                $vue.curso.creditosTeoria = 0;
                $vue.curso.horasTeoria = 0;
                $vue.curso.horasTeoriaVerano = 0;
            } else {
                $vue.curso.creditosTeoria = "";
                $vue.curso.horasTeoria = "";
                $vue.curso.horasTeoriaVerano = "";
                $vue.curso.creditosPractica = "";
                $vue.curso.horasPractica = "";
                $vue.curso.horasPracticaVerano = "";
            }
            $vue.setCreditos();
        },
        verDocente(item) {
            if (item.id == "") {
                return null;
            }
            return item.persona.apellidosNombres;
        },
        verCarrera(item) {
            if (item.id == "") {
                return null;
            }
            return item.tipoEnum.value + " - " + item.nombre;
        },
        changeModalidad(item) {
            let $vue = this;
            if (item.id == 1) {
                $vue.nivelesCurso = $vue.nivelesPregrado;
            } else if (item.id == 2) {
                $vue.nivelesCurso = $vue.nivelesPosgrado;
            }

            let existe = false;
            for (var i = 0; i < $vue.nivelesCurso.length; i++) {
                var obj = $vue.nivelesCurso[i];
                if (obj.id == $vue.nivelCurso) {
                    existe = true;
                }
            }
            if (!existe) {
                $vue.nivelCurso = {id: ""};
            }
        },
        save() {
            var form = $("#formCurso");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            $vue.curso.tipoCurso = $vue.tipoCursoEnum.name;
            $vue.curso.tipoCursoEnum = $vue.tipoCursoEnum;
            $vue.curso.nivel = $vue.nivelCurso.id;
            $vue.curso.departamentoAcademico = $vue.dptoCurso;
            $vue.curso.carrera = $vue.carreraCurso;
            $vue.curso.modalidadEstudio = $vue.modalidadCurso;
            $vue.curso.tipoCurricula = $vue.tipoCurriculaEnum.name;
            $vue.curso.tipoCurriculaEnum = $vue.tipoCurriculaEnum;
            $vue.curso.noEncuestar = !$vue.siEncuestar;
            $vue.curso.noCargaAdicional = !$vue.siCargaAdicional;
            $vue.curso.tipoCarpetaTeoria = $vue.tipoCarpetaTeoria;
            $vue.curso.tipoCarpetaPractica = $vue.tipoCarpetaPractica;

            $.ajax({
                url: APP.url('academico/curso/save'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.curso),
                success(response) {
                    if (response.success) {
                        $vue.reloadCurso(response.data);
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error(response) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        reloadCurso(id) {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/curso/' + id + "/find"),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                success(response) {
                    if (response.success) {
                        $vue.curso = response.data;
                        $vue.modalidadCurso = $vue.curso.modalidadEstudio;
                        $vue.dptoCurso = $vue.curso.departamentoAcademico;
                        $vue.tipoCursoEnum = $vue.curso.tipoCursoEnum;
                        $vue.tipoCurriculaEnum = $vue.curso.tipoCurriculaEnum;
                        $vue.curso.tipoCarpetaTeoria = $vue.tipoCarpetaTeoria;
                        $vue.curso.tipoCarpetaPractica = $vue.tipoCarpetaPractica;
                        $vue.nivelCurso = {id: $vue.curso.nivel};
                        $vue.siEncuestar = !$vue.curso.noEncuestar;
                        $vue.siCargaAdicional = !$vue.curso.noCargaAdicional;

                        var url = window.location.href;
                        var newUrl = url.replace("academico/curso/nuevo", "academico/curso/" + $vue.curso.id + "/editar");
                        history.pushState(null, null, newUrl);

                    } else {
                        notify(response.message, "error");
                    }
                },
                error(response) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        saveIdioma(nomCurso, index) {
            let $vue = this;

            bootbox.confirm({
                message: "¿Está seguro que desea guardar esta traducción?",
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $.ajax({
                            url: APP.url('academico/curso/saveIdioma'),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            async: true,
                            data: JSON.stringify(nomCurso),
                            success(response) {
                                if (response.success) {
                                    let nuevo = response.data;
                                    nuevo.locked = true;
                                    $vue.$set($vue.idiomaCurso, index, nuevo);
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error(response) {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        deleteIdioma(nomCurso, index) {
            let $vue = this;

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar esta traducción?",
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $.ajax({
                            url: APP.url('academico/curso/deleteIdioma'),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            async: true,
                            data: JSON.stringify(nomCurso),
                            success(response) {
                                if (response.success) {
                                    $vue.idiomaCurso.splice(index, 1);
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error(response) {   
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    }
});