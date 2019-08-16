new Vue({
    el: '#main',
    data: {
        cursos: []
    },
    mounted: function () {
        let $vue = this;
        $vue.loadData();
    },
    methods: {

        loadData() {
            let $vue = this;
            axios.get(rutaModulo + '/list')
                    .then(response => {
                        if (response.data.success) {
                            $vue.cursos.push({
                                cursosModalidad: response.data.data.pregrado,
                                creditos: response.data.data.creditosPregrado
                            });
                            $vue.cursos.push({
                                cursosModalidad: response.data.data.posgrado,
                                creditos: response.data.data.creditosPosgrado
                            });
                        } else {
                            notify(MESSAGES.errorComunicacion, 'error');
                        }
                    });
        },
        tipoSeccion(seccion) {
            if (seccion.tipoSeccionEnum.value.indexOf(" ") < 0) {
                return seccion.tipoSeccionEnum.value;
            }
            return seccion.tipoSeccionEnum.value.split(" ")[0];
        },
        verHorario(text) {
            return text.replace(" y ", "<br/>");
        },
        download(item) {
            console.log(item);
            location.href = APP.url('docente/cargaacademica/reporteAlumno?seccion=') + item.secciones[0].id;
//            axios.get('docente/cargaacademica/reepoteAlumnro')
//                    .then(response => {
//                        if (response.data.success) {
//
//                        } else {
//                            notify(MESSAGES.errorComunicacion, 'error');
//                        }
//                    });
        }
    }

});
