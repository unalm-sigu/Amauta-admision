new Vue({
    el: '#main',
    data: {
        cronogramas: [],
        cronograma: {},
        dataModalGenerarCronograma: {
            title: 'Nuevo Cronograma',
        },
        dataEditarCronograma: {
            title: 'Editar Cronograma',
        },
    },
    created() {
        let vue = this;
    },
    mounted: function () {
        let vue = this;
        vue.loadCronograma();
    },

    methods: {
        generarCronograma: function () {

            let vue = this;
            vue.cronograma = {};
            vue.dataModalGenerarCronograma.title = "Nuevo Cronograma";
            vue.$refs.modalgenerar.open();
            
            $('.date').datepicker({ minViewMode: 1 });
            $('[name=numeroCuota]').numeric();

        },
        loadCronograma: function () {

            let vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('posgrado/cronograma/all'),
                success: function (response) {
                    if (response.success) {
                        vue.cronogramas = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        saveGenerarCronograma: function () {

            let vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('posgrado/cronograma/generar'),
                data: $('#formGenerar').serialize(),
                success: function (response) {
                    if (response.success) {

                        vue.$refs.modalgenerar.close();

                        vue.loadCronograma();
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        deleteCronograma: function () {

            let vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('posgrado/cronograma/delete'),
                data: {id: ciclo},
                success: function (response) {
                    if (response.success) {

                        vue.loadCronograma();

                        swal({text: response.message, icon: "success", button: false, timer: 1000});

                    } else {

                        swal({text: response.message, icon: "error", button: false, timer: 1000});

                    }
                }, error: function () {

                    swal({text: MESSAGES.errorComunicacion, icon: "error", button: false, timer: 1000});
                }
            });

        },
        eliminarCronograma: function () {

            let vue = this;

            console.log(vue.cronogramas.length);

            if (!(vue.cronogramas.length > 0)) {
                return;
            }

            swal('¿Seguro que desea eliminar los registros ?', {
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

                vue.deleteCronograma();

            });

        },
        modificar: function (cronograma) {


            let vue = this;
            vue.cronograma = cronograma;
            vue.dataEditarCronograma.title = "Nuevo Cronograma";
            vue.$refs.modaleditar.open();

            $('[name=fechaEmision]').datepicker();
            $('[name=fechaPago]').datepicker();

        },
        updateCronograma: function () {

            let vue = this;
            
            if (!($('#formUpdate').parsley().validate() == true)) {
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('posgrado/cronograma/update'),
                data: $('#formUpdate').serialize(),
                success: function (response) {
                    if (response.success) {

                        vue.loadCronograma();

                        vue.$refs.modaleditar.close();

                        notify(response.message, "info");

                    } else {

                        notify(response.message, "error");

                    }
                }, error: function () {

                    notify(Messages.errorComunicacion, "error");

                }
            });

        }
    }
});
