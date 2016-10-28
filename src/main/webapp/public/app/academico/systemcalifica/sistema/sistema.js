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
            var info = {};

            MODAL.init("lg");
            MODAL.title("Detalle del Sistema de Calificación");
            MODAL.body($.templates("#divDetalleSistema").render(info));
            MODAL.buttons(
                    '<a class="btn btn-success">Aceptar</a>' +
                    '<a class="btn btn-primary">Expandir</a>' +
                    '<a class="btn btn-warning">Solicita Modificacion</a>');
            MODAL.show();


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

});