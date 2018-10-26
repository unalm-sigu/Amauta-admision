new Vue({
    el: '#preciocursocicloVUE',
    data: {
        preciocursocicloURL: APP.url('academico/preciocursociclo/list'),
        verTabla: false,
        guardando: false
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    },
    methods: {
        verGuardar() {
           

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea guarda este curso de nivelación?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        setTimeout(function () {
                            $vue.guardar();
                        }, 200);
                    }
                }
            });
        },
        guardar() {
            this.guardando = true;
            let $vue = this;
            //console.dir($vue.$refs.raptorPrecioCursoCiclo);
            //console.dir($vue.$refs.raptorPrecioCursoCiclo.data);
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/preciocursociclo/save"),
                data: JSON.stringify($vue.$refs.raptorPrecioCursoCiclo.data)
            }).then(response => {
                if (response.success) {
                     this.verTabla = false,
                     this.guardando = false;
                    $vue.$refs.raptorPrecioCursoCiclo.loadRemoteData();
                    notify(response.message, "info");
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }
    }
});
