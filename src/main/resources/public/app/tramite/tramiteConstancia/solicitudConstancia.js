Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#solicitudVue',
    mixins: [VueLoader],
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        solicitudURL: APP.url('tramite/solicitudconstancia/list'),
        persona: {},
        solicitud: {},
        tramiteDocumento: {},
        tramiteDocumentoActivo: {},
        colaborador: {},
        archivo: {},
        archivoTmp: {},
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
    methods: {
        classEstado(value) {
            switch (value) {
                case 'ACEP':
                    return "label label-primary";
                    break;
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
                    return "label label-success";
                    break;
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
            $vue.tramiteDocumentoActivo = {...item};
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
        verBoleta(item) {
            var $vue = this;
            $vue.tramiteDocumentoActivo = {...item};

            if ($vue.tramiteDocumentoActivo.archivo) {
                $vue.archivoTmp = {...$vue.tramiteDocumentoActivo.archivo};
                $vue.$refs.viewBoleta.open();
                return;
            }

            bootbox.alert({
                message: `El tramite no tiene una boleta adjunta`,
                buttons: {
                    ok: {label: 'Cerrar', className: "btn-default"}
                }});

        },
        inputFilter(newFile, oldFile, prevent) {
            if (newFile && !oldFile) {
                if (!/\.(jpg|png|jpeg)$/i.test(newFile.name)) {
                    swal('¡Este tipo de  archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
        },
        saveLoadBoleta() {
            let $vue = this;
            $vue.archivo.idInstancia = $vue.tramiteDocumentoActivo.id;
            $vue.archivo.numeroBoleta = $vue.tramiteDocumentoActivo.numeroBoleta;
            axios.post(APP.url('tramite/solicitudconstancia/saveArchivoTramite'),
                    $vue.archivo).then(response => {
                if (response.data.success) {
                    $vue.$refs.modalLoadBoleta.close();
                    $vue.$refs.load.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    $vue.$refs.modalLoadBoleta.stop();
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
        descargarTramite(item) {

            let idToast = 'iziToast' + Date.now();

            noty_download(idToast, 'Desc. word: ' + item.tramite.alumno.codigo);

            axios_blob.get('/tramite/solicitudconstancia/downloadWord/' + item.id)
                    .then(response => {
                        UTIL_BLOB_INLINE.save(response);
                        noty_clouse(idToast);
                    }, (error) => {
                        noty_clouse(idToast);
                        notify(error.response.data.message, 'error')
                    });
        },
        /*anularTramite(tramite) {
            let $vue = this;
            bootbox.confirm({
                message: `¿Seguro que desea anular el tramite?`,
                buttons: {
                    confirm: {label: 'Sí, anular', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-default"}
                },
                callback: (result) => {
                    if (result) {

                        $vue.showLoader();

                        axios.get('/tramite/solicitudconstancia/anulartramite/' + tramite.id)
                                .then(response => {
                                    $vue.hideLoader();
                                    $vue.$refs.load.loadRemoteData();
                                    notify(response.data.message, response.data.success ? 'info' : 'error');
                                }, () => {
                                    $vue.hideLoader();
                                });
                    }
                }
            });
        },*/
        anularTramite(tramite) {
            let $vue = this;
            console.log(tramite.estadoTramite.codigo); // ACEP
            if (tramite.estadoTramite.codigo === 'COMP') {
                const md = bootbox.confirm({
                    message: "<div class='form-group'>" +
                            "<h4 class='text-center bold'>¿Seguro que desea anular el trámite?</h4><br/>" +
                            "<p class='bold'>Ingrese motivo: </p>" +
                            "<textarea class='form-control' id='motivo' rows='3' maxLength='200' placeholder='Describa un motivo, máximo 200 caracteres'></textarea>" +
                            "</div>",
                    //message: `¿Seguro que desea anular el tramite?`,
                    buttons: {
                        confirm: {label: 'Sí, anular', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-default"}
                    },
                    inputType: 'textarea',
                    callback: (result) => {
                        if (result) {
                            if ($("#motivo").val().trim().length < 1)
                                return false;
                            $vue.showLoader();
                            tramite.motivo = $("#motivo").val();
                            axios.post('/tramite/solicitudconstancia/anulartramite', tramite)
                                    .then(response => {
                                        $vue.hideLoader();
                                        $vue.$refs.load.loadRemoteData();
                                        if(response.status === 200) {
                                            notify(response.data, 'success');
                                        } else {
                                            notify(response.status, 'info');
                                        }                                        
                                    }, () => {
                                        $vue.hideLoader();
                                    })
                                    .catch(error => {
                                        notify(error, 'error');
                                    });
                        }
                    }
                });
            } else if (tramite.estadoTramite.codigo === 'ACEP') {
                bootbox.confirm({
                    message: `¿Seguro que desea anular el tramite?`,
                    buttons: {
                        confirm: {label: 'Sí, anular', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-default"}
                    },
                    callback: (result) => {
                        if (result) {

                            $vue.showLoader();

                            axios.get('/tramite/solicitudconstancia/anulartramite/' + tramite.id)
                                    .then(response => {
                                        $vue.hideLoader();
                                        $vue.$refs.load.loadRemoteData();
                                        notify(response.data.message, response.data.success ? 'info' : 'error');
                                    }, () => {
                                        $vue.hideLoader();
                                    });
                        }
                    }
                });
            }

        },
        entregarTramite(tramite) {
            let $vue = this;
            $vue.$refs.modalEntregarTramite.open();
            $vue.tramiteDocumentoActivo = {...tramite};
        },
        saveEntregarTramite() {
            let $vue = this;
            axios.post('/tramite/solicitudconstancia/entregartramite/', $vue.tramiteDocumentoActivo)
                    .then(response => {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.data.message, response.data.success ? 'info' : 'error');
                        $vue.$refs.modalEntregarTramite.close();
                    }, () => {
                        $vue.$refs.modalEntregarTramite.stop();
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        verificarPago(tramite) {

            let $vue = this;
            $vue.$refs.modalValidarBoleta.open();
            $vue.tramiteDocumentoActivo = {...tramite};

        },
        saveValidarBoleta() {

            let $vue = this;
            axios.post('/tramite/solicitudconstancia/validarBoletaTramite/', {...$vue.tramiteDocumentoActivo})
                    .then(response => {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.data.message, response.data.success ? 'info' : 'error');
                        $vue.$refs.modalValidarBoleta.close();
                    }, () => {
                        $vue.$refs.modalValidarBoleta.stop();
                        notify(Messages.errorComunicacion, 'error');
                    });
        },
        subirBoleta(tramite) {

            let $vue = this;
            $vue.$refs.modalLoadBoleta.open();
            $vue.tramiteDocumentoActivo = {...tramite};
        }
    }
});
