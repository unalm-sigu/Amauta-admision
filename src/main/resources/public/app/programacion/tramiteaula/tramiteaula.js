new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        urlfilter: APP.url("tramite/aula/list"),
        tramiteactivo: {id: null, comentario: null},
        dataConfirmModal: {id: 'iddataConfirmModal', title: 'Aceptar Tramite', header: true},
        dataRechazarModal: {id: 'iddataRechazarModal', title: 'Rechazar Tramite', header: true},
        dataResumenModal: {id: 'iddataResumenModal', title: 'Resumen Reserva', header: true, modalsize: 'modal-lg', showaccept: false, cancelclass: 'btn-link'},
        tramiteresumen: {tramite: {}},
        dias: [],
        horas: [],
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        editarTramite(tramite) {
            let $vue = this;
            return APP.url('tramite/aula/' + tramite.id + '/update')
        },
        aceptarTramite(tramite) {
            let $vue = this;
            $vue.tramiteactivo.id = tramite.id;
            console.log('******');
            console.log($vue.tramiteactivo.comentario);
            $vue.tramiteactivo.comentario = '';
            console.log($vue.tramiteactivo.comentario);
            $vue.$refs.confirmModal.open();
        },
        rechazarTramite(tramite) {
            let $vue = this;
            $vue.tramiteactivo.id = tramite.id;
            $vue.tramiteactivo.comentario = '';
            $vue.$refs.rechazarModal.open();
        },
        saveRechazarModal() {
            let $vue = this;
            let miform = $($vue.$refs.formRechazarModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.showLoader();
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('tramite/aula/rechazartramite'),
                data: JSON.stringify($vue.tramiteactivo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.rechazarModal.close();
                        $vue.$refs.raptor.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveConfirmModal() {
            let $vue = this;
            let miform = $($vue.$refs.formConfirmModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.showLoader();
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('tramite/aula/aceptartramite'),
                data: JSON.stringify($vue.tramiteactivo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.confirmModal.close();
                        $vue.$refs.raptor.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        showMePop(e) {
            $(e.originalTarget).popover('toggle');
        },
        giveMeAulas(tramite) {
            if (tramite.reservados.length < 1) {
                return "";
            }
            let aulass = '';
            tramite.reservados.map(function (el) {
                aulass = aulass + el.nombrePublico + "\n\n";
            });
            return aulass;
        },
        resumenTramite(tramite) {
            let $vue = this;
            $vue.showLoader();
            $.ajax({
                method: 'POST',
                async: false,
                url: APP.url('tramite/aula/resumen'),
                data: {id: tramite.id},
                success: function (response) {
                    $vue.hideLoader();
                    if (response.success) {
                        $vue.tramiteresumen = response.data.reservaAula;
                        $vue.dias = response.data.dias;
                        $vue.horas = response.data.horas;
                        $vue.$refs.resumenModal.open();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    $vue.hideLoader();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});
