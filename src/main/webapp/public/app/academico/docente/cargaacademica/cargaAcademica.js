$(function () {

//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('academico/docente/cargaacademica/list'),
//            perPageDefault: 100
//        },
//        writers: {
//            _rowWriter: ulWriter
//        },
//        table: {
//            bodyRowSelector: 'tbody tr'
//        }
//    }).data('dynatable');
//
//    function ulWriter(rowIndex, record, columns, cellWriter) {
//        var colorEstado = {ACT: "success", CER: "danger", CRE: "default"};
//        record.colorEstado = colorEstado[record.estado];
//        record.index = rowIndex;
//
//        var html = $.templates("#templateCargaAcademica").render(record);
//        return html;
//    }

    CargaAcademica = {
        aceptarSistemaCalificacion: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
//            var rec = dynatable.settings.dataset.records[idx];

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Sistema de Calificación " + rec.sistemaCalificacion);
            MODAL.show();
            MODAL.buttons(
                    '<a class="btn btn-success" id="cmbAceptar">Aceptar</a>' +
                    '<a class="btn btn-warning expandir-sistema" href="#">Expandir</a>' +
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
            //           var rec = dynatable.settings.dataset.records[idx];

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
        },
        confirmaSistemaCalificacion: function ($this, e) {
            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                }, callback: function (result) {
                    if (result) {
                        alert("gut");
                    } else {
                        alert("weas");
                    }
                }
            });
        },
        expandirSistema: function (e) {
            e.preventDefault();
            location.href = APP.url("academico/docente/cargaacademica/expandir");
        },
        expandirEvaluacion: function (e) {
            e.preventDefault();
//            var tr = e.closest("tr");
//            var idx = tr.attr("rel");

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Expandir Evaluación");
            MODAL.show();

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
        notasAcademicas: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            location.href = APP.url('academico/docente/cargaacademica/') + rec.id + '/notasAcademicas';
        }
    };

    $("body").delegate(".aceptar-sistema-calificacion", "click", function (e) {
        CargaAcademica.aceptarSistemaCalificacion($(this), e);
    });
    $("body").delegate(".sistema-calificacion", "click", function (e) {
        CargaAcademica.verSistemaCalificacion($(this), e);
    });
    $("body").delegate("#cmbAceptar", "click", function (e) {
        CargaAcademica.confirmaSistemaCalificacion($(this), e);
    });
    $("body").delegate(".expandir-sistema", "click", function (e) {
        CargaAcademica.expandirSistema(e);
    });
    $("body").delegate(".expandir-evaluacion", "click", function (e) {
        CargaAcademica.expandirEvaluacion(e);
    });
    $("body").delegate(".notas-academicas", "click", function (e) {
        CargaAcademica.notasAcademicas($(this), e);
    });
});
