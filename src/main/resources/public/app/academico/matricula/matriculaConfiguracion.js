new Vue({
    el: '#infoAcademico',
    data: {
        evento: [{id: 1, value: 'Matricula Regular'},
            {id: 2, value: 'Matricula verano'},
            {id: 3, value: 'Reinscripción'}],
        tipo: [{id: 1, value: 'Por barrido'},
            {id: 2, value: 'En línea'}]

    },
    created() {

    },
    mounted: function () {
        let $vue = this;
        $vue.alumnoCursoTemp = $vue.alumnoCurso;
        console.log("tipo::: " + $vue.typeSearch)
    },
    methods: {

        carga() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/historial'),
                contentType: "application/json",
                success: function (response) {
                    $vue.alumnoCurso = response.data;

                    var i = 1;
                    $vue.alumnoCurso.forEach(function (element) {
                        var obj = {id: 1, value: element.descripción};
                        $vue.listCiclos.push(obj);
                        i++;
                    })
                    console.log($vue.listCiclos);
                }
            });
        },

    }

})