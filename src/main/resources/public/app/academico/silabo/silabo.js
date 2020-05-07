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
            showaccept:true
        },
        cursos: [],
        ciclos: [],
        planes: [],
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
            axios.post('/academico/silabo/save', $vue.silaboCurso)
                    .then(response => {
                        MODAL.hideWait();
                        if (response.data.success) {
                            notify(response.data.message, 'info');
                            $vue.$refs.load.loadRemoteData();
                            $vue.$refs.modalSilabo.close();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        MODAL.hideWait();
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
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
            if (nombre != null && nombre != "") {
                $.ajax({
                    url: APP.url("comun/buscar/allCursoMod"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.cursos = response.data;
                    if ($vue.cursos == null) {
                        $vue.cursos = [];
                    }
                })
            } else {
                $vue.cursos = [];
            }
        },
        findCiclo(nombre) {
            let $vue = this;
            if (nombre != null && nombre != "") {
                $.ajax({
                    url: APP.url("comun/buscar/allCiclo"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.ciclos = response.data;
                    if ($vue.ciclos == null) {
                        $vue.ciclos = [];
                    }
                })
            } else {
                $vue.ciclos = [];
            }
        },
        findPlanCalifica(nombre) {
            let $vue = this;
            if (nombre != null && nombre != "") {
                $.ajax({
                    url: APP.url("comun/buscar/allPlanCalificacion"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.planes = response.data;
                    if ($vue.planes == null) {
                        $vue.planes = [];
                    }
                })
            } else {
                $vue.planes = [];
            }
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
                    this.$refs.upload.active = true;
                } else {
                    //console.log("FIN?")
                }
            }

            if ($vue.$refs.upload.uploaded) {
                if ($vue.files.length > 0) {
                    //  $vue.reloadProducto();x
                    $vue.silaboCurso.rutaDocumento = $vue.files[0].response.data;
                    $vue.silaboCurso.fileUpdated = 1;
//                    MODAL.hideWait();
                }
                if ($vue.$refs.upload.clear()) {
                    //   console.log("reiniciar img 2")
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
        },
        editar(item) {
            console.log(item);
            let $vue = this;
            $vue.silaboCurso = item;
            $vue.$refs.modalSilabo.open();
        },
        eliminar(item) {
            let $vue = this;
            axios.post('/academico/silabo/delete', {id: item.id})
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, 'info');
                            $vue.$refs.load.loadRemoteData();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        revision(item, st) {
            let $vue = this;
            axios.post('/academico/silabo/revision', {id: item.id, estado: st})
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, 'info');
                            $vue.$refs.load.loadRemoteData();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
                    });
        }
    }
});
