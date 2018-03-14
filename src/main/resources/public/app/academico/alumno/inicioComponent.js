Vue.component("inicio-component", {
    template: "#inicioComponent",
    props: {
        alumno: {},
        alumnoInfo: {}
    },
    computed: {
        mostrarCreditos() {
            if (this.alumno.creditosCursados !== undefined && this.alumno.creditosCursados !== 0) {
                return true;
            }
            return false;
        },
        mostrarOrientacion() {
            if (this.alumno.planCurricular.orientacionCarrera !== undefined) {
                return true;
            }
            return false;
        }
    }
});