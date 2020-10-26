Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#plantillaVue',
    data: {
        raptorURL: APP.url("tramite/plantillaconstancia/list"),
        tipoConstancia: JSON.parse(tipoDocumentoJson),
        idiomas: JSON.parse(idiomasJson),
        plantilla: {},
        btnFileColor: 'btn-outline-info',
        modalLoadWord: VUE_MODAL.structFormAjax({
            id: 'modalLoadWord',
            okbtn: 'Aceptar',
            header: true,
            cancelbtn: 'Cancelar',
            title: 'Plantilla Word',
            processing: false
        }),
        idPlantilla: null,
        archivo: {}
    },
    created() {
        let $vue = this;
    },
    methods: {
        contenido: function (elem) {
            location.href = APP.url('tramite/plantillaconstancia/' + elem.id)
        },
        modalUpdate: function (elem) {
            let $vue = this;
            $vue.plantilla = {...elem}
            $("#myModal").modal('show');
        },
        nuevo: function () {
            let $vue = this;
            $vue.plantilla = {};
            $("#myModal").modal('show');
        },
        update: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.plantilla);
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptor.loadRemoteData();
                        notify(response.message, 'info');
                        $vue.plantilla = {};
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        save: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.plantilla);
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptor.loadRemoteData();
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        eliminar: function (elem) {

            let $vue = this;
            $vue.plantilla = {...elem}

            var dialog = bootbox.confirm({
                message: "¿Está seguro que desea eliminar la plantilla?",
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/plantillaconstancia/delete'),
                            contentType: "application/json",
                            data: JSON.stringify($vue.plantilla),
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.raptor.loadRemoteData();
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                            }
                        });

                    }
                }
            });

        },
        loadPlantillaWord(item) {
            let $vue = this;
            $vue.idPlantilla = item.id;
            $vue.archivo = {};
            $('#progress-bar').css('width', 0.00 + '%');
            $vue.$refs.modalLoadWord.open();
        },
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                if (!/\.(docx)$/i.test(newFile.name)) {
                    swal('¡Este tipo de  archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
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
                        $vue.archivo.idInstancia = $vue.idPlantilla;
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
        saveLoadWord() {
            let $vue = this;
            axios.post(APP.url('tramite/plantillaconstancia/saveWordPlantilla'),
                    $vue.archivo).then(response => {
                if (response.data.success) {
                    $vue.$refs.modalLoadWord.close();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(err => {
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
    }
});
