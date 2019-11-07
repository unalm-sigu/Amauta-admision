Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#main',
    data: {
        showLugarNacimiento: showLugarNacimiento,
        showUbicacionDomicilio: codigoPaisDomicilio == 'PE',
        files: [],
        fotoCargada: false,
        guardandoFoto: false,
        alumno: JSON.parse(alumnoJson),
        persona: {id: -200, foto: "gj34h5j34h5b34y"}
    },
    created() {
        let vue = this;
    },
    mounted: function () {
        let vue = this;
        if (vue.alumno.persona.foto == null) {
            vue.alumno.persona.foto = "";
        }

        $(".date").datepicker();
        $(".numerico").numeric({negative: false});

        $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
        $('[name="cicloIngreso.id"]').select2({minimumResultsForSearch: -1});
        $('[name="modalidadEstudio.id"]').select2({minimumResultsForSearch: -1});

        $(".buscar-distrito").select2(vue.buscarDistrito());

        $('#nacionalidad').select2(vue.buscarPais());
        $('#carrera').select2(vue.buscarCarrera());
        $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarDirNacimiento();
        });
        $('#paisDomicilio').select2(vue.buscarPais()).on('change.select2', function (e) {
            vue.mostrarUbicacionDomicilio();
        });
    },
    methods: {
        buscarCarrera: function () {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("academico/alumno/allCarrera"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarPais: function () {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarDistrito: function () {
            return {
                placeholder: "  ",
                allowClear: true,
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allDistritos"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel")});
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarDistrito").render(info);
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        mostrarDirNacimiento: function () {
            var vue = this;
            var dataPaisNac = $("#paisNacimiento").select2("data");
            if (dataPaisNac.codigo === "PE") {
                vue.showLugarNacimiento = true;
                setTimeout(function () {
                    $("#distNacimiento").select2(vue.buscarDistrito());
                }, 500);
                $("#distNacimiento").prop('required', true);
            } else {
                vue.showLugarNacimiento = false;
                $("#distNacimiento").select2("val", "");
                $("#distNacimiento").prop('required', false);
            }
        },
        mostrarUbicacionDomicilio: function () {
            var vue = this;
            var dataPaisUni = $("#paisDomicilio").select2("data");
            if (dataPaisUni.codigo === "PE") {
                vue.showUbicacionDomicilio = true;
                setTimeout(function () {
                    $('#ubicacionDomicilio').select2(vue.buscarDistrito());
                }, 500);
                $("#ubicacionDomicilio").prop('required', true);
                $("#ubicacionDomicilio").select2("val", "");
            } else {
                vue.showUbicacionDomicilio = false;
                $("#ubicacionDomicilio").removeProp('required');
            }
        },
        submitForm: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formAlumno").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/alumno/saveAlumnoFisico'),
                type: 'POST',
                async: true,
                data: $("#formAlumno").serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/alumno'));
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function () {
                    self.btnEnable();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        checkMatricula: function () {
            var chkBox = $('#chkbxGenMat');
            if (chkBox.is(':checked')) {
                $("#codigo").prop("disabled", true);
                $("#codigo").prop("required", false);
                $("#codigo").val("");
            } else {
                $("#codigo").prop("disabled", false);
                $("#codigo").prop("required", true);
            }
        },
        sinEspacios: function (e) {
            APP.eliminarEspacios($(e.currentTarget));
        },
        nombrePersona: function (e) {
            APP.revisarNombre($(e.currentTarget));
        },
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                if (!/\.(jpg|jpeg|png)$/i.test(newFile.name)) {
                    swal('¡Este tipo de archivo no esta permitido!', ' ', 'error', {buttons: {ok: "Aceptar"}});
                    return prevent();
                }
            }
            let URL = window.URL || window.webkitURL
            if (URL && URL.createObjectURL) {
                $vue.$refs.imagen.src = URL.createObjectURL(newFile.file)
            }
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            $vue.isprocess = true;
            if (newFile) {
                $('#progress-bar').css('width', newFile.progress + '%');
                if (Boolean(newFile) !== Boolean(oldFile) || oldFile.error !== newFile.error) {
                    if (!$vue.$refs.upload.active) {
                        $vue.$refs.upload.active = true;
                    }
                }
            }
            if (oldFile && newFile) {
                if (newFile.success !== oldFile.success) {
                    $vue.fotoCargada = true;
                    $vue.persona.id = $vue.alumno.persona.id;
                    $vue.persona.foto = newFile.response.data.ruta;
                    //$vue.imagentemporal = newFile.response.data.ruta;
                    //$vue.persona.foto = newFile.response.data.ruta;
                }
            }
        },
        confirmarFoto() {
            let $vue = this;
            let alumno = JSON.parse(JSON.stringify($vue.alumno));
            alumno.persona = $vue.persona;

            $vue.guardandoFoto = true;
            axios.post("/academico/alumno/saveFotoCarnet", alumno).then(response => {
                $vue.guardandoFoto = false;
                if (response.data.success) {
                    $vue.alumno = response.data.data.alumno;
                    $vue.fotoCargada = false;
                    notify(response.data.message, "info");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(e => {
                $vue.guardandoFoto = false;
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        deshacerFoto() {
            let $vue = this;
            $vue.guardandoFoto = false;
            $vue.fotoCargada = false;
            $vue.$refs.imagen.src = $vue.alumno.persona.foto;
        }

    }
});
