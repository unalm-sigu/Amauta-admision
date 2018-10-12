new Vue({
    el: '#becaestudioVUE',
    data: {
        becaestudioURL: APP.url('academico/becaestudio/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Cambiar Institución Otorga',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        becaestudioEdit: {},
        becaestudioSelect: {}
    },
    mounted() {
        $(".numerico").numeric({negative: false});
    },
    methods: {
        guardar() {
            let $vue = this;

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
            $vue.becaestudioEdit = {id: ''};
            $vue.$refs.modalConfirmar.open();
        }
    }
});