Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        carrera: '',
        carreras: [],
        modalidad: '',
        modalidades: JSON.parse(modalidadesJson),
        info: {perAvance: 0},
        bloq: false
    },
    computed: {

    },
    mounted: function () {
        let $vue = this;
        $vue.obtenerInfo();

    },
    methods: {
        descagarFoto() {
            let $vue = this;
            $vue.bloq = true;
            location.href = APP.url('fotos/carne/descargarFotos/' + $vue.carrera.codigo);
            
//            window.open(APP.url('fotos/carne/descargarFotos/' + $vue.carrera.codigo), '_blank');
        },
        carrerasByCarrera(filtroModalidad) {
            let $vue = this;

            axios.get('/comun/buscar/allCarreraByModalidad/' + filtroModalidad)
                    .then(response => {
                        if (response.data.success) {
//                            $vue.carrera = null;
                            $vue.carreras = response.data.data;
                            console.dir($vue.carreras);
                        }
                    })
                    .catch(e => {
                        console.log(e);
                    });
        },
        obtenerInfo() {
            let $vue = this;
            $.ajax({
                url: APP.url('fotos/carne/info'),
                type: 'GET',
                async: true,
                success: function (response) {
                    if (response.success) {
                        $vue.info = response.data;
                        console.log("INFOOO")
                        console.dir($vue.info)
//                        if ($vue.info.estado === 'ACT') {
//                            $vue.bloq = true;
                            setTimeout($vue.obtenerInfo, 3000);
//                        } else {
//                            $vue.bloq = false;
//                        }

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});
 