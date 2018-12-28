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

                swal('¿Seguro que desea asignar en forma masiva el plan de estudios?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Aceptar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }

                    $.ajax({
                        url: APP.url('academico/planCurricular/asignacionmasiva'),
                        type: 'POST',
                        async: false,
                        data: {id: $vue.carrera.id},
                        success(response) {
                            if (response.success) {
                                dynatable.process();
                                $vue.$refs.modalAsignacionMasiva.close();
                                return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                            } else {
                                return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                            }
                        },
                        error() {
                            return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    });

                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });

            } else {
                return  swal({text: "Seleccione una carrera", icon: "error", dangerMode: true, button: {text: "Aceptar"}});
            }
        },
    }
});

