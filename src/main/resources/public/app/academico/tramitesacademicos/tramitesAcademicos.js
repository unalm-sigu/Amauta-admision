var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/listTramites'),
        URL_REUNIONES: APP.url('academico/tramiteacademico/listReunionesConsejo'),
        agendarModal: {
            id: 'modalAgendar',
            header: true,
            title: 'Agendar',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        tramiteSeleccionado: null,
        reunionConsejoSel: null,
        processing: false
    }, created: function () {

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, controlarSeccion(seccion, e) {
            e.preventDefault();
            location.href = APP.url('academico/docente/asistenciaacademica/' + seccion.id + '/lecciones');
        }, cambiarEstadoReincorporacion: function (tramite, estadoDestino, event) {
            event.preventDefault();
            let $vue = this;
            console.log("cambiarEstadoReincorporacion");
            console.dir(tramite);
            $.ajax({
                url: APP.url('academico/tramiteacademico/cambiarEstadoReincorporacion'),
                type: 'POST',
                async: false,
                data: {
                    tramite: tramite.id,
                    estado: estadoDestino
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(response.message, "error");
                }
            });
        }, agenda() {
            location.href = APP.url("academico/tramiteacademico/agendareuniones");
        }, loadModalAgendar(tramite) {
            let $vue = this;
            $vue.reunionConsejoSel = null;
            $.ajax({
                url: APP.url('academico/tramiteacademico/loadModalAgendar'),
                data: JSON.stringify(tramite),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                //    async: true,
                success: function (response) {
                    if (response.success) {
                        $vue.tramiteSeleccionado = tramite;
                    }
                }
            });
            $vue.$refs.modalAgendar.open();
        }, seleccionarReunionConsejo(reunionConsejo, event) {
            //  $("[alt='selReunionConsejo']").addClass('btn-default').removeClass('btn-success');
            //   let target = $(event.target);
            //     $(target).addClass('btn-success').removeClass('btn-default');
            this.reunionConsejoSel = reunionConsejo;
        }, saveAgendar() {
            console.log("agendara");
            let $vue = this;
            if ($vue.reunionConsejoSel == null) {
                notify("Seleccione la reunión consejo.", "error");
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/saveAgendar'),
                data: {
                    tramite: $vue.tramiteSeleccionado.id,
                    reunionConsejo: $vue.reunionConsejoSel.id,
                },
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function (response) {
                    notify(response.message, "error");
                }
            });
            $vue.$refs.modalAgendar.close();
        }, revertirEstadoTramite(tramite, event) {
            console.log("agendara");
            let $vue = this;
            event.preventDefault();
            bootbox.confirm({
                message: "¿Está seguro que desea revertir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result && !$vue.processing) {
                        MODAL.showWait("Espere un momento por favor");

                        $vue.processing = true;
                        $.ajax({
                            url: APP.url('academico/tramiteacademico/revertirEstadoTramite'),
                            data: JSON.stringify(tramite),
                            dataType: "json",
                            contentType: "application/json",
                            type: 'POST',
                            //    async: true,
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                                    $vue.tramiteSeleccionado = null;
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                                MODAL.hideWait();
                                $vue.$refs.modalAgendar.close();
                            }, error: function () {
                                notify(response.message, "error");
                                MODAL.hideWait();
                                $vue.$refs.modalAgendar.close();
                            }
                        });
                    }
                    $vue.processing = false;

                }
            });

        }
    }
})