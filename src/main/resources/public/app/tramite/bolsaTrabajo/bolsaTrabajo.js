Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#bolsaTrabajoVue',
    data: {
        ciclo: JSON.parse(cicloJson),
        tramiteAlumnosURL: APP.url('tramite/bolsatrabajo/list'),
        verTramiteModal: VUE_MODAL.structFormAjax({}),
        tramiteSubvencion: {},
        persona: {},
        horasTrabajo: [{id: 1, horas: 40}, {id: 2, horas: 20}],
        solicitud: {},
        value: {},
        serSupervisor: "AAA",
        rechazado: true,
        aceptado: false
    },
    computed: {

    },
    created() {
        let $vue = this;

    },
    mounted: function () {
        let $vue = this;
        $vue.configModal();
    },
    methods: {
        configModal() {
            this.verTramiteModal = VUE_MODAL.structFormAjax({
                id: 'id-modal-aceptar-supervision',
                okbtn: "Enviar respuesta",
                header: true,
                title: 'Bolsa de trabajo',
                form: "id-form-bolsa-trabajo",
                showaccept: false
            });
        },
        verTramite(item) {
            var $vue = this;
            console.log(item.horas);
            $vue.horasTrabajo.forEach(function (val) {
                if (val.horas === item.horas) {
                    $vue.value = val;
                }
            });

            $vue.tramiteSubvencion = JSON.parse(JSON.stringify(item));
            $vue.persona = JSON.parse(JSON.stringify(item.tramite.alumno.persona));

            $vue.configModal();
            $vue.$refs.verTramiteModal.open();

            if (item.tramite.estado === 'SUPERV_ASIGN') {
                $vue.serSupervisor = "AAA";
            }
        },
        changeAceptar() {
            this.verTramiteModal.showaccept = true;
        },
        saveRespuesta() {
            var $vue = this;
            $vue.tramiteSubvencion.horas = $vue.value.horas;
            if ($vue.serSupervisor === 'SI') {
                $vue.tramiteSubvencion.respuesta = "OK";
                $vue.tramiteSubvencion.voboSupervisor = 1;
            } else if ($vue.serSupervisor === 'NO') {
                $vue.tramiteSubvencion.respuesta = "FALLO";
                $vue.tramiteSubvencion.voboSupervisor = 0;
            } else {
                notify("Debe dar una respuesta correcta", "error");
                return;
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
