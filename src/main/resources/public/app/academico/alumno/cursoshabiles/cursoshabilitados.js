new Vue({
    el: '#alumnosVUE',
    data: {
        alumnosURL: APP.url('academico/alumno/listCursosHabiles/'),
        alumno: JSON.parse(alumnoJson)
    },
    mounted: function () {
        let $vue = this;
        $vue.$refs.load.url = APP.url('academico/alumno/listCursosHabiles/' + $vue.alumno.id);
        $vue.$refs.load.repreload();

    },
    methods: {
        habilitar(item) {
            let $vue = this;
            var data = {id: item.id};
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/habilitar'),
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success")
                    }
                }
            });
        },
        noCumpleRequisitos(item) {
            let $vue = this;
            var data = {id: item.id};
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/noCumpleRequisito'),
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success")
                    }
                }
            });
        }
    }
});

