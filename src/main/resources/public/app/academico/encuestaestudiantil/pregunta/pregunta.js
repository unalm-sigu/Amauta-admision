$(function() {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/encuestaestudiantil/editor/pregunta/list'),
            ajaxData: {idEncuesta: $('[name="encuesta.id"]').val()},
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    $('#dynaTable').bind('dynatable:afterUpdate', function(e, dynatable) {
        $("#opopop").prepend($("#headDynatable"));
        $('#headDynatable').removeClass('hide');
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var colorEstado = {ACT: 'success', INA: 'default', CRE: 'default'};
        record.colorEstado = colorEstado[record.estado];

        record.mostrarOrdenSuperior = (record.numero < 1000 && !record.max);
        record.mostrarOrdenInferior = (1 < record.numero && record.numero < 1000);
        record.index = rowIndex;

        if (record.estado == 'ACT') {
            record.bgestado = 'bg-success';
        }

        var html = $.templates("#preguntaTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Pregunta = {
        init: function() {
        },
        body: $('body'),
        preview: function(e) {
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            var modalPregunta = bootbox.alert({
                size: "large",
                message: APP.template.spincenter,
                buttons: {ok: {label: "Cerrar", className: "btn-link"}}
            }).on('shown.bs.modal', function() {
                setTimeout(function() {
                    modalPregunta.find('.modal-body').css({
                        'overflow-y': 'scroll',
                        'max-height': '600px'});

                    var html = $.templates("#templatePreview").render(rec);
                    modalPregunta.find('.modal-body').html(html);
                    modalPregunta.find('.modal-body').find("select.select2single").select2({minimumResultsForSearch: -1});
                }, 200);
            });
        },
        cambiarEstado: function(e) {
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            var btnClass = rec.estado == "ACT" ? 'btn-warning' : 'btn-success';
            var action = rec.estado == "ACT" ? 'desactivar' : 'activar';
            var estado = rec.estado == "ACT" ? 'INA' : 'ACT';

            var modalEstado = bootbox.confirm({
                message: "¿Está seguro que desea " + action + " la pregunta <strong>" + rec.numero + "</strong>?",
                buttons: {
                    confirm: {label: "Si, " + action, className: btnClass},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuestaestudiantil/editor/pregunta/estado'),
                            type: 'POST',
                            async: false,
                            data: {id: rec.id, estado: estado},
                            success: function(response) {
                                modalEstado.modal('hide');
                                if (response.success) {
                                    dynatable.process();
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(Messages.errorComunicacion, "error");
                                modalEstado.modal('hide');
                            }
                        });
                    } else {
                        modalEstado.modal('hide');
                    }
                    return false;
                }
            });
        },
        eliminar: function(e) {
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            var modalDelete = bootbox.confirm({
                message: "¿Está seguro que desea eliminar la pregunta <strong>" + rec.numero + "</strong>?",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuestaestudiantil/editor/pregunta/delete'),
                            type: 'POST',
                            async: false,
                            data: {id: rec.id},
                            success: function(response) {
                                modalDelete.modal('hide');
                                if (response.success) {
                                    dynatable.process();
                                    notify(response.message, "info");
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(Messages.errorComunicacion, "error");
                                modalDelete.modal('hide');
                            }
                        });
                    } else {
                        modalDelete.modal('hide');
                    }
                    return false;
                }
            });

        },
        orderNumero: function(e) {
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];
            var isUp = self.hasClass('sort-up');
            var isDown = self.hasClass('sort-down');

            var delta = isUp ? 1 : (isDown ? -1 : 0);

            $.ajax({
                url: APP.url('academico/encuestaestudiantil/editor/pregunta/sort'),
                type: 'POST',
                async: true,
                data: {
                    id: rec.id,
                    numero: rec.numero,
                    delta: delta,
                    'evaluacionVirtual.id': $('[name="encuesta.id"]').val()
                },
                success: function(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        dynatable.process();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    };

    Pregunta.body.delegate('.cambiarEstado', 'click', function(e) {
        Pregunta.cambiarEstado(e);
    });

    Pregunta.body.delegate('.eliminar', 'click', function(e) {
        Pregunta.eliminar(e);
    });

    Pregunta.body.delegate(".sort-numero", "click", function(e) {
        Pregunta.orderNumero(e);
    });

    Pregunta.body.delegate('.preview-pregunta', 'click', function(e) {
        Pregunta.preview(e);
    });

    Pregunta.init();

});