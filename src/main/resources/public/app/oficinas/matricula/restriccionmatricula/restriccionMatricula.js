$(function () {
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('oficinas/matricula/restriccionmatricula/list'),
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
        var label = {'Levantado': 'success', 'Restringido': 'warning', 'Anulado': 'primary'};
        record.label = label[record.estado];
        record.index = rowIndex;
        record.editable = record.estado !== "Anulado" && record.estado !== "Levantado";
        var html = $.templates("#templateDeudaAlumno").render(record);
        return html;
    }

    DeudaAlumno = {
        verModalAnular: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.init("md");
            MODAL.title("Anular restricción");
            MODAL.body($.templates("#divAnular").render(rec));
            MODAL.buttons('<a href="#" id="btnAnular" class="btn btn-primary">Guardar</a>');
            MODAL.show();
        },
        verModalEditar: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.init("md");
            MODAL.title("Editar restricción");
            MODAL.body($.templates("#divEditar").render(rec));
            MODAL.buttons('<a href="#" id="btnEditar" class="btn btn-primary">Guardar</a>');
            MODAL.show();
            $("[name='descripcion']").val(rec.descripcion);
        },
        anular: function () {
            var form = MODAL.getBody().find("[name='formAnular']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            console.log(form.serialize());
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/anular'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
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
        },
        levantar: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            bootbox.confirm({
                message: "¿Está seguro que desea levantar la restricción?",
                buttons: {
                    confirm: {label: "Sí, seguro", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (!result) {
                        return;
                    }
                    $.ajax({
                        url: APP.url('oficinas/matricula/restriccionmatricula/levantar'),
                        type: 'POST',
                        async: true,
                        data: {id: rec.id},
                        success: function (response) {
                            if (response.success) {
                                MODAL.hide();
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
            });
        },
        guardar: function () {
            var form = MODAL.getBody().find("[name='formEditar']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            console.log(form.serialize());
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/guardar'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
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
        },
        loadDeuda: function () {
            var tipoDeuda = $("#tipoDeuda").val();
            dynatable.queries.add("tipoDeuda", tipoDeuda);
            dynatable.process();
        }
    };

    $("body").delegate(".anular", "click", function (e) {
        DeudaAlumno.verModalAnular($(this), e);
    });

    $("body").delegate(".levantar", "click", function (e) {
        DeudaAlumno.levantar($(this), e);
    });

    $("body").delegate(".editar", "click", function (e) {
        DeudaAlumno.verModalEditar($(this), e);
    });

    $("body").delegate("#btnEditar", "click", function (e) {
        DeudaAlumno.guardar();
    });

    $("body").delegate("#btnAnular", "click", function (e) {
        DeudaAlumno.anular();
    });

    $("#tipoDeuda").change(function () {
        DeudaAlumno.loadDeuda();
    });

    DeudaAlumno.loadDeuda();

});