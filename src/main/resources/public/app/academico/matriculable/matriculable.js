$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/matriculable/list'),
            perPageDefault: 15
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var colorEstado = {ACT: 'success', FAPR: 'warning', FRES: 'warning'};
        record.colorEstado = colorEstado[record.estado];
        if (record.colorEstado == undefined) {
            record.colorEstado = 'danger';
        }

        var html = $.templates("#matriculableTemplate").render(record);
        return html;
    }

    Matriculable = {
        modalMatriculable: $("#modalMatriculable"),
        divElegido: null,
        verModalidades: function ($this, e) {
            e.preventDefault();
            var div = $this.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            dynatable.queries.remove("moe.codigo");

            if (Matriculable.divElegido != null) {
                Matriculable.divElegido.removeClass(classColor);
                Matriculable.divElegido = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                Matriculable.divElegido = div;
                var estado = $this.attr("rel");
                dynatable.queries.add("moe.codigo", estado);
            }
            dynatable.process();
        },
        viewModal: function (e, $this) {
            e.preventDefault();

            Matriculable.modalMatriculable.modal("show");
            $('[name="motivo"]').val("");
            $('[name="id"]').val($this.attr("rel"));
        },
        nuevoModal: function () {
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/nuevo'),
                success: function (response) {
                    $('#matriculableModal').html(response);
                    $('#viewModal').modal('show');
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        generarMatriculables: function (e, $this) {
            e.preventDefault();
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generar'),
                success: function (response) {
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        generarPrioridad: function (e, $this) {
            e.preventDefault();
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generarPrioridad'),
                success: function (response) {
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        asignarTurno: function (e, $this) {
            e.preventDefault();
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generarPrioridad'),
                success: function (response) {
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        verCursos: function ($this) {
            var rel = $this.attr("rel");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/alumno/' + rel + '/matricula/origen/matriculable'),
                success: function (response) {
                    $('#cursosModal').html(response);
                    $('#viewModal').modal('show');
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    };

    $("body").delegate(".ver-modalidades", "click", function (e) {
        Matriculable.verModalidades($(this), e);
    });
    $("body").delegate(".ver-cursos", "click", function () {
        Matriculable.verCursos($(this));
    });
    $("body").delegate("#agregarMatriculable", "click", function (e) {
        Matriculable.nuevoModal(e, $(this));
    });
    $("body").delegate("#generarMatriculables", "click", function (e) {
        Matriculable.generarMatriculables(e, $(this));
    });

    $("body").delegate("#generarPrioridad", "click", function (e) {
        Matriculable.generarPrioridad(e, $(this));
    });

    $("body").delegate("#asignarTurnos", "click", function (e) {
        Matriculable.asignarTurno(e, $(this));
    });

});
