Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#becaestudioVUE',
    data: {
        becaestudioURL: APP.url('academico/becaestudio/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Nueva Beca de Estudio',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        becaestudioEdit: {},
        becaestudioSelect: {},
        modalInstitucion: {
            id: 'modalInstitucion',
            header: true,
            title: 'Nueva Institución',
        },
        institucion: {},
        instituciones: []
    },
    mounted() {
        $(".numerico").numeric({negative: false});

    },
    methods: {
        guardar() {
            let $vue = this;
            console.log("asdasdsa")
            console.log($vue.becaestudioEdit.nombre)
            if ($vue.becaestudioEdit.nombre == undefined) {
                notify("Ingresar el nombre de la beca", "error")
                return;
            }

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/becaestudio/save"),
                data: JSON.stringify($vue.becaestudioEdit)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalConfirmar.close();
                    $vue.$refs.raptorBecasEstudios.loadRemoteData();
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });

        },
        editar(item) {
            let $vue = this;
            $vue.becaestudioSelect = item;
            $vue.becaestudioEdit = Object.assign({}, item);
            $vue.$refs.modalConfirmar.title = 'Actualizar Beca';
            $vue.$refs.modalConfirmar.open();
            /*
             console.log(item.id)
             bootbox.confirm({
             message: "¿Estas seguor de jshdfsjh?",
             buttons: {
             confirm: {label: "Aceptar"},
             cancel: {label: "Cancelar"}
             },
             callback(result) {
             console.log(result)
             }
             
             });
             //*/
        },
        nuevo() {
            let $vue = this;
            $vue.becaestudioEdit = {id: '', institucion: {id: '', numeroDocIdentidad: '', razonSocial: ''}};
            $vue.instituciones = JSON.parse(institucionesJson);
            $vue.$refs.modalConfirmar.open();
        },
        addInstitucion() {
            let $vue = this;
            $vue.institucion = {id: null, numeroDocIdentidad: null, razonSocial: null};
            $vue.$refs.modalInstitucion.open();

        },
        saveInstitucion() {
            let $vue = this;

            if ($vue.institucion.numeroDocIdentidad == null && $vue.institucion.razonSocial == null) {
                notify("Ingresar los datos obligatorios", 'error');
                return;
            }

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/becaestudio/saveInstitucion"),
                data: JSON.stringify($vue.institucion)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalInstitucion.close();
                    $vue.becaestudioEdit.institucion = response.data;

                    notify(response.message, "info");
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });

        },
        eliminar(item) {
            var $vue = this;

            bootbox.confirm({
                message: '¿Seguro que desea eliminar la beca de estudio?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/becaestudio/delete'),
                            data: {id: item.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.$refs.raptorBecasEstudios.loadRemoteData();
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
        },
    }
});