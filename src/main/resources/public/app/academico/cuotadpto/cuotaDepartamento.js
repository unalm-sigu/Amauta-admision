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
        }, redirectGpo(item, tipo) {
            console.dir(item);
            console.log('aaaaaaaaaaaaaaaaa');
            if (tipo == 'TEO') {
                if (item.gruposUtilizadosTeoria == 0) {
                    return;
                }
            } else {
                if (item.gruposUtilizadosPractica == 0) {
                    return;
                }
            }
            // location.href = APP.url(rutaModulo + '/redirectgpo?cuotaGrupoHoras=' + item.id + ",tipo=" + tipo);
            let $vue = this;
            $.ajax({
                method: "GET",
                data: {cuotaGrupoHorasId: item.id, tipo: tipo},
                url: APP.url(rutaModulo + '/redirectgpo')
            }).then(response => {
                if (response.success) {
                    let lista = response.data;
                    let listaEncode = Base64.encode(lista);
                    let first = lista.split(",")[0];
                    location.href = APP.url("academico/gposeccion/" + first + "/editar") + $vue.getOrigenURL() + "&ids=" + listaEncode;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }, getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        }
    }

});