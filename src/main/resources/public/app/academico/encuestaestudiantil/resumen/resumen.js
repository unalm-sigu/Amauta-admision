new Vue({
    el: '#main',
    data: {
        encuesta: {
            estadoEnum: {value: "No creado"}
        },
        cursosNoEncuestar: [],
        bgColorModalidadClass: {posgrados: '', pregrados: ''},
        bgColorDictadoClass: {modulares: '', semestrales: ''},
        configuraEncuesta: {},
        bgColorClass: {activo: '', anulado: '', innecesario: '', sinperiodo: '', cerrado: '', encuestable: '', encuestado: ''},
    },
    mounted: function () {

        let $vue = this;
        $vue.loadResumen();

    },
    methods: {
        loadResumen() {
            let $vue = this;
            axios_.post("/academico/encuestaestudiantil/resumen/resumen")
                    .then(response => {
                        $vue.encuesta = response.data;
                        $vue.configuraEncuesta = {};
                        if ($vue.encuesta.configuraEncuesta.length > 0) {
                            $vue.configuraEncuesta = $vue.encuesta.configuraEncuesta[0];
                        }
                        $vue.renderChar();
                    });
        },
        renderChar() {

            let $vue = this;


            let totalPregrado = $vue.encuesta.totalEncuestaAlumnoPregrado ? $vue.encuesta.totalEncuestaAlumnoPregrado : 0;
            let noEncuestadosPregrado = $vue.encuesta.pendienteEncuestaAlumnoPregrado ? $vue.encuesta.pendienteEncuestaAlumnoPregrado : 0;
            let encuestadosPregrado = totalPregrado - noEncuestadosPregrado;

            let porcentajePregrado = 100 * encuestadosPregrado / totalPregrado;
            let faltantePregrado = 100 - porcentajePregrado;
            
            Highcharts.chart('containerPRE', {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false,
                    type: 'pie'
                },
                exporting: {
                    enabled: false
                },
                credits: {
                    enabled: false
                },
                title: {
                    text: 'Pregrado'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                accessibility: {
                    point: {
                        valueSuffix: '%'
                    }
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                        }
                    }
                },
                series: [{
                        name: 'Cantidad',
                        colorByPoint: true,
                        data: [{
                                name: 'Encuestado ' + encuestadosPregrado,
                                y: porcentajePregrado,
                                sliced: true,
                                selected: true
                            },
                            {
                                name: 'Pendiente ' + noEncuestadosPregrado,
                                y: faltantePregrado,
                                sliced: true,
                                selected: true
                            }
                        ]
                    }]
            });

            let totalPosgrado = $vue.encuesta.totalEncuestaAlumnoPosgrado ? $vue.encuesta.totalEncuestaAlumnoPosgrado : 0;
            let noEncuestadosPosgrado = $vue.encuesta.pendienteEncuestaAlumnoPosgrado ? $vue.encuesta.pendienteEncuestaAlumnoPosgrado : 0;
            let encuestadosPosgrado = totalPosgrado - noEncuestadosPosgrado;

            let porcentajePosgrado = 100 * encuestadosPosgrado / totalPosgrado;
            let faltantePosgrado = 100 - porcentajePosgrado;

            Highcharts.chart('containerPOST', {
                chart: {
                    plotBackgroundColor: null,
                    plotBorderWidth: null,
                    plotShadow: false,
                    type: 'pie'
                },
                title: {
                    text: 'Posgrado'
                },
                tooltip: {
                    pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
                },
                accessibility: {
                    point: {
                        valueSuffix: '%'
                    }
                },
                exporting: {
                    enabled: false
                },
                credits: {
                    enabled: false
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        cursor: 'pointer',
                        dataLabels: {
                            enabled: true,
                            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
                        }
                    }
                },
                series: [{
                        name: 'Cantidad',
                        colorByPoint: true,
                        data: [{
                                name: 'Encuestado ' + encuestadosPosgrado,
                                y: porcentajePosgrado,
                                sliced: true,
                                selected: true
                            },
                            {
                                name: 'Pendiente ' + noEncuestadosPosgrado,
                                y: faltantePosgrado,
                                sliced: true,
                                selected: true
                            }
                        ]
                    }]
            });

        }
    }
});
