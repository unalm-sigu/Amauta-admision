$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/departamento/list'),
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

        var colorEstado = {ACT: "success", INA: "danger",CRE: 'default'};
        var nameEstado = {ACT: "Activo", INA: "Inactivo", CRE: 'Creado'};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#departamentoTemplate").render(record);
        return $(html).prop('outerHTML');

    }

    Departamento = {
        init: function () {},
        form: {},
        eliminar: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var id = self.attr("rel");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar el departamento académico.",
                size: 'small',
                buttons: {
                    confirm: {label: 'Sí, Eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/departamento/delete'),
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

            Departamento.form.id = id;

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
                            Departamento.form.motivoDesactivacion = mimodal.find('textarea').val();
                            Departamento.saveEstado(mimodal);
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
                    mimodal.find('form').text('¿Desea cambiar el estado del departamento académico?');
                }
            });
        },
        body: $("body"),
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('academico/departamento/estado'),
                type: 'POST',
                async: true,
                data: Departamento.form,
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

    Departamento.body.delegate(".delete", "click", function (e) {
        Departamento.eliminar(e);
    });
    Departamento.body.delegate(".estado", "click", function (e) {
        Departamento.estado(e);
    });
    Departamento.init();
});