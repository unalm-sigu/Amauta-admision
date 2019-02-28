var app = new Vue({
    el: '#main',
    data: {
        URL: APP.url('academico/asignacionaula'),
        processing: false,
        asignacionAula: null
    }, created: function () {
        if (jAsignacionAula != null && jAsignacionAula != '') {
            this.asignacionAula = JSON.parse(jAsignacionAula);
        }

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, procesarAsignacionAulas() {
            let asignacionAula = {id: ""};
            if (this.asignacionAula != null) {
                asignacionAula = this.asignacionAula;
            }
            AXIOS.post(`${this.URL}/procesarAsignacionAulas`, asignacionAula)
                    .then(response => {
                        if (response.data.success) {
                            //  this.reload();
                            //  this.$refs.modalEditar.close();
                        } else {
                            notify(response.data.message, 'error');
                        }
                    });
        }
    }
})
