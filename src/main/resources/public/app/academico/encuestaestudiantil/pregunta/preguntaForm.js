$(function() {

    var Pregunta = {
        init: function() {

            Pregunta.allTiposLikert();

            $('#tipo').select2({
                minimumResultsForSearch: -1,
                allowClear: true
            }).on('change', function(e) {
                Pregunta.loadTipoPregunta(e);
            });

            $('#placePregunta').find('[name="likertInicio"]').select2({
                minimumResultsForSearch: -1,
                allowClear: true
            }).on('change', function(e) {

                $('#placePregunta').find('input[type="hidden"]').remove();
                var self = $(e.currentTarget);
                var limite = parseInt(self.val());
                var opciones = '';
                var char = 'abcdefghijklmnopqrstuvwxyz';
                for (var i = 0; i < limite; i++) {
                    var numeroItem = i + 1;
                    var letra = char.charAt(i);
                    var item3 = '<input name="opcionPregunta[' + i + '].contenido" value="' + numeroItem + '" type="hidden"/>';
                    opciones = opciones + item3;
                }
                $('#placePregunta').append($(opciones));
                console.log("cahnge likert inicioi");
                Pregunta.showTiposByNum(limite);
            });

            $('#placePregunta').find('[name="multiple"]').select2({
                minimumResultsForSearch: -1,
                allowClear: true
            }).on('change', function(e) {

                $('#placePregunta').find('input[type="hidden"]').remove();
                var self = $(e.currentTarget);
                var limite = parseInt(self.val());
                var opciones = '';
                var char = 'abcdefghijklmnopqrstuvwxyz';
                for (var i = 0; i < limite; i++) {
                    var numeroItem = i + 1;
                    var letra = char.charAt(i);
                    var item1 = '<input name="opcionPregunta[' + i + '].esMulti" value="1" type="hidden"/>';
                    var item3 = '<input name="opcionPregunta[' + i + '].contenido" value="Nombre" type="hidden"/>';
                    opciones = opciones + item1 + item3;
                }
                $('#placePregunta').append($(opciones));
            });

            $("#opcionReferencia").select2({
                placeholder: " ",
                allowClear: true,
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/encuestaestudiantil/editor/pregunta/allOpcionReferencia"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {
                            nombre: term,
                            page: page,
                            encuesta: $('[name="examenVirtual.id"]').val()
                        };
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("rev"), referenciaNumero: element.attr('data-pre')});
                    }
                },
                formatResult: function(info) {
                    return '<b>Pgta. ' + info.referenciaNumero + '</b> - ' + '<b>Opción ' + info.codigo + '</b>  - ' + info.nombre;
                },
                formatSelection: function(info) {
                    return '<b>Pgta. ' + info.referenciaNumero + '</b> - ' + '<b>Opción ' + info.codigo + '</b>  - ' + info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            });

            if (preguntaid) {
                $("#grupoTipoLikertForm").select2({minimumResultsForSearch: -1, allowClear: true});
            }

        },
        body: $('body'),
        idReferenciaPregunta: null,
        nameReferenciaPregunta: null,
        numeroReferenciaPregunta: null,
        loadTipoPregunta: function(e) {

            var self = $(e.currentTarget);
            var tipo = self.val();
            $('#placePregunta').html('');

            if (tipo == '') {
                $('#placePregunta').show('fast');
                return;
            }

            if (tipo == 'SIMPLE') {
                var html = $.templates("#plantillaPreguntaSimple").render({});
                $('#placePregunta').html(html);
                Pregunta.totalItemOpcion = 5;
                return;
            }

            if (tipo == 'RPTA_MULTIPLE') {
                var html = $.templates("#plantillaRespuestaMultiple").render({});
                $('#placePregunta').html(html);
                Pregunta.totalItemOpcion = 5;
                return;
            }

            if (tipo == 'MULTIPLE') {

                var html = $.templates("#plantillaPreguntaMultiple").render({});
                $('#placePregunta').html(html);

                $('#placePregunta').find('[name="multiple"]').select2({
                    minimumResultsForSearch: -1,
                    allowClear: true
                }).on('change', function(e) {

                    $('#placePregunta').find('input[type="hidden"]').remove();
                    var self = $(e.currentTarget);
                    var limite = parseInt(self.val());
                    var opciones = '';
                    var char = 'abcdefghijklmnopqrstuvwxyz';
                    for (var i = 0; i < limite; i++) {
                        var numeroItem = i + 1;
                        var letra = char.charAt(i);
                        var item2 = '<input name="opcionPregunta[' + i + '].esMulti" value="1" type="hidden"/>';
                        var item3 = '<input name="opcionPregunta[' + i + '].contenido" value="Nombre" type="hidden"/>';
                        opciones = opciones + item2 + item3;
                    }
                    $('#placePregunta').append($(opciones));
                });
                return;
            }

            if (tipo == 'SINO') {
                var html = $.templates("#plantillaPreguntaSiNo").render({});
                $('#placePregunta').html(html);
                return;
            }

            if (tipo == 'VEFA') {
                var html = $.templates("#plantillaPreguntaVeFa").render({});
                $('#placePregunta').html(html);
                return;
            }

            if (tipo == 'LIKERT') {

                var html = $.templates("#plantillaPreguntaLikert").render({});

                $('#placePregunta').html(html);

                $('#placePregunta').find('[name="likertInicio"]').select2({
                    minimumResultsForSearch: -1,
                    allowClear: true
                }).on('change', function(e) {

                    $('#placePregunta').find('input[type="hidden"]').remove();
                    var self = $(e.currentTarget);
                    var limite = parseInt(self.val());
                    var opciones = '';
                    var char = 'abcdefghijklmnopqrstuvwxyz';
                    for (var i = 0; i < limite; i++) {
                        var numeroItem = i + 1;
                        var letra = char.charAt(i);
                        var item3 = '<input name="opcionPregunta[' + i + '].contenido" value="' + numeroItem + '" type="hidden"/>';
                        opciones = opciones + item3;
                    }
                    $('#placePregunta').append($(opciones));

                    Pregunta.showTiposByNum(limite);
                });
                Pregunta.showTiposByNum(0);
                console.log('tipo likert change');
                return;
            }

            if (tipo == 'ABIERTA') {
                var html = $.templates("#plantillaPreguntaAbierta").render({});
                $('#placePregunta').html(html);
                return;
            }

        },
        preview: function(e) {
            var mimodal = bootbox.alert({
                size: "large",
                title: "Pregunta",
                message: "<div class='text-center'><i class='fa fa-spinner fa-spin' aria-hidden='true'></i></div>",
                buttons: {
                    ok: {label: "Aceptar", className: "btn-link"}
                }
            }).on('shown.bs.modal', function() {
                mimodal.find('.modal-body').css({
                    'overflow-y': 'scroll',
                    'max-height': '600px'});

                var parmForm = $('form').serializeArray();

                var parametros = {};
                var opciones = [];
                var indice = 0;
                var char = 'abcdefghijklmnopqrstuvwxyz';

                $.each(parmForm, function(i, v) {
                    //console.log(i)
                    //console.log(v)
                    var names = v.name.split('.');
                    if (names.length > 1) {
                        var index = names[0].replace(/[\[\]']+/g, '');
                        index = index.replace('opcionPregunta', '');
                        if (!opciones[index]) {
                            opciones[index] = new Object();
                        }
                        opciones[index][names[1]] = v.value;
                        opciones[index]['letra'] = char.charAt(index);
                        indice++;
                    } else {
                        parametros[v.name] = v.value;
                    }
                });

                parametros['opcionPregunta'] = opciones;

                if (parametros.tipo) {
                    if (parametros.tipo == 'LIKERT') {
                        var lik = $('#placePregunta').find('[name="likertInicio"]').select2('val');
                        if (lik != '') {
                            var tipoLik = $("#grupoTipoLikertForm").val();
                            if (tipoLik != '') {
                                var tpo = Pregunta.tiposLikert.find(el => el.cant == lik);
                                var opt = tpo.tipos.find(el => el.id == tipoLik);
                                $.each(parametros['opcionPregunta'], function(i, v) {
                                    var ooo = opt.opciones.find(el => el.peso == v.contenido);
                                    v.contenido = ooo.opcion;
                                    v.letra = ooo.peso;
                                    console.log(v);
                                });
                            }
                        }
                    }
                }

                var html = $.templates("#templatePreview").render(parametros);
                mimodal.find('.modal-body').html(html);
                mimodal.find('.modal-body').find("select.select2single").select2({minimumResultsForSearch: -1});
            });
        },
        removerOpcion: function(e) {
            var self = $(e.currentTarget);
            var tr = self.closest('tr');
            var cantidadOpciones = $('#placePregunta').find('table>tbody>tr');
            if (cantidadOpciones.length < 3) {
                notify("No se admite menos de un ítem", "error");
                return;
            }
            tr.remove();
            Pregunta.reindexOpcion();
        },
        addOpcion: function(e) {
            var cantidadOpciones = $('#placePregunta').find('table>tbody>tr');
            if (cantidadOpciones.length > 25) {
                return;
            }
            var opcion = $.templates("#plantillaAddOpcion").render({});
            $('#placePregunta').find('table>tbody').append(opcion);
            Pregunta.reindexOpcion();
        },
        reindexOpcion: function () {
            var trs = $('#placePregunta').find('table>tbody>tr');
            $('#placePregunta').parsley('destroy');
            var char = 'abcdefghijklmnopqrstuvwxyz';
            trs.each(function (i, v) {
                var numeracion = i + 1;
                var self = $(v);

                self.find('td:first').text(char.charAt(i));

                self.find('td:eq(1)>input').attr('name', 'opcionPregunta[' + (i) + '].contenido');
                self.find('td:eq(1)>input').attr('data-parsley-errors-container', '#erroropcion' + numeracion);
                self.find('td:eq(1)>.tagerror').attr('id', 'erroropcion' + numeracion);

                self.find('td:eq(2)>input').attr('name', 'opcionPregunta[' + (i) + '].esOtro');
                self.find('td:eq(2)>input').attr('data-parsley-errors-container', '#errorotro' + numeracion);
                self.find('td:eq(2)>.tagerror').attr('id', 'errorotro' + numeracion);

                self.find('td:eq(3)>input.itemReferencia').attr('name', 'opcionPregunta[' + (i) + '].referencia.id');

            });
            $('#placePregunta').parsley();
        },
        addReferencia: function (e) {
            var self = $(e.currentTarget);
            var tdParent = self.closest('td');
            Pregunta.idReferenciaPregunta = null;
            Pregunta.nameReferenciaPregunta = null;
            var mimodal = bootbox.confirm({
                title: "Seleccione una referencia",
                message: "<div class='text-center'><i class='fa fa-spinner fa-spin' aria-hidden='true'></i></div>",
                buttons: {
                    confirm: {label: "Agregar", className: "btn-primary"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var html = $.templates("#templateAddRef").render({
                            id: Pregunta.idReferenciaPregunta,
                            nombre: Pregunta.nameReferenciaPregunta,
                            numero: Pregunta.numeroReferenciaPregunta
                        });
                        tdParent.html(html);
                        mimodal.modal('hide');
                        Pregunta.reindexOpcion();
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            }).on('shown.bs.modal', function () {
                mimodal.find('.modal-body').css({
                    'overflow-y': 'scroll',
                    'max-height': '600px'});
                Pregunta.allReferencia(mimodal, 0);
                mimodal.find('table>tbody>tr>td>input[type=radio]').removeAttr('checked');
                mimodal.find('table>tbody>tr').click(function (e) {
                    var el = $(e.currentTarget);
                    el.find('input[type=radio]').prop('checked', true);
                    Pregunta.idReferenciaPregunta = el.attr('rel');
                    Pregunta.numeroReferenciaPregunta = el.attr('rev');
                    Pregunta.nameReferenciaPregunta = el.attr('data-cont-title');
                });
            });
        },
        allReferencia: function (mimodal, idNotInclude) {
            $.ajax({
                url: APP.url('academico/encuestaestudiantil/editor/pregunta/allReferencia'),
                type: 'POST',
                async: false,
                data: {
                    id: idNotInclude,
                    'examenVirtual.id': $('[name="examenVirtual.id"]').val(),
                },
                success: function (response) {
                    if (response.success) {
                        mimodal.find('.bootbox-body').html(response.data);
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        removerReferencia: function (e) {
            var self = $(e.currentTarget);
            var tdParent = self.closest('td');
            var html = $.templates("#templateRemoveRef").render({});
            tdParent.html(html);
        },
        allTiposLikert: function () {
            $.ajax({
                url: APP.url('academico/encuestaestudiantil/editor/pregunta/alltipolikert'),
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        Pregunta.tiposLikert = response.data;
                    } else {
                        notify(Messages.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        showTiposByNum: function (limite) {

            $("#grupoTipoLikertForm").html($("<option/>"));
            $("#grupoTipoLikertForm").select2('val', "");
            $("#grupoTipoLikertForm").select2('destroy');
            $("#grupoTipoLikertForm").select2({minimumResultsForSearch: -1, allowClear: true});

            var opciones = '<option/>';

            var cantidad = Pregunta.tiposLikert.length;
            if (cantidad < 1) {
                return;
            }
            var opt = Pregunta.tiposLikert.find(el => el.cant == limite);
            if (!opt) {
                return;
            }

            for (var i = 0; i < opt.tipos.length; i++) {
                var item3 = '<option value="' + opt.tipos[i].id + '"   >' + opt.tipos[i].grupo + '</option>';
                opciones = opciones + item3;
            }

            $("#grupoTipoLikertForm").html($(opciones));
            $("#grupoTipoLikertForm").select2('destroy');
            $("#grupoTipoLikertForm").select2({minimumResultsForSearch: -1, allowClear: true});

        }
    };

    Pregunta.body.delegate('#preview', 'click', function (e) {
        Pregunta.preview(e);
    });

    Pregunta.body.delegate('.removerOpcion', 'click', function (e) {
        Pregunta.removerOpcion(e);
    });

    Pregunta.body.delegate('.addOpcion', 'click', function (e) {
        Pregunta.addOpcion(e);
    });

    Pregunta.body.delegate('.addReferencia', 'click', function (e) {
        Pregunta.addReferencia(e);
    });

    Pregunta.body.delegate('.removerReferencia', 'click', function (e) {
        Pregunta.removerReferencia(e);
    });

    Pregunta.init();
});