
new Vue({
    el: '#aulaVUE',
    data: {
        idiomasURL: APP.url('general/idioma/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Confirmar Tramite',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        idiomaEdit: {},
        idiomaSelect: {},
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
                url: APP.url("general/idioma/save"),
                data: JSON.stringify($vue.idiomaEdit)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalConfirmar.close();
                    $vue.$refs.raptorIdiomas.loadRemoteData();
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(Messages.errorComunicacion, 'error');
            });



        },
        editar(item) {
            let $vue = this;
            $vue.idiomaSelect = item;
            $vue.idiomaEdit = Object.assign({}, item);
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
            $vue.idiomaEdit = {id: ''};
            $vue.$refs.modalConfirmar.open();
        }

    }
});
        