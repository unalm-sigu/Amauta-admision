new Vue({
    el: '#solicitudVue',
    data: {
        solicitudURL: APP.url('tramite/solicitudconstancia/list'),
        persona: {},
        solicitud: {},
        tramiteDocumento: {},
        colaborador: {},
        mensajeerror: "",
        dataCargarFoto: VUE_MODAL.structFormAjax({
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        }),
        dataEnviarRevision: VUE_MODAL.structFormAjax({
            id: 'modalEnviarRevision',
            header: true,
            title: 'Enviar a revisión',
            okbtn: 'Aceptar'
        })
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
                case 'DEV':
                case 'ENV':
                case 'CRE':
                    return "label label-default";
                    break;
                case 'ANU':
                case 'NPAG':
                    return "label label-danger";
                    break;
                case 'ACT':
                case 'FVAL':
                case 'PIMP':
                case 'COMP':
                case 'VAL_URA':
                    return "label label-primary";
                    break;
                case 'PAG':
                    return "label label-warning";
                case 'REV_HIS':
                case 'CTRL_CALIDAD':
                case 'VB_UR':
                    return "label label-info";
                    break;
                case 'ENT':
                    return "label label-success";
                    break;

            }
        },
        cargarfoto: function (item) {
            var $vue = this;
            $vue.persona = item.tramite.alumno.persona;
            $vue.tramiteDocumento = item;
            $vue.dataCargarFoto.title = 'Cargar fotografía para ' + item.tramite.alumno.persona.apellidosNombres;
            $vue.$refs.cargarFoto.open();
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
        createCargarFoto: function () {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $vue.tramiteDocumento.tramite.alumno.persona = $vue.persona;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/onlyfoto'),
                contentType: "application/json",
                data: JSON.stringify($vue.tramiteDocumento),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.cargarFoto.close();
                        $vue.$refs.load.loadRemoteData();
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
        },
        update(tram, accion) {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $vue.tramiteDocumento = tram;
            $vue.tramiteDocumento.estadoTramite = accion.estadoTramiteFinal;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.tramiteDocumento),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
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
        accion(estado, item) {
            console.log(estado);
            if (estado.estadoTramiteFinal.codigo == 'FVAL') {
                this.cargarfoto(item);
            } else {
                this.update(item, estado);
            }
        },
        procesarTramite(item, event) {
            event.preventDefault();

            location.href = APP.url("academico/procesar/" + item.id);
        }
    }
});
