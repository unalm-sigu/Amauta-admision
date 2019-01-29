Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {pregrado: '', postgrado: '', visitante: '', especial: ''},
        aconsejadosURL: APP.url('consejeria/aconsejado/list'),
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
        alumnoConsejeroForm: {}
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        customLabel( { colaborador }) {
            return `${colaborador.persona.nombreCompleto}`;
        },
        getDocentes(nombreDoc) {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url("consejeria/aconsejado/listConsejero"),
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
            let carrera = item.id;
            $vue.$refs.load.querie = [];
            if ($vue.carreraSelect != null) {
                $vue.$refs.load.querie.push({name: 'car.id', value: carrera});
                $vue.$refs.load.loadRemoteData();
            }
        },
        model(item) {
            let $vue = this;
            $vue.alumnoConsejeroForm = item;
            $vue.$refs.consejeroModal.open();
        },
        cambiarConsejero() {
            let $vue = this;

            $.ajax({
                url: APP.url("consejeria/aconsejado/update"),
                contentType: "application/json",
                data: JSON.stringify($vue.alumnoConsejeroForm),
                type: 'post',
            }).then(response => {
                if (response.success) {
                    $vue.$refs.load.loadRemoteData();
                    notify(response.message, "success");
                }
                $vue.$refs.consejeroModal.close();
            });
        }
    }
});







        