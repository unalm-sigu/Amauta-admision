var app = new Vue({
    el: '#pageGpoSeccion',
    data: {
        grupoSeccion: {},
        secciones: [],
        colorEstado: {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"}
    }, methods: {
        addSeccion: function () {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/addSeccion'),
                data: {
                    grupoSeccion: this.grupoSeccion.id
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.loadSecciones();
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        deleteSeccion: function (seccion) {
            let $vue = this;
            bootbox.confirm({
                message: "¿Está seguro que desea elimar la seccón?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/gposeccion/deleteSeccion'),
                            data: {
                                seccion: seccion.seccionId
                            },
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.loadSecciones();
                                    MODAL.hideWait();
                                } else {
                                    notify(response.message, "error");
                                    MODAL.hideWait();
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                                MODAL.hideWait();
                            }
                        });
                    }
                }
            });

        },
        getEstadoClass: function (estadoCode) {
            return "label-" + this.colorEstado[estadoCode];
        }, loadSecciones: function () {
            let $vue = this;
            this.grupoSeccion = JSON.parse(gpoSeccionJson);
            $.ajax({
                method: 'POST',
                url: APP.url('academico/gposeccion/' + this.grupoSeccion.id + '/findSecciones'),
                success: function (response) {
                    if (response.success) {
                        $vue.secciones = response.data;
                    }
                }
            });
        }
    }, created: function () {
        this.loadSecciones();
    }
})