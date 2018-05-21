var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/listTramites')
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
        }
    }
})