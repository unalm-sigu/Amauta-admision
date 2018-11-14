Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/plantillahorario'),
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: {
            semanasExamen: []
        },
        semanaExamen: null
    },
    mounted() {

    },
    methods: {
        rolExamenCustomLabel( { eventoCicloAcademico }) {
            if (eventoCicloAcademico == null || eventoCicloAcademico.eventoAcademico == null) {
                return "";
            }
            return `${eventoCicloAcademico.eventoAcademico.nombre}`;
        }, changeRolExamen() {
            AXIOS.post(`${this.URL}/changeRolExamen`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            this.rolExamen = response.data.data;
                        }
                    });
        }, changeSemanaExamen() {
            AXIOS.post(`${this.URL}/changeSemanaExamen`, this.semanaExamen)
                    .then(response => {
                        if (response.data.success) {

                        }
                        // MODAL.hideWait();
                    });
        }, calcularPlantillaHorario() {
            AXIOS.post(`${this.URL}/calcularPlantillaHorario`, this.semanaExamen)
                    .then(response => {
                        if (response.data.success) {
                            
                        }
                        // MODAL.hideWait();
                    });
        }
    }
});
