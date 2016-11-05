$(function () {
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/docente/cargaacademica/list'),
            perPageDefault: 100
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {ACT: "success", CER: "danger", CRE: "default"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;

        var html = $.templates("#templateCargaAcademica").render(record);
        return html;
    }

    CargaAcademica = {
        aceptarSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Sistema de Calificación " + rec.sistemaCalificacion);
            MODAL.show();
            MODAL.buttons(
                    '<a class="btn btn-success">Aceptar</a>' +
                    '<a class="btn btn-warning">Expandir</a>' +
                    '<a class="btn btn-danger">Solicita modificación</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + rec.idSistemaCalificacion + '/detalleSistemaCalificacion'),
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
        verSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación - " + rec.sistemaCalificacion);
            MODAL.show();

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/' + rec.idSistemaCalificacion + '/detalleSistemaCalificacion'),
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
    $("body").delegate(".aceptar-sistema-calificacion", "click", function (e) {
        CargaAcademica.aceptarSistemaCalificacion($(this), e);
    });
    $("body").delegate(".sistema-calificacion", "click", function (e) {
        CargaAcademica.verSistemaCalificacion($(this), e);
    });
});
