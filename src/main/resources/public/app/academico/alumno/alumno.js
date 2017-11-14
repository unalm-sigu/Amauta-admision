$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/alumno/list'),
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

        var html = $.templates("#alumnoTemplate").render(record);
        return html;
    }

    Alumno = {
        divElegido: null,
        verModalidades: function ($this, e) {
            e.preventDefault();
            var div = $this.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            dynatable.queries.remove("moe.codigo");

            if (Alumno.divElegido != null) {
                Alumno.divElegido.removeClass(classColor);
                Alumno.divElegido = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                Alumno.divElegido = div;
                var estado = $this.attr("rel");
                dynatable.queries.add("moe.codigo", estado);
            }
            dynatable.process();
        }
    };

    $("body").delegate(".ver-modalidades", "click", function (e) {
        Alumno.verModalidades($(this), e);
    });

});
