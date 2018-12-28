//$(function () {
//
//    var Oficina = {
//        form: $("form"),
//        body: $("body"),
//        init: function () {
//
//            $('[name="tipoOficina.id"]').select2({
//                allowClear: true,
//                minimumInputLength: -1
//            }).on('change', function (e) {
//                if (e.val != '') {
//                    Oficina.addReferencia(e.val);
//                } else {
//                    Oficina.removeReferencia();
//                }
//            });
//
//            $('[name="oficinaSuperior.id"]').select2({
//                allowClear: true,
//                minimumInputLength: 2,
//                placeholder: " ",
//                ajax: {
//                    url: APP.url("general/oficina/allUnidadSuperior"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        callback({id: element.val(), codigo: element.attr("rev"), nombre: element.attr("rel")});
//                    }
//                },
//                formatResult: function (info) {
//                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
//                },
//                formatSelection: function (info) {
//                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//
//            $("[name='personaJefe.id']").select2({
//                allowClear: true,
//                minimumInputLength: 2,
//                placeholder: " ",
//                ajax: {
//                    url: APP.url("general/oficina/allPersona"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        callback({id: element.val(), nombre: element.attr("rel")});
//                    }
//                },
//                formatResult: function (info) {
//                    return  info.nombre;
//                },
//                formatSelection: function (info) {
//                    return  info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//
//            $("[name='cargoJefe.id']").select2({
//                allowClear: true,
//                minimumInputLength: 2,
//                placeholder: " ",
//                ajax: {
//                    url: APP.url("general/oficina/allCargo"),
//                    dataType: 'json',
//                    type: 'post',
//                    data: function (term, page) {
//                        return {nombre: term, page: page};
//                    },
//                    results: function (response, page) {
//                        return {results: response.data};
//                    }
//                },
//                initSelection: function (element, callback) {
//                    if (element.val() != "") {
//                        callback({id: element.val(), nombre: element.attr("rel")});
//                    }
//                },
//                formatResult: function (info) {
//                    return  info.nombre;
//                },
//                formatSelection: function (info) {
//                    return  info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//
//
//            if ($("[name='id']").val() != '') {
//                $('[name="tipoOficina"]').trigger('change');
//            }
//
//        },
//        addReferencia: function (tipo) {
//            var html = $.templates("#referenciaTemplate").render({});
//            $("#placeReferencia").html(html);
//            if (tipo == undefined) {
//                if ($("[name='id']").val() != '') {
//                    tipo = $('#tipoOficina').val();
//                }
//            }
//            $.ajax({
//                url: APP.url('general/oficina/allReferencia'),
//                type: 'POST',
//                async: false,
//                data: {tipo: tipo},
//                success: function (response) {
//                    if (response.success) {
//                        if (response.data.length < 1) {
//                            Oficina.removeReferencia();
//                        }
//                        Oficina.form.find('[name="instanciaOficina"]').select2({
//                            allowClear: true,
//                            minimumInputLength: 0,
//                            placeholder: " ",
//                            data: {results: response.data},
//                            initSelection: function (element, callback) {
//                                if (element.val() != "") {
//                                    callback({id: element.val(), codigo: element.attr("rev"), nombre: element.attr("rel")});
//                                }
//                            },
//                            formatResult: function (info) {
//                                return '<b>' + info.codigo + '</b>  - ' + info.nombre;
//                            },
//                            formatSelection: function (info) {
//                                return '<b>' + info.codigo + '</b>  - ' + info.nombre;
//                            },
//                            escapeMarkup: function (m) {
//                                return m;
//                            }
//                        });
//                    } else {
//                        $("#placeReferencia").html('');
//                    }
//                },
//                error: function () {
//                    $("#placeReferencia").html('');
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        removeReferencia: function () {
//            $("#placeReferencia").html('');
//        },
//        sendDatos() {
//            var form = $("#formOficina");
//            if (!form.parsley().validate()) {
//                return;
//            }
//
//            $.ajax({
//                url: APP.url('general/oficina/save'),
//                type: 'POST',
//                async: true,
//                data: form.serialize(),
//                success: function (response) {
//                    if (response.success) {
//                        notify(response.message, "info");
//                        setTimeout(function () {
//                            location.href = APP.url('general/oficina')
//                        }, 1200);
//                    } else {
//                        notify(response.message, "error");
//                    }
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        }
//    };
//
//    $("body").delegate(".send-datos", "click", function () {
//        Oficina.sendDatos();
//    });
//
//    Oficina.init();
//
//});

Vue.component("multiselect", window.VueMultiselect.default);
console.log(JSON.parse(tiposJson))
new Vue({
    el: '#oficinaFormVUE',
    data: {
        oficina: JSON.parse(oficinaJson),
        tipos: JSON.parse(tiposJson),
        oficinaSuperior: [],
        instanciaOficina: [],
        personas: [],
        cargosJefe: [],
        hayInstancia: false,
        tipoOficina: ''
    },
    mounted() {
        let $vue = this;
        $vue.tipoOficina = $vue.oficina.tipoOficina;
    },
    watch: {

        tipoOficina(value) {
            let $vue = this;
            $vue.oficina.tipoOficina = value;
            if (value != undefined) {
                this.tipoSelect(value);
            }
        }
    },
    methods: {

        findOficinaSuperior(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/oficina/allUnidadSuperior'),
                dataType: 'json',
                type: 'POST',
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.oficinaSuperior = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allPersonas(nombre) {
            let $vue = this;
            axios.post('/general/oficina/allPersona', {nombre: nombre})
                    .then(response => {
                        if (response.data.success) {
                            $vue.personas = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        allCargoJefe(nombre) {
            let $vue = this;

            $.ajax({
                url: APP.url('general/oficina/allCargo'),
                dataType: 'json',
                type: 'POST',
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.cargosJefe = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        tipoSelect(value) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/oficina/allReferencia'),
                dataType: 'json',
                type: 'POST',
                data: {tipo: value.id},
                success: function (response) {
                    if (response.data.length > 0) {
                        $vue.instanciaOficina = response.data;
                        $vue.instanciaOficina.forEach(function (item) {
                            if ($vue.oficina.instanciaOficina == item.id) {
                                $vue.oficina.instanciaOficina = item;
                            } else {
                                $vue.oficina.instanciaOficina = null;
                            }
                        })
                        $vue.hayInstancia = true;
                    } else {
                        $vue.hayInstancia = false;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        save() {
            let $vue = this;
            let target = $("#formControl");
            target.parsley().destroy();
            target.parsley();
            if (target.parsley().validate() !== true) {
                return;
            }
            var data = {};
            data = Object.assign({}, $vue.oficina);
            if ($vue.oficina.instanciaOficina != undefined) {
                data.instanciaOficina = $vue.oficina.instanciaOficina.id;
            }
            $.ajax({
                url: APP.url('general/oficina/save'),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "success");
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
    