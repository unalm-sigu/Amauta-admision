$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/oficina/all'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter

        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        record.index = rowIndex;
        var colorEstado = {ACT: "success", INA: "default"};
        var nameEstado = {ACT: "Activo", INA: "Inactivo"};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#oficinaTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Oficina = {
        init: function () {},
        body: $("body"),
        form: {},
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            Oficina.form.id = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                size: 'small',
                message: '¿Desea cambiar el estado de la oficina?',
                buttons: {
                    confirm: {label: "Cambiar Estado", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Oficina.saveEstado(mimodal);
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('general/oficina/estado'),
                type: 'POST',
                async: true,
                data: Oficina.form,
                success: function (response) {
                    if (response.success) {
                        dynatable.process();
                        mimodal.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la oficina.",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/oficina/delete'),
                            type: 'POST',
                            async: true,
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
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
        },
    };


    Oficina.body.delegate(".delete", "click", function (e) {
        Oficina.eliminar(e);
    });

    Oficina.body.delegate(".estado", "click", function (e) {
        Oficina.estado(e);
    });

});