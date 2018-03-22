$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('encuesta/respuesta/list'),
            perPageDefault: 1000
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        $("#opopop").prepend($("#headDynatable"));
        $('#headDynatable').removeClass('hide');

        $(".item-draggable").draggable({
            axis: "y",
            cursor: "move",
            revert: true,
            containment: "#contenedor-respuestas",
            start: function () {
                $(this).addClass("item-mov");
            },
            stop: function () {
                $(this).removeClass("item-mov");
            }
        });

        $(".contenedor-dragg").droppable({
            accept: ".item-draggable",
            drop: function (event, ui) {
                var $this = $(this);
                var divDragg = ui.draggable;
                var idContenedor = $this.attr("rel");
                var idItem = divDragg.attr("rel");
                if (idContenedor == divDragg) {
                    return;
                }

                Respuestas.verUnificarFrases($this, divDragg);
            }
        });
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {
        record.index = rowIndex;

        var html = $.templates("#respuestaTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Respuestas = {
        itemDraggable: null,
        body: $('body'),
        init: function () {
            $("#pregunta").select2().on("change", function (e) {
                Respuestas.reloadOpciones(e.val);
                dynatable.queries.add("pregunta", e.val);
                dynatable.process();
            });

            Respuestas.reloadOpciones($("#pregunta").val());
        },
        reloadOpciones: function (id) {
            $("#opcion").select2("val", "");
            $.ajax({
                url: APP.url('encuesta/respuesta/allOpciones'),
                type: 'POST',
                async: false,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        $("#opcion").select2("destroy");
                        $("#opcion").select2({data: response.data}).on("change", function (e) {
                            dynatable.queries.add("pregunta", $("#pregunta").val());
                            dynatable.queries.add("opcion", e.val);
                            dynatable.process();
                        });
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        verUnificarFrases: function ($this, divDragg) {
            $this.append('<div class="item-draggable block">' + divDragg.html() + '</div>');
            Respuestas.itemDraggable = divDragg;
            divDragg.addClass("hide");

            var contenido = 'Va unificar las siguientes oraciones. Marque cual es la que va a sustituir a la otra:';
            contenido += '<form id="formFrases">';
            contenido += '<input type="hidden" name="pregunta.id" value="' + $("#pregunta").val() + '"/>';
            contenido += '<input type="hidden" name="id" value="' + $("#opcion").val() + '"/>';

            $this.find(".item-draggable").each(function (i, item) {
                contenido += '<input type="hidden" name="frase' + i + '" value="' + $(item).html() + '"/>';
            });

            contenido += '<ul class="ul-items-mov">';
            $this.find(".item-draggable").each(function (i, item) {
                contenido += '<li class="m-t"><div class="checkbox"><label><input type="radio" class="" name="indiceFrase" value="' + i + '"/> &nbsp; ' + $(item).html() + '</label></div></li>';
            });
            contenido += '</ul></form>';

            var modalFrase = bootbox.confirm({
                message: contenido,
                buttons: {
                    confirm: {label: "Si, unificarlas", className: "btn-warning"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {

                    if (result) {
                        Respuestas.unificarFrases(modalFrase);
                    } else {
                        modalFrase.modal('hide');
                        $this.find(".item-draggable").last().remove();
                        divDragg.removeClass("hide");
                    }
                    return false;
                }
            });
        },
        unificarFrases: function (modalFrase) {
            $.ajax({
                url: APP.url('encuesta/respuesta/unirFrases'),
                type: 'POST',
                async: false,
                data: $("#formFrases").serialize(),
                success: function (response) {
                    modalFrase.modal('hide');
                    dynatable.process();
                    if (response.success) {
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    modalFrase.modal('hide');
                    dynatable.process();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verModificarFrase: function ($this) {
            var contenido = '<p>Ingrese la nueva frase con la que reemplazar:</p>';
            contenido += '<form id="formCambioFrase">';
            contenido += '<input type="hidden" name="pregunta.id" value="' + $("#pregunta").val() + '"/>';
            contenido += '<input type="hidden" name="id" value="' + $("#opcion").val() + '"/>';
            contenido += '<input type="hidden" name="frase0" value="' + $this.html() + '"/>';
            contenido += '<input type="text" class="form-control" name="frase1" value="' + $this.html() + '"/>';
            contenido += '</form>';

            var modalFrase = bootbox.confirm({
                message: contenido,
                buttons: {
                    confirm: {label: "Modificar frase", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Respuestas.modificarFrase(modalFrase);
                    } else {
                        modalFrase.modal('hide');
                    }
                    return false;
                }
            });
        },
        modificarFrase: function (modalFrase) {
            $.ajax({
                url: APP.url('encuesta/respuesta/modificarFrase'),
                type: 'POST',
                async: false,
                data: $("#formCambioFrase").serialize(),
                success: function (response) {
                    modalFrase.modal('hide');
                    if (response.success) {
                        dynatable.process();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    modalFrase.modal('hide');
                    dynatable.process();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    };

    $("body").delegate(".item-draggable", "dblclick", function () {
        Respuestas.verModificarFrase($(this));
    });

    Respuestas.init();
});