Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#main',
    data: {
        silaboURL: APP.url('academico/silabo/list'),
        silaboCurso: {},
        modalSilabo: {
            id: 'modalSilabo',
            modalsize: 'modal-md',
            header: false,
            showaccept: true
        },
        cursos: [],
        departamentos: [],
        files: []
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        save() {
            let $vue = this;
            let form = $("#form");
            if (!form.parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento");
            axios_.post('/academico/silabo/save', $vue.silaboCurso)
                    .then(response => {
                        MODAL.hideWait();
                        notify(response.data, 'info');
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalSilabo.close();
                    })
                    .catch(function (error) {
                        MODAL.hideWait();
                    });
        },
        cancelSave() {
            let $vue = this;
            $vue.silaboCurso = {};
        },
        openModalSilabo() {
            let $vue = this;
            $vue.silaboCurso = {};
            $vue.$refs.modalSilabo.open();
        },
        findCurso(nombre) {
            let $vue = this;
            if (nombre) {
                $vue.cursos = [];
            }
            axios_.get('/academico/silabo/allCursoMod', {params: {nombre: nombre}})
                    .then(response => {
                        $vue.cursos = response.data;
                    })
                    .catch(() => {
                    });
        },
        findDepartamento(nombre) {
            let $vue = this;
            if (nombre) {
                $vue.departamentos = [];
            }
            axios_.get('/academico/silabo/allDepartamentoMod', {params: {nombre: nombre}})
                    .then(response => {
                        $vue.departamentos = response.data;
                    })
                    .catch(() => {
                    });
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            if (newFile && oldFile) {
                if (newFile.active && !oldFile.active) {
                    if (newFile.size >= 0 && this.minSize > 0 && newFile.size < this.minSize) {
                        this.$refs.upload.update(newFile, {error: 'size'})
                    }
                }
                if (newFile.progress !== oldFile.progress) {
                }
                if (newFile.error && !oldFile.error) {
                }
                if (newFile.success && !oldFile.success) {
                }
            }
            if (!newFile && oldFile) {
                if (oldFile.success && oldFile.response.id) {
                }
            }
            if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                if (!this.$refs.upload.active) {
                    this.$refs.upload.active = true;
                } else {
                }
            }

            if ($vue.$refs.upload.uploaded) {
                if ($vue.files.length > 0) {
                    $vue.silaboCurso.rutaDocumento = $vue.files[0].response.data;
                    $vue.silaboCurso.fileUpdated = 1;
                }
                if ($vue.$refs.upload.clear()) {
                }
            }

            if (newFile && oldFile && !newFile.active && oldFile.active) {
                if (newFile.xhr) {
                    if (newFile.xhr.status == 200) {
                        notify(newFile.response.message, "info");
                    } else {
                        notify(newFile.response.message, "error");
                    }
                    MODAL.hideWait();
                } else {
                    notify(response.message, "error");
                }
            }
        },
        inputFilter(newFile, oldFile, prevent) {
            if (newFile && !oldFile) {
                if (!/\.(pdf)$/i.test(newFile.name)) {
                    swal(
                            'Error de formato',
                            '¡Este archivo no esta permitido!',
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
        },
        editar(item) {
            let $vue = this;
            $vue.silaboCurso = {...item};
            $vue.$refs.modalSilabo.open();
        },
        eliminar(item) {
            let $vue = this;
            swal('¿Seguro que desea eliminar el silabu?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Sí, Eliminar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                axios_.post('/academico/silabo/delete', {id: item.id})
                        .then(response => {
                            notify(response.data, 'info');
                            $vue.$refs.load.loadRemoteData();
                            return swal({text: response.data, icon: "success", button: false, timer: 1000});
                        })
                        .catch(() => {
                            return swal(APP.errorComunicacion, "error");
                        });

            }).catch(err => {
                if (err) {
                    swal(APP.errorComunicacion, "error");
                } else {
                    swal.stopLoading();
                    swal.close();
                }
            });



        },
        revision(item, st) {
            let $vue = this;
            axios_.post('/academico/silabo/revision', {id: item.id, estado: st})
                    .then(response => {
                        notify(response.data, 'info');
                        $vue.$refs.load.loadRemoteData();
                    })
                    .catch(function () {
                    });
        }
    }
});
