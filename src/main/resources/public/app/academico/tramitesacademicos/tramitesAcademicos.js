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
        reunionConsejoSel: null
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
        }
    }
})