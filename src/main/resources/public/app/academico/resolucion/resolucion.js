Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resoluciones',
    data: {
        URL_RESOLUCIONES: APP.url('academico/resolucion/listResoluciones'),
        URL_TRAMITES: APP.url('academico/resolucion/listTramitesToConfirm'),
        colorEstado: {CRE: "default", ACT: "success", ANU: "danger", BLO: "warning", FUS: "warning"},
        resolucionModal: {
            id: 'modalResolucion',
            header: true,
            title: 'Resoluciones',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Confirmar Tramite',
            okbtn: 'Aceptar',
            modalsize: 'modal-md'
        },
        resolucion: null,
        tiposResoluciones: null,
        files: []
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        cambiarEstadoReincorporacion: function (tramite, estadoDestino, event) {
            event.preventDefault();
            let $vue = this;
            console.log("cambiarEstadoReincorporacion");
            console.dir(tramite);
            $.ajax({
                url: APP.url('academico/tramiteacademico/cambiarEstadoReincorporacion'),
                type: 'POST',
                async: false,
                data: {
                    tramite: tramite.id,
                    estado: "SOL_ACEP"
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(response.message, "error");
                }
            });
        },
        nuevaResolucion(event) {
            let $vue = this;
            event.preventDefault();
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        $vue.$refs.modalResolucion.open();
                        console.dir(response.data);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, editarResolucion: function (resolucion, e) {
            e.preventDefault();
            location.href = APP.url("academico/resolucion/" + resolucion.id + "/editar");
        }, loadModalSubirDoc: function (resolucion, e) {
            e.preventDefault();
            this.resolucion = resolucion;
            this.$refs.modalResolucion.open();
        }, saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }
            let $vue = this;
            $.ajax({
                url: APP.url('academico/resolucion/saveResolucion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.modalResolucion.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            if (newFile && oldFile) {
                // update
                if (newFile.active && !oldFile.active) {
                    // beforeSend
                    // min size
                    if (newFile.size >= 0 && this.minSize > 0 && newFile.size < this.minSize) {
                        this.$refs.upload.update(newFile, {error: 'size'})
                    }
                }
                if (newFile.progress !== oldFile.progress) {

                    // progress
                }
                if (newFile.error && !oldFile.error) {
                }
                if (newFile.success && !oldFile.success) {
                    //  $vue.producto.productoImagen.splice(0, 0, newFile.response.data)
                }
            }
            if (!newFile && oldFile) {
                if (oldFile.success && oldFile.response.id) {
                }
            }
            // Automatically activate upload
            if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                if (!this.$refs.upload.active) {
                    //console.log('subiendo')
                    this.$refs.upload.active = true
                } else {
                    //console.log("FIN?")
                }
            }

            if ($vue.$refs.upload.uploaded) {
                if ($vue.files.length > 0) {
                    //  $vue.reloadProducto();x
                    $vue.resolucion.rutaUrl = $vue.files[0].response.data;
                }
                if ($vue.$refs.upload.clear()) {
                    //   console.log("reiniciar img 2")
                }
            }

            if (newFile && oldFile && !newFile.active && oldFile.active) {
                // Get response data
                if (newFile.xhr) {
                    //  Get the response status code
                    if (newFile.xhr.status == 200) {
                        notify(newFile.response.message, "info");
                        $vue.$refs.tblResoluciones.loadRemoteData();
                    } else {
                        notify(newFile.response.message, "error");
                    }
                    $vue.$refs.modalResolucion.close();
                    MODAL.hideWait();
                } else {
                    notify(response.message, "error");
                }
            }
        },
        inputFilter(newFile, oldFile, prevent) {
            if (newFile && !oldFile) {
                if (!/\.(gif|jpg|jpeg|png|pdf)$/i.test(newFile.name)) {
                    swal(
                            'Oops...',
                            'Este archivo no esta permitido!',
                            'error'
                            )
                    return prevent();
                }
            }
            if (newFile && (!oldFile || newFile.file !== oldFile.file)) {
                newFile.url = ''
                let URL = window.URL || window.webkitURL
                if (URL && URL.createObjectURL) {
                    newFile.url = URL.createObjectURL(newFile.file)
                }
            }
        }, changeFile(value) {
            console.log("changeFile");
            console.dir(this.files);
        }, getEstadoClass: function (estadoCode) {
            return "label-" + this.colorEstado[estadoCode];
        }, loadModalConfirmar(resolucion, event) {
            event.preventDefault();
            this.resolucion = resolucion;
            this.$refs.tblTramites.ajaxdata = {resolucion: resolucion.id};
            this.$refs.tblTramites.loadRemoteData();
            this.$refs.modalConfirmar.open();
        }, saveConfirmar(event) {
            let $vue = this;
            if (event) {
                event.preventDefault();
            }
            $.ajax({
                url: APP.url('academico/resolucion/saveConfirmar'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.modalResolucion.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})