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
            isUpdate: false
        }
    },
    mounted() {
        let $vue = this;
        $vue.isUpdate = $vue.solicitud.id != null;
    },
    watch: {
        // un getter computado
        tramite: function () {
            // `this` apunta a la instancia de vm
            return this.findAlumno(this.tramite.alumno.id);
        }
    },
    methods: {
        customLabel( { persona, codigo}) {
            return `${codigo} - ${persona.apellidosNombres}`;
        },
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
        clearOption(item) {
            let $vue = this;
            $vue.solicitud = {};
            $vue.ciclo = {};
            $vue.costoDocumento = "";
            $vue.showCostoDocumento = false;
        },
        idiomaDocumento(value) {
            let $vue = this;
//            $vue.solicitud.idioma = null;
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
            if ($vue.solicitud.tipoDocumentoAcademico.tipo == 'CONS') {
                $vue.solicitud.tipoDocumentoAcademico.precioDocumento.forEach(function (item) {
                    if (item.idioma.id == value.id) {
                        $vue.showCostoDocumento = true;
                        $vue.costoDocumento = item.precio;

                    }
                });
            }
        },
        submitForm() {
            let $vue = this;
            $vue.solicitud.tramite = $vue.tramite;
            $vue.solicitud.valorParametro = $vue.ciclo.descripcion;
            console.log($vue.solicitud);
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
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        subirFoto() {
            let $vue = this;
            $vue.$parent.persona = $vue.tramite.alumno.persona;
            $vue.$parent.$refs.cargarFoto.open();
        },
        findAlumno(id) {
            let vue = this;

            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno/' + id + '/data'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        vue.$parent.alumno = response.data;
//                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }
});