Vue.component("multiselect", window.VueMultiselect.default);
const AulaFiltro = httpVueLoader('/app/programacion/tramiteaula/AulaFiltro.vue');
const SolicitanteFiltro = httpVueLoader('/app/programacion/tramiteaula/SolicitanteFiltro.vue');

new Vue({
    el: '#main',
    mixins: [VueLoader],
    components: {
        aulaFiltro: AulaFiltro,
        solicitanteFiltro: SolicitanteFiltro
    },
    data: {
        urlfilter: APP.url("tramite/aula/list"),
        tramiteactivo: {id: null, comentario: null},
        dataConfirmModal: {id: 'iddataConfirmModal', title: 'Aceptar Tramite', header: true},
        dataRechazarModal: {id: 'iddataRechazarModal', title: 'Rechazar Tramite', header: true},
        dataResumenModal: {id: 'iddataResumenModal', title: 'Resumen Reserva', header: true, modalsize: 'modal-lg', showaccept: false, cancelclass: 'btn-link'},
        tramiteresumen: {tramite: {}},
        dias: [],
        horas: [],
        configConfirmar: VUE_MODAL.structFormAjax({
            id: "formConfirmModal",
            header: true,
            title: 'Confirmar',
            okbtn: 'Confirmar',
            okclass: "btn-danger",
            form: "formConfirmModal"
        }),
        configRechazar: VUE_MODAL.structFormAjax({
            id: "formRechazarModal",
            header: true,
            title: 'Rechazar',
            okbtn: 'Rechazar',
            okclass: "btn-danger",
            form: "formRechazarModal"
        }),
        configRegularizar: VUE_MODAL.structFormAjax({
            id: "formRegularizarModal",
            header: true,
            title: 'Regularizar',
            okbtn: 'Regularizar',
            okclass: "btn-danger",
            form: "formRegularizarModal"
        }),
        configConfirmAction: VUE_MODAL.structConfirm({}),
    },
    mounted: function () {

    },
    methods: {
        editarTramite(tramite) {
            // console.log(tramite)
            // if(tramite.tipoReservaAmbiente== 'LIB'){
            //     tramite.tipoReservaAmbiente = 'Libre'
            // }else if(tramite.tipoReservaAmbiente == 'EPG'){
            //     tramite.tipoReservaAmbiente = 'Posgrado'
            // }else {
            //     tramite.tipoReservaAmbiente = 'Pregrado'
            // }
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        regularizarTramite(tramite) {
            let $vue = this;
            $vue.tramiteactivo.id = tramite.id;
            $vue.tramiteactivo.comentario = '';
            $vue.$refs.regularizarModal.open();
        },
         saveRegularizarModal() {
             console.log("asdasds");
            let $vue = this;
            let miform = $($vue.$refs.formRegularizarModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.showLoader();
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('tramite/aula/regularizartramite'),
                data: JSON.stringify($vue.tramiteactivo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.regularizarModal.close();
                        $vue.$refs.raptor.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.hideLoader();
                },
                error() {
                    $vue.hideLoader();
                    notify(Messages.errorComunicacion, "error");
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        cambiarVisible(tramite) {
            let $vue = this;
            $vue.showLoader();
            let reserva = {id: tramite.id, visibleHorario: tramite.visibleHorario}
            $.ajax({
                method: 'POST',
                async: false,
                url: APP.url('tramite/aula/cambiarVisible'),
                data: JSON.stringify(reserva),
                contentType: "application/json",
                success: function (response) {
                    $vue.hideLoader();
                    if (response.success) {
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    $vue.hideLoader();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        afterChangeFilterAula(aula) {
            let $vue = this;
            if (aula) {
                $vue.$refs.raptor.querie.push({name: 'aula', value: aula.id});
            } else {
                $vue.$refs.raptor.querie.push({name: 'aula', value: null});
            }
            $vue.$refs.raptor.loadRemoteData();
        },
        afterChangeFilterSolicitante(tipo, solicitante) {
            let $vue = this;
            if (solicitante) {
                $vue.$refs.raptor.querie.push({name: 'tipo', value: tipo.id});
                $vue.$refs.raptor.querie.push({name: 'solicitante', value: solicitante.id});
            } else {
                $vue.$refs.raptor.querie.push({name: 'tipo', value: null});
                $vue.$refs.raptor.querie.push({name: 'solicitante', value: null});
            }
            $vue.$refs.raptor.loadRemoteData();
        },
        generarReporte() {
            let $vue = this;
            let urll = '';
            $vue.processreporte = true;
            urll = APP.url(`tramite/aula/exportExcel`);

            axios({
                url: urll,
                method: 'POST',
                responseType: 'blob',
            }).then((response) => {
                var namee = response
                    .headers["content-disposition"]
                    .replace("attachment; filename=", "")
                    .replace(/"/g, '');
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', namee);
                document.body.appendChild(link);
                link.click();
                $vue.processreporte = false;
            }).catch(error => {
                $vue.processreporte = false;
                notify(Messages.errorComunicacion, "error");
            });
        },
    }
    
});
