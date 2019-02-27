Vue.component("tipo-carpeta-component", {
    template: "#tipocarpetacomponent",
    props: {
        isSearchingTipoCarpeta: {type: Boolean, default: false},
        tipocarpetas: {type: Object, default: []},
        seccion: {type: Object, default: {}},
        tipocarpeta: {type: Object, default: {}},
    },
    mounted: function () {
        let $vue = this;
        $vue.findtipocarpeta();
    },
    methods: {
        findtipocarpeta() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/findtipocarpeta'),
                type: 'POST',
                data: {id: $vue.seccion.id},
                success(response) {
                    if (response.success) {
                        $vue.tipocarpeta = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        buscartipocarpeta(search) {
            let $vue = this;
            $vue.isSearchingTipoCarpeta = true;
            $.ajax({
                url: APP.url('academico/gposeccion/allTipoCarpeta'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    if (response.success) {
                        $vue.tipocarpetas = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.isSearchingTipoCarpeta = false;
                },
                error() {
                    $vue.isSearchingTipoCarpeta = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        savetipocarpeta(tipocarpeta) {

            let $vue = this;

            var mm = bootbox.confirm({
                message: "¿Desea cambiar el tipo de carpeta de la sección seleccionada?",
                buttons: {
                    confirm: {
                        label: 'Si, cambiar',
                        className: 'btn-success'
                    },
                    cancel: {
                        label: 'cancelar',
                        className: 'btn-link'
                    }
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveTipoCarpetaSeccion'),
                            dataType: 'json',
                            type: 'POST',
                            async: true,
                            data: {
                                id: $vue.seccion.id,
                                "tipoCarpeta.id": tipocarpeta.id,
                            },
                            success(response) {
                                if (response.success) {
                                    mm.modal('hide');
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                        return false;
                    } else{
                        $vue.findtipocarpeta();
                    }
                }
            });
        }
    }
});