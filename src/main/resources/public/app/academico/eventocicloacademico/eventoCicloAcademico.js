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
        },
    },
    created() {
        let vue = this;
    },
    mounted: function() {
        let vue = this;
        $global.$on("eliminar", function(id) {
            vue.eliminar(id);
        });
        $global.$on("editar", function(id) {
            vue.editar(id);
        });
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
    },
    watch: {
        btnActive: function(after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.calendario.metodo1(123);
                vue.onlyOne = false;
            }
        }
    }
});