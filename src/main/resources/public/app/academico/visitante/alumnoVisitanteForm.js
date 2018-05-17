new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUniverdidadName: codigoPaisUniversidad != 'PE',
        showUniverdidadPeru: codigoPaisUniversidad == 'PE',
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
        alumnoVisitante: {
            cicloEstudia: {id: null},
            paisUniversidad: {id: null}
        },
        persona: {
            tipoDocumento: {id: null},
            paisNacer: {id: null},
            nacionalidad: {id: null},
            paisDomicilio: {id: null},
            ubicacionDomicilio: {id: null},
            ubicacionNacer: {id: null}
        },
    },
    mounted: function () {

        let vue = this;

        $('[name="persona.tipoDocumento.id"]').
                select2({minimumResultsForSearch: -1}).
                on("change.select2", function (el) {

                    vue.persona.tipoDocumento.id = el.val;
                    vue.cambiarNumDoc();

                });

        $('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});

        $(".date").datepicker();

        $(".numerico").numeric({negative: false});

        $('#univ-peru').select2(vue.buscarUniversidad());

        $('#nacionalidad').select2(vue.buscarPaisNacionalidad(vue));
        let self = $(vue.$el);
        self.find('#paisNacimiento').select2(vue.buscarPaisNacimiento(vue)).on('change.select2', function (e) {
            vue.persona.paisNacer = e.added;
            vue.mostrarDirNacimiento();
        });
        $('#paisUniversidad').select2(vue.buscarPaisUniversidad(vue)).on('change.select2', function (e) {
            vue.mostrarUniversidadName();
        });
        $('#paisDomicilio').select2(vue.buscarPaisDomicilio(vue)).on('change.select2', function (e) {
            vue.mostrarUbicacionDomicilio();
        });

    },
    updated: function () {
        let vue = this;
        this.$nextTick(function () {
            let self = $(vue.$el);
            self.find('#paisNacimiento').select2(vue.buscarPaisNacimiento(vue)).on('change.select2', function (e) {
                vue.persona.paisNacer = e.added;
                vue.mostrarDirNacimiento();
            });
        });
    },
    methods: {
        buscarUniversidad: function () {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allUniversidad"),
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
        buscarPaisNacionalidad: function (vue) {
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
                    if (vue.persona.paisNacer != null) {
                        callback(vue.persona.paisNacer);
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
        buscarPaisUniversidad: function (vue) {
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
                    if (vue.alumnoVisitante.paisUniversidad != null) {
                        callback(vue.alumnoVisitante.paisUniversidad);
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
        buscarPaisDomicilio: function (vue) {
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
                    if (vue.persona.paisDomicilio != null) {
                        callback(vue.persona.paisDomicilio);
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
        buscarPaisNacimiento: function (vue) {
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
                    if (vue.persona.paisNacer != null) {
                        callback(vue.persona.paisNacer);
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
                    return $.templates("#divBuscarDistrito").render(info);
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        mostrarDirNacimiento: function () {
//            var vue = this;
//            var dataPaisNac = $("#paisNacimiento").select2("data");
//            if (dataPaisNac.codigo === "PE") {
//                vue.showLugarNacimiento = true;
//                setTimeout(function () {
//                    $("#distNacimiento").select2(vue.buscarDistrito());
//                }, 500);
//                $("#distNacimiento").prop('required', true);
//            } else {
//                vue.showLugarNacimiento = false;
//                $("#distNacimiento").select2("val", "");
//                $("#distNacimiento").prop('required', false);
//            }
        },
        mostrarUniversidadName: function () {
//            var vue = this;
//            var dataPaisUni = $("#paisUniversidad").select2("data");
//            if (dataPaisUni.codigo === "PE") {
//                vue.showUniverdidadName = false;
//                vue.showUniverdidadPeru = true;
//                setTimeout(function () {
//                    $('#univ-peru').select2(vue.buscarUniversidad());
//                }, 500);
//                $("#univ-peru").prop('required', true);
//                $("#univ-peru").select2("val", "");
//            } else {
//                vue.showUniverdidadName = true;
//                vue.showUniverdidadPeru = false;
//                $("#universidadExtranjeraName").prop('required', true);
//                $("#universidadExtranjeraName").val("");
//            }
        },
        mostrarUbicacionDomicilio: function () {
//            console.log();
//            var vue = this;
//            var dataPaisUni = $("#paisDomicilio").select2("data");
//            if (dataPaisUni.codigo === "PE") {
//                vue.showUbicacionDomicilio = true;
//                setTimeout(function () {
//                    $('#ubicacionDomicilio').select2(vue.buscarDistrito());
//                }, 500);
//                $("#ubicacionDomicilio").prop('required', true);
//                $("#ubicacionDomicilio").select2("val", "");
//            } else {
//                vue.showUbicacionDomicilio = false;
//                $("#ubicacionDomicilio").removeProp('required');
//            }
        },
        submitForm: function (e) {
            var self = $(e.currentTarget);
            console.log(self);
            self.btnDisabled();
            if (!$("#formAlumnoVisitante").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/visitante/alumno/save'),
                type: 'POST',
                async: true,
                data: $("#formAlumnoVisitante").serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/visitante/alumno/'));
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
        sinEspacios: function (e) {
            var self = $(e.currentTarget);
            APP.eliminarEspacios(self);
        },
        nombrePersona: function (e) {
            var self = $(e.currentTarget);
            APP.revisarNombre(self);
        },
        cambiarNumDoc: function () {
            var vue = this;
            $global.$emit('MODAL-WAIT-OPEN');
            var isvalid = $('[name="persona.tipoDocumento.id"]').parsley().isValid() == true;
            isvalid &= $('[name="persona.numeroDocIdentidad"]').parsley().isValid() == true;
            if (!isvalid) {
                $global.$emit('MODAL-WAIT-CLOSE');
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/visitante/alumno/existealumno'),
                data: {
                    'tipoDocumento.id': vue.persona.tipoDocumento.id,
                    numeroDocIdentidad: vue.persona.numeroDocIdentidad
                },
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        vue.persona = response.data;
                        vue.$forceUpdate();
                    } else {
                        vue.persona.numeroDocIdentidad = null;
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE');
                }
            });
        }
    }
});
