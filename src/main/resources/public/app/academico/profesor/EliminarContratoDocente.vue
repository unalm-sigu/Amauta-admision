<script>
    module.exports = {
        data() {
            return {
                CICLO_ACADEMICO_DESCRIPCION: CICLO_ACADEMICO_DESCRIPCION
            };
        },
        mounted: function () {
        },
        methods: {
            open() {
                swal('¿Seguro que desea eliminar los contratos de los docentes para el ciclo ' + CICLO_ACADEMICO_DESCRIPCION + '?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Sí, Eliminar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    axios_.get("/academico/profesor/eliminar/general")
                            .then(({data}) => {
                                notify(data, 'info');
                                return swal({text: data, icon: "success", button: false, timer: 1000});
                            }, () => {
                                return swal(APP.errorComunicacion, "error");
                            });
                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });
            },
        }
    };
</script>