$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/anexo/list'),
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
        var html = $.templates("#anexoTemplate").render(record);
        return html;
    }

    var Anexo = {
        form: null,
        divElegido: null,
        viewModal: function (e, $this) {
            e.preventDefault();

            var estado = $this.attr("rev");

            var record = {
                form: "formEstadoAnexo",
                activo: estado == 'ACT',
                id: $this.attr("rel")
            };

            MODAL.init("md");
            MODAL.title("");
            MODAL.body($.templates("#divEstadoAnexo").render(record));
            MODAL.buttons('<button type="button" class="btn btn-primary cambio-estado-anexo">Aceptar</button>');
            MODAL.show();
            Anexo.form = $("#" + record.form);
        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = Anexo.form;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/anexo/cambiarEstado'),
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

            if (Anexo.divElegido != null) {
                Anexo.divElegido.removeClass(classColor);
                Anexo.divElegido = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                Anexo.divElegido = div;
                var grupo = $this.attr("rel");
                dynatable.queries.add("ass.id", grupo);
            }
            dynatable.process();
        }

    }

    $("body").delegate(".change-estado", "click", function (e) {
        Anexo.viewModal(e, $(this));
    });

    $("body").delegate(".cambio-estado-anexo", "click", function (e) {
        Anexo.cambioEstado(e);
    });

    $("body").delegate(".view-count", "click", function (e) {
        Anexo.viewCount($(this), e);
    });


});