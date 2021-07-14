Vue.config.devtools = true
Vue.component("inicio-component", {
    template: "#inicioComponent",
    props: {
        alumno: {},
        showactions: {required: false, default: true}
    },
    data: function () {
        return {
            ident: true,
            flag: true,
//            alumno: JSON.parse(alumnoJson),
            orientacionTmp: {},
            esEgresado:false,
            promedioGraduacion:null,

        }
    },
    mounted() {
        let vue = this;
        if (vue.alumno != null) {
            vue.orientacionTmp = vue.alumno.orientacionCarrera;
        }
        if (ID_ALUMNO) {
            this.loadPPG()
        }
    },
    methods: {
        changeOrientacion(orientacion) {
            let vue = this;

            bootbox.confirm({
                message: "El cambio de orientación afectará el avance curricular y los cursos hábiles ¿Desea proceder con el cambio?",
                buttons: {
                    confirm: {
                        label: 'Si, cambiar',
                        className: 'btn-success'
                    },
                    cancel: {
                        label: 'No',
                        className: 'btn-danger'
                    }
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        vue.saveOrientacion(orientacion);
                    } else {
                        vue.alumno.orientacionCarrera = vue.orientacionTmp;
                    }
                }
            });
        },
        saveOrientacion(orientacion) {
            let vue = this;

            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno/' + vue.alumno.id + '/' + orientacion.id + '/saveOrientacion'),
                data: JSON.stringify(vue.alumno),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        vue.$emit("reiniciar-planes-main");
                        notify(response.message, "info");
                    } else {
                        vue.alumno.orientacionCarrera = vue.orientacionTmp;
                        notify(response.message, "error");
                    }
                },
                error() {
                    vue.alumno.orientacionCarrera = vue.orientacionTmp;
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verNota(notax) {
            return APP.verNota(notax);
        },
        loadPPG() {
            let $vue = this;
            axios.get('/tramite/solicitudconstancia/promedioGraduacion/' + ID_ALUMNO)
                    .then(response => {
                        if (response.data.success) {
                            $vue.esEgresado = response.data.data.esEgresado;
                            $vue.promedioGraduacion = response.data.data.egresado.promedioGraduacion;
                        }
                    });
        }
    }
});