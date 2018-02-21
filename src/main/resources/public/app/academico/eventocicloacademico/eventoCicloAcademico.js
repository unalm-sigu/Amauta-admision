new Vue({
    el: '#main',
    data: {
        ciclos: [{id: null}],
        btnActive: 'lista',
        onlyOne: true,
        eventoCicloAcademico: {eventoAcademico: {}},
        motivoAnular: "",
        addEventoCicloAcademicoaModal: {
            id: 'modalAddEventoCicloAcademico',
            header: true,
            title: 'Ciclo académico',
            okbtn: 'Guardar'
        },
        addAnularCicloModal: {
            id: 'modalAddAnularCiclo',
            header: true,
            title: 'Anular Ciclo académico',
            okbtn: 'Aceptar'
        }
    },

    created() {
        let vue = this;
    },

    watch: {
        btnActive: function(after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.fullcalendar.render();
                vue.onlyOne = false;
            }
        }
    },

    mounted: function() {
        let vue = this;
        $global.$on("eliminar", function(id) {
            vue.eliminar(id);
        });
        $global.$on("editar", function(id) {
            vue.editar(id);
        });
        vue.renderEventos();
    },

    methods: {
        formClear: function() {
            $('#formEventoCicloAcademico').parsley('destroy');
            $('[name="eventoAcademico.id"]').select2('val', '');

            $('.date').datepicker();

            $('[name=fechaInicio]').datepicker().on('change', function() {
                $('[name=fechaFin]').datepicker().datepicker('setStartDate', $('[name=fechaInicio]').datepicker().datepicker('getDate'));
                $('[name=fechaFin]').datepicker().datepicker('setDate', $('[name=fechaInicio]').datepicker().datepicker('getDate'));
            });
            $('[name=fechaFin]').datepicker();

        },
        nuevo: function() {
            var vue = this;
            vue.$refs.modalAddEventoCicloAcademico.open();
            vue.eventoCicloAcademico = {eventoAcademico: {}};
            vue.formClear();
            $('[name="eventoAcademico.id"]').select2(vue.selectEvento());
        },
        editar: function(id) {
            var vue = this;
            vue.$refs.modalAddEventoCicloAcademico.open();
            vue.eventoCicloAcademico = {eventoAcademico: {}};
            vue.formClear();
            $.ajax({
                method: 'POST',
                url: APP.url('academico/eventocicloacademico/update'),
                sync: true,
                data: {id: id},
                success: function(response) {
                    if (response.success) {
                        vue.eventoCicloAcademico = response.data;
                        $('[name="eventoAcademico.id"]').select2(vue.selectEvento());
                        $('[name="eventoAcademico.id"]').select2('data', vue.eventoCicloAcademico.eventoAcademico);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveEventoCicloAcademico: function() {
            var vue = this;
            var valid = $('#formEventoCicloAcademico').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/eventocicloacademico/save'),
                data: $('#formEventoCicloAcademico').serialize(),
                success: function(response) {
                    if (response.success) {
                        vue.$refs.modalAddEventoCicloAcademico.close();
                        vue.renderEventos();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function(id) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar el evento académico?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/eventocicloacademico/delete'),
                            data: {id: id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        selectEvento() {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un evento",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/eventocicloacademico/allevento"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function(info) {
                    return info.nombre;
                },
                formatSelection: function(info) {
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        getRecord: function(id) {
            return dynatable.settings.dataset.records.find(item => item.id === id);
        },
        renderEventos: function() {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/eventocicloacademico/allcalendar'),
                sync: true,
                success: function(response) {
                    if (response.success) {
                        vue.$refs.fullcalendar.addEventSource(response.data);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eventClick: function(self, date, jsEvent, view) {
        },
        dayClick: function(self, date, jsEvent, view) {
        },
        dayDbClick: function(self, date, element) {
            var vue = this;

            //var dia = date.format("DD/MM/YYYY HH:mm:ss");
            var dia = date.format("DD/MM/YYYY");
            vue.$refs.modalAddEventoCicloAcademico.open();
            $('[name="eventoAcademico.id"]').select2(vue.selectEvento());
            vue.formClear();

            $('[name=fechaInicio]').datepicker().datepicker('setDate', dia);
            $('[name=fechaInicio]').datepicker().trigger("change");

        },
    },
});