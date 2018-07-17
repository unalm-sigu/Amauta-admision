new Vue({
    el: '#main',
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
    },
    mounted: function () {

        let vue = this;
        let self = $(vue.$el);

        self.find(".numerico").numeric({negative: false});
        self.find(".date").datepicker();
        self.find('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});

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
            self.find('[name="cicloEstudia.id"]').select2('val', vue.alumnoVisitante.cicloEstudia.id);
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
                    'persona.tipoDocumento.id': vue.persona.tipoDocumento.id,
                    'persona.numeroDocIdentidad': vue.persona.numeroDocIdentidad,
                    'id': $('[name="id"]').val()
                },
                success: function (response) {
                    if (response.success) {
                        if (response.data.id) {
                            vue.persona = response.data;
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
    },
});
