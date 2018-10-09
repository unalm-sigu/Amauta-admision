new Vue({
    el: '#solicitudVue',
    data: {
        solicitudURL: APP.url('tramite/solicitudconstancia/list'),
        persona: {},
        solicitud: {},
        colaborador: {},
        mensajeerror: "",
        dataCargarFoto: {
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        },
        dataEnviarRevision: {
            id: 'modalEnviarRevision',
            header: true,
            title: 'Enviar a revisión',
            okbtn: 'Aceptar'
        }
    },
    computed: {

    },
    created() {

    },
    mounted: function () {

    },
    methods: {
        classEstado(value) {
            switch (value) {
                case 'ACEP':
                case 'PEND':
                case 'DEV':
                case 'ENV':
                case 'CRE':
                    return "label label-default ";
                    break;
                case 'ANU':
                    return "label label-danger ";
                    break;
                case 'ACT':
                    return "label label-primary ";
                    break;

            }
        },
        cargarfoto: function (item) {
            var vue = this;
            vue.persona = item.tramite.alumno.persona;
            vue.dataCargarFoto.title = 'Cargar fotografía para ' + item.tramite.alumno.persona.apellidosNombres;
            vue.$refs.cargarFoto.open();
        },
        createEnviarRevision: function () {
            var vue = this;
            var valid = $('#formEnviarRevision').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/revision'),
                data: $('#formEnviarRevision').serialize(),
                success: function (response) {
                    if (response.success) {
//                        dynatable.reload();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        createCargarFoto: function (solicitud) {
            var vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');

            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/onlyfoto'),
                contentType: "application/json",
                data: JSON.stringify(vue.persona),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.cargarFoto.close();
                        vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        getImage(event) {
            var vue = this;
            vue.file = event.target.files[0];
            let formData = new FormData();
            formData.append('file', vue.file);
            AXIOS.post('/tramite/solicitudconstancia/upload',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
            ).then(function (response) {
                vue.persona.rutaFotoTemporal = response.data.data.ruta;
                console.log(response);
            }).catch(function () {
                console.log('FAILURE!!');
            });
        }
    }
});
