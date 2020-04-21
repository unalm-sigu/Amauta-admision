new Vue({
    el: '#alumnosDocenteVUE',
    data: {
        matriculados: [],
        seccion: JSON.parse(seccionJson),
    },
    mounted: function () {
        let $vue = this;
        $vue.loadMatriculados();
    },
    methods: {
        loadMatriculados() {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/' + $vue.seccion.id + '/list'),
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        $vue.matriculados = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }
});


