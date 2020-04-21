Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#tarifaVUE',
    data: {
        URL: APP.url('posgrado/tarifa'),
        tarifa: {id: '', tarifaConcepto: []},
        ciclo: JSON.parse(cicloJson),
        carreras: JSON.parse(carrerasJson),
        conceptos: JSON.parse(conceptosJson),
        ambitos: JSON.parse(ambitosJson),
        ciclos: [],
        tiposMonto: [{name: 'CARR', value: 'Toda la carrera'}, {name: 'SEM', value: 'Semestral'}],
        modalEditar: {
            id: 'modalEditar',
            header: true,
            title: '',
            okbtn: 'Guardar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true,
            modalsize: 'modal-lg',
        },
        isSearchingCiclos: false
    },
    mounted() {
        this.$nextTick(() => {
            $('.numeric').numeric();
        })
    },
    methods: {
        searchCiclos(search) {
            let $vue = this;
            $vue.isSearchingCiclos = true;

            $.ajax({
                url: APP.url('posgrado/tarifa/allCiclos'),
                dataType: 'json',
                type: 'POST',
                async: true,
                data: {nombre: search},
                success(response) {
                    $vue.isSearchingCiclos = false;
                    if (response.success) {
                        $vue.ciclos = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        verCarrera(item) {
            return item.tipoEnum.value + " - " + item.nombre;
        },
        reload() {
            this.$refs.raptor.loadRemoteData();
        },
        nuevo() {
            this.modalEditar.title = `Nueva tarifa`;
            this.tarifa = {id: '', tarifaConcepto: []};
            this.$set(this.tarifa, 'tipoMonto', this.tiposMonto[1]);
            this.$refs.modalEditar.open();
        },
        clonar(item) {
            AXIOS.get(`${this.URL}/find/${item.id}`)
                    .then(response => {
                        let tarifaBD = response.data.data;
                        this.tarifa = tarifaBD;
                        this.$set(this.modalEditar, 'title', 'Nueva tarifa');
                        this.$set(this.tarifa, 'tipoMonto', this.tiposMonto[1]);
                        this.$set(this.tarifa, 'id', null);
                        this.$refs.modalEditar.open();
                    })
        },
        guardar() {
            this.$set(this.tarifa, 'tipoMonto', this.tarifa.tipoMontoEnum.name);
            this.$set(this.tarifa, 'ambito', this.tarifa.ambitoEnum.name);

            AXIOS.post(`${this.URL}/save`, this.tarifa)
                    .then(response => {
                        if (response.data.success) {
                            this.reload();
                            this.$refs.modalEditar.close();
                        } else {
                            notify(response.data.message, 'error');
                        }
                    })
        },
        activar(item) {
            AXIOS.post(`${this.URL}/activar`, item)
                    .then(response => {
                        this.reload();
                    })
        },
        editar(item) {
            AXIOS.get(`${this.URL}/find/${item.id}`)
                    .then(response => {
                        if (response.data.success) {
                            let tarifaBD = response.data.data;
                            this.modalEditar.title = `Tarifa - ${tarifaBD.carrera.tipoEnum.value} en ${tarifaBD.carrera.nombre}`;
                            this.tarifa = tarifaBD;
                            this.$refs.modalEditar.open();
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
        },
        eliminarTarifa(item) {
            AXIOS.post(`${this.URL}/eliminar`, item)
                    .then(response => {
                        this.reload();
                    })
        },
        agregar() {
            this.tarifa.tarifaConcepto.push({
                id: '',
                fraccionable: true
            })
            this.$nextTick(() => {
                $('.numeric').numeric();
            })
        },
        eliminar(index) {
            this.tarifa.tarifaConcepto.splice(index, 1);
        },
        actualizarConcepto(concepto) {
            if (!concepto.fraccionable) {
                this.$set(concepto, 'porcentajeInicial', 100);
            }
        }

    }
});
