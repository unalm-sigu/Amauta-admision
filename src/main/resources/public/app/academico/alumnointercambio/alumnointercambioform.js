Vue.component("multiselect", window.VueMultiselect.default);
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
            becaEstudio: {id: null, institucion: {id: null, razonSocial: null}}
        },
        dataNuevaUniversidadExtranjera: {
            id: 'modalNuevaUniversidadExtranjera',
            header: false,
        },
        modalNuevaBeca: {
            id: 'modalNuevaBeca',
            header: true,
            title:'Nueva Beca de Estudio'
        },
        nuevauniversidad: {},
        isprocess: false,
        instituciones: JSON.parse(institucionesJson),
        becaEstudio: {id: null, institucion: {id: null, razonSocial: null}}
    },
    created() {
        let $vue = this;
//        
    },
    mounted: function () {
        var vue = this;
        $('[name="monto"]').numeric({negative:false});
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
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
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
        addBeca() {
            let vue = this;
            vue.becaEstudio = {id: null, institucion: {id: null, razonSocial: null}};
            vue.$refs.modalNuevaBeca.open();
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
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        changePaisDestino() {
            var vue = this;
            console.log('changePaisDestino');
            console.dir(vue.becado.universidadDestino);
            if (vue.becado.universidadDestino.id != null) {
                if (vue.becado.universidadDestino.pais.id != vue.becado.paisDestino.id) {
                    vue.becado.universidadDestino = {id: null};
                }
            }
        },
        saveBeca() {
            var vue = this;
            var valid = $('#formNuevaBeca').parsley().validate();
//            vue.isprocess = true;
            console.log("save Beca")
            console.dir(vue.becaEstudio)
            if (valid != true) {
                return;
            }

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url('academico/becado/alumno/saveBeca'),
                data: JSON.stringify(vue.becaEstudio),
                success: function (response) {
                    if (response.success) {
                        vue.becado.becaEstudio = response.data;
                        notify(response.message, 'info');
                        vue.$refs.modalNuevaBeca.close();
                    } else {
//                        vue.isprocess = false;
                        notify(response.message, 'error');
                    }
                }, error: function () {
//                    vue.isprocess = false;
                    notify(Messages.errorComunicacion, "error");
                }
            });


        }
    }
});
