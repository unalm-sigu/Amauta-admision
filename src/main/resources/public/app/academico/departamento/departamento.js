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

        var colorEstado = {ACT: "success", INA: "danger", CRE: 'default'};
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
        },
        allDocentes: function (e, $this, estado) {
            e.preventDefault();
            var idDpto = $this.attr('rel');

            $.ajax({
                url: APP.url('academico/departamento/allDocentesByDptoEstado'),
                type: 'POST',
                async: true,
                data: {id: idDpto, estado: estado},
                success: function (response) {
                    if (response.success && response.total > 0) {
                        console.dir(response.data)
                        MODAL.init("lg");
                        MODAL.title('');
                        MODAL.body($.templates("#divListaDocente").render({docentes: response.data}));
                        MODAL.buttons('')
                        MODAL.show();
                    }
                },
                error: function () {
                    MODAL.hide();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allCursos: function (e, $this, estado) {
            e.preventDefault();
            var idDpto = $this.attr('rel');

            $.ajax({
                url: APP.url('academico/departamento/allCursosByDptoEstado'),
                type: 'POST',
                async: true,
                data: {id: idDpto, estado: estado},
                success: function (response) {
                    if (response.success && response.total > 0) {
                        MODAL.init("lg");
                        MODAL.body($.templates("#divListaCurso").render({cursos: response.data}));
                        MODAL.buttons('')
                        MODAL.show();
                    }
                },
                error: function () {
                    MODAL.hide();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    };

    Departamento.body.delegate(".delete", "click", function (e) {
        Departamento.eliminar(e);
    });
    Departamento.body.delegate(".estado", "click", function (e) {
        Departamento.estado(e);
    });
    Departamento.body.delegate("#docenteActivos", "click", function (e) {
        Departamento.allDocentes(e, $(this), "ACT");
    });
    Departamento.body.delegate("#docenteInactivos", "click", function (e) {
        Departamento.allDocentes(e, $(this), "INA");
    });
    Departamento.body.delegate("#cursoActivos", "click", function (e) {
        Departamento.allCursos(e, $(this), "ACT");
    });
    Departamento.body.delegate("#cursoInactivos", "click", function (e) {
        Departamento.allCursos(e, $(this), "INA");
    });
    Departamento.init();
});