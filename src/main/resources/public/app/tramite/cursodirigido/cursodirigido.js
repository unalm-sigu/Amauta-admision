Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#main',
    data: {
        cursoDirigidoURL: APP.url("academico/cursodirigido/list")
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
        json(item) {
            if (item.situacionActual.cruceHorario != null) {
                return;
            }
            item.situacionActual = JSON.parse(item.situacionActual);
        },
        actualizar(item, accion) {
            let $vue = this;
            item.situacionActual = JSON.stringify(item.situacionActual);
            item.accionTramiteAcademicos = [];
            item.accionTramiteAcademicos.push(accion);
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/cursodirigido/update'),
                data: JSON.stringify(item),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
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
