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
        }
    };

    $("body").delegate(".expandir-evaluacion", "click", function (e) {
        ExpandirSCN.expandirEvaluacion($(this), e);
    });
});
