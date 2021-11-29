new Vue({
    el: '#main',
    data: {
        cursos: [],
        aulaDataZoom: {},
        seccionMain: {}
    },
    mounted: function () {
        let $vue = this;
        $vue.loadData();
    },
    methods: {
        loadData() {
            let $vue = this;
            axios.get('/docente/cargaacademica/list')
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
            location.href = APP.url('academico/docente/alumnosDocente/') + item.id + '/alumnosDocente' + URL_UTIL.getOrigenURL();
        },
        download(item) {
            location.href = APP.url('docente/cargaacademica/reporteAlumno?seccion=') + item.id;
        },
        downloadOfFoto(seccion) {
            location.href = APP.url('reporte/cursos/matriculados/' + seccion.codigo2)
        },
        dataZoomModal(item) {
            let $vue = this;
            $vue.aulaDataZoom = item;
            $vue.$refs.modalDataZoom.open();
        },
        linkZoomModal(seccion) {
            let $vue = this;
            $vue.seccionMain = {...seccion};
            $vue.$refs.modalLinkZoom.open();
        },
        copiarLink() {
            let $vue = this;
            navigator.clipboard.writeText($vue.seccionMain.linkZoom);
        }
    }

});
