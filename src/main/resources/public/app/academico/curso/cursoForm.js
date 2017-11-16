$(function () {

    var CursoForm = {
        init: function () {
            $(".numerico").numeric({negatice: false});
            $('[name="tipoCurso"]').select2({placeholder: "Seleccione el tipo curso"});
            $('[name="modalidadEstudio.id"]').select2({placeholder: "Seleccione la modalidad de estudio"});
            $('[name="tipoCurricula"]').select2({allowClear: true, placeholder: "Seleccione el tipo curricula"});
            $('[name="idIdioma"]').select2({placeholder: "Seleccione el idioma"});

            if ($("[name='departamentoAcademico.id']").val() != '') {
                CursoForm.loadCoordinadores($(this))
            }

            CursoForm.loadDepartamentos();

            var modEstudio = $("[name='modalidadEstudio.id']");
            var modCodigo = modEstudio.find("option:selected").attr("rel");

            if (modEstudio.val() != '' && modCodigo == 'PRE') {
                CursoForm.loadNiveles(modEstudio);
                CursoForm.validarDivEspecialidad(modCodigo);
            } else if (modEstudio.val() != '' && modCodigo == 'EPG') {
                CursoForm.loadEspecialidades(modEstudio);
                CursoForm.validarDivEspecialidad(modCodigo);
            }

        },
        loadDepartamentos: function () {
            $("[name='departamentoAcademico.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un departamento",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("comun/buscar/allDepartamentoAcademico"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    var data = '<span class="block bold">' + info.nombre + '</span>';
                    data += '<span class="block"><strong>Facultad:</strong> ' + info.facultad + '</span>';
                    return data;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        loadCoordinadores: function ($this) {
            $("[name='coordinador.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un coordinador",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("comun/buscar/allCoordinadores"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {dpto: $this.val(), nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        saveUpdate: function () {
            var form = $("#formularioCurso");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        },
        loadNiveles: function ($this) {
            var codigo = $this.find("option:selected").attr("rel");

            $('[name="nivel"]').select2({
                allowClear: true,
                placeholder: "Seleccione",
                minimumInputLength: -1,
                ajax: {
                    url: APP.url("academico/curso/nivel"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {codigo: codigo, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            text: element.val()
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return  info.text;
                },
                formatSelection: function (info) {
                    return info.text;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        loadEspecialidades: function ($this) {
            var codigo = $this.find("option:selected").attr("rel");
            CursoForm.validarDivEspecialidad(codigo);
            $('[name="carrera.id"]').select2({
                allowClear: true,
                placeholder: "Seleccione la especialidad",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/curso/allCarreras"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {codigo: codigo, nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    var data = '<span class="block text-black bold">' + info.nombre + ' - ' + info.codigo + '</span>';
                    data += '<span class="block"> ' + (info.tipoEstudio != '' ? (info.tipoEstudio + ' - ') : '') + info.modalidadEstudio + '</span>';
                    return data;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        validarDivEspecialidad: function (codigo) {
            if (codigo == 'PRE') {
                $(".divEspecialidad").addClass("hide");
                $(".divTipoCurricula").removeClass("hide");
            } else {
                $(".divEspecialidad").removeClass("hide");
                $(".divTipoCurricula").addClass("hide");
            }
        },
        addIdioma: function () {
            var html = $.templates("#divNombreCurso").render({id: Math.random()});

            $(".bodyTabla tr:last").before(html);
            $(".bodyTabla").find('[name="idIdioma"]').select2();
        },
        existeIdioma: function ($this, e) {
            e.preventDefault();
            var idioma = $this.find("option:selected").val();
            var count = 0;

            $(".bodyTabla tr").each(function () {

                var td = $(this).find("td:first");
                var valor = td.find('[name="idIdioma"]').select2();

                if (idioma == valor.val()) {
                    count++;
                    if (count == 2) {
                        bootbox.alert("Idioma existente!");
                        CursoForm.deleteRowIdioma($this)
                    }
                }
            });
        },
        deleteRowIdioma: function ($this) {
            var tr = $this.closest("tr");
            tr.remove();
        }
    };
    CursoForm.init();

    $("body").delegate(".save-update-curso", "click", function (e) {
        CursoForm.saveUpdate(e);
    });
    $("body").delegate("[name='coordinador.id']", "change", function () {
        $(this).parsley().destroy();
    });
    $("body").delegate("[name='departamentoAcademico.id']", "change", function () {
        $(this).parsley().destroy();
    });
    $("body").delegate("[name='tipoCurso']", "change", function () {
        $(this).parsley().destroy();
    });
    $("body").delegate("[name='nivel']", "change", function () {
        $(this).parsley().destroy();
    });
    $("body").delegate("[name='tipoCurricula']", "change", function () {
        $(this).parsley().destroy();
    });
    $("body").delegate("[name='modalidadEstudio.id']", "change", function () {
        $(this).parsley().destroy();
        $('[name="nivel"]').select2('val', "");
        $('[name="carrera.id"]').select2("val", "");
        $('[name="tipoCurricula"]').select2("val", "");
        CursoForm.loadNiveles($(this));
        CursoForm.loadEspecialidades($(this));
    });
    $("body").delegate("[name='departamentoAcademico.id']", "change", function () {
        $(this).parsley().destroy();
        CursoForm.loadCoordinadores($(this));
    });
    $("body").delegate(".add-idioma", "click", function () {
        CursoForm.addIdioma();
    });
    $("body").delegate('[name="idIdioma"]', "change", function (e) {
        CursoForm.existeIdioma($(this), e);
    });
    $("body").delegate(".delete-idioma", "click", function () {
        CursoForm.deleteRowIdioma($(this));
    });
});