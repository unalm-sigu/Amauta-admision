Vue.component("retiro-curso-component", {
    template: "#retiroCursoComponent",
    props: {
        alumno: {}
    },
    data() {
        return {
            retirosCurso: [],
            total: 0,
            totalContable: 0,
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
                url: APP.url('academico/alumno/retirocurso'),
                data: {id: $vue.alumno.id},
                success: function (response) {
                    if (response.success) {

                        $vue.total = response.total;
                        $vue.totalContable = response.data.totalContable;
                        $vue.retirosCurso = response.data.retirosCurso;

                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        getFechaSmall(fecha) {
            if (fecha) {
                return fecha.substring(0, 10);
            }
            return '';
        }
    }
});