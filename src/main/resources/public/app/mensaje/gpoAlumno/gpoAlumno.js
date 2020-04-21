new Vue({
    el: '#gpoAlumnoVUE',
    data: {
        gpoAlumnoURL: APP.url('mensajeria/gpoalumno/list'),
        gpoAlumno: {},
        cicloAcademico: JSON.parse(cicloJson),
        addGpoAlumno: {
            id: 'modalGpoAlumno',
            header: true,
            title: 'Crear Grupo Alumno',
            okbtn: 'Guardar',
            showaccept: true,
            modalsize: "modal-md"
        }
    },
    computed: {

    },
    mounted: function () {
    },
    methods: {
        init() {
            let $vue = this;
            $vue.gpoAlumno = {};

        },
        nuevo() {
            let $vue = this;
            $vue.init();
            $vue.addGpoAlumno.okbtn = "Guardar";
            $vue.addGpoAlumno.title = "Nuevo Grupo Alumno";
            $vue.$refs.modalGpoAlumno.open();
        },
        saveUpdate(event) {
            let $vue = this;

            console.log(event.target)

            if (!$("#formGpoAlumno").parsley().validate() == true) {
                return;
            }

            console.dir($vue.gpoAlumno);

            $.ajax({
                method: 'POST',
                url: APP.url('mensajeria/gpoalumno/saveUpdate'),
                data: JSON.stringify($vue.gpoAlumno),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalGpoAlumno.close();
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (error) {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        editar(item) {
            let $vue = this;
            $vue.init();
            $vue.gpoAlumno = JSON.parse(JSON.stringify(item));
            $vue.addGpoAlumno.okbtn = "Actualizar";
            $vue.addGpoAlumno.title = "Actualizar grupo alumno";
            $vue.$refs.modalGpoAlumno.open();
        },
        eliminar: function (item) {
            console.log(item)
            let $vue = this;

            swal({
                title: "Eliminación del Registro",
                text: "¿Desea eliminar el grupo alumno?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            }).then((success) => {
                if (success) {
                    $.ajax({
                        method: 'POST',
                        url: APP.url('mensajeria/gpoalumno/eliminar'),
                        data: JSON.stringify(item),
                        contentType: "application/json",
                        success: function (response) {
                            if (response.success) {
                                $vue.$refs.load.loadRemoteData();
                                swal(response.message, {
                                    icon: "success",
                                });
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function (error) {
                            notify(GlobalMessages.errorComunicacion, "error");
                        }
                    });

                }
            });
        },
        detalle: function (item) {
            let $vue = this;
            $vue.gpoAlumno = JSON.parse(JSON.stringify(item));
            
        }
    }
});
