Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#bolsaTrabajoVue',
    data: {
        cicloAcademico: JSON.parse(cicloacademicoJson),
        citasAlumnoURL: APP.url('tramite/bolsatrabajo/list'),
        verTramiteModal: {
            id: 'verTramiteModal',
            header: true,
            title: 'Bolsa Trabajo',
            showaccept: true
        },
        tramiteSubvencion: {},
        persona: {},
        horas: [{value: 40}, {value: 20}],
        solicitud: {},
        rechazado: true
    },
    computed: {

    },
    created() {
        let $vue = this;

    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        verTramite(item) {
            var $vue = this;
            $vue.tramiteSubvencion = item;
            $vue.persona = item.tramite.alumno.persona;

            $vue.$refs.verTramiteModal.open();

        }, valor(horas) {
            return horas.value;
        },
        saveRespuesta() {
            var $vue = this;
            $vue.tramiteSubvencion.horas = $vue.tramiteSubvencion.horas.value;
            if (this.rechazado) {
                $vue.tramiteSubvencion.tramite.estado = "VBS";
            } else {
                $vue.tramiteSubvencion.tramite.estado = "RCHS";
            }
            $.ajax({
                url: APP.url("tramite/bolsatrabajo/save"),
                contentType: "application/json",
                type: 'post',
                data: JSON.stringify($vue.tramiteSubvencion)
            }).then(response => {
                notify(response.message, "success");
                $vue.$refs.verTramiteModal.close();
                $vue.$refs.load.loadRemoteData();
            });
        }
    }
});
