Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#informesVUE',
    data: {
        itemSelect: {},
        modalObservaInforme: VUE_MODAL.structFormAjax({
            id: "id-modal-observaciones",
            header: true,
            title: 'Observar informe',
            okbtn: 'Enviar observación',
            okclass: "btn-danger"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "id-modal-confirm",
        })
    },
    mounted() {
    },
    methods: {
        classEstado(item) {
            let estilos = {'PENDIENTE': 'danger', 'PAGADO': 'success', 'OBSERVA': 'warning', 'ANULADO': 'dark', 'VENCIDO': 'danger'};
            let rpta = estilos[item.estado];
            if (rpta === undefined) {
                return "label-primary";
            }
            return "label-" + rpta;
        },
        verTemporal(bean) {
            let $vue = this;
            let ruta = "";

            if (bean.id) {
                ruta = bean.ruta;
            } else {
                ruta = APP.url("archivo/verArchivoTemporal/") + bean.nombre;
            }

            var linkPdf = document.createElement('A');
            linkPdf.href = ruta;
            linkPdf.download = ruta.substr(ruta.lastIndexOf('/') + 1);
            document.body.appendChild(linkPdf);
            linkPdf.target = "_blank";
            linkPdf.click();
            document.body.removeChild(linkPdf);

        },
        verAprobar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));

            $vue.configConfirmAction.message = "¿Está seguro que desea APROBAR este informe?";
            $vue.configConfirmAction.okbtn = "Si, aprobar";
            $vue.configConfirmAction.okclass = "btn-success";
            $vue.configConfirmAction.okaction = $vue.aprobar;
            $vue.$refs.modalConfirmAction.open();
        },
        verDesaprobar(item) {
            let $vue = this;
            $vue.itemSelect = JSON.parse(JSON.stringify(item));
            $vue.$refs.modalObservaInforme.open();
        },
        aprobar(item) {
            let $vue = this;

            axios.post(APP.url(`${rutaModulo}/aprobarInforme`), $vue.itemSelect).then(response => {
                $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorInformes.loadRemoteData();
                    notify(response.data.message, "info");

                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalConfirmAction.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        saveObservar(item) {
            let $vue = this;

            $vue.$refs.modalObservaInforme.beginProcessing();
            axios.post(APP.url(`${rutaModulo}/observarInforme`), $vue.itemSelect).then(response => {
                $vue.$refs.modalObservaInforme.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorInformes.loadRemoteData();
                    notify(response.data.message, "warning");

                } else {
                    notify(response.data.message, "warning");
                }
            }).catch(e => {
                $vue.$refs.modalObservaInforme.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verObservacion(item) {
            swal({title: item.observaciones, type: "warning", showConfirmButton: false});
        }
    }
});







