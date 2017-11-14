$(function () {
    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/carrera/list'),
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
        record.esActivo = record.estado == 'ACT';
        record.esCreado = record.estado == 'CRE';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#carreraTemplate").render(record);
        return html;
    }

    var Carrera = {
        init: function () {

        },
        divseleccionado: null,
        modalCarrera: $("#modalEstadoCarrera"),
        form: null,
        viewModal: function (e, $this) {
            e.preventDefault();

            var estado = $this.attr("rev");

            var record = {
                id: $this.attr("rel"),
                estado: estado,
                form: "formEstadoCarrera",
                activo: estado == 'ACT',
                creado: estado == 'CRE',
                inactivo: estado == 'INA'
            };

            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#divEstadoCarrera").render(record));
            MODAL.buttons('<button type="button" class="btn btn-primary cambio-estado-carrera">Aceptar</button>');
            MODAL.show();
            Carrera.form = $("#" + record.form);

        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Carrera.form;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/cambiarEstadoCarrera'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        dynatable.process();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        viewCount: function ($this, e) {
            e.preventDefault();
            var div = $this.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            dynatable.queries.remove("ass.id");

            if (Carrera.divElegido != null) {
                Carrera.divElegido.removeClass(classColor);
                Carrera.divElegido = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                Carrera.divElegido = div;
                var grupo = $this.attr("rel");
                dynatable.queries.add("ass.id", grupo);
            }
            dynatable.process();
        }
    }

    Carrera.init();

    $("body").delegate(".change-estado", "click", function (e) {
        Carrera.viewModal(e, $(this));
    });
    $("body").delegate(".cambio-estado-carrera", "click", function (e) {
        Carrera.cambioEstado(e);
    });
    $("body").delegate(".view-count", "click", function (e) {
        Carrera.viewCount($(this), e);
    });

});