new Vue({
    el: '#main',
    data: {
        encuestaURL: APP.url('docente/encuesta/list'),
        docentesSecciones: [],
        encuesta: JSON.parse(encuestaJson),
        configuraEncuesta: {},
        modalPreguntas: {
            id: 'modalPreguntas',
            title: 'Resumen de preguntas',
            modalsize: 'modal-lg',
            header: true,
            footer: false,
            showaccept: false
        },
        preguntas: [],
        modalComentarios: {
            id: 'modalComentarios',
            title: 'Comentarios',
            modalsize: 'modal-md',
            header: true,
            footer: false,
            showaccept: false
        },
        comentarios: [],
        modalTemas: {
            id: 'modalTemas',
            title: 'Temas',
            modalsize: 'modal-lg',
            header: true,
            footer: false,
            showaccept: false
        },
        temas: [],
        cursos: [],
        cursosNoEnc: [],
    },
    mounted: function () {
        let $vue = this;
        $vue.refreshEncuesta();

        let tipo = $vue.$refs.load.getParameterByName('queries[ed.estado]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'ed.estado', value: tipo});
        }
        $vue.$refs.load.repreload();
    },
    methods: {
        refreshEncuesta() {
            let vue = this;
            axios.post('/academico/encuestaestudiantil/docente/encuestaDocente')
                    .then(response => {
                        if (response.data.success) {
                            vue.encuesta = response.data.data;
                            vue.periodosEncuesta = vue.encuesta.periodosEncuesta;
                            vue.cursosNoEncuestar = vue.encuesta.cursosNoEncuestar;
                            if (vue.encuesta.configuraEncuesta.length > 0) {
                                vue.configuraEncuesta = vue.encuesta.configuraEncuesta[0];
                            } else {
                                vue.configuraEncuesta = {};
                            }
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
        },
        getDia(fecha) {
            if (fecha == "") {
                return "";
            }
            return fecha.split(" ")[0];
        },
        getHora(fecha) {
            if (fecha == "")
                return "";
            var time = fecha.split(" ")[1].split(":");
            var aamm = (parseInt(time[0]) > 11) ? "pm" : "am";
            var hh = (parseInt(time[0]) > 12) ? (parseInt(time[0]) - 12) : parseInt(time[0]);
            return (hh < 10 ? "0" : "") + hh + ":" + time[1] + " " + aamm;
        },
        findPreguntas(item) {
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/preguntas`)
                    .then(response => {
                        if (response.data.success) {
                            this.preguntas = response.data.data;
                            this.$refs.modalPreguntas.open();
                        }
                    })
        },
        findComentarios(item) {
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/comentarios`)
                    .then(response => {
                        if (response.data.success) {
                            this.comentarios = response.data.data;
                            this.$refs.modalComentarios.open();
                        }
                    })
        },
        findTemas(item) {
            AXIOS.get(`/academico/encuestaestudiantil/docente/${item.id}/resumen/temas`)
                    .then(response => {
                        if (response.data.success) {
                            this.temas = response.data.data;
                            this.$refs.modalTemas.open();
                            this.generateChart(response.data.data);
                        }
                    })
        },
        generateChart(items) {
            var aData = [];
            for (var i = 0; i < items.length; i++) {
                let obj = {};
                obj.name = items[i].temaEncuesta.nombre;
                obj.y = items[i].puntaje;
                aData.push(obj);
            }

            Highcharts.chart('container', {
                chart: {
                    type: 'column'
                },
                title: {
                    text: 'Encuesta Estudiantil'
                },
                subtitle: {
                    text: '(Escala 1 - 5)'
                },
                xAxis: {
                    type: 'category'
                },
                yAxis: {
                    title: {
                        text: 'Puntaje'
                    }
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            format: '{point.y:.1f}%'
                        }
                    }
                },
                tooltip: {
                    headerFormat: '<span style="font-size:11px">{series.name}</span><br>',
                    pointFormat: '<span style="color:{point.color}">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'
                },
                "series": [
                    {
                        "name": "Tema",
                        "colorByPoint": true,
                        "data": aData
                    }
                ]
            });
        },
        sinEncuesta() {
            var $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuestaestudiantil/editor/allcursosinencuesta'),
                data: {
                    'id': $vue.encuesta.id
                },
                async: false,
                success: function (response) {
                    if (response.success) {
                        console.log(response.data);
                        $vue.cursosNoEnc = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

            $vue.curso = {};
            $vue.$refs.modalAddCurso.open();
        }
    }
});
