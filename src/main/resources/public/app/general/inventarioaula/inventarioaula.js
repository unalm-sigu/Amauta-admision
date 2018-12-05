Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);
new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        inventario: {imagen: APP.url('phobos/images/img.svg')},
        categorias: [],
        categoria: null,
        productos: [],
        producto: null,
        aula: {id: idaula},
        inventarioURL: APP.url('general/aula/inventario/' + idaula + '/all'),
        nuevoproducto: {},
        dataNuevoProducto: {
            id: 'modalNuevoProducto',
            header: false,
            cancelbtn: 'cancelar'
        },
        isprocess: false,
        isactiveprogressbar: false,
        micomentario: '',
        imagentemporal: '',
        files: [],
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        },
        activonuevo: false,
        isindividual: false,
        verTablaEditable: false,
        updatable: [],
    },
    mounted: function () {
        let $vue = this;
        $vue.allProducto();
        $('[name="times"]').numeric();
    },
    updated: function () {
        let $vue = this;
        $('[name="times"]').numeric();
    },
    methods: {
        allProducto() {
            let $vue = this;
            $.ajax({
                url: APP.url('general/aula/inventario/allProducto'),
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.categorias = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeCategoria(item) {
            let vue = this;
            vue.producto = null;
            vue.productos = item.productos;
        },
        addNuevoProducto() {
            let vue = this;
            if (vue.categoria == null) {
                notify('Seleccione una categoria', 'info');
                return;
            }
            $('#formNuevoProducto').parsley().destroy();
            $('#formNuevoProducto').parsley();
            var keys = Object.keys(vue.nuevoproducto);
            for (var key in keys) {
                vue.nuevoproducto['' + keys[key]] = null;
            }
            vue.$refs.nuevoProducto.open();
            $('#formNuevoProducto').find('[name=tipo]').select2({minimumResultsForSearch: -1});
        },
        saveNuevoProducto() {
            var vue = this;
            if ($('#formNuevoProducto').parsley().validate() != true) {
                return;
            }
            vue.showLoader();
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/inventario/saveproducto'),
                data: $('#formNuevoProducto').serialize(),
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.producto = response.data;
                        vue.$refs.nuevoProducto.close();
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.hideLoader();
                }, error: function () {
                    vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        guardarInventario() {
            var vue = this;
            if ($('#formInventario').parsley().validate() != true) {
                return;
            }
            vue.isprocess = true;
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/inventario/save'),
                data: $('#formInventario').serialize(),
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.$refs.load.loadRemoteData();
                        notify(response.message, "info");
                        vue.inventario = {imagen: APP.url('phobos/images/img.svg')};
                        vue.micomentario = '';
                        vue.categoria = null;
                        vue.producto = null;
                        vue.imagentemporal = '';
                        vue.activonuevo = false;

                    } else {
                        notify(response.message, 'error');
                    }
                    vue.isprocess = false;
                }, error: function () {
                    vue.isprocess = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cancelarUpdate() {
            let vue = this;
            vue.categoria = null;
            vue.producto = null;
            vue.imagentemporal = '';
            vue.inventario = {imagen: APP.url('phobos/images/img.svg')};
            vue.activonuevo = false;
        },
        editarInventario(item) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/inventario/update'),
                data: {id: item.id},
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.inventario = response.data;
                        vue.categoria = response.data.producto.productoSuperior;
                        vue.producto = response.data.producto;
                        vue.micomentario = response.data.comentario;
                        vue.activonuevo = true;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminarInventario(item) {
            var vue = this;
            swal('¿Seguro que desea eliminar el inventario seleccionado?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {
                if (value != true) {
                    return;
                }
                $.ajax({
                    method: 'POST',
                    async: false,
                    url: APP.url('general/aula/inventario/delete'),
                    data: {id: item.id},
                    success: function (response) {
                        if (response.success) {
                            vue.$refs.load.loadRemoteData();
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
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
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                if (!/\.(gif|jpg|jpeg|png|webp)$/i.test(newFile.name)) {
                    swal('Error de tipo de archivo', 'Este archivo no es una imagen!', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
            let URL = window.URL || window.webkitURL
            if (URL && URL.createObjectURL) {
                $vue.inventario.imagen = URL.createObjectURL(newFile.file)
            }
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            $vue.isprocess = true;
            if (newFile) {
                $('#progress-bar').css('width', newFile.progress + '%');
                if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                    if (!$vue.$refs.upload.active) {
                        $vue.$refs.upload.active = true
                    }
                }
            }
            if (oldFile && newFile) {
                if (newFile.success !== oldFile.success) {
                    $vue.imagentemporal = newFile.response.data.ruta;
                    $vue.isprocess = false;
                }
            }
        },
        nuevoInventario() {
            let vue = this;
            vue.activonuevo = true;
            vue.categoria = null;
            vue.producto = null;
            vue.imagentemporal = '';
            vue.inventario = {imagen: APP.url('phobos/images/img.svg')};
        },
        confirmUpdateCodigoInventario() {

            var vue = this;

            swal('¿Está seguro que desea actualizar el número  de inventario ?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "No", closeModal: true, visible: true},
                    confirm: {text: "Si, guardar", closeModal: false}
                }
            }).then((value) => {

                console.log(value);

                if (value != true) {
                    return;
                }

                console.log('post to backen');
                vue.updatable = [];

                vue.$refs.load.data.map((v, i) => {
                    vue.updatable.push({id: v.id, codigo: v.codigo, codeEdit: v.codeEdit});
                });

                $.ajax({
                    method: 'POST',
                    contentType: "application/json",
                    url: APP.url('general/aula/inventario/updateCode'),
                    data: JSON.stringify(vue.updatable),
                    success: function (response) {
                        if (response.success) {
                            vue.$refs.load.loadRemoteData();
                            vue.verTablaEditable = false;
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
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
        nextEditable($event) {
            let vue = this;
            var inx = vue.$refs.editable.indexOf($event.target);
            var idx = inx + 1;
            if (vue.$refs.editable.length > idx) {
                vue.$refs.editable[idx].focus()
            } else {
                swal({text: "Ya llegó al último registro", icon: "warning",  button: {text: "Aceptar"}});
            }
        }
    }
});      