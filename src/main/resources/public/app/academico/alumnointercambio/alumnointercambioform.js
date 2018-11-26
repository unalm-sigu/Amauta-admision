new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        becado: {
            id: null,
            alumno: {
                persona: {
                    tipoDocumento: {id: null},
                    paisNacer: {id: null},
                    nacionalidad: {id: null},
                    paisDomicilio: {id: null},
                    ubicacionDomicilio: {id: null},
                    ubicacionNacer: {id: null}
                }
            },
            cicloIntercambio: {},
            paisDestino: {id: null},
            universidadDestino: {id: null},
            becaEstudio: {id: null}
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
        console.log(idalumno)
        if (idalumno) {
            vue.editar(idalumno)
        }
    },
    methods: {
        editar(id) {
            var vue = this;
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
        addUniversidad() {
            var vue = this;

            if (vue.becado.paisDestino.id == null) {
                notify('Seleccione el pais de destino', 'info');
                return;
            }
            console.log(vue.becado.paisDestino.id);
            console.log(vue.becado.paisDestino.nombre);
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

                        vue.becado.universidadDestino = response.data;
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
        changePaisDestino() {
            var vue = this;
            console.log('changePaisDestino');
            if (vue.becado.universidadDestino.pais.id != vue.becado.paisDestino.id) {
                vue.becado.universidadDestino = {id: null};
            }
        }
    }
});
