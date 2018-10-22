new Vue({
    el: '#preciocursoestructuraVUE',
    data: {
        URL: APP.url('academico/preciocursoestructura'),
        estructuras: [],
        editar: false,
        guardando: false
    },
    mounted() {
      this.list();  
    },
    methods: {
        list() {
            axios.get(`${this.URL}/list`)
                    .then(response => this.estructuras = response.data.data);

        },
        guardar() {
            this.guardando = true;
            AXIOS.post(`${this.URL}/save`, this.estructuras)
                    .then(response => {
                        if(response.data.success){
                            this.list();
                            this.editar = false;
                            this.guardando = false;
                        }
                    })
        }
    }
});
