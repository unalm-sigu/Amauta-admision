$(function() {

    var $global = new Vue({});

    let  dynatable = null;

    var HorarioTemplate = Vue.component("horarioTemplate", {
        template: "#horarioTemplate",
        data: function() {
            return {horarios: [], dias: []};
        },
        methods: {
        }
    });


    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        data: function() {
            return {
                horarios: [{}],
                showTitle: false,
            }
        },
        mounted: function() {
            var vue = this;
        },
        mounted: function() {
            let vue = this;
            $global.$on("updataHorario", function(datos) {
                vue.updataHorario(datos);
            });
            $global.$on("updateShowTitle", function(estado) {
                vue.updateShowTitle(estado);
            });
        },
        methods: {
            updataHorario(datos) {
                var vue = this;
                vue.horarios = datos;
            },
            updateShowTitle(estado) {
                var vue = this;
                vue.showTitle = estado;
            },
            openHorario(id) {
                $global.$emit("openHorario", id);
            }
        }
    });


    new Vue({
        el: '#main',
        data: {
            curso: 0,
        },
        created: function() {
            let vue = this;
        },
        mounted: function() {
            let vue = this;
            $('[name="carrera"]').select2({allowClear: true, placeholder: "Seleccione una carrera"}).on("change.select2", function(e) {
                vue.horario(e.val);
                dynatable.settings.dataset.ajaxData.id = e.val;
                $global.$emit("updateShowTitle", true);
                if (e.val == '') {
                    dynatable.settings.dataset.ajaxData.id = 0;
                    $global.$emit("updateShowTitle", false);
                }
                vue.loadHeader(e.val);
                dynatable.process();
            });
            vue.createDynatable();
            $global.$on("openHorario", function(id) {
                vue.openHorario(id);
            });
        },
        methods: {
            generarHorario: function(e) {
                var self = $(e.currentTarget);
                self.btnDisabled();
                $.ajax({
                    method: 'POST',
                    sync: false,
                    url: APP.url("academico/horariocachimbo/horario/generar"),
                    success: function(response) {
                        if (response.success) {
                            dynatable.process();
                            notify(response.message, 'info');
                        } else {
                            notify(response.message, 'error');
                        }
                        self.btnEnable();
                    },
                    error: function() {
                        notify(MESSAGES.errorComunicacion, "error");
                        self.btnEnable();
                    }
                });
            },
            getRecord: function(id) {
                return dynatable.settings.dataset.records.find(item => item.id === id);
            },
            createDynatable: function() {
                let vue = this;

                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/horario/allHorario'),
                        ajaxData: {id: 0}
                    },
                    features: {
                        search: false,
                        paginate: false,
                        recordCount: false,
                    },
                    writers: {_rowWriter: vue.writter},
                    table: {bodyRowSelector: "tbody tr"}

                }).data('dynatable');

            },
            writter: function(rowIndex, record, columns, cellWriter) {
                var html = $.templates("#dynatableRowTemplate").render(record);
                return $(html).prop('outerHTML');
            },
            horario: function(carrera) {
                let vue = this;
                if (carrera == '') {
                    $('#tableHorario').html("");
                    return;
                }
            },
            loadHeader: function(id) {
                var vue = this;

                if (id == '') {
                    $global.$emit("updataHorario", [{}]);
                    return;
                }

                $.ajax({
                    method: 'POST',
                    url: APP.url("academico/horariocachimbo/horario/allHorarioHeader"),
                    data: {id: id},
                    success: function(response) {
                        if (response.success) {
                            console.log(response.data);
                            $global.$emit("updataHorario", response.data);
                        } else {
                            notify(response.message, 'error');
                        }
                    },
                    error: function() {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });

            },
            openHorario(id) {

                var boot = bootbox.alert({
                    size: 'large',
                    message: APP.template.spincenter,
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-link"},
                    },
                });

                $.ajax({
                    method: 'POST',
                    url: APP.url("academico/horariocachimbo/horario/openHorario"),
                    data: {id: id},
                    success: function(response) {
                        if (response.success) {
                            console.log(response.data.horarios);
                            console.log(response.data.dias);
                            var horarioTemplate = new HorarioTemplate();
                            horarioTemplate.horarios = response.data.horarios;
                            horarioTemplate.dias = response.data.dias;
                            var component = horarioTemplate.$mount();
                            boot.find('.bootbox-body').html(component.$el);
                        } else {
                            notify(response.message, 'error');
                        }
                    },
                    error: function() {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });


            }
        }
    });
});
