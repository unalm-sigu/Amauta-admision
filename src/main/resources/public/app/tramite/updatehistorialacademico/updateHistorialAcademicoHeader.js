Vue.component("header-info-component", {
    template: "#headerInfoComponent",
    props: {
        alumno: {}
    },
    data: function() {
        return {
            ident: true,
            flag: true
        }
    },
    beforeMount() {
        let $vue = this;
        if ($vue.alumno.persona.numeroDocIdentidad === undefined) {
            $vue.ident = false;
        }
        if ($vue.alumno.modalidadEstudio.codigo === 'VIS' || $vue.alumno.modalidadEstudio.codigo === 'ESP') {
            $vue.flag = false;
        }
    },
    methods: {
        getCarreraName: function(alumno) {
            let namee = alumno.carrera.nombre + '   ';
            alumno.carrera.codigo !== alumno.carrera.facultad.codigo ? namee + ' - ' + alumno.carrera.facultad.nombre : namee = namee;
            return namee;
        }
    }
});