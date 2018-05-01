new Vue({
    el: '#main',
    data: {
        tipodocumento: [],
        showCostoDocumento: false,
        costoDocumento: 0.0
    },
    mounted: function() {
        let vue = this;
        $('[name="matricula.id"]').select2({minimumResultsForSearch: -1});

        $('[name="tipoDocumentoAcademico.id"]').
                select2({minimumResultsForSearch: -1}).
                on("change.select2", function(el) {
                    vue.changeTipo();
                });

        $('[name="idioma.id"]').select2({minimumResultsForSearch: -1}).
                on("change.select2", function(el) {
                    vue.changeTipo();
                });

        vue.allTipoDocumento();
    },
    methods: {
        submitForm: function() {
            var vue = this;
            var valid = $('#formSolicitudConstancia').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/save'),
                data: $('#formSolicitudConstancia').serialize(),
                success: function(response) {
                    if (response.success) {
                        location.href = APP.url("tramite/solicitudconstancia");
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allTipoDocumento: function() {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/tipodocumento'),
                sync: true,
                success: function(response) {
                    if (response.success) {
                        vue.tipodocumento = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeTipo: function() {
            let vue = this;
            var idTipo = parseInt($('[name="tipoDocumentoAcademico.id"]').select2('val'));
            var idIdioma = parseInt($('[name="idioma.id"]').select2('val'));
            var tipoDocumento = vue.tipodocumento.find(item => item.id === idTipo);
            var subtipo = tipoDocumento.tipo;
            console.log(tipoDocumento.tipo);
            var precioss = tipoDocumento.precios;
            console.log(precioss.length);
            vue.showCostoDocumento = false;
            vue.costoDocumento = 0.0;
            if (precioss.length > 0) {
                if (subtipo == 'CONS') {
                    var precio = precioss.find(item => item.idioma.id === idIdioma);
                    if (precio) {
                        console.log(precio.precio);
                        vue.showCostoDocumento = true;
                        vue.costoDocumento = precio.precio;
                    }
                }
            }
        },
    }
});