new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
        alumno: JSON.parse(alumnoJson),
        persona: {
            tipoDocumento: {},
            paisNacer: {}
        }
    },
    created() {
        let vue = this;
    },
    mounted: function () {
        let vue = this;

        $(".date").datepicker();
        $(".numerico").numeric({negative: false});

        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1}).
                on("change.select2", function (el) {
                    /*  */
                    vue.persona.tipoDocumento.id = el.val;
                    vue.cambiarNumDoc();

                    console.log("change persona.tipoDocumento.id");
                });
        $('[name="cicloIngreso.id"]').select2({minimumResultsForSearch: -1});

        $(".buscar-distrito").select2(vue.buscarDistrito());

        $('#nacionalidad').select2(vue.buscarPais());
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarDirNacimiento();
        });
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarUbicacionDomicilio();
        });
    },
    methods: {
        cambiarNumDoc: function () {
            var vue = this;
            //  $global.$emit('MODAL-WAIT-OPEN');
            var isvalid = $('[name="persona.tipoDocumento.id"]').parsley().isValid() == true;
            isvalid &= $('[name="persona.numeroDocIdentidad"]').parsley().isValid() == true;
            if (!isvalid) {
                //    $global.$emit('MODAL-WAIT-CLOSE');
                return;
            }
            // return;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/especial/existealumno'),
                data: {
                    'persona.tipoDocumento.id': vue.persona.tipoDocumento.id,
                    'persona.numeroDocIdentidad': vue.persona.numeroDocIdentidad,
                    'id': $('[name="id"]').val()
                },
                success: function (response) {
                    if (response.success) {
                        if (response.data != null && (response.data.id != null && response.data.id != "")) {
                            // let personaView = Object.assign({}, vue.persona);
                            vue.persona = response.data;
                            // vue.$set(this, 'persona', response.data);
                            if (vue.persona.paisNacer != null && vue.persona.paisNacer.id != "") {
                                $('#paisNacimiento').select2('data', {
                                    id: vue.persona.paisNacer.id,
                                    codigo: vue.persona.paisNacer.codigo,
                                    nombre: vue.persona.paisNacer.nombre
                                }
                                );
                                var dataPaisNac = $("#paisNacimiento").select2("data");
                                $('#paisNacimiento').attr("rel", dataPaisNac.nombre);
                                $('#paisNacimiento').attr("codigo", dataPaisNac.codigo);
                                $('#paisNacimiento').trigger('change');
                            }
                            console.log("nacionalidad");
                            console.dir(vue.persona.nacionalidad);

                            if (vue.persona.nacionalidad != null && vue.persona.nacionalidad.id != "") {
                                $('#nacionalidad').select2('data', {
                                    id: vue.persona.nacionalidad.id,
                                    codigo: vue.persona.nacionalidad.codigo,
                                    nombre: vue.persona.nacionalidad.nombre
                                }
                                );
                                var dataPaisNacionalidad = $("#nacionalidad").select2("data");
                                $('#nacionalidad').attr("rel", dataPaisNacionalidad.nombre);
                                $('#nacionalidad').attr("codigo", dataPaisNacionalidad.codigo);
                                //   $('#nacionalidad').trigger('change');
                            }

                            console.log("paisDomicilio");
                            console.dir(vue.persona.paisDomicilio);

                            if (vue.persona.paisDomicilio != null && vue.persona.paisDomicilio.id != "") {
                                $('#paisDomicilio').select2('data', {
                                    id: vue.persona.paisDomicilio.id,
                                    codigo: vue.persona.paisDomicilio.codigo,
                                    nombre: vue.persona.paisDomicilio.nombre
                                }
                                );
                                var paisDomicilio = $("#paisDomicilio").select2("data");
                                $('#nacionalidad').attr("rel", paisDomicilio.nombre);
                                $('#nacionalidad').attr("codigo", paisDomicilio.codigo);
                            }

                        } else {
                            vue.persona.id = "";
                        }
                    } else {
                        vue.persona.numeroDocIdentidad = null;
                        notify(response.message, 'error');
                    }
                    //  $global.$emit('MODAL-WAIT-CLOSE');
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                    //   $global.$emit('MODAL-WAIT-CLOSE');
                }
            });
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
            console.log("mostrarDirNacimiento");
            var vue = this;

            var dataPaisNac = $("#paisNacimiento").select2("data");
            console.log("dataPaisNac");
            console.dir(dataPaisNac);
            if (dataPaisNac.codigo === "PE") {
                vue.showLugarNacimiento = true;
                setTimeout(function () {
                    if (vue.persona.ubicacionNacer != null && vue.persona.ubicacionNacer.id != "") {

                        $('#distNacimiento').attr("rel", vue.persona.ubicacionNacer.distrito);
                        $('#distNacimiento').attr("value", vue.persona.ubicacionNacer.id);

                        $('#distNacimiento').select2('data', {
                            id: vue.persona.paisNacer.id,
                            nombre: vue.persona.ubicacionNacer.distrito
                        }
                        );
                    }
                    $("#distNacimiento").select2(vue.buscarDistrito());
                }, 0);
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
            if (!$("#formAlumno").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/alumno/saveAlumnoEspecial'),
                type: 'POST',
                async: true,
                data: $("#formAlumno").serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/alumno'));
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
        checkMatricula: function () {
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
        sinEspacios: function (e) {
            APP.eliminarEspacios($(e.currentTarget));
        },
        nombrePersona: function (e) {
            APP.revisarNombre($(e.currentTarget));
        }
    }
});
