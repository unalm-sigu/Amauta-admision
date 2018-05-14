Vue.component("multiselect", window.VueMultiselect.default);

var app = new Vue({
    el: '#resoluciones',
    data: {
        URL_RESOLUCIONES: APP.url('academico/resolucion/listResoluciones'),
        resolucionModal: {
            id: 'modalResolucion',
            header: true,
            title: 'Resoluciones',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        resolucion: null,
        tiposResoluciones: null
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        cambiarEstadoReincorporacion: function (tramite, estadoDestino, event) {
            event.preventDefault();
            let $vue = this;
            console.log("cambiarEstadoReincorporacion");
            console.dir(tramite);
            $.ajax({
                url: APP.url('academico/tramiteacademico/cambiarEstadoReincorporacion'),
                type: 'POST',
                async: false,
                data: {
                    tramite: tramite.id,
                    estado: "SOL_ACEP"
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(response.message, "error");
                }
            });
        },
        nuevaResolucion(event) {
            let $vue = this;
            event.preventDefault();
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        $vue.$refs.modalResolucion.open();
                        console.dir(response.data);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }
            var form = $("[id='frmResolucion']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            let $vue = this;
            console.log("save");
            console.dir($vue.resolucion);
            console.log(JSON.stringify($vue.resolucion));

            $.ajax({
                url: APP.url('academico/resolucion/saveResolucion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.modalResolucion.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})