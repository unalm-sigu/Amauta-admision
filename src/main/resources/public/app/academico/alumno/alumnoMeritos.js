Vue.config.devtools = true
Vue.component("alumno-merito-component", {
    template: "#alumnoMeritoComponent",
    props: {
        alumno: {}
    },
    data: function () {
        return {
            ordenesMerdito: {}
        }
    },
    mounted() {
        let vue = this;

    },
    watch: {
        // un getter computado
        alumno: function () {
            // `this` apunta a la instancia de vm
            return this.findMeritos();
        }
    },
    methods: {
        titleTable(titleTable) {
            switch (titleTable) {
                case "CICLO":
                    return "Orden de Merito por Ciclo";
                    break;
                case "FAC":
                    return "Orden de Merito por Facultad";
                    break;
                case "CAR":
                    return "Orden de Merito por Especialidad";
                    break;
            }
        },
        findMeritos() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno/' + $vue.alumno.id + '/dataAlumnoMerito'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.ordenesMerdito = response.data;
                        console.log($vue.ordenesMerdito);
//                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});