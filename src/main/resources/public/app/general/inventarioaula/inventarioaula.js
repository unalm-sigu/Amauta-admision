Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        inventario: {},
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
        micomentario: ''
    },
    mounted: function () {
        let $vue = this;
        $vue.allProducto();
        $vue.applyFileupload();
        $('[name="fechaVencimientoGarantia"]').datepickerBoot();
        $('[name="anoFabricacion"]').datepickerBoot();
        $('[name="fechaIngreso"]').datepickerBoot();
        $('[name="fechaBaja"]').datepickerBoot();
    },
    updated: function () {
        $('[name="fechaVencimientoGarantia"]').datepickerBoot();
        $('[name="anoFabricacion"]').datepickerBoot();
        $('[name="fechaIngreso"]').datepickerBoot();
        $('[name="fechaBaja"]').datepickerBoot();
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
                        vue.inventario = {};
                        vue.micomentario = '';
                        vue.categoria = null;
                        vue.producto = null;
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
            var vue = this;
            vue.inventario = {};
            vue.micomentario = '';
            vue.categoria = null;
            vue.producto = null;
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
        openFilePicker() {
            $('#fileupload').trigger('click');
        },
        applyFileupload() {

            let $vue = this;

            $('#fileupload').fileupload({
                url: APP.url('general/aula/inventario/upload'),
                maxNumberOfFiles: 1,
                dataType: 'json',
                dropZone: '#dragarea',
                add: function (e, data) {
                    if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                        notify("Formato de archivo no soportado.", "error");
                        return;
                    }
                    if (data.files && data.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            $('#imagenProfile').attr('src', e.target.result);
                        };
                        reader.readAsDataURL(data.files[0]);
                    }
                    data.submit();

                    $vue.isprocess = true;
                    $vue.isactiveprogressbar = true;
                },
                progress: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('#progress-bar').css('width', progress + '%');
                    if (progress === 100) {
                        $vue.isactiveprogressbar = false;
                        $('#progress-bar').css('width', 0 + '%');
                    }
                },
                done: function (e, data) {
                    if (data.result.success) {
                        notify(data.result.message, "info");
                    } else {
                        notify(data.result.message, "error");
                    }
                    $vue.isprocess = false;
                    $vue.isactiveprogressbar = false;
                },
                fail: function (e, data) {
                    $vue.isprocess = false;
                    $vue.isactiveprogressbar = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});      