Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

new Vue({
    el: '#pageOficinasVUE',
    data: {
        oficinasURL: APP.url(rutaModulo + '/all'),
        oficina: {personaJefe: {}, jefeEncargado: {}},
        ausencia: {oficina: {}, jefe: {}, encargado: {}},
        cfgColaboradores: VUE_MODAL.structInfo({
            id: 'colaboradoresModal',
            header: true,
            title: 'Relación de colaboradores'
        }),
        configModalAsignarJefe: VUE_MODAL.structFormAjax({
            id: 'idModalAsignarJefe',
            form: 'formAsignaJefe'
        }),
        configModalAsignarEncargado: VUE_MODAL.structFormAjax({
            id: 'idModalAsignarEncargado',
            form: 'formAsignaEncargado'
        }),
        configModalRetirarEncargado: VUE_MODAL.structFormAjax({
            id: 'idModalRetirarEncargado',
            form: 'formRetirarEncargado'
        }),
        configModalRetirarJefe: VUE_MODAL.structFormAjax({
            id: 'idModalRetirarJefe',
            form: 'formRetirarJefe'
        }),
        configConfirmAction: VUE_MODAL.structConfirm({}),
        oficinaSelect: {},
        personas: [],
        colaboradores: [],
        modalBootbox: {},
        configDate: {
            format: "DD/MM/YYYY",
            useCurrent: false
        }
    },
    mounted() {

    },
    methods: {
        classEstado(item) {
            var classesEstado = {ACT: "label-success", INA: "label-danger"};
            return classesEstado[item.estado];
        },
        urlNuevaOficina() {
            let $vue = this;
            return APP.url(rutaModulo + '/nuevo') + $vue.getOrigenURL();
        },
        urlEditar(item) {
            let $vue = this;
            return APP.url(rutaModulo + "/" + item.id + '/update') + $vue.getOrigenURL();
        },
        urlColaboradores(item) {
            let $vue = this;
            return APP.url(rutaModulo + '/' + item.id + '/colaboradores') + $vue.getOrigenURL();
        },
        cambiarEstado(item, accion) {
            let $vue = this;
            let title = "Cambiar estado de la Oficina";
            let msg = "¿Desea cambiar el estado de la oficina?";
            let classBtn = "btn-info";
            let labelBtn = "Cambiar Estado";

            if (accion == "activar") {
                title = "Activar oficina";
                msg = "¿Está seguro que desea activar esta oficina?";
                classBtn = "btn-success";
                labelBtn = "Si, activar";
            } else if (accion == "desactivar") {
                title = "Desactivar oficina";
                msg = "¿Está seguro que desea desactivar esta oficina?";
                classBtn = "btn-warning";
                labelBtn = "Si, desactivar";
            } else if (accion == "eliminar") {
                title = "Eliminar oficina";
                msg = "¿Está seguro que desea eliminar esta oficina?";
                classBtn = "btn-danger";
                labelBtn = "Si, eliminar";
            }

            $vue.modalBootbox = bootbox.confirm({
                title: title,
                size: 'small',
                message: msg,
                buttons: {
                    confirm: {label: labelBtn, className: classBtn},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $vue.saveEstado(item, accion);
                    } else {
                        $vue.modalBootbox.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado(item, accion) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/cambiarEstado/' + accion),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify(item),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptorOficinas.loadRemoteData();
                        $vue.modalBootbox.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        verColaboradores(item) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/allColaborador'),
                type: 'POST',
                async: false,
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.cfgColaboradores.title = "Colaboradores: " + item.nombre;
                        $vue.colaboradores = response.data;
                        $vue.$refs.colaboradoresModal.open();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verAsignarJefe(item) {
            let $vue = this;
            if (item.cargoJefe.nombre == '') {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            $vue.oficina = Object.assign({}, item, {});
            $vue.oficina.esNuevo = true;

            $vue.configModalAsignarJefe.title = 'Asignar Jefe de Unidad';
            $vue.configModalAsignarJefe.okbtn = 'Asignar Jefe';
            $vue.configModalAsignarJefe.okaction = $vue.asignarJefe;
            $vue.$refs.modalAsignarJefe.open();
        },
        asignarJefe() {
            let $vue = this;
            $vue.procesarJefe("asignarJefe");
        },
        verRevisarJefe(item) {
            let $vue = this;
            if (item.cargoJefe.nombre == '') {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            $vue.oficina = Object.assign({}, item, {});
            $vue.oficina.esNuevo = false;
            $vue.configModalAsignarJefe.title = 'Actualizar información Jefe de Unidad';
            $vue.configModalAsignarJefe.okbtn = 'Actualizar Jefe';
            $vue.$refs.modalAsignarJefe.open();
            $vue.$refs.modalAsignarJefe.okaction = $vue.actualizaJefe;
        },
        actualizaJefe() {
            let $vue = this;
            $vue.procesarJefe("actualizarJefe");
        },
        procesarJefe(ruta) {
            let $vue = this;
            let form = $("#" + $vue.configModalAsignarJefe.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalAsignarJefe.beginProcessing();
            axios.post(APP.url(rutaModulo + '/' + ruta), $vue.oficina)
                    .then(response => {
                        $vue.$refs.modalAsignarJefe.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.$refs.raptorOficinas.loadRemoteData();
                            $vue.$refs.modalAsignarJefe.close();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        $vue.$refs.modalAsignarJefe.confirmReaction(false);
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        verAsignarEncargado(item) {
            let $vue = this;
            if (item.cargoJefe.nombre == '') {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            $vue.oficina = Object.assign({}, item, {});
            $vue.oficina.esNuevo = true;
            $vue.configModalAsignarEncargado.title = 'Asignar Jefe Encargado';
            $vue.configModalAsignarEncargado.okbtn = 'Asignar Encargado';
            $vue.configModalAsignarEncargado.okaction = $vue.asignarEncargado;
            $vue.$refs.modalAsignarEncargado.open();
        },
        asignarEncargado() {
            let $vue = this;
            $vue.procesarEncargado("asignarEncargado");
        },
        procesarEncargado(ruta) {
            let $vue = this;
            let form = $("#" + $vue.configModalAsignarEncargado.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalAsignarEncargado.beginProcessing();
            axios.post(APP.url(rutaModulo + '/' + ruta), $vue.oficina)
                    .then(response => {
                        $vue.$refs.modalAsignarEncargado.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.$refs.raptorOficinas.loadRemoteData();
                            $vue.$refs.modalAsignarEncargado.close();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        $vue.$refs.modalAsignarEncargado.confirmReaction(false);
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        verRevisarEncargado(item) {
            let $vue = this;
            if (item.cargoJefe.nombre == '') {
                bootbox.alert({
                    message: 'Falta definir el cargo de la jefatura de esta Unidad',
                    buttons: {
                        ok: {label: 'Cerrar', className: "btn-warning"}
                    }
                });
                return;
            }

            $vue.oficina = Object.assign({}, item, {});
            $vue.oficina.esNuevo = false;
            $vue.configModalAsignarEncargado.title = 'Actualizar información Jefe Encargado';
            $vue.configModalAsignarEncargado.okbtn = 'Actualizar Jefe Encargado';
            $vue.configModalAsignarEncargado.okaction = $vue.actualizaEncargado;
            $vue.$refs.modalAsignarEncargado.open();
        },
        actualizaEncargado() {
            let $vue = this;
            $vue.procesarEncargado("actualizarEncargado");
        },
        previoRetirarEncargado(item) {
            let $vue = this;
            $vue.oficina = Object.assign({}, item, {});
            $vue.configConfirmAction.message = "¿Está seguro desea dar por finalizada la <b>Encargatura</b> de esta Unidad?";
            $vue.configConfirmAction.okbtn = "Si, finalizar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = $vue.verRetirarEncargado;
            $vue.$refs.modalConfirmAction.open();

        },
        verRetirarEncargado() {
            let $vue = this;
            $vue.$refs.modalConfirmAction.confirmReaction(true);
            $vue.ausencia = Object.assign({}, $vue.oficina.ausenciaJefe, {});
            $vue.configModalRetirarEncargado.okclass = "btn-danger";
            $vue.$refs.modalRetirarEncargado.open();
        },
        retirarEncargado() {
            let $vue = this;
            let form = $("#" + $vue.configModalRetirarEncargado.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalRetirarEncargado.beginProcessing();
            axios.post(APP.url(rutaModulo + '/retirarEncargado'), $vue.ausencia)
                    .then(response => {
                        $vue.$refs.modalRetirarEncargado.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.$refs.raptorOficinas.loadRemoteData();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        $vue.$refs.modalRetirarEncargado.confirmReaction(false);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        previoRetirarJefe(item) {
            let $vue = this;
            $vue.oficina = Object.assign({}, item, {});
            $vue.configConfirmAction.message = "¿Está seguro desea dar por finalizada la <b>Jefatura</b> de esta Unidad?";
            $vue.configConfirmAction.okbtn = "Si, finalizar";
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = $vue.verRetirarJefe;
            $vue.$refs.modalConfirmAction.open();
        },
        verRetirarJefe() {
            let $vue = this;
            $vue.$refs.modalConfirmAction.confirmReaction(true);
            $vue.ausencia = Object.assign({}, $vue.oficina.ausenciaJefe, {});
            $vue.configModalRetirarJefe.okclass = "btn-danger";
            $vue.$refs.modalRetirarJefe.open();
        },
        retirarJefe() {
            let $vue = this;
            let form = $("#" + $vue.configModalRetirarJefe.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.$refs.modalRetirarJefe.beginProcessing();
            axios.post(APP.url(rutaModulo + '/retirarJefe'), $vue.oficina)
                    .then(response => {
                        $vue.$refs.modalRetirarJefe.confirmReaction(response.data.success);
                        if (response.data.success) {
                            $vue.$refs.raptorOficinas.loadRemoteData();
                            notify(response.data.message, "info");
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        $vue.$refs.modalRetirarJefe.confirmReaction(false);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        allPersonas(nombre) {
            let $vue = this;
            axios.get(APP.url(rutaModulo + '/allPersona'), {params: {nombre: nombre}})
                    .then(response => {
                        if (response.data.success) {
                            $vue.personas = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        }
    }
});
    