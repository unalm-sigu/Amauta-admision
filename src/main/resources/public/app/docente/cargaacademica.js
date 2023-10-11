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
        calcularCreditoCarga(profeSecc, item) {            
            if(item.length > 1) {
                console.log("calcularCreditoCarga secciones", item.length);
                if(profeSecc.porcentajeCarga === 100) {
                    console.log("profeSecc.creditosCarga", profeSecc.creditosCarga);
                    if(profeSecc.creditosCarga === 3) {
                        return profeSecc.creditosCarga * 0.22;
                    }
                    else if(profeSecc.creditosCarga === 2) {
                        return profeSecc.creditosCarga * 0.33;
                    }
                    else if(profeSecc.creditosCarga === 1) {
                        return profeSecc.creditosCarga * 0.33;
                    }
                }
            } else {
                console.log("calcularCreditoCarga secciones", item.length);
                if(profeSecc.porcentajeCarga === 100) {
                    console.log("profeSecc.creditosCarga", profeSecc.creditosCarga);
                    if(profeSecc.creditosCarga === 3 || profeSecc.creditosCarga === 2 || profeSecc.creditosCarga === 1) {
                        return profeSecc.creditosCarga * 0.33;
                    }
                    else if(profeSecc.creditosCarga === 4) {
                        return profeSecc.creditosCarga * 0.25;
                    }
                }
            }
            /*if (item.porcentajeCarga > 0) {
                if (item.porcentajeCarga == 100) {
                    return item.creditosCarga * 0.33;
                }
            }*/
        },
        totalCreditos(item) {
            let sumCreditosPRE = 0;
            for (let el of item) {
                if(el.secciones.length > 1) {
                    console.log("totalCreditos secciones", item.length);
                    for (let seccion of el.secciones) {
                        for (let docenteSeccion of seccion.docenteSeccion) {
                            if (el.cursoDirigido) {
                                console.log("docenteSeccion.creditosCarga", docenteSeccion.creditosCarga);
                                if(docenteSeccion.creditosCarga === 3) {
                                    sumCreditosPRE += docenteSeccion.creditosCarga * 0.22;
                                }
                                else if(docenteSeccion.creditosCarga === 1 || docenteSeccion.creditosCarga === 2) {
                                    sumCreditosPRE += docenteSeccion.creditosCarga * 0.33;
                                }
                                else if(docenteSeccion.creditosCarga === 4) {
                                    sumCreditosPRE += docenteSeccion.creditosCarga * 0.25;
                                }
                                /*sumCreditosPRE += docenteSeccion.creditosCarga * 0.33;*/
                            } else {
                                sumCreditosPRE += docenteSeccion.creditosCarga;
                            }
                        }
                    }
                } else {
                    console.log("totalCreditos secciones", item.length);
                    for (let seccion of el.secciones) {
                        for (let docenteSeccion of seccion.docenteSeccion) {
                            if (el.cursoDirigido) {
                                console.log("docenteSeccion.creditosCarga", docenteSeccion.creditosCarga);
                                if(docenteSeccion.creditosCarga === 4) {
                                    sumCreditosPRE += docenteSeccion.creditosCarga * 0.25;
                                } else {
                                    sumCreditosPRE += docenteSeccion.creditosCarga * 0.33;
                                }                                                                
                            } else {
                                sumCreditosPRE += docenteSeccion.creditosCarga;
                            }
                        }
                    }
                }
                
            }
            return sumCreditosPRE;
        }
    }

});
