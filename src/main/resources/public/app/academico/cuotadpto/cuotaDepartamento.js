Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cuotadptoVUE',
    data: {
        cuotadptoURL: APP.url(rutaModulo + '/list'),
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        grupoHoras: {},
        grupos: []
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.loadGrupos();
    },
    methods: {
        changeAnexoMain() {
            let $vue = this;
            $vue.$refs.raptorCuotaGpoHoras.querie = [];
            $vue.$refs.raptorCuotaGpoHoras.ajaxdata = {grupoHoras: $vue.grupoHoras.id};
            $vue.$refs.raptorCuotaGpoHoras.loadRemoteData();
        },
        loadGrupos() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url(rutaModulo + "/allGrupos")
            }).then(response => {
                if (response.success) {
                    $vue.grupos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }
    }

});