Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {sinconsejero: '', activo: ''},
        aconsejadosURL: APP.url(rutaModulo + '/list'),
        ciclo: JSON.parse(cicloJson),
        carreras: JSON.parse(carrerasJson),
        isLoading: false,
        consejeroModal: {
            id: 'consejeroModal',
            header: 'true',
            title: "Consejeros",
            okbtn: 'Agregar',
            showaccept: true
        },
        carreraSelect: {},
        consejeros: [],
        seleccionado: '',
        alumnoConsejeroForm: {},
        count: {activos: 0, sinConsejero: 0, sinAsignar: 0}
    },
    mounted: function () {
        let $vue = this;
        let query = $vue.$refs.raptorAconsejados.getParameterByName('queries[carrera]');
        query = (query == null) ? '' : query;
        if (query != '') {
            $vue.carreraSelect = $vue.carreras.filter(value => value.id == query)[0];
            $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: query});
            $vue.countData();
            $vue.$refs.raptorAconsejados.repreload();
        }
    },
    methods: {
        findAconsejado(tipo) {
            let $vue = this;
            $vue.$refs.raptorAconsejados.querie = [];

            if ($vue.carreraSelect.id != undefined) {
                $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: $vue.carreraSelect.id});
            }

            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: tipo, value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.raptorAconsejados.querie.push({name: tipo, value: tipo});

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.raptorAconsejados.changeUrl('queries[' + tipo + ' ]', null);
            }
            $vue.$refs.raptorAconsejados.loadRemoteData();
        },
        countData() {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/countData"),
                data: {idCarrera: $vue.carreraSelect.id},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.count = response.data;
            });
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
        cargaAconsejados(item) {
            let $vue = this;
            $vue.carreraSelect = item;
            $vue.$refs.raptorAconsejados.querie = [];
            if ($vue.carreraSelect != null) {
                $vue.countData();
                $vue.$refs.raptorAconsejados.querie.push({name: 'carrera', value: $vue.carreraSelect.id});
                $vue.$refs.raptorAconsejados.loadRemoteData();
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







        