$(function () {

    $(".date").datepicker();

    PersonaForm = {
        tipoDNI: "",
        numeroDNI: "",
        iniciar: function () {
            PersonaForm.tipoDNI = $("#tipoDNI").val();
            PersonaForm.numeroDNI = $("#numeroDocIdentidad").val();

            $("#buscarDistrito").select2({
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
            });
        },
        save: function () {
            var form = $("#formPersonaEdit");
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('general/persona/savePersona'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        PersonaForm.modalViky.modal('hide');
                        $(location).attr('href', APP.url('persona/' + $('#id').val() + '/update#perfil'));
                        location.reload();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
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
                    url: APP.url('general/persona/validarEmail'),
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
                        notify(Messages.errorComunicacion, "error");
                    }
                });
            }

        },
        guardarPersona: function () {
            var form = $("#formPersonaEdit");
            if (!form.parsley().validate()) {
                return;
            }
            var user = $("#personaId");
            var perso = $("#personaId");

            $.ajax({
                url: APP.url('general/persona/savePersona'),
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
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
                    $("#footerEditPersona").find("a").each(function (i, item) {
                        $(item).removeAttr("disabled");
                    });
                }
            });
        },
        verificarPersona: function ($this) {
            var tipo = $("#tipoDNI");
            var dni = $("#numeroDocIdentidad");
            var user = $("#personaId");
            var perso = $("#personaId");
            var form = $("#formPersonaEdit");
            var error = "errorValidacionDocIdentidad";

//            if (user.val() != "" && perso.val() != "") {
//                tipo.select2("val", PersonaForm.tipoDNI);
//                dni.val(PersonaForm.numeroDNI);
//                notify("No puede modificar el número de documento de identidad por este formulario.", "error");
//                return;
//            }

            console.log("TIPO=" + tipo.val() + " :::: DNI=" + dni.val())
            if (!(tipo.val() != "" && dni.val() != "")) {
                PersonaForm.tipoDNI = tipo.val();
                PersonaForm.numeroDNI = dni.val();
                return;
            }

            MODAL.showWait("Buscando datos de la persona");
            var inputChange = $this.parsley();
            var inputTipo = tipo.parsley();
            var inputDNI = dni.parsley();

            window.ParsleyUI.removeError(inputTipo, error);
            window.ParsleyUI.removeError(inputDNI, error);
            window.ParsleyUI.addError(inputChange, error, "Validando documento de identidad....");

            $.ajax({
                url: APP.url('general/persona/findPersona'),
                type: 'POST',
                async: true,
                data: {"tipoDocumento.id": tipo.val(), numeroDocIdentidad: dni.val()},
                success: function (response) {
                    window.ParsleyUI.removeError(inputChange, error);
                    MODAL.hideWait();
                    if (response.success) {
                        var data = response.data;
                        $("#sexoM").prop("checked", false);
                        $("#sexoF").prop("checked", false);
                        PersonaForm.tipoDNI = tipo.val();
                        PersonaForm.numeroDNI = dni.val();
                        if (data.id == null && perso.val() == "") {
                            return;
                        }
                        $.each(data, function (a, b) {
                            form.find("input[name='persona." + a + "']").val(b);
                        });
                        $("input[name='persona.fechaNacer']").datepicker('setDate', data.fechaNacer);
                        $("#sexo" + data.sexo).prop("checked", true);


                    } else {
                        window.ParsleyUI.addError(inputChange, error, "Ya existe un persona con este documento");
                        tipo.select2("val", PersonaForm.tipoDNI);
                        dni.val(PersonaForm.numeroDNI);
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    window.ParsleyUI.removeError(inputChange, error);
                    MODAL.hideWait();
                    tipo.select2("val", PersonaForm.tipoDNI);
                    dni.val(PersonaForm.numeroDNI);
                    notify(Messages.errorComunicacion, "error");
                }
            });

        }
    };

    PersonaForm.iniciar();

    $("body").delegate(".domicilio", "change", function () {
        APP.revisarDireccion($(this));
    });
    $("body").delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });
    $("body").delegate(".validar-email", "change", function () {
        APP.revisarEmail($(this));
        PersonaForm.validarEmail($(this));
    });
    $("body").delegate(".validar-email-empresa", "change", function () {
        PersonaForm.validarEmailEmpresa($(this));
    });
    $("body").delegate(".nombre-persona", "change", function () {
        APP.revisarNombre($(this));
    });
    $("body").delegate(".buscar-persona", "change", function () {
        PersonaForm.verificarPersona($(this));
    });
    $("body").delegate("#btnSavePersona", "click", function () {
        PersonaForm.guardarPersona();
    });
});
