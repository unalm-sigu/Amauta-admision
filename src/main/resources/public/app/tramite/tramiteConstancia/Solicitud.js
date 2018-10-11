Vue.component("multiselect", window.VueMultiselect.default)
console.log(JSON.parse(solicitudJson));
new Vue({
    el: '#main',
    data: {
        alumnos: [],
        solicitud: JSON.parse(solicitudJson),
        tiposDocumentoAcademico: JSON.parse(tiposDocumentoAcademicoJson),
        idiomas: [],
        persona: {},
        tramite: {},
        dataCargarFoto: {
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        },
        showCostoDocumento: false,
        mensajeerror: "",
        guardando: false,
    },
    computed: {

    },
    created() {
        let $vue = this;
        if ($vue.solicitud.tramite != null) {
            $vue.tramite = $vue.solicitud.tramite;
        }
    },
    mounted: function () {

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
        searchColaborador(nombre) {
            let $vue = this;
            if (nombre != null && nombre != "") {
                $vue.isLoading = true;
                $.ajax({
                    url: APP.url("tramite/solicitudconstancia/searchcolaborador/" + nombre),
                    type: 'post',
                }).then(response => {
                    $vue.tramites = response.data;
                    $vue.isLoading = false;
                })
            }
        },
        idiomaDocumento(value) {
            let $vue = this;
            $vue.idiomas = value.idiomas;

        },
        costoDocumento(value) {
            let $vue = this;

            console.log($vue.solicitud.tipoDocumentoAcademico);
//            if ($vue.solicitud.tipoDocumentoAcademico.tipo == 'CONS') {
            $vue.solicitud.tipoDocumentoAcademico.precioDocumento.forEach(function (item) {
                if (item.idioma.id == value.id) {
                    $vue.showCostoDocumento = true;
                    $vue.costoDocumento = item.precio;
                }
            })
//            }
        },
        customLabel( { persona}) {
            return `${persona.apellidosNombres}`
        },
        createCargarFoto: function () {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $vue.solicitud.tramite = $vue.tramite;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/onlyfoto'),
                contentType: "application/json",
                data: JSON.stringify($vue.solicitud),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.cargarFoto.close();
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        getImage(event) {
            var $vue = this;
            $vue.file = event.target.files[0];
            let formData = new FormData();
            formData.append('file', $vue.file);
            AXIOS.post('/tramite/solicitudconstancia/upload',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
            ).then(function (response) {
                $vue.persona.rutaFotoTemporal = response.data.data.ruta;
                console.log(response);
            }).catch(function () {
                console.log('FAILURE!!');
            });
        },
        subirFoto() {
            let $vue = this;
            $vue.persona = $vue.tramite.alumno.persona;
            $vue.$refs.cargarFoto.open();
        },
        submitForm() {
            let $vue = this;
            $vue.solicitud.tramite = $vue.tramite;
            console.log($vue.solicitud)
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
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});
