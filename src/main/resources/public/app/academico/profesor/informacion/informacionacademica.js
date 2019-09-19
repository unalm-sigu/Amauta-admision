new Vue({
    el: '#main',
    data: {
        actualizar: false,
        stepactivo: 1,
        ciclo: JSON.parse(cicloJson),
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
        resolucion: {
            id: ''
        },
        labelContrato: {'ACT': 'label-success', 'PEND': 'label-warning', 'RESL': 'label-primary', 'CFIN': 'label-danger', 'VENC': 'label-default'},
        modalContrato: {
            id: 'modalContrato',
            header: true,
            title: 'Nuevo contrato',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        },
        modalResolucionConsejo: {
            id: 'modalResolucionConsejo',
            header: true,
            title: 'Agregar Resolución del Consejo',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        },
        modalResolucionFacultad: {
            id: 'modalResolucionFacultad',
            header: true,
            title: 'Agregar Resolucíon de Facultad',
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
        self.find("[name='cicloFinContrato.id']").select2({
            minimumInputLength: 1,
            ajax: {
                url: APP.url("academico/profesor/contrato/searchciclo"),
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
                return info.descripcion;
            },
            formatSelection: function (info) {
                return info.descripcion;
            }
        });
        self.find("[name='cicloInicioContrato.id']").select2({
            placeholder: '',
            minimumInputLength: 1,
            ajax: {
                url: APP.url("academico/profesor/contrato/searchciclo"),
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
                return null;
            },
            formatResult: function (info) {
                return info.descripcion;
            },
            formatSelection: function (info) {
                return info.descripcion;
            },
            escapeMarkup: function (m) {
                return m;
            }
        });

        self.find("#resConsejoId").select2({
            minimumInputLength: 1,
            ajax: {
                url: APP.url("academico/profesor/contrato/searchresolucionconsejo"),
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
                return info.descripcion;
            },
            formatSelection: function (info) {
                return info.descripcion;
            }
        });

        self.find("#resFacultadId").select2({
            minimumInputLength: 1,
            ajax: {
                url: APP.url("academico/profesor/contrato/searchresolucionfacultad"),
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
                return info.descripcion;
            },
            formatSelection: function (info) {
                return info.descripcion;
            }
        });

        if (vue.docente.id !== null) {
            vue.updateDocente(vue.docente.id);
        }
        this.$refs.loadHorario.cargaHorario();
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
        tipoSeccion(seccion) {
            if (seccion.tipoSeccionEnum.value.indexOf(" ") < 0) {
                return seccion.tipoSeccionEnum.value;
            }
            return seccion.tipoSeccionEnum.value.split(" ")[0];
        },
        submitForm: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formDocente").parsley().validate() == true) {
                self.btnEnable();
                notify("Falta llenar campos en el formulario, verifique.", "error");
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
            let self = $(this.$el);
            self.find("[name='modalidadEstudio.id']").select2("val", "");
            self.find("[name='categoria.id']").select2("val", "");
            self.find("[name='situacion.id']").select2("val", "");
            self.find("[name='dedicacion.id']").select2("val", "");
            self.find("[name='cicloFinContrato.id']").select2("val", "");
            self.find("[name='cicloInicioContrato.id']").select2("val", "");
            this.$refs.modalContrato.open();
        },
        saveContrato() {
            let form = $('#formContrato');
            if (!form.parsley().validate()) {
                return;
            }
            let envelope = form.serialize();
            AXIOS.post(`/academico/profesor/${this.docente.id}/contratos/save`, envelope)
                    .then(response => {
                        if (response.data.success) {
                            this.$refs.raptorContratos.loadRemoteData();
                            this.$refs.modalContrato.close();
                        }
                    })
        },
        addVistoBueno(item) {
            bootbox.confirm({
                message: `¿Seguro que desea añadir el visto bueno al contrato?`,
                buttons: {
                    confirm: {label: 'Sí, seguro', className: 'btn-success'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`/academico/profesor/contrato/${item.id}/vistobueno`)
                                .then(response => {
                                    if (response.data.success) {
                                        this.$refs.raptorContratos.loadRemoteData();
                                    }
                                })
                    }
                }
            });
        },
        addResolucionFacultad(item) {
            this.resolucion.id = item.id;
            this.$refs.modalResolucionFacultad.open();
        },
        addResolucionConsejo(item) {
            this.resolucion.id = item.id;
            this.$refs.modalResolucionConsejo.open();
        },
        saveResolucionFacultad() {
            let form = $('#formResolucionFacultad');
            if (!form.parsley().validate()) {
                return;
            }
            AXIOS.post(`/academico/profesor/contrato/${this.resolucion.id}/resolucionfacultad`, form.serialize())
                    .then(response => {
                        if (response.data.success) {
                            this.$refs.raptorContratos.loadRemoteData();
                            this.$refs.modalResolucionFacultad.close();
                        }
                    })
        },
        saveResolucionConsejo() {
            let form = $('#formResolucionConsejo');
            if (!form.parsley().validate()) {
                return;
            }
            AXIOS.post(`/academico/profesor/contrato/${this.resolucion.id}/resolucionconsejo`, form.serialize())
                    .then(response => {
                        if (response.data.success) {
                            this.$refs.raptorContratos.loadRemoteData();
                            this.$refs.modalResolucionConsejo.close();
                        }
                    })
        },
        finalizar(item) {
            bootbox.confirm({
                message: `¿Seguro que desea finalizar el contrato?`,
                buttons: {
                    confirm: {label: 'Sí, seguro', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post(`/academico/profesor/contrato/${item.id}/finalizar`)
                                .then(response => {
                                    if (response.data.success) {
                                        this.$refs.raptorContratos.loadRemoteData();
                                    }
                                })
                    }
                }
            });
        }, cargarHorario() {
            console.log("cargarHorario");
            this.stepactivo = 5;
            //    $vue.loadPages.horario = true;
            this.$refs.loadHorario.cargaHorario();
        }
    },
});
