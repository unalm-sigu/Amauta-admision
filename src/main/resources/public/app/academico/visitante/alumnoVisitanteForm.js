new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        actualizar: false,
        alumnoVisitante: {
            cicloEstudia: {id: null},
            paisUniversidad: {id: null},
            universidad: {id: null},
        },
        persona: {
            tipoDocumento: {id: null},
            paisNacer: {id: null},
            nacionalidad: {id: null},
            paisDomicilio: {id: null},
            ubicacionDomicilio: {id: null},
            ubicacionNacer: {id: null}
        },
        dataNuevaUniversidadExtranjera: {
            id: 'modalNuevaUniversidadExtranjera',
            header: false,
        },
        nuevauniversidad: {}
    },
    mounted: function () {

        let vue = this;
        let self = $(vue.$el);

        self.find(".numerico").numeric({negative: false});
        self.find(".date").datepicker();
        // self.find('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});

        self.find('[name="persona.tipoDocumento.id"]').
                select2({minimumResultsForSearch: -1}).
                on("change.select2", function (el) {
                    vue.persona.tipoDocumento.id = el.val;
                    vue.cambiarNumDoc();
                });

        if ($('[name="id"]').val() != '') {
            vue.actualizar = true;
            vue.updateAlumnoVisitante($('[name="id"]').val());
        }

    },
    updated: function () {
        let vue = this;
        this.$nextTick(function () {
            let self = $(vue.$el);
            // self.find('[name="cicloEstudia.id"]').select2('val', vue.alumnoVisitante.cicloEstudia.id);
            self.find('[name="persona.tipoDocumento.id"]').select2('val', vue.persona.tipoDocumento.id);
        });
    },
    methods: {
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
                error: function (response) {
                    self.btnEnable();
                    console.dir(response);
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
                    'persona.tipoDocumento.id': vue.persona.tipoDocumento.id,
                    'persona.numeroDocIdentidad': vue.persona.numeroDocIdentidad,
                    'id': $('[name="id"]').val()
                },
                success: function (response) {
                    if (response.success) {
                        console.log("##############");
                        console.dir(response);
                        if (response.data.id != null && response.data.id != "") {
                            // let personaView = Object.assign({}, vue.persona);
                            vue.persona = response.data;
                        } else {
                            vue.persona.id = "";
                        }
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
        },
        updateAlumnoVisitante: function (idAlumno) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/visitante/alumno/find'),
                data: {id: idAlumno},
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.alumnoVisitante = response.data.alumnoVisitante;
                        vue.persona = response.data.persona;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addUniversidad() {
            var vue = this;
            vue.alumnoVisitante.paisUniversidad;
            vue.$refs.nuevaUniversidadExtranjera.open();
            var keys = Object.keys(vue.nuevauniversidad);

            console.dir("===")
            console.dir(vue.alumnoVisitante.paisUniversidad.id)
            console.dir("===")
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
                        vue.alumnoVisitante.universidad = response.data;
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
            var keys = Object.keys(vue.alumnoVisitante.universidad);
            for (var key in keys) {
                vue.alumnoVisitante.universidad['' + keys[key]] = null;
            }
        }
    }
});
