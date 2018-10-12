new Vue({
    el: '#becaestudioVUE',
    data: {
        alumnotarifaURL: APP.url('academico/becaestudio/list'),
        
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
                url: APP.url("posgrado/alumnotarifa/save"),
                data: JSON.stringify($vue.aluTarifa)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalTarifa.close();
                    $vue.$refs.raptorAlumnoTarifa.loadRemoteData();
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
            $vue.aluTarifa = Object.assign({}, item);
       
          
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("posgrado/alumnotarifa/allOtrasTarifas"),
                data: JSON.stringify(item.alumno)
            }).then(response => {
                if (response.success) {
                    $vue.otrasTarifas = response.data;
                    $vue.$refs.modalTarifa.open();
                } else {
                    notify(response.message, 'error');
                }
            },
                    error => {
                        notify(MESSAGES.errorComunicacion, 'error');
                    });

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
        }
    }
});