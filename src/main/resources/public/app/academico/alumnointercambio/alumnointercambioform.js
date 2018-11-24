new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        becado: {id: null, alumno: {persona: {}}, cicloBeca: {}, universidad: {}, paisDestino: {}, universidadDestino: {id: null}},
        persona: {
            tipoDocumento: {id: null},
            paisNacer: {id: null},
            nacionalidad: {id: null},
            paisDomicilio: {id: null},
            ubicacionDomicilio: {id: null},
            ubicacionNacer: {id: null}
        },
        alumnoVisitante: {
            cicloEstudia: {id: null},
            paisUniversidad: {id: null},
            universidad: {id: null},
        },
        dataNuevaUniversidadExtranjera: {
            id: 'modalNuevaUniversidadExtranjera',
            header: false,
        },
        nuevauniversidad: {},
        isprocess: false,
    },
    created() {
        let $vue = this;
    },
    mounted: function () {
        var vue = this;

        $('#formAlumnoBecado').parsley().destroy();
        $('[name="universidad.id"]').select2(vue.buscarUniversidad());
        $('[name="alumno.id"]').select2(vue.buscarAlumno());
        $('[name="paisDestino.id"]').select2(vue.buscarPais());
        $('[name="cicloBeca.id"]').select2({minimumResultsForSearch: -1});
        $('[name="monto"]').numeric();

        $('[name="alumno.id"]').select2('val', '');
        $('[name="paisDestino.id"]').select2('val', '');
        $('[name="cicloBeca.id"]').select2('val', '');
        
        console.log(idalumno)
        
        if(idalumno){
            vue.editar()
        }


    },
    methods: {
        editar(id) {

            var vue = this;
//            vue.becado = {id: null, alumno: {persona: {}}, cicloBeca: {}, universidad: {}, paisDestino: {}};

            $.ajax({
                url: APP.url('academico/becado/alumno/update'),
                type: 'POST',
                async: false,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        vue.becado = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

//            vue.$refs.modalAddAlumnoBecado.open();
//
//            $('#formAlumnoBecado').parsley().destroy();
//            $('[name="alumno.id"]').select2(vue.buscarAlumno());
//            $('[name="paisDestino.id"]').select2(vue.buscarPais());
//            $('[name="universidadDestino.id"]').select2(vue.buscarUniversidad());
//            $('[name="cicloBeca.id"]').select2({minimumResultsForSearch: -1});
//            $('[name="monto"]').numeric();
//
//            $('[name="alumno.id"]').select2('data', {
//                id: vue.becado.alumno.id,
//                nombre: vue.becado.alumno.persona.nombreCompleto
//            }).trigger("change");
//
//            $('[name="paisDestino.id"]').select2('data', {
//                id: vue.becado.paisDestino.id,
//                nombre: vue.becado.paisDestino.nombre
//            }).trigger("change");
//
//            $('[name="universidadDestino.id"]').select2('data', {
//                id: vue.becado.universidadDestino.id,
//                nombre: vue.becado.universidadDestino.nombre
//            }).trigger("change");
//
//            $('[name="cicloBeca.id"]').select2('data', {
//                id: vue.becado.cicloBeca.id,
//                nombre: vue.becado.cicloBeca.descripcion
//            }).trigger("change");

        },
        buscarAlumno() {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un alumno",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/becado/alumno/searchAlumno"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarAlumno").render(info);
                },
                formatSelection: function (info) {
                    vue.becado.alumno = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarUniversidad: function () {
            var vue = this;
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
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.becado.universidad = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarPais: function () {
            var vue = this;
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
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.becado.paisDestino = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        createAlumnoBecado() {

            var vue = this;
            var valid = $('#formAlumnoBecado').parsley().validate();
            vue.isprocess = true;

            if (valid != true) {
                console.log('no valido');
                console.log(valid);
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/becado/alumno/save'),
                data: $('#formAlumnoBecado').serialize(),
                success: function (response) {
                    if (response.success) {
                        $(location).attr('href', APP.url('academico/becado/alumno'));
                    } else {
                        vue.isprocess = false;
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    vue.isprocess = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        actualizar() {
        },
        addUniversidad() {
            var vue = this;
            vue.alumnoVisitante.paisUniversidad;
            vue.$refs.nuevaUniversidadExtranjera.open();
            var keys = Object.keys(vue.nuevauniversidad);
            for (var key in keys) {
                vue.nuevauniversidad['' + keys[key]] = null;
            }
            $('#formNuevaUniversidadExtranjera').find('[name=gestion]').select2({minimumResultsForSearch: -1});
        },
        saveNuevaUniversidadExtranjera() {
            var vue = this;
            if ($('#formNuevaUniversidadExtranjera').parsley().validate() != true) {
                return;
            }
            vue.showLoader();
            $.ajax({
                method: 'POST',
                url: APP.url('academico/visitante/alumno/saveuniversidad'),
                data: $('#formNuevaUniversidadExtranjera').serialize(),
                async: false,
                success: function (response) {
                    if (response.success) {
                        var keys = Object.keys(response.data);
                        for (var key in keys) {
                            vue.becado.universidadDestino['' + keys[key]] =  response.data['' + keys[key]];
                        }
                        vue.$refs.nuevaUniversidadExtranjera.close();
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.hideLoader();

                }, error: function () {
                    vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        changePaisUniversidad(alf) {
            var vue = this;
            console.log(alf.id);
            var keys = Object.keys(vue.becado.universidadDestino);
            for (var key in keys) {
                vue.becado.universidadDestino['' + keys[key]] = null;
            }
        },
    }
});
