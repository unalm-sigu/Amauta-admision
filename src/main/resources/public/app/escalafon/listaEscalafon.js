Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        escalafonURL: "/escalafon/list",
        escalafon: {persona: null},
        listPersona: [],
        escalofonModal: VUE_MODAL.structConfirm({
            id: 'escalofonModal',
            title: 'Nuevo Escalafón',
            okbtn: 'Aceptar',
            okclass: 'btn-success'
        }),
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        })
    },
    mounted: function () {
    },
    methods: {
        openMolda() {
            let $vue = this;
            $vue.escalafon = {persona: null};
            $vue.$refs.escalofonModal.open();
        },
        searchPersona(nombre) {
            let $vue = this;
            if (nombre == null || nombre.trim().length == 0) {
                return;
            }
            $vue.listPersona = [];
            axios.get("/comun/buscar/allPersona", {params: {nombre: nombre}})
                    .then(response => {
                        $vue.listPersona = response.data.data;
                    });
        },
        save() {
            let $vue = this;
            if (!$("#form-validar-escalafon").parsley().validate()) {
                notify.warning("Debe completar todos los campos requeridos.");
                return;
            }
            $vue.$refs.escalofonModal.beginProcessing();
            axios.post("/escalafon/save", $vue.escalafon)
                    .then(function (response) {
                        if (response.data.success) {
                            notify(response.data.message, 'success');
                            $vue.$refs.escalofonModal.confirmReaction(true);
                            if (response.data.data != null) {
                                location.href = $vue.editar(response.data.data);
                            }
                            $vue.$refs.raptorEscalafon.loadRemoteData();
                        } else {
                            $vue.$refs.escalofonModal.confirmReaction(false);
                            notify(response.data.message, 'warning');
                        }
                    })
                    .catch(function (error) {
                        notify(error.errorComunicacion, "error");
                        $vue.$refs.escalofonModal.confirmReaction(false);
                    });
        },
        editar(item) {
            return APP.url('escalafon/update/' + item.id) + this.getOrigenURL();
        },
        ver(item) {
            return location.href = '/escalafon/info/' + item.id;
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        eliminar(item) {
            let $vue = this;
            $vue.configConfirmAction.message = Messages.confirmDelete;
            $vue.configConfirmAction.okbtn = "Si, eliminar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = function () {
                axios.post("/escalafon/eliminar", item).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        notify(response.data.message, "success");
                        $vue.$refs.raptorEscalafon.loadRemoteData();
                    } else {
                        notify(response.data.message, "warning");
                    }
                }).catch(e => {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            };
            $vue.$refs.modalConfirmAction.open();
        }
    }
});