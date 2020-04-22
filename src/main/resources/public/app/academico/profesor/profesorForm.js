new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
        sexoM: null,
        sexoF: null
    },
    watch: {
        sexoM: function () {
            $("#inlineCheckbox1").removeProp('required');
            $("#inlineCheckbox2").removeProp('required');
        },
        sexoF: function () {
            $("#inlineCheckbox2").removeProp('required');
            $("#inlineCheckbox1").removeProp('required');
        },
    },
    computed: {
    },
    created() {
        let vue = this;
    },
    mounted: function () {
        let vue = this;

        $(".numerico").numeric({negative: false});
        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
        $(".buscar-distrito").select2(vue.buscarDistrito());
        $(".date").datepickerBoot();
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarDirNacimiento();
        });
        $('#nacionalidad').select2(vue.buscarPais());
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarUbicacionDomicilio();
        });
        $("[name='departamentoAcademico.id']").select2(vue.departamentoAcademico());
        $("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});
        vue.initFileupload();
        vue.avatarInit();
    },
    methods: {
        buscarPais: function () {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
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
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarDistrito: function () {
            return {
                placeholder: "  ",
                allowClear: true,
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
            };
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
        mostrarDirNacimiento: function () {
            var vue = this;
            var dataPaisNac = $("#paisNacimiento").select2("data");

            if (dataPaisNac.codigo === "PE") {
                vue.showLugarNacimiento = true;
                setTimeout(function () {
                    $("#distNacimiento").select2(vue.buscarDistrito());
                }, 500);
                $("#distNacimiento").prop('required', true);
            } else {
                vue.showLugarNacimiento = false;
                $("#distNacimiento").select2("val", "");
                $("#distNacimiento").prop('required', false);
            }
        },
        mostrarUbicacionDomicilio: function () {
            var vue = this;

            var dataPaisUni = $("#paisDomicilio").select2("data");

            if (dataPaisUni.codigo === "PE") {
                vue.showUbicacionDomicilio = true;
                setTimeout(function () {
                    $('#ubicacionDomicilio').select2(vue.buscarDistrito());
                }, 500);
                $("#ubicacionDomicilio").prop('required', true);
                $("#ubicacionDomicilio").select2("val", "");
            } else {
                vue.showUbicacionDomicilio = false;
                $("#ubicacionDomicilio").removeProp('required');
            }
        },
        submitForm: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formDocente").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/profesor/save'),
                type: 'POST',
                async: true,
                data: $("#formDocente").serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/profesor/'));
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function () {
                    self.btnEnable();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        sinEspacios: function (e) {
            APP.eliminarEspacios($(e.currentTarget));
        },
        nombrePersona: function (e) {
            APP.revisarNombre($(e.currentTarget));
        },
        initFileupload: function () {

            $('#fileupload').fileupload({
                url: APP.url('academico/profesor/upload'),
                maxNumberOfFiles: 1,
                dataType: 'json',
                dropZone: '#upload',
                add: function (e, data) {

                    $('#fileuploadtrigger').btnDisabled();
                    $('#btnSavePersona').btnDisabled();

                    if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                        notify("Formato de archivo no soportado.", "error");
                        $('#fileuploadtrigger').btnEnable();
                        $('#btnSavePersona').btnEnable();
                        return;
                    }

                    if (data.files && data.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            $('#imagenProfile').attr('src', e.target.result);
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

        },
        fileuploadtrigger: function () {
            $('#fileupload').trigger('click');
        },
        avatarInit: function () {
            if ($('#avatar').val() != "") {
                var ruta = $('#avatar').val();
                $('#imagenProfile').attr("src", APP.url("academico/profesor/view/" + ruta));
            } else {
                $('#imagenProfile').attr("src", "/phobos/images/unalm/male.png");
            }
        },
        buscarPersona: function () {
            let vue = this;
            var form = $("#formDocente");

            if ($('[name="persona.numeroDocIdentidad"]').val() != '' && $('[name="persona.tipoDocumento.id"]').val() == '') {
                notify("Seleccione primero el tipo documento",'error');
                $('[name="persona.numeroDocIdentidad"]').val('');
                return;
            }

            $.ajax({
                url: APP.url('academico/profesor/findPersonaProfesor'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        var data = response.data;
                        $("#nombreDocente").text(data.paterno + " " + data.materno + ", " + data.nombres);
                        form.find('[name="persona.foto"]').val(data.foto);
                        form.find('[name="persona.id"]').val(data.idPersona);
                        form.find('[name="persona.tipoDocumento.id"] option[value=' + data.tipoDocumentoId + ']').attr("selected", true);
                        form.find('[name="persona.numeroDocIdentidad"]').val(data.numeroDoc);
                        form.find('[name="persona.numeroDocIdentidad"]').attr("readonly", true)
                        form.find('[name="persona.paterno"]').val(data.paterno);
                        form.find('[name="persona.materno"]').val(data.materno);
                        form.find('[name="persona.nombres"]').val(data.nombres);
                        form.find('[name="persona.emailCompania"]').val(data.emailCompania);
                        form.find('[name="persona.sexo"]').val([data.sexo]);
                        form.find('[name="persona.paisNacer.id"]').select2('data', {id: data.paisNacerId, nombre: data.paisNacerNombre});
                        form.find('[name="persona.conDiscapacidad"]').val([data.conDiscapacidad]);

                        var paisPeru = $('[name="persona.paisNacer.id"]').val();
                        if (paisPeru === '178') {
                            vue.showLugarNacimiento = true;
                            setTimeout(function () {
                                $("#distNacimiento").select2(vue.buscarDistrito());
                                $("#distNacimiento").select2('data', {id: data.ubicacionNacerId, nombre: data.ubicacionNacerNombre});
                            }, 500);
                            $("#distNacimiento").prop('required', true);
                        } else {
                            $("#distNacimiento").select2("val", "");
                            $("#distNacimiento").prop('required', false);
                        }

                        form.find('[name="persona.fechaNacer"]').val(data.fechaNacer);
                        form.find('[name="persona.nacionalidad.id"]').select2('data', {id: data.nacionalidadId, nombre: data.nacionalidadNombre});
                        form.find('[name="persona.telefono"]').val(data.telefono);
                        form.find('[name="persona.celular"]').val(data.celular);
                        form.find('[name="persona.email"]').val(data.email);
                        form.find('[name="persona.paisDomicilio.id"]').select2('data', {id: data.paisDomiciliodId, nombre: data.paisDomicilioNombre});

                        var paisPeru = $('[name="persona.paisDomicilio.id"]').val();
                        if (paisPeru === '178') {
                            vue.showUbicacionDomicilio = true;
                            setTimeout(function () {
                                $('#ubicacionDomicilio').select2(vue.buscarDistrito());
                                $("#ubicacionDomicilio").select2('data', {id: data.ubicaiconDomiciliodId, nombre: data.ubicacionDomicilioNombre});
                            }, 500);

                            $("#ubicacionDomicilio").prop('required', true);
                        } else {
                            $("#ubicacionDomicilio").select2("val", "");
                            $("#ubicacionDomicilio").prop('required', false);
                        }
                        form.find('[name="persona.direccion"]').val(data.direccion);
                        form.find('[name="persona.foto"]').val(data.foto);
                        vue.avatarInit();
                        notify("Profesor ya registrado.",'error')
                    } else {
                        $("#nombreDocente").text("");
                        form.find('[name="persona.foto"]').val('');
                        form.find('[name="persona.id"]').val('');
                        form.find('[name="persona.paterno"]').val('');
                        form.find('[name="persona.materno"]').val('');
                        form.find('[name="persona.nombres"]').val('');
                        form.find('[name="persona.emailCompania"]').val('');
                        form.find('[name="persona.sexo"]').val('');
                        form.find('[name="persona.paisNacer.id"]').select2('val', '');
                        form.find('[name="persona.ubicacionNacer.id"]').select2('val', '');
                        form.find('[name="persona.fechaNacer"]').val('');
                        form.find('[name="persona.nacionalidad.id"]').select2('val', '');
                        form.find('[name="persona.telefono"]').val('');
                        form.find('[name="persona.celular"]').val('');
                        form.find('[name="persona.email"]').val('');
                        form.find('[name="persona.paisDomicilio.id"]').select2('val', '');
                        form.find('[name="persona.ubicacionDomicilio.id""]').select2('val', '');
                        form.find('[name="persona.direccion"]').val('');
                        form.find('[name="persona.conDiscapacidad"]').val('');
                        vue.avatarInit();
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});
