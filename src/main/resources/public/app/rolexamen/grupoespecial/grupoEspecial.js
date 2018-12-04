Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#main',
    data: {
        URL: APP.url('rolexamen/grupoespecial'),
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
            this.$refs.raptor.ajaxdata = {rolexamenes: this.rolExamen.id};
            this.$refs.raptor.loadRemoteData();
        }, calcularGrupoEspecial() {
            let vue = this;
            MODAL.showWait("Espere un momento por favor");
            AXIOS.post(`${vue.URL}/calcularGrupoEspecial`, vue.rolExamen)
                    .then(response => {
                        if (response.data.success) {
                            // notify(response.data.message, 'info');
                           
                        } else {
                            //   notify(response.data.message, 'error');
                        }
                        MODAL.hideWait();
                    });
        }
    }
});
