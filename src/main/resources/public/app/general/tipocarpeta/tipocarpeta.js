Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    data: {
        tipoCarpetaSuperior: null,
        tipoCarpeta: '',
        carpetaUrl: APP.url("general/tipocarpeta/allTipoCarpeta"),
        tipocarpetas: [],
        modalnuevoTipocarpeta: {
            id: 'modalnuevoTipocarpeta',
            header: true,
            cancelclass: 'btn btn-link',
            showaccept: true,
            modalsize: 'modal-lg',
        },
    },
    mounted: function () {
        let $vue = this;
        $vue.loadTipoCarpeta()
    },
    methods: {
        loadTipoCarpeta() {
            let $vue = this;
            $.ajax({
                url: APP.url("general/tipocarpeta/allTipoCarpeta"),
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.tipocarpetas = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        nuevoTipocarpeta() {
            let $vue = this;
            console.log("modal");
            $vue.tipoCarpeta = {};
            $vue.tipoCarpetaSuperior = null;
            $vue.modalnuevoTipocarpeta.title = "Nueva Carpeta";
            $vue.modalnuevoTipocarpeta.okbtn = "Crear";
            $vue.$refs.modalnuevoTipocarpeta.open();

            $vue.isLoading = true;
        }, agregarTipoCarpetaHija(tipoCarpeta) {
            let $vue = this;
            $vue.tipoCarpetaSuperior = tipoCarpeta;
            console.log("modal");
            $vue.tipoCarpeta = {};
            $vue.modalnuevoTipocarpeta.title = "Tipo Carpeta Padre " + $vue.tipoCarpetaSuperior.nombre;
            $vue.modalnuevoTipocarpeta.okbtn = "Guardar";
            $vue.$refs.modalnuevoTipocarpeta.open();
            $vue.isLoading = true;
        }, editar(tipoCarpeta) {
            console.dir(tipoCarpeta);
            let $vue = this;
            // $vue.tipoCarpetaSuperior = tipoCarpeta;
            $vue.tipoCarpeta = tipoCarpeta;
            $vue.modalnuevoTipocarpeta.title = "Editar Tipo Carpeta ";
            $vue.modalnuevoTipocarpeta.okbtn = "Guardar";
            $vue.$refs.modalnuevoTipocarpeta.open();
            $vue.isLoading = true;
        },
        save() {
            let $vue = this;
            if ($('#formTipocarpeta').parsley().validate() !== true) {
                return  swal({text: "Debe completar todos los campos requeridos", icon: "error", dangerMode: true, button: {text: "Aceptar"}});
            } else {
                if ($vue.tipoCarpetaSuperior != null) {
                    $vue.tipoCarpeta.tipoCarpetaSuperior = $vue.tipoCarpetaSuperior;
                }
                $.ajax({
                    method: 'POST',
                    async: false,
                    url: APP.url('general/tipocarpeta/save'),
                    contentType: "application/json",
                    data: JSON.stringify($vue.tipoCarpeta),
                    success: function (response) {
                        if (response.success) {
                            $vue.$refs.modalnuevoTipocarpeta.close();
                            $vue.loadTipoCarpeta();
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });
            }
        }
    }
});


