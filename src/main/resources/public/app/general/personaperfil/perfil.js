$(function () {

    Perfil = {
        activar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmActive,
                title: 'Activar Perfil',
                buttons: {
                    confirm: {label: 'Activar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Actualizando Información...");
                        $.ajax({
                            url: APP.url('general/personaperfil/activate'),
                            type: 'POST',
                            async: true,
                            data: {personaPerfil: el.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        inactivar: function (el) {
            bootbox.confirm({
                message: MESSAGES.confirmActive,
                title: 'Desactivar Perfil',
                buttons: {
                    confirm: {label: 'Desactivar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                       MODAL.showWait("Actualizando Información...");
                        $.ajax({
                            url: APP.url('general/personaperfil/desactivar'),
                            type: 'POST',
                            async: true,
                            data: {personaPerfil: el.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    };

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/personaperfil/list'),
            perPageDefault: 12
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');


    function ulWriter(rowIndex, record, columns, cellWriter) {
        var color = {Activo: 'success', Inactivo: 'danger', Creado: 'default'};

        record.estado = color[record.estadoPersonaPerfil];
        record.index = rowIndex;
        var html = $.templates("#templatePerfil").render(record);
        return html;
    }
    
    $("body").delegate(".activar", "click", function () {
        Perfil.activar($(this));
    });

    $("body").delegate(".inactivar", "click", function () {
        Perfil.inactivar($(this));
    });

    $("body").delegate(".close-popover", "mouseout", function () {
        $(this).popover("hide");
    });
    
    $("body").delegate(".close-popover", "click", function () {
        $(this).popover("show");
    });
});