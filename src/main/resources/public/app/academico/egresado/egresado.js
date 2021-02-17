Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#alumnosVUE',
    data: {
        raptorurl: APP.url('academico/egresado/list'),
        tiposBusqueda: JSON.parse(tiposBusquedaJson),
        tipoBusquedaTemp: JSON.parse(tiposBusquedaJson),
        seleccionado: -1,
        bgColorClass: ['text-primary', 'text-success'],
        resumen: [...Array(2).keys()],
        ciclos: [],
        modalReporte: {
            id: 'modalReporte',
            header: true,
            title: 'Reporte',
            okbtn: "Descargar",
            showaccept: true
        },
        forCiclo: false,
        tipoBusqueda: [],
        carreras: [],
        facultades: [],
        busquedaBean: {tipoBusquedaEnum: []}
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        openModal() {
            let $vue = this;
            $vue.$refs.modalReporte.open();
        },
        validBusqueda(selected) {
            let $vue = this;

            var idx = [];
            var error;
            var i = 0;
            while (i < $vue.busquedaBean.tipoBusquedaEnum.length) {
                error = true;
                var item = $vue.busquedaBean.tipoBusquedaEnum[i];
                for (var j = 0; j < item.permisos.length; j++) {
                    var item2 = item.permisos[j];
                    if (item2.name === selected.name) {
                        error = false;
                        break;
                    }
                }
                if (error) {
                    $vue.busquedaBean.tipoBusquedaEnum.splice(i, 1);
                    continue;
                } else {
                    i++;
                }
            }
        },
        verifyContains(value) {
            let $vue = this;
            for (var i = 0; i < $vue.busquedaBean.tipoBusquedaEnum.length; i++) {
                var item = $vue.busquedaBean.tipoBusquedaEnum[i];
                if (item.name === value) {
                    return true;
                }
            }
            return false;
        },
        loadFacultades(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url(`academico/egresado/allFacultad`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.facultades = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        loadCarreras(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url(`academico/egresado/allCarrera`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.carreras = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        loadCiclo(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url(`academico/egresado/allCiclos`),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.ciclos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        }
    }
});

