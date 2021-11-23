Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {sinconsejero: '', activo: ''},
        ciclo: JSON.parse(cicloJson),
        persona: JSON.parse(personaJson),
        carrera: JSON.parse(carreraJson),
        aconsejadosURL: APP.url(rutaModulo + '/list'),
        origen: origen,
        isLoading: false,
        consejeroModal: {
            id: 'consejeroModal',
            header: true,
            title: "Tutores",
            okbtn: 'Aceptar',
            showaccept: true
        },
        carreraSelect: {},
        consejeros: [],
        seleccionado: '',
        alumnoConsejeroForm: {},
        count: {matriculados: 0, noMatriculados: 0, retiroCiclo: 0},
        situaciones: [],
        countSituaciones: [],
        colors: ['text-success', 'text-black', 'text-primary', 'text-warning', 'text-info']
    },
    mounted: function () {
        let $vue = this;
        $vue.countData();
        let query = $vue.$refs.load.getParameterByName('queries[estado]');
        query = (query == null) ? '' : query;
        if (query != '') {
            $vue.$refs.load.querie.push({name: 'estado', value: query});
            $vue.$refs.load.repreload();
        }

        if ($vue.persona.id != undefined) {
            $vue.loadAlumnosURL();
        }

    },
    methods: {
        findAconsejado(tipo) {
            let $vue = this;
            $vue.$refs.load.querie = [];
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'estado', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'estado', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.load.changeUrl('queries[estado ]', null);
            }
            $vue.$refs.load.loadRemoteData();

        },
        findAconsejadoSituacion(tipo) {
            let $vue = this;
            $vue.$refs.load.querie = [];
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'situacion', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'situacion', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.load.changeUrl('queries[situacion]', null);
            }
            $vue.$refs.load.loadRemoteData();

        },
        loadAlumnosURL() {
            let $vue = this;
            $vue.$refs.load.url = APP.url(rutaModulo + '/list/' + $vue.persona.id + "/" + $vue.carrera.id);
            $vue.$refs.load.loadRemoteData();


        },
        loadDataSituaciones(alumnosAconsejados) {
            let $vue = this;
            if ($vue.situaciones.length > 0) {
                return;
            }
            miMapa = new Map();
            for (var i = 0; i < alumnosAconsejados.length; i++) {
                var codigo = alumnosAconsejados[i].alumno.situacionAcademica.codigo;
                var nombre = alumnosAconsejados[i].alumno.situacionAcademica.nombre;
                if (miMapa.get(codigo) == undefined) {
                    var color = $vue.colors[i];
                    var data = {codigo: codigo, nombre: nombre, color: color == null ? 'text-primary' : color};
                    $vue.situaciones.push(data);
                    miMapa.set(codigo, 1);
                } else {
                    var cantidad = miMapa.get(codigo);
                    cantidad += 1;
                    miMapa.set(codigo, cantidad);
                }
            }
            for (var [clave, valor] of  miMapa.entries()) {
                var dataCount = {codigo: clave, count: valor};
                $vue.countSituaciones.push(dataCount);
            }

        },
        countData() {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/countData/" + $vue.persona.id + "/" + $vue.carrera.id),
                data: {idCarrera: $vue.carreraSelect.id},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.count = response.data;
                this.loadDataSituaciones(response.data.alumnosConsejeros);
            });
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        }
    }
});







        