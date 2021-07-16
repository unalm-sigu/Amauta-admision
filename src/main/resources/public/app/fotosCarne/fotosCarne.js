Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('vue-simple-progress', VueSimpleProgress.default)
new Vue({
    el: '#main',
    data: {
        carrera: '',
        carreras: [],
        modalidad: '',
        modalidades: JSON.parse(modalidadesJson),
        info: {perAvance: 0},
        procesando: false
    },
    mounted: function () {
        let $vue = this;
        $vue.obtenerInfo();
    },
    methods: {
        descagarFoto() {
            let $vue = this;
            $vue.procesando = true;
            axios_blob.get(APP.url('fotos/carne/descargarFotos/' + $vue.carrera.codigo))
                    .then(response => {
                        UTIL_BLOB.save(response);
                        $vue.procesando = false;
                    }, () => {
                        $vue.procesando = false;
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        carrerasByCarrera(filtroModalidad) {
            let $vue = this;
            console.log(filtroModalidad);
            axios.get('/comun/buscar/allCarreraByModalidad/' + filtroModalidad)
                    .then(response => {
                        if (response.data.success) {
                            $vue.carreras = response.data.data;
                            console.log($vue.carreras);
                        }
                    }, () => {
                        notify(response.message, "error");
                    });
        },
        obtenerInfo() {
            let $vue = this;
            axios.get(APP.url('fotos/carne/info'))
                    .then(response => {
                        $vue.info = response.data;
                        setTimeout($vue.obtenerInfo, 3000);
                    }, () => {
                        notify(response.message, "error");
                    });
        }
    }
});
 