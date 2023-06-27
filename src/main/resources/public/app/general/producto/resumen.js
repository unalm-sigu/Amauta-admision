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
            console.log("test")
            axios_.post("/general/aula/producto/resumen/resumen")
                    .then(response => {
                        $vue.encuesta = response.data;
                        console.log($vue.encuesta.data.length);
                        // $vue.configuraEncuesta = {};
                        // if ($vue.encuesta.configuraEncuesta.length > 0) {
                        //     $vue.configuraEncuesta = $vue.encuesta.configuraEncuesta[0];
                        // }
                        $vue.renderChar();
                    });
        },
        renderChar() {

            let $vue = this;


            let totalPregrado = $vue.encuesta ? $vue.encuesta.data.length : 0;
            // let noEncuestadosPregrado = $vue.encuesta.pendienteEncuestaAlumnoPregrado ? $vue.encuesta.pendienteEncuestaAlumnoPregrado : 0;
            let noEncuestadosPregrado=10;
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
                    text: 'Estado Productos'
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
                                name: 'Activos ' + encuestadosPregrado,
                                y: porcentajePregrado,
                                sliced: true,
                                selected: true
                            },
                            {
                                name: 'Baja ' + noEncuestadosPregrado,
                                y: faltantePregrado,
                                sliced: true,
                                selected: true
                            }
                        ]
                    }]
            });

        }
    }
});
