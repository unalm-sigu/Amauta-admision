$(function () {

    var AulaForm = {
        form: null,
        init: function () {
            var tipoAmbiente = $('[name="tipoAmbiente"]').select2({placeholder: "Seleccione el tipo ambiente"});

            if ($('[name="id"]').val() != '') {
                AulaForm.viewDivs(tipoAmbiente);
            }

        },
//        loadSedes: function () {
//            $(".addBody").find("[name='sede.id']").select2({
//                allowClear: true,
//                placeholder: "Seleccione una sede",
//                minimumInputLength: 1,
//                ajax: {
//                    url: APP.url("general/aula/allSedes"),
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
//                        var datos = {
//                            id: element.val(),
//                            nombre: element.attr("rel")
//                        };
//                        callback(datos);
//                    }
//                },
//                formatResult: function (info) {
//                    return info.nombre;
//                },
//                formatSelection: function (info) {
//                    return info.nombre;
//                },
//                escapeMarkup: function (m) {
//                    return m;
//                }
//            });
//        },
        loadGestor: function () {
            $(".addBody").find("[name='oficinaSupervisora.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un coordinador",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("general/aula/allGestores"),
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
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        loadAulasSuperior: function () {
            $(".addBody").find("[name='aulaSuperior.id']").select2({
                allowClear: true,
                placeholder: "Seleccione un ambiente superior",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("general/aula/allAulasSuperiores"),
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
                        var datos = {
                            id: element.val(),
                            nombre: element.attr("rel")
                        };
                        callback(datos);
                    }
                },
                formatResult: function (info) {
                    return  info.nombre;
                },
                formatSelection: function (info) {
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            });
        },
        viewDivs: function ($this) {
            $this.parsley().destroy();
            var valor = $this.select2('val');
            var body = $(".addBody");

            switch (valor) {
                case 'AMB':
                    body.html($.templates("#divAmbiente").render(null));
                    break;
                case 'EDI':
                    body.html($.templates("#divEdificio").render(null));
                    break;
                case 'ESP':
                    body.html($.templates("#divEspacio").render(null));
                    break;
            }

            body.find(".numerico").numeric({negatice: false});
            //body.find('[name="tipoAula.id"]').select2({placeholder: "Seleccione el tipo aula"});
            body.find('[name="tipoAula.id"]').select2();
            body.find('[name="sede.id"]').select2();

//            AulaForm.loadSedes();
            AulaForm.loadAulasSuperior();
            AulaForm.loadGestor();
        },
        saveUpdate: function () {
            var form = $("#formularioAula");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        },
        validandoOficina: function (e) {

            if (e.added != undefined) {
                var codigo = e.added.codigo;
                if (codigo == 'OERA') {
                    $('[name="nombre"]').val("");
                    $('[name="nombre"]').attr("readonly", true);
                }
            } else {
                $('[name="nombre"]').removeAttr("readonly");
            }

        }
    };
    AulaForm.init();

    $("body").delegate(".save-update-aula", "click", function (e) {
        AulaForm.saveUpdate(e);
    });

    $("body").delegate("[name='sede.id']", "change", function () {
        $(this).parsley().destroy();
    });

    $("body").delegate("[name='aulaSuperior.id']", "change", function () {
        $(this).parsley().destroy();
    });

    $("body").delegate("[name='oficinaSupervisora.id']", "change", function (e) {
        AulaForm.validandoOficina(e);
        $(this).parsley().destroy();
    });

    $("body").delegate("[name='tipoAmbiente']", "change", function () {
        AulaForm.viewDivs($(this));
    });

    $('#codigo').keyup(function () {
        this.value = this.value.toUpperCase().replace(/\s/g, '');
    });



});