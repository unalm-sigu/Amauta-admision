new Vue({
    el: '#main',
    data: {
        cursos: [],
        modalDataZoom: {
            id: 'modalDataZoom',
            header: true,
            title: "Data Zoom",
            showaccept: false,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link'
        },
        aulaDataZoom: {}
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
                            notify(Messages.errorComunicacion, 'error');
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
        verAlumnos(item) {
            let $vue = this;
            location.href = APP.url('academico/docente/alumnosDocente/') + item.id + '/alumnosDocente' + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        download(item) {
            console.log(item);
            location.href = APP.url('docente/cargaacademica/reporteAlumno?seccion=') + item.id;
        },
        downloadOfFoto(seccion) {
            location.href = APP.url('reporte/cursos/matriculados/' + seccion.codigo2)
        },
        dataZoomModal(item) {
            let $vue = this;
            $vue.aulaDataZoom = item;
            $vue.$refs.modalDataZoom.open();
        }
    }

});
