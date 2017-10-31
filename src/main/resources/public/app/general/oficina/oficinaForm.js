$(function () {

    var Oficina = {
        form: $("form"),
        body: $("body"),
        init: function () {

            $('[name="tipoOficina"]').select2({
                allowClear: true,
                minimumInputLength: -1
            }).on('change', function (e) {
                if (e.val != '') {
                    Oficina.addReferencia(e.val);
                } else {
                    Oficina.removeReferencia();
                }
            });

            $('[name="oficinaSuperior.id"]').select2({
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("general/oficina/allUnidadSuperior"),
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
                        callback({id: element.val(), codigo: element.attr("rev"), nombre: element.attr("rel")});
                    }
                },
                formatResult: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                formatSelection: function (info) {
                    return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });

            $("[name='personaJefe.id']").select2({
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("general/oficina/allPersona"),
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
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return  info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });

            $("[name='cargoJefe.id']").select2({
                allowClear: true,
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("general/oficina/allCargo"),
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
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return  info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });


            if ($("[name='id']").val() != '') {
                $('[name="tipoOficina"]').trigger('change');
            }

        },
        addReferencia: function (tipo) {
            var html = $.templates("#referenciaTemplate").render({});
            $("#placeReferencia").html(html);
            if (tipo == undefined) {
                if ($("[name='id']").val() != '') {
                    tipo = $('#tipoOficina').val();
                }
            }
            $.ajax({
                url: APP.url('general/oficina/allReferencia'),
                type: 'POST',
                async: false,
                data: {tipo: tipo},
                success: function (response) {
                    if (response.success) {
                        if (response.data.length < 1) {
                            Oficina.removeReferencia();
                        }
                        Oficina.form.find('[name="instanciaOficina"]').select2({
                            allowClear: true,
                            minimumInputLength: 0,
                            placeholder: " ",
                            data: {results: response.data},
                            initSelection: function (element, callback) {
                                if (element.val() != "") {
                                    callback({id: element.val(), codigo: element.attr("rev"), nombre: element.attr("rel")});
                                }
                            },
                            formatResult: function (info) {
                                return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                            },
                            formatSelection: function (info) {
                                return '<b>' + info.codigo + '</b>  - ' + info.nombre;
                            },
                            escapeMarkup: function (m) {
                                return m;
                            }
                        });
                    } else {
                        $("#placeReferencia").html('');
                    }
                },
                error: function () {
                    $("#placeReferencia").html('');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        removeReferencia: function () {
            $("#placeReferencia").html('');
        }
    };

    Oficina.init();

});
