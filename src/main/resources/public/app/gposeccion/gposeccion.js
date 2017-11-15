$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/gposeccion/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $('[data-toggle="tooltip"]').tooltip();
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {CRE: 'default', ACT: 'success', INA: 'danger'};
        record.index = rowIndex;
        record.esActivo = record.estado == 'ACT' || record.estado == 'CRE';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        record.cantSecciones = record.secciones.length;
        record.cantDocentes = 0;
        record.class = (rowIndex % 2 == 0) ? 'bg-row' : '';


        for (var i = 0; record.cantSecciones > i; i++) {
            record.secciones[i].index = i;
            record.secciones[i].class = record.class;
            record.secciones[i].colorEstadoSec = labelColor[record.secciones[i].estadoSec];
            record.secciones[i].cantDocentes = record.secciones[i].docentes.length;
            record.cantDocentes += record.secciones[i].cantDocentes;
            for (var j = 0; j < record.secciones[i].docentes.length; j++) {
                record.secciones[i].docentes[j].index = j;
                record.secciones[i].docentes[j].class = record.class;
            }
        }
        var html = $.templates("#gpoSeccionTemplate").render(record);
        return html;
    }

    var GrupoSeccion = {
        viewCount: function ($this, e) {
            e.preventDefault();
            var div = $this.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            dynatable.queries.remove("ass.id");

            if (GrupoSeccion.divSeleccionado != null) {
                GrupoSeccion.divSeleccionado.removeClass(classColor);
                GrupoSeccion.divSeleccionado = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                GrupoSeccion.divSeleccionado = div;
                var grupo = $this.attr("rel");
                dynatable.queries.add("ass.id", grupo);
            }
            dynatable.process();
        }

    }

    $("body").delegate(".view-count", "click", function (e) {
        GrupoSeccion.viewCount($(this), e);
    });


});