new Vue({
    el: '#alumnosVUE',
    data: {
        raptorurl: APP.url('academico/egresado/list'),
        seleccionado: -1,
        bgColorClass: ['text-primary', 'text-success'],
        resumen: [...Array(2).keys()],
    },
    mounted: function () {
        let $vue = this;
        $vue.loadResumen();
    },
    methods: {
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verTipoCarrera(item) {
            return (item.alumno.carrera.tipo == "MAE" || item.alumno.carrera.tipo == "DOC");
        },
        verFacultad(item) {
            return (item.alumno.modalidadEstudio.codigo == "PRE" && item.alumno.carrera.codigo != item.alumno.carrera.facultad.codigo);
        },
        verModalidades(item, tipo) {
            let $vue = this;
            if ($vue.seleccionado == item) {
                $vue.seleccionado = -1;
                $vue.addFilterRaptorTable('moe.codigo');
                return;
            }
            $vue.addFilterRaptorTable('moe.codigo', tipo);
            $vue.seleccionado = item;
        },
        addFilterRaptorTable(name, value) {
            let $vue = this;
            $vue.$refs.raptoregresado.querie.push({name: name, value: value});
            $vue.$refs.raptoregresado.loadRemoteData();
        },
        loadResumen() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/egresado/resumen'),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = Object.entries(response.data);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }
});

