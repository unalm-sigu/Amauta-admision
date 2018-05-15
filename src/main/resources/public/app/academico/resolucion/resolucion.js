Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#resoluciones',
    data: {
        URL_RESOLUCIONES: APP.url('academico/resolucion/listResoluciones'),
        resolucionModal: {
            id: 'modalResolucion',
            header: true,
            title: 'Resoluciones',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
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

        },
        saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }


            $('#frmResolucion').find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            $('#frmResolucion').find('.multiselect__input').each(function () {
                var input = $(this);
                let element = input.closest('.multiselect').find('.multiselect__tags-wrap');

                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });


            var form = $("[id='frmResolucion']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            let $vue = this;
            console.log("save");
            console.dir($vue.resolucion);
            console.log(JSON.stringify($vue.resolucion));

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
        }
    }
})