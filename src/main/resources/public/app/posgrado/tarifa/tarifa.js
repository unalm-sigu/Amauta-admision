Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#tarifaVUE',
    data: {
        URL: APP.url('posgrado/tarifa'),
        tarifa: {tarifaConcepto: []},
        carreras: JSON.parse(carrerasJson),
        ciclos: JSON.parse(ciclosJson),
        conceptos: JSON.parse(conceptosJson),
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
        }
    },
    mounted() {
        this.$nextTick(() => {
            $('.numeric').numeric();
        })
    },
    methods: {
        verCarrera(item) {
            return item.tipoEnum.name + " - " + item.nombre;
        },
        reload() {
            this.$refs.raptor.loadRemoteData();
        },
        nuevo() {
            this.modalEditar.title = `Nueva tarifa`;
            this.tarifa = {tarifaConcepto: []};
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
            AXIOS.post(`${this.URL}/save`, this.tarifa)
                    .then(response => {
                        this.reload();
                        this.$refs.modalEditar.close();
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
                        let tarifaBD = response.data.data;
                        this.modalEditar.title = `Tarifa de ${tarifaBD.carrera.nombre}`;
                        this.tarifa = tarifaBD;
                        this.$refs.modalEditar.open();
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
