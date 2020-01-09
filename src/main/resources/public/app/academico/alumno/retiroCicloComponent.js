Vue.component("retiro-ciclo-component", {
    template: "#retiroCicloComponent",
    props: {
        alumno: {}
    },
    data() {
        return {
            retirosCiclo: [],
            totalRetiros: 0,
            totalCicloContable: 0
        }
    },
    methods: {
        classEnumTramite(item) {
            switch (item) {
                case 'COMP':
                case 'APR':
                case 'ACEP':
                    return "label label-success";
                    break;
                case 'ENV':
                case 'ENV':
                case 'DEV':
                case 'CRE':
                case 'ACT':
                    return "label label-info";
                    break;
                case 'CANC':
                case 'RCHZ':
                case 'NPAG':
                case 'RCHCS':
                case 'ANU':
                    return "label label-danger";
                    break;
                default:
                    return "label label-default";
            }
        },
        obtenerDatos() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/retirociclo'),
                data: {id: $vue.alumno.id},
                success: function (response) {
                    if (response.success) {

                        $vue.totalRetiros = response.data.totalRetiros;
                        $vue.totalCicloContable = response.data.totalCicloContable;
                        $vue.retirosCiclo = response.data.retiroCiclo;

                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        aplicarRetiro(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/aplicarRetiroCiclo'),
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.totalRetiros = response.data.totalRetiros;
                        $vue.totalCicloContable = response.data.totalCicloContable;
                        $vue.retirosCiclo = response.data.retiroCiclo;

                    } else {
                        notify(response.message, "error");
                    }
                    MODAL.hideWait();
                },
                error() {
                    MODAL.hideWait();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }
    }
});