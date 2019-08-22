Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#alumnosVUE',
    data: {
        alumnosURL: APP.url('academico/alumno/listCursosHabiles/'),
        alumno: JSON.parse(alumnoJson),
        cursoscurriculas: JSON.parse(cursosElectivosJson),
        cursocurricula: {}
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
        customlabel( { curso }){
            if (curso == null) {
                return;
            }
            return `${curso.nombre}`;

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
        }, agregar() {
            let $vue = this;
            if ($vue.cursocurricula.id == null) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/agregarElectivo/' + $vue.alumno.id),
                contentType: "application/json",
                data: JSON.stringify($vue.cursocurricula),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.url = APP.url('academico/alumno/listCursosHabiles/' + $vue.alumno.id);
                        $vue.$refs.load.repreload();
                        notify(response.message, "success");
                    }
                }
            });
        }
    }
});

