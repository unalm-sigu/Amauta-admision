$(function () {

    var CursoForm = {
//        idCurso: $("#idCurso").val(),
        init: function () {

            $(".numerico").numeric({negative: false});
//            $('[name="tipoCurso"]').select2({placeholder: "Seleccione el tipo curso"});
//
//            if (CursoForm.idCurso == '') {
//                $('[name="modalidadEstudio.id"]').select2({
//                    placeholder: "Seleccione la modalidad de estudio"
//                }).on("select2-selecting", function (e) {
//                    console.log("selecting val=" + e.val + " choice=" + JSON.stringify(e.choice));
//                });
//            }
//            $('[name="tipoCurricula"]').select2({allowClear: true, placeholder: "Seleccione el tipo curricula"});
//            $('[name="idIdioma"]').select2({placeholder: "Seleccione el idioma"});
//
//
//            if ($("[name='departamentoAcademico.id']").val() != '') {
//                CursoForm.loadCoordinadores($("[name='departamentoAcademico.id']").val())
//            }
//            CursoForm.loadDepartamentos();
//
//            var modCodigo = $("#codigoModalidad").val();
//            console.log(modCodigo)
//
//            if (modCodigo == 'PRE') {
//                //CursoForm.loadNiveles(modCodigo);
//                CursoForm.validarDivEspecialidad(modCodigo);
//            } else if (modCodigo == 'EPG') {
//                CursoForm.loadEspecialidades(modCodigo);
//                CursoForm.validarDivEspecialidad(modCodigo);
//            }
//
//            CursoForm.tipoCredito($("[name='tipoCredito']:checked"));

        },
//        loadDepartamentos: function () {
//            if (CursoForm.idCurso != '') {
//                return;
//            }
//
//            $("[name='departamentoAcademico.id']").select2({
//                allowClear: true,
//                placeholder: "Seleccione un departamento",
//                minimumInputLength: 1,
//                ajax: {
//                    url: APP.url("comun/buscar/allDepartamentoAcademico"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
//                    }
//                },
//                formatResult: function (info) {
//                    var data = '<span class="block bold">' + info.nombre + '</span>';
//                    data += '<span class="block">Facultad de ' + info.facultad + '</span>';
//                    return data;
//                },
//                formatSelection: function (info) {
//                    return info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//        },
//        loadCoordinadores: function (idDpto) {
//            $("[name='coordinador.id']").select2({
//                placeholder: "Seleccione un coordinador",
//                allowClear: true,
//                minimumInputLength: 1,
//                ajax: {
//                    url: APP.url("comun/buscar/allCoordinadores"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {dpto: idDpto, nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
//                    }
//                },
//                formatResult: function (info) {
//                    return  info.nombre;
//                },
//                formatSelection: function (info) {
//                    return info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//        },
//        saveUpdate: function () {
//            var form = $("#formularioCurso");
//            if (!form.parsley().validate()) {
//                return;
//            }
//            if ($("[name='tipoCredito']:checked").val() == 'FIJO') {
//                var credTotal = +$("[name='creditos']").val();
//                var credprac = +$("[name='creditosPractica']").val();
//                var credTeo = +$("[name='creditosTeoria']").val();
//
//                if (credTotal != credprac + credTeo) {
//                    notify("La suma de créditos no coincide con el total.", "error");
//                    $("[name='creditosPractica']").focus();
//                    return;
//                }
//            }
//
//            $.ajax({
//                url: APP.url('academico/curso/save'),
//                type: 'POST',
//                async: true,
//                data: form.serialize(),
//                success: function (response) {
//                    if (response.success) {
//                        notify(response.message, "info");
//                        setTimeout(function () {
//                            location.href = APP.url("academico/curso");
//                        }, 1500);
//                    } else {
//                        notify(response.message, "error");
//                    }
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        changeModalidad: function ($this, e) {
//            var codModalidad = $this.find(":selected").data("codigo");
//            $('[name="nivel"]').select2('val', "");
//            $('[name="carrera.id"]').select2("val", "");
//            $('[name="tipoCurricula"]').select2("val", "");
//
//            CursoForm.loadNiveles(codModalidad);
//            CursoForm.loadEspecialidades(codModalidad);
//        },
//        loadNiveles: function (codigo) {
//            console.log("creando niveles")
//            $('[name="nivel"]').select2({
//                allowClear: true,
//                placeholder: "Seleccione",
//                minimumInputLength: -1,
//                ajax: {
//                    url: APP.url("academico/curso/nivel"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {codigo: codigo, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        var datos = {
//                            id: element.val(),
//                            text: element.val()
//                        };
//                        callback(datos);
//                    }
//                },
//                formatResult: function (info) {
//                    return  info.text;
//                },
//                formatSelection: function (info) {
//                    return info.text;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//        },
//        loadEspecialidades: function (codigo) {
//            CursoForm.validarDivEspecialidad(codigo);
//            $('[name="carrera.id"]').select2({
//                allowClear: true,
//                placeholder: "Seleccione la especialidad",
//                minimumInputLength: 1,
//                ajax: {
//                    url: APP.url("academico/curso/allCarreras"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {codigo: codigo, nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
//                    }
//                },
//                formatResult: function (info) {
//                    var data = '<span class="block bold">' + info.nombre + ' - ' + info.codigo + '</span>';
//                    data += '<span class="block"> ' + (info.tipoEstudio != '' ? (info.tipoEstudio + ' - ') : '') + info.modalidadEstudio + '</span>';
//                    return data;
//                },
//                formatSelection: function (info) {
//                    return info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//        },
//        validarDivEspecialidad: function (codigo) {
//            if (codigo == 'PRE') {
//                $(".divEspecialidad").addClass("hide");
//                $(".divTipoCurricula").removeClass("hide");
//            } else {
//                $(".divEspecialidad").removeClass("hide");
//                $(".divTipoCurricula").addClass("hide");
//            }
//        },
//        addIdioma: function () {
//            var html = $.templates("#divNombreCurso").render({id: Math.random()});
//
//            $(".bodyTabla tr:last").before(html);
//            $(".bodyTabla").find('[name="idIdioma"]').select2();
//        },
//        existeIdioma: function ($this, e) {
//            e.preventDefault();
//            var idioma = $this.find("option:selected").val();
//            var count = 0;
//
//            $(".bodyTabla tr").each(function () {
//
//                var td = $(this).find("td:first");
//                var valor = td.find('[name="idIdioma"]').select2();
//
//                if (idioma == valor.val()) {
//                    count++;
//                    if (count == 2) {
//                        bootbox.alert("Idioma existente!");
//                        CursoForm.deleteRowIdioma($this)
//                    }
//                }
//            });
//        },
//        deleteRowIdioma: function ($this) {
//            var tr = $this.closest("tr");
//            tr.remove();
//        },
//        validandoHoras: function ($this) {
//            var tipo = $this.val();
//            console.log("tipo:: " + tipo)
//            if (tipo == 'TEO') {
//                $('[name="horasTeoria"]').attr("required", true);
//                $('[name="horasTeoria"]').attr("readonly", false);
//                $('[name="horasPractica"]').attr("readonly", true);
//                $('[name="horasPractica"]').val(0);
//            } else if (tipo == 'PRA') {
//                $('[name="horasPractica"]').attr("required", true);
//                $('[name="horasPractica"]').attr("readonly", false);
//                $('[name="horasTeoria"]').attr("readonly", true);
//                $('[name="horasTeoria"]').val(0);
//            } else {
//                $('[name="horasPractica"]').attr("required", true);
//                $('[name="horasPractica"]').attr("readonly", false);
//                $('[name="horasTeoria"]').attr("readonly", false);
//                $('[name="horasTeoria"]').attr("required", true);
//                $('[name="horasTeoria"]').val(0);
//            }
//        },
//        tipoCredito($this) {
//            console.log($this.val());
//            if ($this.val() == 'VAR') {
//                $("#credPract").addClass('hide');
//                $("#credTeoria").addClass('hide');
//                $("[name='creditosPractica']").val('');
//                $("[name='creditosPractica']").attr('required', false);
//                $("[name='creditosTeoria']").val('');
//                $("[name='creditosTeoria']").attr('required', false);
//            } else if ($this.val() == 'FIJO') {
//                $("[name='creditosTeoria']").attr('required', true);
//                $("[name='creditosPractica']").attr('required', true);
//                $("#credPract").removeClass('hide');
//                $("#credTeoria").removeClass('hide');
//            }
//        }
    };

    CursoForm.init();

//    $("body").delegate(".save-update-curso", "click", function (e) {
//        CursoForm.saveUpdate(e);
//    });
//    $("body").delegate("[name='coordinador.id']", "change", function () {
//        $(this).parsley().destroy();
//    });
//    $("body").delegate("[name='departamentoAcademico.id']", "change", function () {
//        $(this).parsley().destroy();
//    });
//    $("body").delegate("[name='tipoCurso']", "change", function () {
//        $(this).parsley().destroy();
//    });
//    $("body").delegate("[name='nivel']", "change", function () {
//        $(this).parsley().destroy();
//    });
//    $("body").delegate("[name='tipoCurricula']", "change", function () {
//        $(this).parsley().destroy();
//    });
//    $("body").delegate("[name='modalidadEstudio.id']", "change", function (e) {
//        CursoForm.changeModalidad($(this), e);
//    });
//    $("body").delegate("[name='departamentoAcademico.id']", "change", function () {
//        $(this).parsley().destroy();
//        CursoForm.loadCoordinadores($(this).val());
//    });
//    $("body").delegate(".add-idioma", "click", function () {
//        CursoForm.addIdioma();
//    });
//    $("body").delegate('[name="idIdioma"]', "change", function (e) {
//        CursoForm.existeIdioma($(this), e);
//    });
//    $("body").delegate(".delete-idioma", "click", function () {
//        CursoForm.deleteRowIdioma($(this));
//    });
//    $("body").delegate('[name="tipoCurso"]', "change", function () {
//        CursoForm.validandoHoras($(this));
//    });
//    $("body").delegate('[name="tipoCredito"]', "change", function () {
//        CursoForm.tipoCredito($(this));
//    });
});

Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#pageCursoVUE',
    data: {
        curso: JSON.parse(cursoJson),
        modalidades: JSON.parse(modalidadesJson),
        tiposCurso: JSON.parse(tiposCursoJson),
        tiposCurricula: JSON.parse(tiposCurriculaJson),
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
        isLoadingDocentes: false,
        siEncuestar: true,
        siCargaAdicional: true,
    },
    created: function () {
        let $vue = this;
        for (var i = 1; i < 7; i++) {
            $vue.nivelesPregrado.push({id: i});
        }
        for (var i = 6; i < 10; i++) {
            $vue.nivelesPosgrado.push({id: i});
        }

        $vue.modalidadCurso = $vue.curso.modalidadEstudio;
        $vue.dptoCurso = $vue.curso.departamentoAcademico;
        $vue.tipoCursoEnum = $vue.curso.tipoCursoEnum;
        $vue.nivelCurso = {id: $vue.curso.nivel};
        $vue.siEncuestar = !$vue.curso.noEncuestar;
        $vue.siCargaAdicional = !$vue.curso.noCargaAdicional;


        if ($vue.curso.tipoCurricula == '') {
            $vue.curso.tipoCurriculaEnum = {};
        }

        $vue.tipoCurriculaEnum = $vue.curso.tipoCurriculaEnum;
        $vue.setTipoCredito();
    },
    mounted: function () {

    },
    methods: {
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
//            $vue.curso.tipoCurso = item.name;
//            $vue.curso.tipoCursoEnum = item;

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
        }
    }
});