$(function () {

    $(".date").datepicker();
    
    UserForm = {
        tipoDNI: "",
        numeroDNI: "",
        iniciar: function () {
            if ($("#usuarioId").val() != "") {
                $("#tabPerfil").removeClass("hide");
            }
            UserForm.tipoDNI = $("#tipoDNI").val();
            UserForm.numeroDNI = $("#numeroDocIdentidad").val();
        },
        deshabilitaPerfil: function ($this) {

            var userRol = $this.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea deshabilitar el perfil?",
                title: "Inhabilitar perfil",
                buttons: {
                    confirm: {label: 'Inhabilitar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('seguridad/usuario/deshabilitarPerfil'),
                            type: 'GET',
                            async: true,
                            data: {userRol: userRol},
                            success: function (response) {
                                if (response.success) {
                                    UserForm.reloadRoles();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        save: function () {
            var form = $("#formUsuarioEdit");
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('seguridad/usuario/saveUsuario'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        UserForm.modalViky.modal('hide');
                        $(location).attr('href', APP.url('usuario/' + $('#id').val() + '/update#perfil'));
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
        nuevoPerfil: function ($this, e) {
            e.preventDefault();
            var form = $("#formUsuarioEdit");
            var usuario = form.find("[name='id']").val();
            $.ajax({
                url: APP.url('seguridad/usuario/usuarioRol'),
                type: 'GET',
                async: true,
                data: {usuario: usuario},
                success: function (response) {
                    MODAL.init("md");
                    MODAL.title("Agregar perfil");
                    MODAL.buttons('<a href="#" class="btn btn-primary" id="btnAgregarPerfil" rel="' + usuario + '">Agregar perfil</a>');
                    MODAL.body(response);
                    MODAL.show();

                    $("#perfilUsuario").select2();
                    $("#fechaInicio").datepicker();
                    $("#dirreg").select2();
                    $("#sedes").select2();
                    UserForm.showCombosByPerfil();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        savePerfil: function (e) {
            e.preventDefault();
            var form = $("#formPerfil");

            form.parsley().destroy();
            if (!form.parsley().validate()) {
                console.log("form no validado")
                return;
            }

            $.ajax({
                url: APP.url('seguridad/usuario/savePerfil'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        UserForm.reloadRoles();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        reloadRoles: function () {

            var usuario = $("#usuarioId").val();
            $.ajax({
                url: APP.url('seguridad/usuario/rolesUsuario'),
                type: 'POST',
                async: true,
                data: {usuario: usuario},
                success: function (response) {
                    $("#tBodyRoles").html(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        showCombosByPerfil: function () {

            var tipo = $("#perfilUsuario option:selected").attr('rel');
            console.log(":::: " + tipo)
            $("#divDirReg").hide();
            $("#divSedes").hide();
            $("#sedes").select2("val", "");
            $("#dirreg").select2("val", "");
            $("#sedes").prop('required', false);
            $("#dirreg").prop('required', false);

            if (tipo == 'REG' || tipo == 'SUP') {
                $("#divDirReg").show();
                $("#dirreg").prop('required', true);
            } else if (tipo == 'SEDE') {
                $("#divSedes").show();
                $("#sedes").prop('required', true);
            }
        },
        validaEmail: function ($this) {

            var form = $("#formUsuarioEdit");
            var emailCompania = form.find("[name='emailCompania']").val();
            console.log("email: " + emailCompania);
            if ($this.parsley().isValid()) {

                var email = emailCompania;
                $.ajax({
                    url: APP.url('seguridad/usuario/validarEmail'),
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
        guardarUsuario: function () {
            var form = $("#formUsuarioEdit");
            if (!form.parsley().validate()) {
                return;
            }
            var user = $("#usuarioId");
            var perso = $("#personaId");

            $.ajax({
                url: APP.url('seguridad/usuario/saveUsuario'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        $("#tabPerfil").removeClass("hide");
                        $('#tabPerfil a:first').tab('show');
                        $('#nombreCompleto').html(data.nombreCompleto);

                        user.val(data.usuarioId);
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
            $("#footerUsuario").find("a").each(function (i, item) {
                $(item).attr("disabled", "disabled");
            });
            $.ajax({
                url: APP.url('seguridad/usuario/validarEmail'),
                type: 'POST',
                data: {email: $this.val(), persona: $("input[name*='persona.id']").val()},
                success: function (response) {
                    var data = response.data;
                    window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
                    if (!response.success) {
                        window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", data.respuesta);
                    }

                    $("#footerUsuario").find("a").each(function (i, item) {
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
        validarEmailCompania: function ($this, e) {
            APP.revisarEmail($this);
            if ($this.val() == "") {
                return;
            }
            var inputEmail = $this.parsley();
            window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
            window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", "Validando el correo....");
            $("#footerUsuario").find("a").each(function (i, item) {
                $(item).attr("disabled", "disabled");
            });
            $.ajax({
                url: APP.url('seguridad/usuario/validarEmailCompania'),
                type: 'POST',
                data: {email: $this.val(), persona: $("input[name*='persona.id']").val()},
                success: function (response) {
                    var data = response.data;
                    window.ParsleyUI.removeError(inputEmail, "errorValidacionEmail");
                    if (!response.success) {
                        window.ParsleyUI.addError(inputEmail, "errorValidacionEmail", data.respuesta);
                    }

                    $("#footerUsuario").find("a").each(function (i, item) {
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
        verificarPersona: function ($this) {
            var tipo = $("#tipoDNI");
            var dni = $("#numeroDocIdentidad");
            var user = $("#usuarioId");
            var perso = $("#personaId");
            var form = $("#formUsuarioEdit");
            var error = "errorValidacionDocIdentidad";

            if (user.val() != "" && perso.val() != "") {
                tipo.select2("val", UserForm.tipoDNI);
                dni.val(UserForm.numeroDNI);
                notify("No puede modificar el número de documento de identidad por este formulario.", "error");
                return;
            }

            console.log("TIPO=" + tipo.val() + " :::: DNI=" + dni.val())
            if (!(tipo.val() != "" && dni.val() != "")) {
                UserForm.tipoDNI = tipo.val();
                UserForm.numeroDNI = dni.val();
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
                url: APP.url('seguridad/usuario/findPersona'),
                type: 'POST',
                async: true,
                data: {tipoDNI: tipo.val(), numeroDNI: dni.val()},
                success: function (response) {
                    window.ParsleyUI.removeError(inputChange, error);
                    MODAL.hideWait();
                    if (response.success) {
                        var data = response.data;
                        $("#sexoM").prop("checked", false);
                        $("#sexoF").prop("checked", false);
                        UserForm.tipoDNI = tipo.val();
                        UserForm.numeroDNI = dni.val();
                        if (data.id == null && perso.val() == "") {
                            return;
                        }
                        $.each(data, function (a, b) {
                            form.find("input[name='persona." + a + "']").val(b);
                        });
                        $("input[name='persona.fechaNacer']").datepicker('setDate', data.fechaNacer);
                        $("#sexo" + data.sexo).prop("checked", true);


                    } else {
                        window.ParsleyUI.addError(inputChange, error, "Ya existe un usuario con este documento");
                        tipo.select2("val", UserForm.tipoDNI);
                        dni.val(UserForm.numeroDNI);
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    window.ParsleyUI.removeError(inputChange, error);
                    MODAL.hideWait();
                    tipo.select2("val", UserForm.tipoDNI);
                    dni.val(UserForm.numeroDNI);
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }
    };

    UserForm.iniciar();

    $('body').delegate('#addPerfil', 'click', function (e) {
        UserForm.nuevoPerfil($(this), e);
    });
    $('body').delegate('.delete', 'click', function () {
        UserForm.deshabilitaPerfil($(this));
    });
    $('body').delegate('#perfilUsuario', 'change', function () {
        UserForm.showCombosByPerfil();
    });
    $('body').delegate('#btnSaveUser', 'click', function () {
        UserForm.guardarUsuario();
    });
    $("body").delegate("#btnAgregarPerfil", "click", function (e) {
        UserForm.savePerfil(e);
    });
    $("body").delegate(".domicilio", "change", function () {
        APP.revisarDireccion($(this));
    });
    $("body").delegate(".sin-espacios", "change", function () {
        APP.eliminarEspacios($(this));
    });
    $("body").delegate(".validar-email", "change", function () {
        APP.revisarEmail($(this));
        UserForm.validarEmail($(this));
    });
    $("body").delegate(".validar-email-empresa", "change", function () {
        UserForm.validarEmailCompania($(this));
    });
    $("body").delegate(".nombre-persona", "change", function () {
        APP.revisarNombre($(this));
    });
    $("body").delegate(".buscar-persona", "change", function () {
        UserForm.verificarPersona($(this));
    });
});
