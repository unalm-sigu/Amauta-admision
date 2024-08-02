Vue.component("multiselect", window.VueMultiselect.default)
Vue.component('date-picker', VueBootstrapDatetimePicker);
new Vue({
    el: '#main',
    data: {
        agendaConsejeroURL: APP.url('consejeria/administracion/agendaconsejero/all'),
        carreras: [],
        alumnos: [],
        consejeros: [],
        origen: APP.url('consejeria/administracion'),
        filtro: {},
        nombreCarreraSupervisor: nombreCarreraSupervisor // Asigna la variable aqu
    },
    mounted: function () {
        let $vue = this;
        $vue.setOrigin();
        nombreCarreraSupervisor = this.nombreCarreraSupervisor;
        axios_.get(APP.url("consejeria/administracion/allCarrera"), {params: {nombre: this.nombreCarreraSupervisor}})
                .then(({data}) => {
                    if (data.length > 0) {
                        $vue.filtro.carrera = data[0]; // Asume que el primer resultado es el correcto
                }
                });
    },
    methods: {
        styleColor(item) {
            switch (item.name) {
                case "AGEN":
                    return "label label-primary";
                case "ANU" :
                    return "label label-danger";
                case "NASIS" :
                    return "label label-warning";
                case "ASIS" :
                case "VEN" :
                case "ATEN" :
                    return "label label-success";
            }
        },
        reporte() {
            let $vue = this;
            axios_blob.post(APP.url('consejeria/administracion/agendaconsejero/reporte'), $vue.filtro)
                    .then(response => {
                        UTIL_BLOB.save(response);
                    }, () => {
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        setOrigin() {
            let $vue = this;
            $vue.origen = URL_UTIL.getOrigenDecodeURL();
        },
        changeFilter() {
            let $vue = this;
            $vue.$refs.raptorConsejero.querie.push({name: 'carrera', value: $vue.filtro.carrera ? $vue.filtro.carrera.id : null});
            $vue.$refs.raptorConsejero.querie.push({name: 'consejero', value: $vue.filtro.consejero ? $vue.filtro.consejero.id : null});
            $vue.$refs.raptorConsejero.querie.push({name: 'alumno', value: $vue.filtro.alumno ? $vue.filtro.alumno.id : null});
            $vue.$refs.raptorConsejero.loadRemoteData();
        },
        searchCarrera(nombre) {
            let $vue = this;
            if (!nombre) {
                return;
            }
            axios_.get(APP.url("consejeria/administracion/allCarrera"),
                    {params: {nombre: nombre}})
                    .then(({data}) => {
                        $vue.carreras = data;
                    }, () => {
                    });
        },
        searchConsejero(nombre) {
            let $vue = this;
            if (!nombre) {
                return;
            }
            axios_.get(APP.url("consejeria/administracion/allConsejero"),
                    {params: {nombre: nombre}})
                    .then(({data}) => {
                        $vue.consejeros = data;
                    }, () => {
                    });
        },
        searchAlumno(nombre) {
            let $vue = this;
            if (!nombre) {
                return;
            }
            axios_.get(APP.url("consejeria/administracion/allAlumno"),
                    {params: {nombre: nombre}})
                    .then(({data}) => {
                        $vue.alumnos = data;
                    }, () => {
                    });
        }
    }
});







        