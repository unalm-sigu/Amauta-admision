new Vue({
    el: '#cargaMasivaTeamsVUE',
    data: {
        file: null,
        btnText: 'Iniciar Carga',
        observaciones: [],
        exitosos: [],
        procesando: false,
    },
    methods: {

        startUpload() {
            let $vue = this;
            var form = $("#formLoadFiles");
            if (!form.parsley().validate()) {
                return;
            }

            let formData = new FormData();
            formData.append('file', $vue.file);
            $vue.procesando = true;
            $vue.observaciones = [];
            $vue.exitosos = [];
            AXIOS.post('/academico/gposeccion/cargarReunionesTeams',
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            ).then(response => {
                if (response.data.success) {
                    $vue.exitosos = response.data.data.exitosos;
                    $vue.observaciones = response.data.data.observaciones;
                    notify(response.data.message, "success");
                } else {
                    $vue.observaciones = response.data.data.observaciones || [];
                    notify(response.data.message, "error");
                }
                $vue.procesando = false;
            }).catch(err => {
                notify(Messages.errorComunicacion, "error");
                $vue.procesando = false;
            });
        },
        getImage(event) {
            var $vue = this;
            $vue.file = event.target.files[0];
        }
    }
});
