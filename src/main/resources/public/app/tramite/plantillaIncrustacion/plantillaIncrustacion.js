Vue.component("multiselect", window.VueMultiselect.default)
console.log(JSON.parse(idiomasJson))
new Vue({
    el: '#plantillaVUE',
    data: {
        tipos: JSON.parse(tiposJson),
        idiomas: JSON.parse(idiomasJson),
        plantillaURL: APP.url('tramite/plantillainscrustacion/list'),
        modalIncrustacion: {
            id: 'modalIncrustacion',
            header: true,
            title: '',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        },
        incrustacion: {}
    },
    computed: {

    },
    created() {
    },
    mounted: function () {

    },
    methods: {
        update() {
            let $vue = this;
            bootbox.confirm({
                message: `¿Seguro que desea actualizar la plantilla?`,
                buttons: {
                    confirm: {label: 'Sí, Actualizar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: (result) => {
                    if (result) {
                        AXIOS.post('/tramite/plantillainscrustacion/save', $vue.incrustacion)
                                .then(response => {
                                    if (response.data.success) {
                                        this.$refs.load.loadRemoteData();
                                        this.$refs.modalIncrustacion.close();
                                    }
                                });
                    }
                }
            });
        },
        nuevo(item) {
            let $vue = this;
            $vue.incrustacion = Object.assign({}, item);
            if (item == null) {
                $vue.incrustacion = {};
                $vue.modalIncrustacion.title = "Agregar plantilla";
                $vue.modalIncrustacion.okbtn = "Agregar";
            } else {
                $vue.modalIncrustacion.title = "Actualizar plantilla";
                $vue.modalIncrustacion.okbtn = "Actualizar";
            }
            $vue.$refs.modalIncrustacion.open();
        },
        save() {
            let $vue = this;

            AXIOS.post('/tramite/plantillainscrustacion/save', $vue.incrustacion)
                    .then(response => {
                        if (response.data.success) {
                            this.$refs.load.loadRemoteData();
                            this.$refs.modalIncrustacion.close();
                        }
                    });
        }
    }
});
