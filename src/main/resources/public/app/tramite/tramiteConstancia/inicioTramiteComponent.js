Vue.component("inicio-tram-component", {
    template: "#inicioTramComponent",
    props: {
        tramite: {}
    },
    data: function () {
        return {
            solicitud: JSON.parse(solicitudJson),
            tiposDocumentoAcademico: JSON.parse(tiposDocumentoAcademicoJson),
            idiomas: [],
            alumnos: [],
            showCostoDocumento: false,
            guardando: false,
            ciclo: {},
            isUpdate: false,
            isEdit: IS_EDICION,
            tramiteAcademico: {},
        }
    },
    mounted() {
        let $vue = this;
        console.log("$vue.isEdit ...", $vue.isEdit);
        //$vue.isUpdate = $vue.solicitud.id != null;
    },
    watch: {
        // un getter computado
        tramite: function () {
            // `this` apunta a la instancia de vm
            return this.findAlumno(this.tramite.alumno.id);
        }
    },
    methods: {
        searchAlumnos(nombre) {
            let $vue = this;
            if (nombre != null && nombre != "") {
                $vue.isLoading = true;
                $.ajax({
                    url: APP.url("tramite/solicitudconstancia/searchalumno"),
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.alumnos = response.data;
                    $vue.isLoading = false;
                })

            }
        },
        selectAlumno(item) {
            let $vue = this;
            $vue.findAlumno(item.id);
        },
        clearOption(data) {
            
            let $vue = this;
            $vue.solicitud = {};
            $vue.ciclo = {};
            $vue.costoDocumento = "";
            $vue.showCostoDocumento = false;

            Vue.set($vue.solicitud, "personaContacto", data.persona.nombreCompleto);
            Vue.set($vue.solicitud, "telefono", data.persona.telefono);
            Vue.set($vue.solicitud, "celular", data.persona.celular);
            Vue.set($vue.solicitud, "email", data.persona.email);

        },
        idiomaDocumento(value) {
            let $vue = this;
            this.$delete($vue.solicitud, 'idioma');
            $vue.costoDocumento = "";
            $vue.showCostoDocumento = false;
            $vue.idiomas = value.idiomas;
        },
        costoDocumentoEvent(value) {
            let $vue = this;
            $vue.temp = {};
            $vue.temp.plantillaDocumentoAcademico = {};
            $vue.temp.alumno = $vue.tramite.alumno;
            $vue.temp.plantillaDocumentoAcademico.tipoDocumentoAcademico = $vue.solicitud.tipoDocumentoAcademico;
            $vue.temp.plantillaDocumentoAcademico.idioma = value;
            axios.post('/tramite/solicitudconstancia/allParametros', $vue.temp)
                    .then(response => {
                        if (response.data.success) {
                            if (response.data.data.haveParams) {
                                if (response.data.data.lista.length > 0) {
                                    $vue.$parent.ciclos = response.data.data.lista;
                                    $vue.$parent.haveParams = response.data.data.haveParams;
                                    $vue.$parent.$refs.ciclosModal.open();
                                }
                            }
                        } else {
                            notify(response.data.message, "error");
                        }
                    });

            $vue.tramiteAcademico = {};
            $vue.tramiteAcademico.tramite = {};
            $vue.tramiteAcademico.tramite.alumno = {id: $vue.tramite.alumno.id};
            $vue.tramiteAcademico.tipoDocumentoAcademico = {id: $vue.solicitud.tipoDocumentoAcademico.id};
            $vue.tramiteAcademico.idioma = {id: value.id};

            axios.post('/tramite/solicitudconstancia/calcularPrecio', $vue.tramiteAcademico)
                    .then(response => {
                        console.log(response);
                        if (response.data.success) {
                            $vue.showCostoDocumento = response.data.data.showCostoDocumento;
                            $vue.costoDocumento = response.data.data.costoDocumento;
                            $vue.costoTotal = response.data.data.costoTotal;
                            $vue.cantidadCiclos = response.data.data.cantidadCiclos
                            $vue.$forceUpdate()
                        } else {
                            $vue.showCostoDocumento = false;
                        }
                    });

        },
        submitForm() {
            let $vue = this;
            $vue.solicitud.tramite = $vue.tramite;
            $vue.solicitud.valorParametro = $vue.ciclo.descripcion;

            var valid = $('#formSolicitudConstancia').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.solicitud),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url("tramite/solicitudconstancia");
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        updateForm() {
            let $vue = this;
            $vue.solicitud.tramite = $vue.tramite;
            $vue.solicitud.valorParametro = $vue.ciclo.descripcion;

            var valid = $('#formSolicitudConstancia').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.solicitud),
                success: function (response) {
                    if (response.success) {
                        location.href = APP.url("tramite/solicitudconstancia");
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        subirFoto() {
            let $vue = this;
            $vue.$parent.persona = $vue.tramite.alumno.persona;
            $vue.$parent.$refs.cargarFoto.open();
        },
        findAlumno(id) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno/' + id + '/data'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$parent.alumno = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});