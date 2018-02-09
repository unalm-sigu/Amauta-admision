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

        $(".date").datepicker();
        $(".numerico").numeric({negative: false});

        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
        $('[name="cicloIngreso.id"]').select2({minimumResultsForSearch: -1});
        $('[name="modalidadEstudio.id"]').select2({minimumResultsForSearch: -1});

        $(".buscar-distrito").select2(vue.buscarDistrito());

        $('#nacionalidad').select2(vue.buscarPais());
        $('#carrera').select2(vue.buscarCarrera());
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function(e) {
            vue.mostrarDirNacimiento();
        });
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function(e) {
            vue.mostrarUbicacionDomicilio();
        });
    },
    methods: {
        buscarCarrera: function() {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("academico/alumno/allCarrera"),
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
                    return info.nombre;
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
        mostrarUbicacionDomicilio: function() {
            console.log();
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
            if (!$("#formAlumno").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/alumno/saveAlumnoFisico'),
                type: 'POST',
                async: true,
                data: $("#formAlumno").serialize(),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/alumno'));
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
        checkMatricula: function() {
            var chkBox = $('#chkbxGenMat');
            if (chkBox.is(':checked')) {
                $("#codigo").prop("disabled", true);
                $("#codigo").prop("required", false);
                $("#codigo").val("");
            } else {
                $("#codigo").prop("disabled", false);
                $("#codigo").prop("required", true);
            }
        },
        sinEspacios: function(e) {
            APP.eliminarEspacios($(e.currentTarget));
        },
        nombrePersona: function(e) {
            APP.revisarNombre($(e.currentTarget));
        }
    }
});
