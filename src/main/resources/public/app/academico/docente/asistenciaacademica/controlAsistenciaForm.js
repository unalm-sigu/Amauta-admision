var app = new Vue({
    el: '#controlAsistenciaApp',
    data: {
        URL_MATRICULAS_SECCION: APP.url("academico/docente/asistenciaacademica/listMatriculasSeccionDyna"),
        seccion: null,
        matriculasSeccion: null,
        temaLeccion: null
    }, created: function () {
        this.seccion = JSON.parse(seccionJson);
        this.temaLeccion = JSON.parse(temaLeccionJson);
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        saveAsistencia(event) {
            event.preventDefault();

            var form = $("[id='frmAsistencia']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            $vue.seccion.matriculaSeccion = $vue.$refs.tblMatriculasSeccion.data;
            $vue.temaLeccion.seccion = $vue.seccion;
            $.ajax({
                url: APP.url('academico/docente/asistenciaacademica/saveAsistencia'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.temaLeccion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblMatriculasSeccion.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                        $vue.$refs.tblMatriculasSeccion.loadRemoteData();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        selectAllHour(horSeccion) {
            let $vue = this;
            console.dir(horSeccion);
            let matriculasSeccion = $vue.$refs.tblMatriculasSeccion.data;
            $(matriculasSeccion).each(function () {

                $(this.seccion.horarioSeccion).each(function () {
                    if (this.hora.id == horSeccion.hora.id) {
                        console.dir(this);
                        this.seleccionado = horSeccion.seleccionado;
                    }
                });
            });
        }
    }
})