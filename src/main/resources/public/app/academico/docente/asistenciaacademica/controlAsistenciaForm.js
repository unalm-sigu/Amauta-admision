var app = new Vue({
    el: '#controlAsistenciaApp',
    data: {
        URL_MATRICULAS_SECCION: APP.url(rutaModulo + "/listMatriculasSeccionDyna"),
        seccion: null,
        matriculasSeccion: null,
        temaLeccion: null,
        btnSaveDisabled: false,
        btnSaveTexto: 'Guardar Asistencia'
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
            $vue.btnSaveDisabled = true;
            $vue.btnSaveTexto = '<i class="fa fa-spinner fa-spin fa-fw"></i> Espere por favor...';

            $vue.seccion.matriculaSeccion = $vue.$refs.tblMatriculasSeccion.data;
            $vue.temaLeccion.seccion = $vue.seccion;
            $.ajax({
                url: APP.url(rutaModulo + '/saveAsistencia'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                data: JSON.stringify($vue.temaLeccion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.temaLeccion.id = response.data.id;
                        $vue.$refs.tblMatriculasSeccion.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                        $vue.$refs.tblMatriculasSeccion.loadRemoteData();
                    }
                    $vue.btnSaveDisabled = false;
                    $vue.btnSaveTexto = 'Guardar Asistencia';
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        selectAllHour(horSeccion) {
            let $vue = this;
            let matriculasSeccion = $vue.$refs.tblMatriculasSeccion.data;
            $(matriculasSeccion).each(function () {
                $(this.seccion.horarioSeccion).each(function () {
                    if (this.hora.id == horSeccion.hora.id) {
                        this.seleccionado = horSeccion.seleccionado;
                    }
                });
            });
        }
    }
})