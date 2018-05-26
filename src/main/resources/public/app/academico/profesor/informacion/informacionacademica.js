new Vue({
    el: '#main',
    data: {
        actualizar: false,
        stepactivo: 1,
        docente: {
            id: iddocente,
            modalidadEstudio: {id: null},
            departamentoAcademico: {id: null},
            persona: {
                id: null,
                tipoDocumento: {id: null},
                paisNacer: {id: null},
                nacionalidad: {id: null},
                paisDomicilio: {id: null},
                ubicacionDomicilio: {id: null},
                ubicacionNacer: {id: null}
            },
        },
    },
    mounted: function () {

        let vue = this;
        let self = $(vue.$el);

        self.find(".numerico").numeric({negative: false});
        self.find(".date").datepickerBoot().on('changeDate', function (e) {
            var ella = $(e.currentTarget);
            vue.docente.persona.fechaNacer = ella.find('input').val();
        });

        self.find('[name="persona.tipoDocumento.id"]').
                select2({minimumResultsForSearch: -1}).
                on("change.select2", function (el) {
                    vue.docente.persona.tipoDocumento.id = el.val;
                    vue.cambiarNumDoc();
                });

        self.find("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});

        if (vue.docente.id != null) {
            vue.updateDocente(vue.docente.id);
        }

    },
    updated: function () {
        let vue = this;
        this.$nextTick(function () {
            let self = $(vue.$el);
            self.find('[name="persona.tipoDocumento.id"]').select2('val', vue.docente.persona.tipoDocumento.id);
            self.find("[name='modalidadEstudio.id']").select2('val', vue.docente.modalidadEstudio.id);
        });
    },
    methods: {
        submitForm: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formDocente").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/profesor/save'),
                type: 'POST',
                async: true,
                data: $("#formDocente").serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/profesor'));
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
                url: APP.url('academico/profesor/existedocente'),
                data: {
                    'persona.tipoDocumento.id': vue.docente.persona.tipoDocumento.id,
                    'persona.numeroDocIdentidad': vue.docente.persona.numeroDocIdentidad,
                    'id': vue.docente.id
                },
                success: function (response) {
                    if (response.success) {
                        if (response.data.id) {
                            vue.docente.persona = response.data;
                        }
                    } else {
                        vue.docente.persona.numeroDocIdentidad = null;
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE');
                }
            });
        },
        validarEmailEmpresa: function () {
            var vue = this;
            $global.$emit('MODAL-WAIT-OPEN');
            var isvalid = $('[name="persona.emailCompania"]').parsley().isValid() == true;
            if (!isvalid) {
                $global.$emit('MODAL-WAIT-CLOSE');
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/profesor/validarEmailEmpresa'),
                data: {
                    email: vue.docente.persona.emailCompania,
                    persona: vue.docente.persona.id
                },
                success: function (response) {
                    if (!response.success) {
                        vue.docente.persona.emailCompania = null;
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE');
                }
            });
        },
        validarEmail: function () {
            var vue = this;
            $global.$emit('MODAL-WAIT-OPEN');
            var isvalid = $('[name="persona.email"]').parsley().isValid() == true;
            if (!isvalid) {
                $global.$emit('MODAL-WAIT-CLOSE');
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/profesor/validarEmail'),
                data: {
                    email: vue.docente.persona.email,
                    persona: vue.docente.persona.id
                },
                success: function (response) {
                    if (!response.success) {
                        vue.docente.persona.email = null;
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE');
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    $global.$emit('MODAL-WAIT-CLOSE');
                }
            });
        },
        updateDocente: function (idDocente) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/profesor/find'),
                data: {id: idDocente},
                async: false,
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        vue.docente = response.data;
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
