$(function () {
    NuevoSistema = {
        addTipoEvaluacion: function (e) {
            e.preventDefault();
            var record = {};
            var html = $.templates("#templateNuevoSistema").render(record);

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
        },
        regresar : function (e){
            e.preventDefault();
            location.href = APP.url("academico/systemcalifica/sistema");
        }
    };

    $("body").delegate(".add-tipo-evaluacion", "click", function (e) {
        NuevoSistema.addTipoEvaluacion(e);
    });

    $("body").delegate(".delete-tipo-evaluacion", "click", function (e) {
        NuevoSistema.deleteTipoEvaluacion($(this), e);
    });

    $("body").delegate(".cancelar", "click", function (e) {
        NuevoSistema.regresar(e);
    });
});