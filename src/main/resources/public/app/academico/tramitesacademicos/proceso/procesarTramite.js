var app = new Vue({
    el: '#procesarTramitesAcademicos',
    data: {
        tramite: null,
        componentForm: null,
        compomentProps: null,
        accionSeleccionada: null
    }, created: function () {
        this.tramite = JSON.parse(tramiteJson);
        console.dir(this.tramite);
        this.loadFormProcesarTramite();
    }, mounted: function () {


    }, methods: {
        loadFormProcesarTramite() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/' + $vue.tramite.id + '/loadFormProcesar'),
                success: function (response) {
                    if (response.success) {
                        $vue.tramite = response.data.tramite;
                        $vue.componentForm = $vue.tramite.formularioEstadoTramite.formulario;
                        $vue.compomentProps = {alumno: $vue.tramite.alumno};
                    }
                }
            });
        }, procesarTramite(accion) {
            let $vue = this;
            $vue.accionSeleccionada = accion;
            if ($vue.accionSeleccionada.esSolicitarMotivo) {
                bootbox.prompt("Cual es motivo", function (result) {
                    if (result) {
                        $vue.procesarTramitePost(result, $vue);
                    }
                });
            } else {
                $vue.procesarTramitePost(null, $vue);
            }
        }, procesarTramitePost(motivo, $vue) {
            //  let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/procesarTramite'),
                data: {
                    tramite: $vue.tramite.id,
                    accionTramite: $vue.accionSeleccionada.id,
                    motivo: motivo
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        location.href = APP.url('academico/tramiteacademico/successProcess');
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})