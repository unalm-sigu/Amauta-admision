Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#personaFirmaVUE',
    data: {
        persona: JSON.parse(personaJson),
        firmas: [],
        firmaPersona: {},
        modalAddFirma: VUE_MODAL.structFormAjax({
            id: "id-modal-add-firm",
            header: true,
            title: 'Subir firma',
            okbtn: 'Guardar',
            okclass: "btn-primary"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "id-modal-confirm-2"
        }),
        itemSelect: {},
        archivo: {},
        files: []
    },
    mounted: function () {
        let $vue = this;
        $vue.reloadFirmas();
    },
    methods: {
        reloadFirmas() {
            let $vue = this;
            axios.post(APP.url(`${rutaModulo}/${$vue.persona.id}/allFirmas`)).then(response => {
                if (response.data.success) {
                    $vue.firmas = response.data.data;
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        verAddFirma() {
            let $vue = this;
            $vue.firmaPersona = {};
            $vue.archivo = {};
            $vue.$refs.modalAddFirma.open();
        },
        saveFirma() {
            let $vue = this;
            $vue.firmaPersona.persona = $vue.persona;
            $vue.firmaPersona.archivo = $vue.archivo;

            $vue.$refs.modalAddFirma.beginProcessing();
            axios.post(APP.url(`${rutaModulo}/saveFirma`), $vue.firmaPersona).then(response => {
                $vue.$refs.modalAddFirma.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadFirmas();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalAddFirma.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verEliminar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea anular esta firma?";
            $vue.configConfirmAction.okbtn = "Si, anular";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = $vue.anularFirma;
            $vue.$refs.modalConfirmAction.open();

        },
        verActivar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea activar esta firma?";
            $vue.configConfirmAction.okbtn = "Si, activar";
            $vue.configConfirmAction.okclass = "btn-primary";
            $vue.configConfirmAction.okaction = $vue.activar;
            $vue.$refs.modalConfirmAction.open();
        },
        anularFirma() {
            let $vue = this;

            axios.post(APP.url(`${rutaModulo}/anularFirma`), $vue.itemSelect).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadFirmas();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        activar() {
            let $vue = this;
            axios.post(APP.url(`${rutaModulo}/activarFirma`), $vue.itemSelect).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.reloadFirmas();
                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                if (!/\.(jpg|jpeg|png)$/i.test(newFile.name)) {
                    swal('¡Este tipo de archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            $vue.isprocess = true;
            if (newFile) {
                $('#progress-bar').css('width', newFile.progress + '%');
                if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                    if (!$vue.$refs.uploadFirma.active) {
                        $vue.$refs.uploadFirma.active = true;
                    }
                }
            }
            if (oldFile && newFile) {
                if (newFile.success !== oldFile.success) {
                    $vue.archivo = newFile.response.data;
                }
            }
        },
        existeArchivo(archivo) {
            let $vue = this;
            if (archivo.id === undefined) {
                return false;
            } else if (archivo.id !== "") {
                return true;
            } else if (archivo.nombre !== "") {
                return true;
            } else {
                return false;
            }
        },
        verTemporal(archivo) {
            let ruta = "";

            if (archivo.id === undefined) {
                return;
            } else if (archivo.id !== "") {
                ruta = archivo.ruta;
            } else if (archivo.nombre !== "") {
                ruta = APP.url("comun/archivo/verArchivoTemporal/") + archivo.nombre;
            } else {
                return;
            }
            window.open(ruta, "_blank");
        },
        classByArchivo() {
            let $vue = this;
            if ($vue.archivo.id === "") {
                return "text-danger";
            }
            return "text-primary";
        }
    }
});
