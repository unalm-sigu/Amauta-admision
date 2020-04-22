Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        curriculasURL: APP.url(rutaModulo + "/list"),
        carrera: {},
        carreras: JSON.parse(carrerasJson),
        modalAsignacionMasiva: {
            id: 'idAsignacionMasiva',
            title: 'Asignación Masiva',
            okbtn: 'Asignar planes',
            okclass: 'btn-success btn-asignar',
            showaccept: true
        },
        procesando: false,
        revisando: false,
        showProgress: false,
        msgProgress: '',
        styleProgress: '',
        porcentProgress: 0,
        colorEstado: {
            CRE: "default",
            ACT: "success",
            INA: "danger",
            CER: "danger",
            APR: "primary",
            ACEP: "primary",
            OBS: "warning",
            SOL: "info",
            RHZ: "danger",
            REE: "info"
        },
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        }),
        isFull: false
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
      
        asignacionMasiva() {
            let $vue = this;
            $vue.carrera = {};
            $vue.isFull = false;
            $vue.$refs.modalAsignacionMasiva.open();
        },
        saveAsignacionMasiva() {
            let $vue = this;
            console.log($vue.carrera.id);

            if ($vue.carrera.id) {

                if ($vue.revisando) {
                    notify("Estamos verificando procesamiento", "info");
                    return;
                }
                if ($vue.procesando) {
                    notify("Ya se está asignando los planes de esta especialidad", "error");
                    return;
                }

                var mbb = bootbox.confirm({
                    message: '¿Seguro que desea asignar en forma masiva el plan de estudios?',
                    buttons: {
                        confirm: {label: 'Si, asignar', className: 'btn-warning btn-modal btn-procesar'},
                        cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                    },
                    callback: function (result) {
                        if (result) {
                            $(".btn-procesar").html('<i class="fa fa-spinner fa-pulse"></i> Procesando...');
                            $(".btn-modal").prop('disabled', true);

                            $.ajax({
                                url: APP.url('academico/planCurricular/asignacionmasiva'),
                                type: 'POST',
                                data: {id: $vue.carrera.id},
                                success(response) {
                                    mbb.modal("hide");
                                    if (response.success) {
                                        //$vue.$refs.modalAsignacionMasiva.close();
                                        //bootbox.alert(response.message);
                                        $vue.avanceReal();

                                    } else {
                                        $(".btn-modal").prop('disabled', false);
                                        $(".btn-procesar").html('Si, asignar');
                                        bootbox.alert(response.message);
                                    }
                                },
                                error() {
                                    mbb.modal("hide");
                                    $(".btn-modal").prop('disabled', false);
                                    $(".btn-procesar").html('Si, asignar');
                                    bootbox.alert(Messages.errorComunicacion);
                                }
                            });

                            return false;
                        }
                    }
                });



            } else {
                notify("Seleccione una especialidad", "error");
            }
        },
        revisarCarrera(carr) {
            let $vue = this;
            $vue.revisando = true;
            $vue.procesando = false;
            $vue.showProgress = true;
            $vue.porcentProgress = 20;
            $vue.styleProgress = 'width: 20%';
            $vue.msgProgress = 'Estamos verificando procesamiento';
            $(".btn-asignar").prop('disabled', true);

            $vue.avanceFalso();

            $.ajax({
                url: APP.url('academico/planCurricular/existeAsignacionMasiva'),
                type: 'POST',
                data: {id: $vue.carrera.id},
                success(response) {
                    if (response.success) {
                        $vue.avanceReal();
                    } else {
                        $vue.revisando = false;
                        $vue.procesando = false;
                        $vue.showProgress = false;
                        $(".btn-asignar").prop('disabled', false);
                    }
                },
                error() {
                    $vue.$refs.modalAsignacionMasiva.close();
                    bootbox.alert(Messages.errorComunicacion);
                }
            });

        },
        avanceFalso() {
            let $vue = this;
            if ($vue.procesando) {
                return;
            }

            $vue.porcentProgress++;
            if ($vue.porcentProgress > 100) {
                $vue.porcentProgress = 20;
            }
            $vue.styleProgress = 'width: ' + $vue.porcentProgress + '%';
            setTimeout(function () {
                $vue.avanceFalso();
            }, 300);
        },
        closeAsignacion() {
            let $vue = this;
            $vue.revisando = false;
            $vue.procesando = false;
            $vue.showProgress = false;
        },
        avanceReal() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/planCurricular/avanceAsignacionMasiva'),
                type: 'POST',
                data: {id: $vue.carrera.id},
                success(response) {
                    if (response.success) {
                        $vue.procesando = true;
                        $vue.showProgress = true;
                        $vue.porcentProgress = response.total;
                        $vue.msgProgress = response.message;
                        $vue.styleProgress = 'width: ' + $vue.porcentProgress + '%';

                        console.log(response.total)

                        setTimeout(function () {
                            $vue.avanceReal();
                        }, 1000);

                    } else {
                        $vue.showProgress = false;
                        bootbox.alert({
                            message: 'Proceso finalizado',
                            callback: function () {
                                $vue.$refs.modalAsignacionMasiva.close();
                            }
                        });
                    }
                },
                error() {
                    $vue.$refs.modalAsignacionMasiva.close();
                    bootbox.alert(Messages.errorComunicacion);
                }
            });
        },
        labelEstado(item) {
            let $vue = this;
            return "label-" + $vue.colorEstado[item.estado];
        },
        nuevoPlan() {
            let $vue = this;
            location.href = APP.url(rutaModulo + '/nuevo') + $vue.getOrigenURL();
        },
        urlEditarPlan(item) {
            let $vue = this;
            return APP.url(rutaModulo + '/' + item.id + '/editarPlanCurricular') + $vue.getOrigenURL();
        },
        verPlan(item) {
            let $vue = this;
            location.href = APP.url(rutaModulo + '/' + item.id + '/editarPlanCurricular') + $vue.getOrigenURL();
        },
        desactivarPlan(item) {
            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea desactivar este plan?";
            $vue.configConfirmAction.okbtn = "Si, desactivar";
            $vue.configConfirmAction.okclass = "btn-warning";
            $vue.configConfirmAction.okaction = function () {
                axios.post("/" + rutaModulo + "/desactivarPlan", {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        notify(response.data.message, "info");
                        $vue.$refs.raptorCurriculas.loadRemoteData();
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(e => {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            }
            $vue.$refs.modalConfirmAction.open();
        },
        activarPlan(item) {
            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea activar este plan?";
            $vue.configConfirmAction.okbtn = "Si, activar";
            $vue.configConfirmAction.okclass = "btn-primary";
            $vue.configConfirmAction.okaction = function () {
                axios.post("/" + rutaModulo + "/activarPlan", {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        notify(response.data.message, "info");
                        $vue.$refs.raptorCurriculas.loadRemoteData();
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(e => {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            }
            $vue.$refs.modalConfirmAction.open();
        },
        clonarPlan(item) {
            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea clonar este plan?";
            $vue.configConfirmAction.okbtn = "Si, clonar";
            $vue.configConfirmAction.okclass = "btn-primary";
            $vue.configConfirmAction.okaction = function () {
                axios.post("/" + rutaModulo + "/clonarPlan", {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        notify(response.data.message, "info");
                        $vue.$refs.raptorCurriculas.loadRemoteData();
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(e => {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            }
            $vue.$refs.modalConfirmAction.open();
        },
        eliminarPlan(item) {
            let $vue = this;
            $vue.configConfirmAction.message = "¿Está seguro que desea eliminar este plan?";
            $vue.configConfirmAction.okbtn = "Si, eliminar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = function () {
                axios.post("/" + rutaModulo + "/eliminarPlan", {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        notify(response.data.message, "info");
                        $vue.$refs.raptorCurriculas.loadRemoteData();
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(e => {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                });
            }
            $vue.$refs.modalConfirmAction.open();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
    }
});

