Vue.component("cancelar-seccion-component", {
    template: "#cancelarSeccionComp",
    matriculasSeccion: [],
    props: {
        seccion: {type: Object, default: {}, required: false}
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent();
    },
    methods: {
        loadComponent() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/loadCancelarSeccionComp'),
                type: 'POST',
                data: {seccion: $vue.seccion.id},
                success(response) {
                    if (response.success) {
                        console.dir(response.data);
                        $vue.matriculasSeccion = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});