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
    }
});