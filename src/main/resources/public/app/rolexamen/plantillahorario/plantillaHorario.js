Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/plantillahorario'),
        rolesExamenes: JSON.parse(jRolesExamenes),
        rolExamen: null
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
            AXIOS.post(`${this.URL}/rolExamenInformation`, this.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');
                            this.listGruposRegulares(this.rolExamen);
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }
    }
});
