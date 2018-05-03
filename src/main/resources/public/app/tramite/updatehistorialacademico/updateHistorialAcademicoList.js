new Vue({
    el: '#main',
    data: {
        solicitud: {id: null},
    },
    mounted() {
        let vue = this;
        $global.$on("eliminar", function(id) {
            vue.eliminar(id);
        });
        $global.$on("cancelar", function(id) {
            vue.cancelar(id);
        });
    },
    methods: {
        eliminar: function(id) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar la solicitud de constancia?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Salir', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/solicitudconstancia/updatehistorial/delete'),
                            data: {id: id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        cancelar: function(id) {
            var vue = this;
            console.log(id);
            bootbox.confirm({
                message: '¿Seguro que desea cancelar la solicitud de constancia?',
                buttons: {
                    confirm: {label: 'Si, Cancelar', className: "btn-danger"},
                    cancel: {label: 'Salir', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/solicitudconstancia/updatehistorial/cancelar'),
                            data: {id: id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        imprimir: function() {
            let vue = this;
            let urll = APP.url('tramite/solicitudconstancia/updatehistorial/imprimir');
            vue.showSpinner = true;

            $.fileDownload(urll, {
                httpMethod: "POST",
                data: {id: vue.rowActive}
            }).done(function() {
                setTimeout(function() {
                    vue.showSpinner = false;
                }, 2000);
            }).fail(function() {
                setTimeout(function() {
                    vue.showSpinner = false;
                }, 2000);
                notify(MESSAGES.errorComunicacion, "error");
            });

        },
        imprimirr: function(solicitud, el) {

            let vue = this;
            let self = $(el.currentTarget);
            self.find('i').removeClass('fa-print').addClass('fa-spinner fa-spin');
            self.prop("disabled", true);
            let urll = APP.url('tramite/solicitudconstancia/updatehistorial/imprimir');

            $.fileDownload(urll, {
                httpMethod: "POST",
                data: {id: solicitud.id}
            }).done(function() {
                setTimeout(function() {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-print');
                    self.removeProp("disabled");
                }, 2000);
            }).fail(function() {
                setTimeout(function() {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-print');
                    self.removeProp("disabled");
                }, 2000);
                notify(MESSAGES.errorComunicacion, "error");
            });

        }
    },
});