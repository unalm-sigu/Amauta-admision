$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/acta/listGrupo'),
            perPageDefault: 10,
            ajaxData: {departamento: $('#txtDepartamentoAcademico').val()}
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');
    function ulWriter(rowIndex, record, columns, cellWriter) {
        var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "danger", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};
        // record.colorEstado = colorEstado[record.estado];
        record.index = rowIndex;
        var html = $.templates("#templateGrupos").render(record);
        return html;
    }

    ActaDepartamento = {
        grupoInicio: $("#grupoInicio"),
        elegirGrupo: function (item, e) {
            var grupo = item.attr("rel");
            alert(grupo);

            if (ActaDepartamento.grupoInicio !== null) {
                ActaDepartamento.grupoInicio.removeClass("active");
            }
            item.addClass("active");
            ActaDepartamento.grupoInicio = item;
            /*
             AdmisionSede.loadInfoGrado(item.attr("rel"), sede);
             if (grado == "0") {
             dynatable.queries.remove("g.id");
             } else {
             dynatable.queries.add("g.id", grado);
             }
             dynatable.process();
             e.preventDefault();
             */
        },
        reabrir: function (item, e) {
            bootbox.confirm({
                message: "Seguro que desea reabrir",
                title: 'Reabrir Acta del Grupo',
                buttons: {
                    confirm: {label: 'Aceptar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/acta/reabrir'),
                            type: 'POST',
                            async: true,
                            data: {grupo: item.attr('rel')},
                            success: function (response) {
                                MODAL.hideWait();
                                MODAL.hide();
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        }
    };


    $("body").delegate(".reabrir", "click", function () {
        ActaDepartamento.reabrir($(this));
    });
});