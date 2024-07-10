Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    components: {
        ModalSimple: use("/_vue/modules/ModalSimple.vue"),
        RaptorTable: use("/_vue/modules/RaptorTable.vue"),
    },
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteRetiroExcepcional/list'),
        ciclos: JSON.parse(ciclosJson),
        retiroExcepcional: {},
        alumnos: [],
        isLoading: false
    },
    methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {
            return APP.url('academico/tramiteacademico/tramiteRetiroExcepcional/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.retiroExcepcional = {};
            $vue.$refs.modalRetiroExcep.open();
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true;

            if (nombre) {

                $.ajax({
                    url: APP.url("academico/tramitecondicional/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                });

            }

        },
        saveRetiro() {
            let $vue = this;
            axios_.post("/academico/tramiteacademico/tramiteRetiroExcepcional/save", $vue.retiroExcepcional)
                    .then(response => {
                        console.log(response.data);
                        if (response.data.success) {
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalRetiroExcep.close();
                    }, () => {
                        $vue.$refs.modalRetiroExcep.stop();
                    });
        },
        labelColor(item) {
            switch (item) {
                case  "SOL":
                    return "label label-success"
                    break;
                case  "ANU":
                    return "label label-danger"
                    break;
                case  "RCHR":
                    return "label label-warning"
                    break;
                default :
                    return "label label-primary"
                    break;
            }
        },
        anular(item) {
            let $vue = this;
            $vue.retiroExcepcional = {...item};
            $vue.$refs.modalEliminarTramite.open();
        },
        anularActionHandler() {
            let $vue = this;
            axios_.post("/academico/tramiteacademico/tramiteRetiroExcepcional/anular", $vue.retiroExcepcional)
                    .then(response => {
                        $vue.$refs.modalEliminarTramite.close();
                        $vue.$refs.load.loadRemoteData();
                        notify(response.data, "success");
                    }, () => {
                        $vue.$refs.modalEliminarTramite.stop();
                    });
        }
    }
})