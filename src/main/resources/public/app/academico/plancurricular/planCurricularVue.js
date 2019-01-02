Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        carrera: {},
        carreras: JSON.parse(carrerasJson),
        modalAsignacionMasiva: {
            id: 'idAsignacionMasiva',
            title: 'Asignación Masiva',
        }
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        asignacionMasiva() {
            let $vue = this;
            $vue.carrera = {};
            $vue.$refs.modalAsignacionMasiva.open();
        },
        saveAsignacionMasiva() {
            let $vue = this;
            console.log($vue.carrera.id);

            if ($vue.carrera.id) {

                var mbb = bootbox.confirm({
                    message: '¿Seguro que desea asignar en forma masiva el plan de estudios?',
                    buttons: {
                        confirm: {label: 'Si, asignar', className: 'btn-warning btn-modal btn-procesar'},
                        cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                    },
                    callback: function (result) {
                        if (result) {
                            $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                            $(".btn-modal").prop('disabled', true);

                            $.ajax({
                                url: APP.url('academico/planCurricular/asignacionmasiva'),
                                type: 'POST',
                                data: {id: $vue.carrera.id},
                                success(response) {
                                    if (response.success) {
                                        $vue.$refs.modalAsignacionMasiva.close();
                                        mbb.modal("hide");
                                        notify(response.message, "info");

                                    } else {
                                        $(".btn-modal").prop('disabled', false);
                                        $(".btn-procesar").html('Si, reiniciar');
                                        notify(response.message, "error");
                                    }
                                },
                                error() {
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si, reiniciar');
                                    notify(MESSAGES.errorComunicacion, "error");
                                }
                            });

                            return false;
                        }
                    }
                });



            } else {
                notify("Seleccione una especialidad", "error");
            }
        },
    }
});

