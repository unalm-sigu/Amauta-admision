$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/facultad/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $('[data-toggle="tooltip"]').tooltip();
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var colorEstado = {ACT: "success", DES: "default"};
        var nameEstado = {ACT: "Activo", DES: "Desactivado"};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#facultadTemplate").render(record);
        return $(html).prop('outerHTML');

    }

    Facultad = {
        init: function () {},
        form: {},
        eliminar: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la facultad.",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/facultad/delete'),
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
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            Facultad.form.id = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                message: APP.template.spincenter,
                buttons: {
                    confirm: {label: "Cambiar Estado", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        if (mimodal.find('form').parsley().validate()) {
                            Facultad.form.motivoDesactivacion = mimodal.find('textarea').val();
                            Facultad.saveEstado(mimodal);
                        }
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            }).on('shown.bs.modal', function () {
                var html = $.templates("#motivoDesactivacionTemplate").render({});
                mimodal.find('.bootbox-body').html(html);
                if (estado != 'ACT') {
                    mimodal.find('form').text('¿Desea cambiar el estado de la facultad?');
                }
            });
        },
        body: $("body"),
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('academico/facultad/estado'),
                type: 'POST',
                async: true,
                data: Facultad.form,
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
        }
    };

    Facultad.body.delegate(".delete", "click", function (e) {
        Facultad.eliminar(e);
    });
    Facultad.body.delegate(".estado", "click", function (e) {
        Facultad.estado(e);
    });
    Facultad.init();
});