Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#bolsaTrabajoVue',
    data: {
        cicloAcademico: JSON.parse(cicloacademicoJson),
        tramiteAlumnosURL: APP.url('tramite/bolsatrabajo/list'),
        verTramiteModal: {
            id: 'verTramiteModal',
            header: true,
            title: 'Bolsa Trabajo',
            showaccept: true
        },
        tramiteSubvencion: {},
        persona: {},
        horasTrabajo: [{id: 1, horas: 40}, {id: 2, horas: 20}],
        solicitud: {},
        value: {},
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
            console.log(item.horas);
            $vue.horasTrabajo.forEach(function (val) {
                if (val.horas === item.horas) {
                    $vue.value = val;
                }
            });

            $vue.tramiteSubvencion = item;
            $vue.persona = item.tramite.alumno.persona;
            $vue.$refs.verTramiteModal.open();

        },
        saveRespuesta() {
            var $vue = this;
            $vue.tramiteSubvencion.horas = $vue.value.horas;
            if ($vue.rechazado) {
                $vue.tramiteSubvencion.respuesta = "OK";
                $vue.tramiteSubvencion.voboSupervisor = 1;
            } else {
                $vue.tramiteSubvencion.respuesta = "FALLO";
                $vue.tramiteSubvencion.voboSupervisor = 0;
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
