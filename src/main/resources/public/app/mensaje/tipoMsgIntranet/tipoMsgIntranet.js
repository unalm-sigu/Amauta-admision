Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#tipoMsgVUE',
    data: {
        tipoMsgURL: APP.url('mensajeria/tipomsgintranet/list'),
        tipoMsgIntranet: {},
        cicloAcademico: JSON.parse(cicloJson),
        tipoEnums: JSON.parse(tipoMsjIntraEnums),
        addTipoMsgIntranet: {
            id: 'modalTipoMsgIntranet',
            header: true,
            title: 'Crear Tipo Mensaje Intranet',
            okbtn: 'Guardar',
            showaccept: true,
            modalsize: "modal-md"
        },
        isLoading: false
    },
    computed: {

    },
    mounted: function () {
    },
    methods: {
        init() {
            let $vue = this;
            $vue.tipoMsgIntranet = {};

        },
        nuevo() {
            let $vue = this;
            $vue.init();
            $vue.addTipoMsgIntranet.okbtn = "Guardar";
            $vue.addTipoMsgIntranet.title = "Nuevo Tipo Mensaje Intranet";
            $vue.$refs.modalTipoMsgIntranet.open();
        },
        saveUpdate(e) {
            let $vue = this;

            if (!$("#formTipoMsg").parsley().validate() == true) {
                return;
            }
            $vue.tipoMsgIntranet.codigo = $vue.tipoMsgIntranet.codigoEnum.name;

            $.ajax({
                method: 'POST',
                url: APP.url('mensajeria/tipomsgintranet/saveUpdate'),
                data: JSON.stringify($vue.tipoMsgIntranet),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalTipoMsgIntranet.close();
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        editar(item) {
            let $vue = this;
            $vue.init();
            $vue.tipoMsgIntranet = JSON.parse(JSON.stringify(item));
            $vue.addTipoMsgIntranet.okbtn = "Actualizar";
            $vue.addTipoMsgIntranet.title = "Actualizar tipo mensaje intranet";
            $vue.$refs.modalTipoMsgIntranet.open();
        },
        eliminar: function (item) {
            console.log(item)
            let $vue = this;

            swal({
                title: "Eliminación del Registro",
                text: "¿Desea eliminar el tipo mensaje intranet?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            }).then((success) => {
                if (success) {
                    $.ajax({
                        method: 'POST',
                        url: APP.url('mensajeria/tipomsgintranet/eliminar'),
                        data: JSON.stringify(item),
                        contentType: "application/json",
                        success: function (response) {
                            if (response.success) {
                                $vue.$refs.load.loadRemoteData();
                                swal(response.message, {
                                    icon: "success",
                                });
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function (error) {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });

                }
            });
        }
    }
});
