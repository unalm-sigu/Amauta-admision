$(function () {

    var Docente = {
        form: $("form"),
        body: $("body"),
        init: function () {
            Docente.form.find('#fileupload').fileupload({
                url: APP.url('academico/docente/upload'),
                maxNumberOfFiles: 1,
                dataType: 'json',
                dropZone: '#upload',
                add: function (e, data) {

                    $('#fileuploadtrigger').btnDisabled();
                    $('#btnSavePersona').btnDisabled();

                    if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                        notify("Formato de archivo no soportado.", "error");
                        return;
                    }

                    if (data.files && data.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            Docente.form.find('#imagenProfile').attr('src', e.target.result);
                        };
                        reader.readAsDataURL(data.files[0]);
                    }

                    data.submit();
                },
                progress: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    if (progress === 100) {
                    }
                },
                done: function (e, data) {
                    $('input:submit').removeAttr("disabled");
                    if (data.result.success) {
                        var ruta = data.result.data.ruta;
                        $('#avatar').val(ruta);
                        notify(data.result.message, "info");
                    } else {
                        notify(data.result.message, "error");
                    }
                    $('#fileuploadtrigger').btnEnable();
                    $('#btnSavePersona').btnEnable();
                },
                fail: function (e, data) {
                    $('input:submit').removeAttr("disabled");
                    $('#fileuploadtrigger').btnEnable();
                    $('#btnSavePersona').btnEnable();
                    notify(data.result.message, "error");
                }
            });
            Docente.form.find("[name='departamentoAcademico.id']").select2(Docente.departamentoAcademico());
            Docente.form.find(".date").datepickerBoot();
            Docente.form.find("#buscarDistrito").select2(Docente.buscarDistrito());

            Docente.form.find("[name='persona.tipoDocumento.id']").
                    select2({minimumResultsForSearch: -1}).
                    on("change", function () {
                        if (Docente.form.find("[name='persona.numeroDocIdentidad']").parsley().validate() == true) {
                            Docente.form.find("[name='persona.numeroDocIdentidad']").trigger('change');
                        }
                    });
            Docente.form.find("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});
        },
        departamentoAcademico: function () {
            return {
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
                    return '<p>' + info.nombre + '</p>  ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                formatSelection: function (info) {
                    return '<p>' + info.nombre + '</p>   ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                escapeMarkup: function (m) {
                    return m;
                }
            }
        },
        validaEmail: function ($this) {

            var form = $("#formPersonaEdit");
            var emailEmpresa = form.find("[name='emailEmpresa']").val();

            if ($this.parsley().isValid()) {

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
        save: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            self.btnDisabled();
            var form = $("form");
            if (form.parsley().validate() != true) {
                self.btnEnable();
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
                        window.location.replace(APP.url('academico/docente'));
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function () {
                    self.btnEnable();
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
            if (Docente.form.find("[name='persona.numeroDocIdentidad']").val() == "") {
                return;
            }
            var mibox = bootbox.dialog({message: APP.template.wait, closeButton: false});
            if (Docente.form.find("[name='id']").val() != "") {
                $.ajax({
                    url: APP.url('academico/docente/disponibilidad'),
                    type: 'POST',
                    async: true,
                    data: Docente.form.serialize(),
                    success: function (response) {
                        if (response.success) {
                            if (response.data.existePersona) {
                                if (response.data.passPersona) {
                                    if (response.data.validadoReniec) {
                                        Docente.form.find("[name='persona.numeroDocIdentidad']").val(response.data.numeroDocOriginal);
                                        notify(response.message, "error");
                                    }
                                } else {
                                    Docente.form.find("[name='persona.numeroDocIdentidad']").val(response.data.numeroDocOriginal);
                                    Docente.form.find("[name='persona.tipoDocumento.id']").select2('val', response.data.tipoIdDocOriginal);
                                    var dnni = '<b>' + response.data.simboloDoc + '</b> :  ' + response.data.numeroDoc;
                                    notify("Docente ya registrado con  " + dnni, "error");
                                }
                            } else {
                                if (response.data.validadoReniec) {
                                    Docente.form.find("[name='persona.numeroDocIdentidad']").val(response.data.numeroDocOriginal);
                                    Docente.form.find("[name='persona.tipoDocumento.id']").select2('val', response.data.tipoIdDocOriginal);
                                    notify(response.message, "error");
                                }
                            }
                            mibox.modal('hide');
                        } else {
                            Docente.form.find("[name='persona.numeroDocIdentidad']").val('');
                            mibox.modal('hide');
                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        Docente.form.find("[name='persona.numeroDocIdentidad']").val('');
                        mibox.modal('hide');
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
                return;
            }
            $('#nombreDocente').text('Nuevo Docente');
            $.ajax({
                url: APP.url('academico/docente/findPersona'),
                type: 'POST',
                async: true,
                data: Docente.form.serialize(),
                success: function (response) {
                    if (response.success) {

                        if (response.data.existeDocente) {
                            var dnni = '<b>' + response.data.simboloDoc + '</b> :  ' + response.data.numeroDoc;
                            notify("Docente ya registrado con  " + dnni, "error");
                            Docente.form.find("[name='persona.numeroDocIdentidad']").val('');
                        } else {

                            if (response.data.existePersona) {
                                $('#nombreDocente').text(response.data.name);
                            }
                            Docente.form.find('#formularioDocente').html(response.data.html);
                            Docente.form.find("[name='departamentoAcademico.id']").select2(Docente.departamentoAcademico());
                            Docente.form.find("#buscarDistrito").select2(Docente.buscarDistrito());
                            Docente.form.find("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});
                            Docente.form.find(".date").datepickerBoot();

                            mibox.modal('hide');
                        }

                        mibox.modal('hide');
                    } else {
                        Docente.form.find("[name='persona.numeroDocIdentidad']").val('');
                        mibox.modal('hide');
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    Docente.form.find("[name='persona.numeroDocIdentidad']").val('');
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
    Docente.body.delegate("#btnSavePersona", "click", function (e) {
        Docente.save(e);
    });
    Docente.body.delegate("#fileuploadtrigger", "click", function () {
        $('#fileupload').trigger('click');
    });

    Docente.init();
});
