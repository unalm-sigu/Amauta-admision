new Vue({
    el: '#solicitudVue',
    data: {
        solicitudURL: APP.url('tramite/documentoconsejero/list'),
        persona: {},
        solicitud: {},
        tramiteDocumento: {},
        colaborador: {},
    },
    computed: {

    },
    created() {

    },
    mounted: function () {

    },
    methods: {
        classEstado(value) {
            switch (value) {
                case 'ACEP':                
                case 'DEV':
                case 'ENV':
                case 'CRE':
                    return "label label-default";
                    break;
                case 'ANU':
                case 'NPAG':
                    return "label label-danger";
                    break;
                case 'ACT':
                case 'FVAL':
                case 'PIMP':
                case 'COMP':
                case 'VAL_URA':
                    return "label label-primary";
                    break;
                case 'PAG':
                    return "label label-warning";
                case 'REV_HIS':
                case 'CTRL_CALIDAD':
                case 'VB_UR':
                    return "label label-info";
                    break;
                case 'ENT':
                    return "label label-success";
                    break;

            }
        },
        update(tram, accion) {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $vue.tramiteDocumento = tram;
            $vue.tramiteDocumento.estadoTramite = accion.estadoTramiteFinal;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.tramiteDocumento),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
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
        accion(estado, item) {
            console.log(estado);
            if (estado.estadoTramiteFinal.codigo == 'FVAL') {
                this.cargarfoto(item);
            } else {
                this.update(item, estado);
            }
        }
    }
});
