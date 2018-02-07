$(function() {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/alumno/list'),
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

        var colorEstado = {ACT: 'success', FAPR: 'warning', FRES: 'warning'};
        record.colorEstado = colorEstado[record.estado];
        record.verTipoCarrera = (record.tipoCarrera == "MAE" || record.tipoCarrera == "DOC");
        record.verFacultad = (record.codigoModalidad == "PRE" && record.codigoCarrera != record.codigoFacultad);
        if (record.colorEstado == undefined) {
            record.colorEstado = 'danger';
        }

        var html = $.templates("#alumnoTemplate").render(record);
        return html;
    }

    Alumno = {
        divElegido: null,
        verModalidades: function($this, e) {
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
        },
        verDatosPersonales: function(e) {
            e.preventDefault;
            var self = $(e.currentTarget);
            var alumno = self.attr('rel');

            var box = bootbox.alert({
                size: 'large',
                message: APP.template.spincenter,
                buttons: {
                    ok: {label: "Cerrar", className: "btn-default"},
                }
            });

            $.ajax({
                url: APP.url('academico/alumno/resumen'),
                type: 'POST',
                async: false,
                data: {idAlumno: alumno},
                success: function(response) {
                    if (response.success) {
                        var html = $.templates("#alumnoResumenTemplate").render(response.data);
                        box.find('.bootbox-body').html(html);
                    } else {
                        box.modal('close');
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    box.modal('close');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }
    };

    $("body").delegate(".ver-modalidades", "click", function(e) {
        Alumno.verModalidades($(this), e);
    });

    $("body").delegate(".ver-datos-personales", "click", function(e) {
        Alumno.verDatosPersonales(e);
    });

});
