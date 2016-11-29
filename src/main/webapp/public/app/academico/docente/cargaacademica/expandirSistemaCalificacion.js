$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/docente/cargaacademica/listEvaluacionPlan'),
            perPageDefault: 10,
            ajaxData: {evaluacionSeccion: $("#txtEvalSeccionId").val()}
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", APR: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        if (record.esHijo) {
            record.styleHijo = 'padding-left:90px;';
        }
        var html = $.templates("#templateEvaluacionPlan").render(record);
        return html;
    }

    ExpandirSCN = {
        expandirEvaluacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Expandir Evaluación");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success grabar-expansion" id="cmbAceptar">Aceptar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleExpandirEvaluacion'),
                type: 'POST',
                async: false,
                data: {evaluacion: idx},
                success: function (response) {
                    MODAL.body(response);
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addTipoEvaluacion: function (e) {
            e.preventDefault();
            var record = {};

            var rowCount = $('#tbodyEvaluaciones tr').length;
            record.index = rowCount - 1;

            var html = $.templates("#templateExpandirEvaluacion").render(record);
            var tbody = $("#tbodyEvaluaciones");
            tbody.append(html);

            $(".item-select2").select2();
            $(".item-select2").each(function () {
                $(this).removeClass("item-select2");
            });
        },
        deleteTipoEvaluacion: function ($this, e) {
            e.preventDefault();

            var tr = $this.closest("tr");
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar este registro?",
                buttons: {
                    cancel: {label: "Cancelar", className: "btn-default"},
                    confirm: {label: "Eliminar", className: "btn-danger"}
                },
                callback: function (result) {
                    if (result) {
                        tr.remove();
                    }
                }
            });
        }, saveExpandir: function () {
            bootbox.confirm({
                message: "¿Está seguro que desea expandir?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        var form = $("[id='frmExpandirEvals']");

                        form.parsley().destroy();
                        form.parsley();
                        if (!form.parsley().validate()) {
                            return;
                        }
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/saveExpandir'),
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


                    }
                }
            });
        }
    };

    $("body").delegate(".expandir-evaluacion", "click", function (e) {
        ExpandirSCN.expandirEvaluacion($(this), e);
    });

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        ExpandirSCN.addTipoEvaluacion(e);
    });

    $("body").delegate(".delete-tipo-evaluacion", "click", function (e) {
        ExpandirSCN.deleteTipoEvaluacion($(this), e);
    });

    $("body").delegate(".grabar-expansion", "click", function (e) {
        ExpandirSCN.saveExpandir();
    });

});
