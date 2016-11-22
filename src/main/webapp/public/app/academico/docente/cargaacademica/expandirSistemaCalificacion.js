$(function () {

    ExpandirSCN = {
        expandirEvaluacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Expandir Evaluación");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="cmbAceptar">Aceptar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleExpandirEvaluacion'),
                type: 'POST',
                async: false,
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


});
