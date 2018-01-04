$(function () {

    new Vue({
        el: '#main',
        data: {
            showLugarNacimiento: false,
            showUniverdidadName: false,
        },
        created() {
            let vue = this;
        },
        mounted: function () {
            let vue = this;
            $('[name="persona.tipoDocumento.id"]').select2({minimumResultsForSearch: -1});
            $(".buscar-distrito").select2(vue.buscarDistrito());
            $(".date").datepicker();
            $(".numerico").numeric({negative: false});
            $('#paisNacimiento').select2(vue.buscarPais()).on('change.select2', function (e) {
                vue.mostrarDirNacimiento();
            });
            $('#paisUniversidad').select2(vue.buscarPais()).on('change.select2', function (e) {
                vue.mostrarUniversidadName();
            });
        },
        methods: {
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
                    $("#distNacimiento").prop('required', true);
                } else {
                    vue.showLugarNacimiento = false;
                    $("#distNacimiento").select2("val", "");
                    $("#distNacimiento").prop('required', false);
                }
            },
            mostrarUniversidadName: function () {
                var vue = this;
                var dataPaisUni = $("#paisUniversidad").select2("data");
                if (dataPaisUni.codigo === "PE") {
                    vue.showUniverdidadName = true;
                    $("#distNacimiento").prop('required', true);
                } else {
                    vue.showUniverdidadName = false;
                    $("#distNacimiento").select2("val", "");
                    $("#distNacimiento").prop('required', false);
                }
            },
            submitForm: function () {
                $.ajax({
                    url: APP.url('academico/visitante/alumno/save'),
                    type: 'POST',
                    async: true,
                    data: $("#formAlumnoVisitante").serialize(),
                    success: function (response) {
                        if (response.success) {
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        }
    });

});
