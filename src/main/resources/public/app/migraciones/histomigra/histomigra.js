new Vue({
    el: '#histoVUE',
    data: {
        histoURL: APP.url(rutaModulo + "/list/" + JSON.parse(alumnoJson).id),
        pagination: {'total-items': 0, 'items-per-page': 500, 'max-size': 3, 'boundary-link-numbers': true},
        alumno: JSON.parse(alumnoJson),
        historias: []
    },
    mounted: function () {
        let $vue = this;
        //$vue.loadData();
    },
    methods: {
        loadData() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(rutaModulo + "/list/" + $vue.alumno.id),
                success(response) {
                    if (response.success) {
                        $vue.historias = response.data;
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

