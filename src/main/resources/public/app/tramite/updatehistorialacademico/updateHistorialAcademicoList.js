new Vue({
    el: '#main',
    data: {
        solicitud: {id: null},
        colaborador: {id: null},
        dataEnviarRevision: {
            id: 'modalEnviarRevision',
            header: true,
            title: 'Enviar a revisión',
            okbtn: 'Aceptar'
        },
        solicitudActiva: {},
        dataCargarFoto: {
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        },
        rutaFotoTemporal: null
    },
    mounted() {
        let vue = this;
        $global.$on("eliminar", function (id) {
            vue.eliminar(id);
        });
        $global.$on("cancelar", function (id) {
            vue.cancelar(id);
        });
        $global.$on("enviarrevision", function (solicitud) {
            vue.enviarrevision(solicitud);
        });
        $global.$on("cargarfoto", function (solicitud) {
            vue.cargarfoto(solicitud);
        });
    },
    methods: {
        eliminar: function (id) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar la solicitud de constancia?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Salir', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/solicitudconstancia/updatehistorial/delete'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        cancelar: function (id) {
            var vue = this;
            console.log(id);
            bootbox.confirm({
                message: '¿Seguro que desea cancelar la solicitud de constancia?',
                buttons: {
                    confirm: {label: 'Si, Cancelar', className: "btn-danger"},
                    cancel: {label: 'Salir', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/solicitudconstancia/updatehistorial/cancelar'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        imprimirr: function (solicitud, el) {

            let vue = this;
            let self = $(el.currentTarget);
            self.find('i').removeClass('fa-print').addClass('fa-spinner fa-spin');
            self.prop("disabled", true);
            let urll = APP.url('tramite/solicitudconstancia/updatehistorial/imprimir');

            $.fileDownload(urll, {
                httpMethod: "POST",
                data: {id: solicitud.id}
            }).done(function () {
                setTimeout(function () {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-print');
                    self.removeProp("disabled");
                }, 2000);
            }).fail(function () {
                setTimeout(function () {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-print');
                    self.removeProp("disabled");
                }, 2000);
                notify(MESSAGES.errorComunicacion, "error");
            });

        },
        selectColaborador: function (vm) {
            return {
                allowClear: true,
                placeholder: "Seleccione un colaborador",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/searchcolaborador"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vm.colaborador.id != null) {
                        callback(vm.colaborador);
                    }
                },
                formatResult: function (info) {
                    var colSearch = new ColaboradorSearch();
                    colSearch.colaborador = info;
                    var cmp = colSearch.$mount();
                    return cmp.$el;
                },
                formatSelection: function (info) {
                    return info.codigo + " - " + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        enviarrevision: function (solicitud) {
            let vue = this;
            vue.solicitudActiva = solicitud;
            vue.$refs.enviarRevision.open();
            $('[name="colaborador.id"]').select2(vue.selectColaborador(vue));
        },
        createEnviarRevision: function () {
            var vue = this;
            var valid = $('#formEnviarRevision').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/revision'),
                data: $('#formEnviarRevision').serialize(),
                success: function (response) {
                    if (response.success) {
                        dynatable.reload();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cargarfoto: function (solicitud) {
            var vue = this;
            vue.solicitudActiva = solicitud;
            vue.dataCargarFoto.title = 'Cargar fotografía para ' + solicitud.nombre;
            vue.$refs.cargarFoto.open();
        },
        createCargarFoto: function (solicitud) {
            var vue = this;
            
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/updatehistorial/onlyfoto'),
                data: $('#formCargarFoto').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.cargarFoto.close();
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
    },
});