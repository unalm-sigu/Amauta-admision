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
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${this.URL}/procesarAsignacionAulas`, asignacionAula)
                    .then(response => {
                        if (response.data.success) {
                            this.asignacionAula = response.data.data;
                            MODAL.hideWait();
                        } else {
                            notify(response.data.message, 'error');
                            MODAL.hideWait();
                        }
                    });
        }
    }
})
