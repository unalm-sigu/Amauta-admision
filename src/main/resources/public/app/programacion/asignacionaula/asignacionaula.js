var app = new Vue({
    el: '#main',
    data: {
        URL: APP.url('academico/asignacionaula'),
        processing: false,
        asignacionAula: null
    }, created: function () {
        if (jAsignacionAula != null && jAsignacionAula != '') {
            this.asignacionAula = JSON.parse(jAsignacionAula);
        }

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, procesarAsignacionAulas() {
            let vue = this;
            MODAL.showWait("Espere un momento por favor");
            if (vue.asignacionAula == null) {
                vue.asignacionAula = {id: ""};
            }
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la asignación de aulas?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        AXIOS.post(`${vue.URL}/procesarAsignacionAulas`, vue.asignacionAula)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.asignacionAula = response.data.data;
                                        MODAL.hideWait();
                                    } else {
                                        notify(response.data.message, 'error');
                                        MODAL.hideWait();
                                    }
                                });
                    } else {
                        MODAL.hideWait();
                    }
                }
            });

        }, eliminarAsignacion() {
            let vue = this;
            MODAL.showWait("Espere un momento por favor");

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la asignación de aulas?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        AXIOS.post(`${vue.URL}/aliminarAsignacion`, vue.asignacionAula)
                                .then(response => {
                                    if (response.data.success) {
                                        vue.asignacionAula = null;
                                        //  vue.asignacionAula = response.data.data;
                                        // vue.loadAsignacionAula();
                                        MODAL.hideWait();
                                    } else {
                                        notify(response.data.message, 'error');
                                        MODAL.hideWait();
                                    }
                                });
                    } else {
                        MODAL.hideWait();
                    }
                }
            });


        }, loadAsignacionAula() {
            let vue = this;
            AXIOS.post(`${this.URL}/loadAsignacionAula`, vue.asignacionAula)
                    .then(response => {
                        if (response.data.success) {
                            vue.asignacionAula = response.data.data;
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        },
        editarGpoSecciones(item) {
            console.dir(item);
            let $vue = this;
            let lista = item.idsGpoSecciones;
            if (lista == "") {
                return;
            }
            console.dir(lista);
            let listaEncode = Base64.encode(lista);
            let first = lista.split(",")[0];
            location.href = APP.url("academico/gposeccion/" + first + "/editar") + $vue.getOrigenURL() + "&ids=" + listaEncode;
        },
        getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        }
    }
})
