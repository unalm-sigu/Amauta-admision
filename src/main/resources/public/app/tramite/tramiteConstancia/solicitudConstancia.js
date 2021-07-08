Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#solicitudVue',
    data: {
        solicitudURL: APP.url('tramite/solicitudconstancia/list'),
        persona: {},
        solicitud: {},
        tramiteDocumento: {},
        colaborador: {},
        archivo: {},
        files: [],
        idSolicitud: null,
        mensajeerror: "",
        dataCargarFoto: VUE_MODAL.structFormAjax({
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        }),
        viewBoleta: VUE_MODAL.structFormAjax({
            id: 'viewBoleta',
            header: true,
            title: 'Boleta',
            showaccept: false
        }),
        btnFileColor: 'btn-outline-info',
        modalLoadBoleta: VUE_MODAL.structFormAjax({
            id: 'modalLoadBoleta',
            header: true,
            title: 'Boleta de Pago',
            okbtn: 'Aceptar',
            cancelbtn: 'Cancelar',
            modalsize: 'modal-md',
            processing: false
        }),
        dataEnviarRevision: VUE_MODAL.structFormAjax({
            id: 'modalEnviarRevision',
            header: true,
            title: 'Enviar a revisión',
            okbtn: 'Aceptar'
        })
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
            $vue.archivo = {idAlumno: item.tramite.alumno.id};
            $vue.idSolicitud = item.tramiteDocumento.id;
            $vue.$refs.modalLoadBoleta.open();
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
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        accion(estado, item) {
            console.log(estado);
            if (estado.estadoTramiteFinal.codigo == 'VERPAGO') {
                this.cargarfoto(item);
            } else {
                this.update(item, estado);
            }
        },
        procesarTramite(item, event) {
            event.preventDefault();

            location.href = APP.url("academico/procesar/" + item.id);
        },
        verBoleta(item) {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $.ajax({
                method: 'GET',
                url: APP.url('tramite/solicitudconstancia/verBoleta/' + item.id),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.archivo = response.data;
                        $vue.archivo.numeroBoleta = item.numeroBoleta;
                        $vue.$refs.viewBoleta.open();
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                if (!/\.(jpg|png|jpeg)$/i.test(newFile.name)) {
                    swal('¡Este tipo de  archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
        },
        saveLoadBoleta() {
            let $vue = this;
            axios.post(APP.url('tramite/solicitudconstancia/saveArchivoTramite'),
                    $vue.archivo).then(response => {
                if (response.data.success) {
                    $vue.$refs.modalLoadBoleta.close();
                    $vue.$refs.load.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(err => {
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            if (newFile) {
                $('#progress-bar').css('width', newFile.progress + '%');
                if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                    if (!$vue.$refs.upload.active) {
                        $vue.$refs.upload.active = true;
                    }
                }
            }

            if (oldFile && newFile) {
                if (newFile.success) {
                    let URL = window.URL || window.webkitURL;
                    if (URL && URL.createObjectURL) {
//                        let itemCoAsesor = $vue.miembros[$vue.indiceForArchivo];
                        $vue.archivo.rutaTemporal = URL.createObjectURL(newFile.file);
                        $vue.archivo.nombre = newFile.response.data.name;
                        $vue.archivo.tipo = newFile.response.data.contentType;
                        $vue.archivo.ruta = newFile.response.data.ruta;
                        $vue.archivo.idInstancia = $vue.idSolicitud;
                    }
                }

                if (newFile.success !== oldFile.success) {
                    $vue.btnFileColor = "btn-success";
                    notify("Archivo cargado satisfactoriamente", "success");
                }
            }

            if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                if (!this.$refs.upload.active) {
                    this.$refs.upload.active = true;
                }
            }
        },
        descargarTramite(uri, item) {

            let idToast = 'iziToast' + Date.now();
    
            noty_download(idToast, 'Desc. word: ' + item.tramite.alumno.codigo);

            axios_blob.get(uri)
                    .then(response => {
                        UTIL_BLOB_INLINE.save(response);
                        noty_clouse(idToast);
                    }, () => {
                        noty_clouse(idToast);
                        notify(Messages.errorComunicacion, 'error')
                    });
        }
    }
});
