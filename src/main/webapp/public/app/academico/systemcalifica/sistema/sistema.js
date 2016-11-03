$(function () {
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/systemcalifica/sistema/list'),
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
        var colorEstado = {ACT: "success", CER: "danger", CRE: "default"};
        record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;

        var html = $.templates("#templateSistema").render(record);
        return html;
    }

    Sistema = {
        verNuevoSistema: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/systemcalifica/sistema/nuevo");
        },
        verEditarSistema: function ($this, e) {
            e.preventDefault();
        },
        verDetalleSistema: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación " + rec.codigo);
            MODAL.show();

            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/' + rec.id + '/detalleSistema'),
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
        verSolicitud: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            MODAL.init("lg");
            MODAL.title('Solicitud de creación: <stron>Sistema de Calificación ' + rec.codigo + '</strong>');
            MODAL.buttons(
                    '<a class="btn btn-success">Aprobar</a>' +
                    '<a class="btn btn-warning">Observar</a>' +
                    '<a class="btn btn-danger">Rechazar</a>');
            MODAL.show();

            $.ajax({
                url: APP.url('academico/systemcalifica/sistema/' + rec.id + '/detalleSolicitud'),
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
        asignarCursos: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            location.href = APP.url('academico/systemcalifica/sistema/' + rec.id + '/cursos');
        }
    };

    $("body").delegate(".nuevo-sistema", "click", function (e) {
        Sistema.verNuevoSistema(e);
    });

    $("body").delegate(".caso1-sistema", "click", function (e) {
        Sistema.verEditarSistema($(this), e);
    });

    $("body").delegate(".detalle-sistema", "click", function (e) {
        Sistema.verDetalleSistema($(this), e);
    });

    $("body").delegate(".ver-solicitud", "click", function (e) {
        Sistema.verSolicitud($(this), e);
    });

    $("body").delegate(".asignar-cursos", "click", function (e) {
        Sistema.asignarCursos($(this), e);
    });
});