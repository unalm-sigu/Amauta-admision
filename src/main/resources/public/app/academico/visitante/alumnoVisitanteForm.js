$(function() {

    new Vue({
        el: '#main',
        data: {
            showLugarNacimiento: false,
            showUniverdidadName: false,
            showUniverdidadPeru: false,
        },
        created() {
            let vue = this;
        },
        mounted: function() {

            let vue = this;

            $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
            $('[name="cicloEstudia.id"]').select2({minimumResultsForSearch: -1});

            $(".buscar-distrito").select2(vue.buscarDistrito());
            $(".date").datepicker();

            $(".numerico").numeric({negative: false});

            $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function(e) {
                vue.mostrarDirNacimiento();
            });

            $('#nacionalidad').select2(vue.buscarPais());

            $('#paisUniversidad').select2(vue.buscarPais()).on('change.select2', function(e) {
                vue.mostrarUniversidadName();
            });
            $('#univ-peru').select2(vue.buscarUniversidad());
        },
        methods: {
            buscarUniversidad: function() {
                return {
                    minimumInputLength: 2,
                    ajax: {
                        url: APP.url("comun/buscar/allUniversidad"),
                        dataType: 'json',
                        type: 'post',
                        data: function(term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function(response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function(element, callback) {
                        if (element.val() != "") {
                            callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                        }
                    },
                    formatResult: function(info) {
                        return info.nombre + " | " + info.codigo;
                    },
                    formatSelection: function(info) {
                        return info.nombre;
                    },
                    escapeMarkup: function(m) {
                        return m;
                    }
                };
            },
            buscarPais: function() {
                return {
                    minimumInputLength: 2,
                    ajax: {
                        url: APP.url("comun/buscar/allPaises"),
                        dataType: 'json',
                        type: 'post',
                        data: function(term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function(response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function(element, callback) {
                        if (element.val() != "") {
                            callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                        }
                    },
                    formatResult: function(info) {
                        return info.nombre + " | " + info.codigo;
                    },
                    formatSelection: function(info) {
                        return info.nombre;
                    },
                    escapeMarkup: function(m) {
                        return m;
                    }
                };
            },
            buscarDistrito: function() {
                return {
                    placeholder: "  ",
                    allowClear: true,
                    minimumInputLength: 2,
                    ajax: {
                        url: APP.url("comun/buscar/allDistritos"),
                        dataType: 'json',
                        type: 'post',
                        data: function(term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function(response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function(element, callback) {
                        if (element.val() != "") {
                            callback({id: element.val(), nombre: element.attr("rel")});
                        }
                    },
                    formatResult: function(info) {
                        return $.templates("#divBuscarDistrito").render(info);
                    },
                    formatSelection: function(info) {
                        return info.nombre;
                    },
                    escapeMarkup: function(m) {
                        return m;
                    }
                };
            },
            mostrarDirNacimiento: function() {
                var vue = this;
                var dataPaisNac = $("#paisNacimiento").select2("data");
                if (dataPaisNac.codigo === "PE") {
                    vue.showLugarNacimiento = true;
                    setTimeout(function() {
                        $(".buscar-distrito").select2(vue.buscarDistrito());
                    }, 500);
                    $("#distNacimiento").prop('required', true);
                } else {
                    vue.showLugarNacimiento = false;
                    $("#distNacimiento").select2("val", "");
                    $("#distNacimiento").prop('required', false);
                }
            },
            mostrarUniversidadName: function() {
                var vue = this;
                var dataPaisUni = $("#paisUniversidad").select2("data");
                if (dataPaisUni.codigo === "PE") {
                    vue.showUniverdidadName = false;
                    vue.showUniverdidadPeru = true;
                    console.log($('#univ-peru'));
                    setTimeout(function() {
                        $('#univ-peru').select2(vue.buscarUniversidad());
                    }, 500);
                    $("#distNacimiento").prop('required', true);
                } else {
                    vue.showUniverdidadName = true;
                    vue.showUniverdidadPeru = false;
                    $("#distNacimiento").select2("val", "");
                    $("#distNacimiento").prop('required', false);
                }
            },
            submitForm: function(e) {
                var self = $(e.currentTarget);
                console.log(self);
                self.btnDisabled();
                if (!$("#formAlumnoVisitante").parsley().validate() == true) {
                    self.btnEnable();
                    return;
                }
                $.ajax({
                    url: APP.url('academico/visitante/alumno/save'),
                    type: 'POST',
                    async: true,
                    data: $("#formAlumnoVisitante").serialize(),
                    success: function(response) {
                        if (response.success) {
                            notify(response.message, "info");
                            location.reload();
                        } else {
                            notify(response.message, "error");
                        }
                        self.btnEnable();
                    },
                    error: function() {
                        self.btnEnable();
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        }
    });

});
