Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#procesarTramitesAcademicos',
    data: {

        componentForm: null,
        compomentProps: null,
        accionSeleccionada: null,
        processingAjaxData: null,
        tramite: JSON.parse(tramiteJson)
//        oficina: JSON.parse(oficinasJson)
    }, created: function () {
        console.log("created");
//        this.tramite = JSON.parse(tramiteJson);
        console.dir(this.tramite);
    }, mounted: function () {
        this.loadFormProcesarTramite();


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
                        console.log("loadFormProcesarTramite");
                        console.dir($vue.componentForm);
                        //    var returnParams = Object.assign(params, $vue.ajaxdata);
                        console.log("compomentProps");
                        $vue.compomentProps = {alumno: $vue.tramite.alumno, tramite: $vue.tramite};
                        console.log($vue.compomentProps);
                    }
                }
            });
        },
        procesarTramite(accion) {
            let $vue = this;
            $vue.accionSeleccionada = accion;


            $vue.processingAjaxData = {
                tramite: $vue.tramite.id,
                accionTramite: $vue.tramite.tipoTramite.codigo == 'CONS' ? null : $vue.accionSeleccionada.id,
                accionTramiteDoc: $vue.tramite.tipoTramite.codigo != 'CONS' ? null : $vue.accionSeleccionada.id
            }

            if ($vue.accionSeleccionada.estadoTramiteFinal.esAgendadoConsejoFacultad) {
                if ($vue.$refs.procesarComponent.reunionConsejoSel == null) {
                    notify("Seleccione la reunión consejo.", "error");
                    return;
                }
                //  $vue.processingAjaxData.tramiteReunionConsejo={};
                $vue.processingAjaxData.reunionConsejo = $vue.$refs.procesarComponent.reunionConsejoSel.id;
            }

            if ($vue.accionSeleccionada.esSolicitarMotivo) {
                bootbox.prompt("Cual es motivo", function (result) {
                    if (result) {
                        $vue.processingAjaxData.motivo = result;
                        $vue.procesarTramitePost($vue);
                    }
                });
            } else {
                $vue.procesarTramitePost($vue);
            }
        },
        procesarTramitePost($vue) {

            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/procesarTramite'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.processingAjaxData),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");

                        location.href = APP.url('academico/tramiteacademico/' + $vue.tramite.id + '/successProcess');
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        }
    }
})
