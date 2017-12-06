$(function () {

    var $global = new Vue({});

    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {horario: []};
        },
    });

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        mounted: function () {
            var $vue = this;
        },
        methods: {

        }
    });

    new Vue({
        el: '#main',
        data: {
            curso: 0,
        },
        created: function () {
            let vue = this;
        },
        mounted: function () {
            let vue = this;
            $('[name="carrera"]').select2({allowClear: true, placeholder: "Seleccione una carrera"}).on("change.select2", function (e) {
                vue.horario(e.val);
            });
        },
        methods: {
            generarHorario: function (id) {
                console.log('generando hoarrios');
            },
            getRecord: function (id) {
                return dynatable.settings.dataset.records.find(item => item.id === id);
            },
            horario: function (carrera) {
                let vue = this;
                if (carrera == '') {
                    $('#tableHorario').html("");
                    return;
                }
                $.ajax({
                    method: 'POST',
                    url: APP.url("academico/horariocachimbo/horario/allHorario"),
                    data: {id: carrera},
                    success: function (response) {
                        if (response.success) {
                            var HorarioTemplate = Vue.component("horarioTemplate", {
                                template: response.data
                            });
                            var horarioTemplate = new HorarioTemplate();
                            var component = horarioTemplate.$mount();
                            $('#tableHorario').html("");
                            $('#tableHorario').append(component.$el);
                        } else {
                            notify(response.message, 'error');
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        }
    });
});
