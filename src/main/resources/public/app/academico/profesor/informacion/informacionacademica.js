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
        labelContrato: {'ACT': 'label-success'},
        textContrato: {'ACT': 'Activo'},
        modalContrato: {
            id: 'modalContrato',
            header: true,
            title: 'Nuevo contrato',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        }
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
                });

        self.find("[name='modalidadEstudio.id']").select2({minimumResultsForSearch: -1});

        self.find("[name='categoria.id']").select2({minimumResultsForSearch: -1});

        self.find("[name='situacion.id']").select2({minimumResultsForSearch: -1});

        self.find("[name='dedicacion.id']").select2({minimumResultsForSearch: -1});

        self.find("[name='cicloInicioContrato.id']").select2({minimumResultsForSearch: -1});

        self.find("[name='cicloFinContrato.id']").select2({minimumResultsForSearch: -1});

        if (vue.docente.id !== null) {
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
                        self.btnEnable();
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
        updateDocente: function (idDocente) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/profesor/find'),
                data: {id: idDocente},
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.docente = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        buildSeccionesHtml: function (record) {
            var seccionesHtml = "";
            var secciones = record.secciones.split(",");

            for (var i = 0; i < secciones.length; i++) {
                seccionesHtml += '<div class="m-l-md inline"><a href="#" ';
                if (record.estado == 'ACEP' && secciones[i].split("|")[4] == "VER") {
                    seccionesHtml += 'class="notas-academicas"';
                } else if (secciones[i].split("|")[4] == "VER") {
                    seccionesHtml += 'class="ver-alumnos"';
                } else {
                    seccionesHtml += 'class="text-danger no-ver-alumnos"';
                }
                seccionesHtml += ' rel="' + secciones[i].split("|")[0] + '">' + secciones[i].split("|")[1];
                if (secciones[i].split("|")[3] != " ") {
                    seccionesHtml += " - " + secciones[i].split("|")[3];
                }
                seccionesHtml += '</a></div>';
            }
            return seccionesHtml;
        },
        buildResponsable: function (record) {
            if (record.responsable != null) {
                return '<div class="block"><strong>Responsable:</strong> ' + record.responsable + '</div>';
            } else {
                return '<div class="text-danger block">Sin responsable</div>';
            }
        },
        nuevoContrato() {
            this.$refs.modalContrato.open();
        },
        saveContrato() {
            if (!$('#formContrato').parsley().validate()) {
                return;
            }
        }
    },
});
