new Vue({
    el: '#main',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        cursos: [],
        aulaDataZoom: {},
        seccionMain: {},
        isCongCicloPre: false,
        iddocente: iddocente,
        codigoDocente: codigoDocente,
    },
    mounted: function () {
        let $vue = this;
        $vue.loadData();
    },
    methods: {
        loadData() {
            let $vue = this;
            axios.get('/docente/cargaacademica/list')
                    .then(({data}) => {

                        $vue.isCongCicloPre = data.isCongCicloPre;

                        $vue.cursos.push({
                            cursosModalidad: data.pregrado,
                            creditos: data.creditosPregrado
                        });

                        $vue.cursos.push({
                            cursosModalidad: data.posgrado,
                            creditos: data.creditosPosgrado
                        });

                    }, () => notify(Messages.errorComunicacion, 'error'));
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
        downloadHorario(item) {
            console.log("item:::" + item)
            $.fileDownload("/academico/profesor/reporteHorarioDocente", {
                httpMethod: "POST",
                data: {
                    id: item
                },
                successCallback: function (responseHtml, url) {

                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(Messages.errorComunicacion, 'error')
                }
            });
//            location.href = APP.url('academico/profesor/reporteHorarioDocente/' + item)
        },
        dataZoomModal(item) {
            let $vue = this;
            $vue.aulaDataZoom = item.aula;
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
        },
        calcularCreditoCarga(item) {

            if (item.porcentajeCarga > 0) {
                if (item.porcentajeCarga == 100) {
                    return (item.creditosCarga * 0.3325).toFixed(2);
                }
            }
        },
        totalCreditos(item) {
            let sumCreditosPRE = 0;
            for (let el of item) {
                for (let seccion of el.secciones) {
                    for (let docenteSeccion of seccion.docenteSeccion) {
                        if (el.cursoDirigido) {
                            sumCreditosPRE += docenteSeccion.creditosCarga * 0.3325;
                        } else {
                            sumCreditosPRE += docenteSeccion.creditosCarga;
                        }
                    }
                }
            }
            return sumCreditosPRE;
        }
    }

});
