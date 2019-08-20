Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {sinConsejero: '', conConsejero: '', inhabilitado: ''},
//        aconsejadosURL: APP.url(rutaModulo + '/list'),
        ciclo: {},
        carreras: [],
        resumenCarrera: {
            consejerosActivos: 0,
            consejerosInactivos: 0,
            aconsejadosActivos: 0,
            aconsejadosInactivos: 0,
            sinconsejeroInactivos: 0,
            sinconsejeroActivos: 0,
            inhabilitados: 0
        },
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
        count: {activos: 0, sinConsejero: 0, sinAsignar: 0},
        loadResumen: false
    },
    mounted: function () {
        let $vue = this;
        $vue.ciclo = JSON.parse(cicloJson);
        $vue.carreras = JSON.parse(carrerasJson);

        let carrera = $vue.$refs.raptorAconsejados.getParameterByName('queries[carrera]');
        carrera = (carrera == null) ? '' : carrera;

        if ($vue.carreras.length > 0 && carrera == '') {
            $vue.carreraSelect = $vue.carreras[0];
        } else if (carrera != '') {
            for (var i = 0; i < $vue.carreras.length; i++) {
                if ($vue.carreras[i].id == carrera) {
                    $vue.carreraSelect = $vue.carreras[i];
                }
            }
        }

        if ($vue.carreraSelect.id != undefined) {
            $vue.cargaAconsejados($vue.carreraSelect);
        }
    },
    methods: {
        findAconsejado(tipo) {
            let $vue = this;
            if ($vue.carreraSelect.id == undefined) {
                return;
            }
            if ($vue.carreraSelect.id == '') {
                return;
            }

            $vue.$refs.raptorAconsejados.querie = [];
            $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: $vue.carreraSelect.id});

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: "estado", value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: "estado", value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.raptorAconsejados.changeUrl('queries[estado]', null);
            }
            $vue.$refs.raptorAconsejados.loadRemoteData();
        },
        customLabel( { colaborador }) {
            return `${colaborador.persona.nombreCompleto}`;
        },
        getDocentes(nombreDoc) {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/listConsejero"),
                data: {idCarrera: $vue.carreraSelect.id, nombre: nombreDoc},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.consejeros = response.data;
                $vue.isLoading = false;
            });
        },
        cargaAconsejados() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;

            if ($vue.seleccionado !== '') {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
            }

            $vue.resumenCarrera.aconsejadosActivos = 0;
            $vue.resumenCarrera.aconsejadosInactivos = 0;
            $vue.resumenCarrera.sinconsejeroActivos = 0;
            $vue.resumenCarrera.sinconsejeroInactivos = 0;

            $vue.$refs.raptorAconsejados.querie = [];
            $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: carrera});
            $vue.$refs.raptorAconsejados.url = APP.url(rutaModulo + '/list/' + carrera);
            $vue.$refs.raptorAconsejados.loadRemoteData();
            $vue.loadResumen = true;

        },
        getResumenCarrera(carrera) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + "/resumenCarrera"),
                data: {carrera: carrera},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.resumenCarrera = response.data;
                $vue.isLoading = false;
                $vue.loadResumen = false;
            });
        },
        loadResumenCarrera() {
            let $vue = this;
            let carrera = $vue.carreraSelect.id;
            if ($vue.loadResumen) {
                $vue.getResumenCarrera(carrera);
            }
        },
        model(item) {
            let $vue = this;
            $vue.alumnoConsejeroForm = Object.assign({}, item);
            $vue.$refs.consejeroModal.open();
        },
        cambiarConsejero() {
            let $vue = this;

            $.ajax({
                url: APP.url(rutaModulo + "/update"),
                contentType: "application/json",
                data: JSON.stringify($vue.alumnoConsejeroForm),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.raptorAconsejados.loadRemoteData();
                    notify(response.message, "success");
                }
                $vue.$refs.consejeroModal.close();
            });
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        }
    }
});







        