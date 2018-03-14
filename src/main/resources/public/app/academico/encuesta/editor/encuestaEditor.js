$(function() {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/encuesta/editor/list'),
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
        record.index = rowIndex;

        var html = $.templates("#encuestaTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Encuesta = {
        body: $('body'),
        init: function() {
        },
        delete: function(e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            bootbox.confirm({
                message: "¿Seguro que desea eliminar la encuesta " + rec.codigo + "?",
                size: "medium",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('encuesta/editor/delete'),
                            type: 'POST',
                            async: false,
                            data: {id: rec.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        duplicar: function(e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            bootbox.confirm({
                message: "¿Seguro que desea crear una nueva encuesta en base al " + rec.codigo + "?",
                size: "medium",
                buttons: {
                    confirm: {label: "Si, duplicar", className: "btn-success"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('encuesta/editor/duplicar'),
                            type: 'POST',
                            async: false,
                            data: {id: rec.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        estado: function(e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var tr = self.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            var action = (rec.estado == "ACT") ? "desactivar" : "activar";
            var btnClass = (rec.estado == "ACT") ? "danger" : "primary";

            var mymodal = bootbox.confirm({
                size: "medium",
                message: "¿Está seguro que desea <strong>" + action + "</strong> la encuesta " + rec.codigo + "?",
                buttons: {
                    confirm: {label: "Si, " + action, className: "btn-" + btnClass},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('encuesta/editor/estado'),
                            type: 'POST',
                            async: true,
                            data: {id: rec.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });


        }
    };

    Encuesta.body.delegate('.delete-encuesta', 'click', function(e) {
        Encuesta.delete(e);
    });

    Encuesta.body.delegate('.cambiar-estado', 'click', function(e) {
        Encuesta.estado(e);
    });

    Encuesta.body.delegate('.duplicar-encuesta', 'click', function(e) {
        Encuesta.duplicar(e);
    });

    Encuesta.init();
});