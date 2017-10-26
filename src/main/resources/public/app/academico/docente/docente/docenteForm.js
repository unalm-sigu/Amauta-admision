$(function () {


    var Docente = {
        form: $("form"),
        body: $("body"),
        init: function () {
            $("[name='departamentoAcademico.id']").select2({
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("academico/departamento/allDepartamento"),
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
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("rev")});
                    }
                },
                formatResult: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                formatSelection: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        save: function () {

            var form = $("form");
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/docente/save'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        Docente.modalViky.modal('hide');
                        $(location).attr('href', APP.url('persona/' + $('#id').val() + '/update#perfil'));
                        location.reload();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        validaEmail: function ($this) {

            var form = $("#formPersonaEdit");
            var emailEmpresa = form.find("[name='emailEmpresa']").val();
            console.log("email: " + emailEmpresa);
            if ($this.parsley().isValid()) {

                //var email = $this.val();
                var email = emailEmpresa;
                $.ajax({
                    url: APP.url('academico/docente/validarEmail'),
                    type: 'POST',
                    async: true,
                    data: {email: email},
                    success: function (response) {
                        if (response.success) {

                            if (response.data.validate == true) {

                                if (response.data.persona != $('#id').val()) {

                                    notify("El email ingresado ya esta asociado a una persona.", "error");
                                    $("#email").val("");

                                }
                            }
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }

        },
        guardar: function () {
            var form = $("form");
            if (!form.parsley().validate()) {
                return;
            }

            var user = $("#personaId");
            var perso = $("#personaId");

            $.ajax({
                url: APP.url('academico/docente/save'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        $("#tabPerfil").removeClass("hide");
                        $('#tabPerfil a:first').tab('show');
                        $('#nombreCompleto').html(data.nombreCompleto);

                        user.val(data.personaId);
                        perso.val(data.personaId);
                        notify(response.message, "info");

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        validarEmail: function ($this, e) {
            if ($this.val() == "") {
                return;
            }
            var inputEmail = $this.parsley();
            window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
            window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", "Validando el correo....");
            $("#footerPersona").find("a").each(function (i, item) {
                $(item).attr("disabled", "disabled");
            });
            $.ajax({
                url: APP.url('general/persona/validarEmail'),
                type: 'POST',
                data: {email: $this.val(), persona: $("input[name*='persona.id']").val()},
                success: function (response) {
                    var data = response.data;
                    window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
                    if (!response.success) {
                        window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", data.respuesta);
                    }

                    $("#footerPersona").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                },
                error: function () {
                    window.ParsleyUI.updateError(inputEmail, "errorValidacionEmail", "Este correo no se pudo validar");
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#footerEditPersona").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                }
            });
        },
        validarEmailEmpresa: function ($this, e) {
            APP.revisarEmail($this);
            if ($this.val() == "") {
                return;
            }
            var inputEmail = $this.parsley();
            window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
            window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", "Validando el correo....");
            $("#footerPersona").find("a").each(function (i, item) {
                $(item).attr("disabled", "disabled");
            });
            $.ajax({
                url: APP.url('general/persona/validarEmailEmpresa'),
                type: 'POST',
                data: {email: $this.val(), persona: $("input[name*='persona.id']").val()},
                success: function (response) {
                    var data = response.data;
                    window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
                    if (!response.success) {
                        window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", data.respuesta);
                    }

                    $("#footerPersona").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                },
                error: function () {
                    window.ParsleyUI.updateError(inputEmail, "errorValidacionEmail", "Este correo no se pudo validar");
                    notify(MESSAGES.errorComunicacion, "error");
                    $("#footerEditPersona").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                }
            });
        },
        verificarPersona: function (self) {

            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false})
            var numeroDocIdentidad = $("[name='persona.numeroDocIdentidad']").parsley();

            $('#nombreDocente').text('Nuevo Docente');

            $.ajax({
                url: APP.url('academico/docente/findPersona'),
                type: 'POST',
                async: true,
                data: $('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        if (response.data.existePersona) {
                            $('#nombreDocente').text(response.data.name);
                            if (response.data.existeDocente) {
                                Docente.form.find("select[name='modalidadEstudio.id']").select2('val', response.data.docModalidad);
                                Docente.form.
                                        find("[name='departamentoAcademico.id']").
                                        select2('val', {id: response.data.docDepartamento,
                                            nombre: response.data.docDepartamentoName,
                                            codigo: response.data.docDepartamentoCodigo});
                            } else {
                            }
                            mibox.modal('hide');
                            $("#formPersona").html('');
                        } else {
                            var html = $.templates("#docentePersonaTemplate").render({});
                            Docente.form.find("#formPersona").html(html);
                            Docente.form.find(".date").datepicker();
                            Docente.form.find("#buscarDistrito").select2(Docente.buscarDistrito());
                        }
                        mibox.modal('hide');
                    } else {
                        $("[name='persona.numeroDocIdentidad']").val('');
                        mibox.modal('hide');
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    $("[name='persona.numeroDocIdentidad']").val('');
                    mibox.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });


        },
        buscarDistrito: function () {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allDistritos"),
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
                        callback({id: element.val(), nombre: element.attr("rel")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            }
        }
    };


    Docente.body.delegate(".domicilio", "change", function () {
        APP.revisarDireccion($(this));
    });
    Docente.body.delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });
    Docente.body.delegate(".validar-email", "change", function () {
        APP.revisarEmail($(this));
        Docente.validarEmail($(this));
    });
    Docente.body.delegate(".validar-email-empresa", "change", function () {
        Docente.validarEmailEmpresa($(this));
    });
    Docente.body.delegate(".nombre-persona", "change", function () {
        APP.revisarNombre($(this));
    });

    Docente.body.delegate("[name='persona.numeroDocIdentidad']", "change", function () {
        Docente.verificarPersona($(this));
    });
    Docente.body.delegate("#btnSavePersona", "click", function () {
        Docente.guardar();
    });

    Docente.init();
});
