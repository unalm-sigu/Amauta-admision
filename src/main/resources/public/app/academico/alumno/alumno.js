new Vue({
    el: '#alumnosVUE',
    data: {
        alumnosURL: APP.url('academico/alumno/list'),
        seleccionado: '',
        bgColorClass: {pregrado: '', postgrado: '', visitante: '', especial: ''}
    },
    mounted: function () {
        let $vue = this;
        let tipo = $vue.$refs.load.getParameterByName('queries[moe.codigo]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
        }
        $vue.$refs.load.repreload();
    },
    methods: {
        verTipoCarrera(item) {
            return (item.carrera.tipo == "MAE" || item.carrera.tipo == "DOC");
        },
        verFacultad(item) {
            return (item.modalidadEstudio.codigo == "PRE" && item.carrera.codigo != item.carrera.facultad.codigo);
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        urlDataPersonal(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/fisicoupdate') + $vue.getOrigenURL();
        },
        urlMatricula(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/gomatricula') + $vue.getOrigenURL();
        },
        urlConfigCursos(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/configcursos') + $vue.getOrigenURL();
        },
        urlConvalidarTraslado(item) {
            let $vue = this;

            axios.post("/academico/alumno/verificarTramiteTraslado", item)
                    .then(response => {
                        if (response.data.success) {
                            location.href = '/academico/alumno/' + item.id + '/trasladoexterno' + $vue.getOrigenURL();
                        } else {
                            notify("El alumno " + item.persona.apellidosNombres + " no tiene resolución de traslado externo", "warning");
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            });
//            return APP.url('academico/alumno/' + item.id + '/trasladoexterno') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        isPosgrado(modalidad) {
            return modalidad.codigo == 'EPG' ? true : false;
        },
        verModalidades(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.load.querie = [];
                $vue.$refs.load.changeUrl('queries[moe.codigo]', null);
                $vue.$refs.load.loadRemoteData();
            }
        }
    }
});

