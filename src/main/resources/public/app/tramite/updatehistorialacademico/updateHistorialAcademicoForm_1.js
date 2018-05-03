var AlumnoSearch = Vue.component("alumnoSearch", {
    template: "#alumnoSearchTemplate",
    data: function() {
        return {alumno: {}};
    }
});
new Vue({
    el: '#main',
    data: {
        tipodocumento: [],
        showCostoDocumento: false,
        costoDocumento: 0.0,
        guardando: false,
        alumno: {id: null},
        solicitud: {id: null},
        eseditar: false
    },
    watch: {
        alumno: function(newQuestion, oldQuestion) {
            let vue = this;
            if (!vue.eseditar) {
                vue.solicitud.personaContacto = vue.alumno.nombre;
                vue.solicitud.telefono = vue.alumno.telefono;
                vue.solicitud.celular = vue.alumno.celular;
                vue.solicitud.email = vue.alumno.email;
            }
        }
    },
    mounted: function() {
        let vue = this;

        $('[name="tipoDocumentoAcademico.id"]').
                select2({minimumResultsForSearch: -1}).
                on("change.select2", function(el) {
                    vue.changeTipo();
                });

        $('[name="idioma.id"]').select2({minimumResultsForSearch: -1}).
                on("change.select2", function(el) {
                    vue.changeTipo();
                });

        $('[name="tramite.alumno.id"]').select2(vue.selectAlumno(vue)).
                on("change.select2", function(el) {
                    if (el.val.length < 1) {
                        vue.alumno = {id: null};
                    }
                });

        vue.eseditar = $('[name="id"]').val() > 0;
        vue.allTipoDocumento();

    },
    methods: {
        selectAlumno: function(vue) {
            return {
                allowClear: true,
                placeholder: "Seleccione un alumno",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchalumno"),
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
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function(info) {
                    var alumnoSearch = new AlumnoSearch();
                    alumnoSearch.alumno = info;
                    var cmp = alumnoSearch.$mount();
                    return cmp.$el;
                },
                formatSelection: function(info) {
                    vue.alumno = info;
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        submitForm: function() {
            var vue = this;
            vue.guardando = true;
            var valid = $('#formSolicitudConstancia').parsley().validate();
            if (valid != true) {
                vue.guardando = false;
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/save'),
                data: $('#formSolicitudConstancia').serialize(),
                success: function(response) {
                    if (response.success) {
                        location.href = APP.url("tramite/solicitudconstancia/updatehistorial");
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.guardando = false;
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                    vue.guardando = false;
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
            var precioss = tipoDocumento.precios;
            vue.showCostoDocumento = false;
            vue.costoDocumento = 0.0;
            if (precioss.length > 0) {
                if (subtipo == 'CONS') {
                    var precio = precioss.find(item => item.idioma.id === idIdioma);
                    if (precio) {
                        vue.showCostoDocumento = true;
                        vue.costoDocumento = precio.precio;
                    }
                }
            }
        },
    }
});