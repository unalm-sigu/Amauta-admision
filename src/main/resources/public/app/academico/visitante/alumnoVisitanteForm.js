new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUniverdidadName: codigoPaisUniversidad != 'PE',
        showUniverdidadPeru: codigoPaisUniversidad == 'PE',
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
        docente:{},
        persona:{tipoDocumento:{id:null}},
    },
    mounted: function () {

        let vue = this;

        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1}).on("change.select2", function (el) {
            vue.cambioTipoDoc();
        });
        $('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});
        
        $(".buscar-distrito").select2(vue.buscarDistrito());
        $(".date").datepicker();
        $(".numerico").numeric({negative: false});
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarDirNacimiento();
        });
        $('#nacionalidad').select2(vue.buscarPais());
        $('#paisUniversidad').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarUniversidadName();
        });
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarUbicacionDomicilio();
        });
        $('#univ-peru').select2(vue.buscarUniversidad());
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
        mostrarUniversidadName: function () {
            var vue = this;
            var dataPaisUni = $("#paisUniversidad").select2("data");
            if (dataPaisUni.codigo === "PE") {
                vue.showUniverdidadName = false;
                vue.showUniverdidadPeru = true;
                setTimeout(function () {
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
        mostrarUbicacionDomicilio: function () {
            console.log();
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
        cambioTipoDoc: function () {
            var vue=this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            vue.completarPersona();
            $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
        },
        cambiarNumDoc: function () {
            var vue=this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            vue.completarPersona();
            $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
        }, completarPersona: function () {
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/calcularpromedio'),
                data: {id: vue.alumno.id},
                success: function (response) {
                    if (response.success) {
                        vue.cargaHistorial();
                        notify(response.message, 'error');
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }
            });
        }
    }
});
