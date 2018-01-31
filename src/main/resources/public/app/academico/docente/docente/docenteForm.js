new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
    },
    created() {
        let vue = this;
    },
    mounted: function() {
        let vue = this;
        $(".numerico").numeric({negative: false});
        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
        $('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});
        $(".buscar-distrito").select2(vue.buscarDistrito());
        $(".date").datepickerBoot();
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function(e) {
            vue.mostrarDirNacimiento();
        });
        $('#nacionalidad').select2(vue.buscarPais());
        $('#paisUniversidad').select2(vue.buscarPais()).on('change.select2', function(e) {
            vue.mostrarUniversidadName();
        });
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function(e) {
            vue.mostrarUbicacionDomicilio();
        });
        $('#univ-peru').select2(vue.buscarUniversidad());
        $("[name='departamentoAcademico.id']").select2(vue.departamentoAcademico());
        $("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});
        $('#fileupload').fileupload({
            url: APP.url('academico/docente/upload'),
            maxNumberOfFiles: 1,
            dataType: 'json',
            dropZone: '#upload',
            add: function(e, data) {

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
                    reader.onload = function(e) {
                        $('#imagenProfile').attr('src', e.target.result);
                    };
                    reader.readAsDataURL(data.files[0]);
                }

                data.submit();
            },
            progress: function(e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                if (progress === 100) {
                }
            },
            done: function(e, data) {
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
            fail: function(e, data) {
                $('input:submit').removeAttr("disabled");
                $('#fileuploadtrigger').btnEnable();
                $('#btnSavePersona').btnEnable();
                notify(data.result.message, "error");
            }
        });

    },
    methods: {
        buscarUniversidad: function() {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allUniversidad"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function(info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function(info) {
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        buscarPais: function() {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function(info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function(info) {
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        buscarDistrito: function() {
            return {
                placeholder: "  ",
                allowClear: true,
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allDistritos"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel")});
                    }
                },
                formatResult: function(info) {
                    return $.templates("#divBuscarDistrito").render(info);
                },
                formatSelection: function(info) {
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        departamentoAcademico: function() {
            return {
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("academico/departamento/allDepartamento"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("rev")});
                    }
                },
                formatResult: function(info) {
                    return '<p>' + info.nombre + '</p>  ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                formatSelection: function(info) {
                    return '<p>' + info.nombre + '</p>   ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                escapeMarkup: function(m) {
                    return m;
                }
            }
        },
        mostrarDirNacimiento: function() {
            var vue = this;
            var dataPaisNac = $("#paisNacimiento").select2("data");
            if (dataPaisNac.codigo === "PE") {
                vue.showLugarNacimiento = true;
                setTimeout(function() {
                    $("#distNacimiento").select2(vue.buscarDistrito());
                }, 500);
                $("#distNacimiento").prop('required', true);
            } else {
                vue.showLugarNacimiento = false;
                $("#distNacimiento").select2("val", "");
                $("#distNacimiento").prop('required', false);
            }
        },
        mostrarUniversidadName: function() {
            var vue = this;
            var dataPaisUni = $("#paisUniversidad").select2("data");
            if (dataPaisUni.codigo === "PE") {
                vue.showUniverdidadName = false;
                vue.showUniverdidadPeru = true;
                setTimeout(function() {
                    $('#univ-peru').select2(vue.buscarUniversidad());
                }, 500);
                $("#univ-peru").prop('required', true);
                $("#univ-peru").select2("val", "");
            } else {
                vue.showUniverdidadName = true;
                vue.showUniverdidadPeru = false;
                $("#universidadExtranjeraName").prop('required', true);
                $("#universidadExtranjeraName").val("");
            }
        },
        mostrarUbicacionDomicilio: function() {
            var vue = this;
            var dataPaisUni = $("#paisDomicilio").select2("data");
            if (dataPaisUni.codigo === "PE") {
                vue.showUbicacionDomicilio = true;
                setTimeout(function() {
                    $('#ubicacionDomicilio').select2(vue.buscarDistrito());
                }, 500);
                $("#ubicacionDomicilio").prop('required', true);
                $("#ubicacionDomicilio").select2("val", "");
            } else {
                vue.showUbicacionDomicilio = false;
                $("#ubicacionDomicilio").removeProp('required');
            }
        },
        submitForm: function(e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formDocente").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/docente/save'),
                type: 'POST',
                async: true,
                data: $("#formDocente").serialize(),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/docente/'));
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function() {
                    self.btnEnable();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        sinEspacios: function(e) {
            APP.eliminarEspacios($(e.currentTarget));
        },
        nombrePersona: function(e) {
            APP.revisarNombre($(e.currentTarget));
        },
        fileuploadtrigger: function() {
            $('#fileupload').trigger('click');
        }
    }
});
