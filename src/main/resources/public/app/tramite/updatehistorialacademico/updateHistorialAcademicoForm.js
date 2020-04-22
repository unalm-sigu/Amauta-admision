var AlumnoSearch = Vue.component("alumnoSearch", {
    template: "#alumnoSearchTemplate",
    data: function () {
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
        solicitud: {
            id: null,
            tipoDocumentoAcademico: {id: null, precioDocumento: []},
            tramite: {id: null,persona:{id:null}},
            idioma: {id: null}
        },
        eseditar: false,
    },
    computed: {
        guardar: function () {
            let vue = this;
            return  vue.eseditar ? 'Actualizar' : 'Guardar';
        }
    },
    watch: {
        alumno: function (newQuestion, oldQuestion) {
            let vue = this;
            if (!vue.eseditar) {
                vue.solicitud.personaContacto = vue.alumno.nombre;
                vue.solicitud.telefono = vue.alumno.telefono;
                vue.solicitud.celular = vue.alumno.celular;
                vue.solicitud.email = vue.alumno.email;
            }
        }
    },
    mounted: function () {
        let vue = this;
        vue.allTipoDocumento();
        vue.eseditar = $('[name="id"]').val() > 0;


        $global.$on("changeTipo", function () {
            vue.changeTipo();
        });

        $('[name="idioma.id"]').select2({minimumResultsForSearch: -1}).on("change.select2", function (el) {
            vue.solicitud.idioma = el.added;
            vue.changeTipo();
        });

        $('[name="tramite.alumno.id"]').select2(vue.selectAlumno(vue)).on("change.select2", function (el) {
            if (el.val.length < 1) {
                vue.alumno = {id: null};
            }
        });

        if (vue.eseditar) {
            vue.updateSolicitud($('[name="id"]').val());
            $('[name="tramite.alumno.id"]').select2('data', vue.alumno);
            $('[name="idioma.id"]').select2('val', vue.solicitud.idioma.id);
        }

    },
    updated: function () {
        let vue = this;
        $('[name="idioma.id"]').select2('val', vue.solicitud.idioma.id);
    },
    methods: {
        selectAlumno: function (vue) {
            return {
                allowClear: true,
                placeholder: "Seleccione un alumno",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchalumno"),
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
                    if (vue.alumno.id != null) {
                        callback(vue.alumno);
                    }
                },
                formatResult: function (info) {
                    var alumnoSearch = new AlumnoSearch();
                    alumnoSearch.alumno = info;
                    var cmp = alumnoSearch.$mount();
                    return cmp.$el;
                },
                formatSelection: function (info) {
                    vue.alumno = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        submitForm: function () {
            var vue = this;
            vue.guardando = true;
            var valid = $('#formSolicitudConstancia').parsley().validate();
            if (valid != true) {
                vue.guardando = false;
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/save'),
                data: $('#formSolicitudConstancia').serialize(),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url("tramite/solicitudconstancia/updatehistorial");
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.guardando = false;
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                    vue.guardando = false;
                }
            });
        },
        allTipoDocumento: function () {
            let vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/tipodocumento'),
                success: function (response) {
                    if (response.success) {
                        vue.tipodocumento = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        updateSolicitud: function (idSolicitud) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/update'),
                data: {id: idSolicitud},
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.solicitud = response.data.solicitud;
                        vue.alumno = response.data.alumno;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        changeTipo: function () {
            let vue = this;
            var subtipo = vue.solicitud.tipoDocumentoAcademico.tipo;
            var precioss = vue.solicitud.tipoDocumentoAcademico.precioDocumento;
            vue.showCostoDocumento = false;
            vue.costoDocumento = 0.0;
            if (precioss.length > 0) {
                if (subtipo == 'CONS') {
                    var precio = precioss.find(item => item.idioma.id == vue.solicitud.idioma.id);
                    if (precio) {
                        vue.showCostoDocumento = true;
                        vue.costoDocumento = precio.precio;
                    }
                }
            }
        }
    }
});