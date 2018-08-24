var app = new Vue({
    el: '#reunionConsejo',
    data: {
        URL_REUNIONES: APP.url('academico/reunionconsejo/listReunionesConsejo'),
        reunionConsejo: null,
        btnActive: 'lista',
        onlyOne: true,
        reunionConsejoModal: {
            id: 'modalReunionConsejo',
            header: true,
            title: 'Reunion Consejo',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        }
    }, created: function () {

    }, mounted: function () {
        let $vue = this;
        $vue.renderEventos();
    }, watch: {
        btnActive: function (after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.fullcalendar.render();
                vue.onlyOne = false;
            }
        }
    }, methods: {
        saveReunionConsejo: function (event) {
            event.preventDefault();
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/reunionconsejo/saveReunionConsejo'),
                            data: JSON.stringify($vue.reunionConsejo),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            async: true,
                            success: function (response) {
                                if (response.success) {
                                    $vue.renderEventos();
                                    notify(response.message, "info");
                                    MODAL.hideWait();
                                    $vue.$refs.tblReunionesConsejo.loadRemoteData();
                                    $vue.$refs.modalReunionConsejo.close();
                                } else {
                                    notify(response.message, "error");
                                    MODAL.hideWait();
                                    $vue.$refs.modalReunionConsejo.close();
                                }
                            },
                            error: function (response) {
                                MODAL.hideWait();
                                $vue.$refs.modalReunionConsejo.close();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        },
        btnActive: function (after, before) {
            var vue = this;
            if (after == 'calendar' && vue.onlyOne) {
                vue.$refs.fullcalendar.render();
                vue.onlyOne = false;
            }
        },
        eventClick: function (self, date, jsEvent, view) {
        },
        dayClick: function (self, date, jsEvent, view) {
        },
        dayDbClick: function (self, date, element) {
            var $vue = this;

            //var dia = date.format("DD/MM/YYYY HH:mm:ss");
            var fecha = date.format("DD/MM/YYYY");


            $.ajax({
                method: 'POST',
                url: APP.url('academico/reunionconsejo/loadModalReunionConsejo'),
                data: {
                    fechaReunion: fecha
                },
                success: function (response) {
                    if (response.success) {
                        $vue.reunionConsejo = response.data.reunionConsejo;
                        $vue.reunionConsejo.fecha = fecha;
                    }
                }
            });
            this.$refs.modalReunionConsejo.open();
        }, renderEventos: function () {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/reunionconsejo/allcalendar'),
                sync: true,
                success: function (response) {
                    if (response.success) {
                        vue.$refs.fullcalendar.addEventSource(response.data);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})