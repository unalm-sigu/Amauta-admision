Vue.component("multiselect", window.VueMultiselect.default)
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

new Vue({
    el: '#mensajeriaVUE',
    data: {
        mensajeriaURL: APP.url('mensajeria/list'),
        mensajeria: {},
        gruposAlumno: JSON.parse(gruposAlumnoJson),
        tiposMensaje: JSON.parse(tiposMensajeJson),
        cicloAcademico: JSON.parse(cicloJson),
        addMensajeria: {
            id: 'modalMensajeria',
            header: 'true',
            title: 'Crear Mensajeria',
            okbtn: 'Guardar',
            showaccept: true,
            modalsize: "modal-lg"
        },
        configDate: {
            format: 'DD/MM/YYYY',
            useCurrent: false
        },
    },
    computed: {

    },
    mounted: function () {
        $('.numeric').numeric({negative: false});
    },
    methods: {
        init() {
            let $vue = this;
            $vue.mensajeria = {};
        },
        nuevo() {
            let $vue = this;
            $vue.mensajeria = {esObligatorio: 0};
            $vue.addMensajeria.okbtn = "Guardar";
            $vue.addMensajeria.title = "Nuevo Mensaje Intranet";
            $vue.$refs.modalMensajeria.open();
        },
        saveUpdate(event) {
            let $vue = this;

            if (!$("#formMensajeria").parsley().validate() == true) {
                return;
            }

//            $vue.mensajeria.esObligatorio = $vue.getBoolean($vue.mensajeria.esObligatorio);
//            $vue.mensajeria.conCronograma = $vue.getBoolean($vue.mensajeria.conCronograma);

            $.ajax({
                method: 'POST',
                url: APP.url('mensajeria/saveUpdate'),
                data: JSON.stringify($vue.mensajeria),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalMensajeria.close();
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
//        getBoolean(valor) {
//            if (valor) {
//                return 1;
//            }
//            return 0;
//        },
        editar(item) {
            let $vue = this;
            $vue.init();
            $.ajax({
                method: 'POST',
                url: APP.url('mensajeria/edit'),
                data: JSON.stringify(item),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.mensajeria = response.data;
                        $vue.addMensajeria.okbtn = "Actualizar";
                        $vue.addMensajeria.title = "Actualizar Mensaje Intranet";
                        $vue.$refs.modalMensajeria.open();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (error) {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function (item) {
            console.log(item)
            let $vue = this;

            swal({
                title: "Eliminación del Registro",
                text: "¿Desea eliminar el mensaje intranet?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            }).then((success) => {
                if (success) {
                    $.ajax({
                        method: 'POST',
                        url: APP.url('mensajeria/eliminar'),
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
        },
        tipoSelected(item) {
            let $vue = this;
            $vue.mensajeria.contenido = item.contenido;
        },
        onChangePreObli(e) {
            let $vue = this;
            let val = e.target.value;
            if (val > 0) {
                $vue.mensajeria.esObligatorio = 1;
            }
        },
        chkbIsObli(e) {
            let $vue = this;
            if (!e.target.checked) {
                $vue.mensajeria.preObligatorio = 0;
            }
        }
    }
});
