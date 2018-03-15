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
        // var colorEstado = {CRE: "default", ACT: "success", INA: "danger", CER: "", APR: "primary", ACEP: "primary", OBS: "warning", SOL: "info", RHZ: "danger", REE: "info"};


        var colorEstadoPlan = {
            ACT: "success", APR: "success", EXPR: "success", EXP: "success", ACEP: "success",
            PEND: "warning", CRE: "warning", OBS: "warning", SOL: "warning", REE: "warning", PRO: "warning",
            INA: "danger", RHZ: "danger", CER: "danger"
        };
        var colorEstadoActa = {ABI: "danger", CER: "success", RAB: "danger"};
        var estado = {ACT: "success", INA: "danger", CER: "danger"};

        record.colorEstadoGrupo = colorEstadoActa[record.estadoGrupo];
        record.colorEstadoPlan = colorEstadoPlan[record.estadoPlan];
        record.colorEstado = estado[record.estado];

        record.index = rowIndex;
        var secciones = "";
        if (record.secciones != "")
            secciones = record.secciones.split(",");

        var grupoHoras = "";
        if (record.grupoHoras != "") {
            grupoHoras = record.grupoHoras.toString().split(",")
        }
        var seccionesResult = "";

        for (var i = 0; i < secciones.length; i++) {
            seccionesResult += '<div ><span ';
            if (record.estado == 'ACEP') {
                seccionesResult += 'class="notas-academicas"';
            }
            var grupoText = "";
            if (grupoHoras[i] != null) {
                grupoText = grupoHoras != "" ? (' - ' + grupoHoras[i].split("|")[1]) : "";
            }

            seccionesResult += ' ">' + secciones[i].split("|")[1] + grupoText + '</span></div>';

        }
        record.secciones = seccionesResult;

        var html = $.templates("#templateGrupos").render(record);

        return html;
    }

    ActaDepartamento = {
        listActasSelec: [],
        lstDivElegido: [],
        verModalidades: function ($this, e) {
            function unique(value, index, self) {
                return self.indexOf(value) === index;
            }

            var div = $this.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            var value = $this.attr("value");

            if (ActaDepartamento.divElegido != null) {
                var valueAnt = ActaDepartamento.divElegido.context.attributes[1].value;
                var estadoAnt = ActaDepartamento.divElegido.context.rel;
                
                ActaDepartamento.listActasSelec.forEach(function (elem, index) {
                    if (elem == value) {
                        dynatable.queries.remove('filter'+index)
                        ActaDepartamento.lstDivElegido[index].removeClass(classColor);
                        ActaDepartamento.lstDivElegido.splice(index, 1);
                        ActaDepartamento.listActasSelec.splice(index, 1);
                        if (ActaDepartamento.listActasSelec.length == 0) {
                            ActaDepartamento.divElegido = null;
                        }
                    }
                })

                ActaDepartamento.listActasSelec.forEach(function (elem, index) {

                    if ((value == 1 && elem == 2) || (value == 2 && elem == 1) || (value == 3 && elem == 4) || (value == 4 && elem == 3)) {
                        ActaDepartamento.lstDivElegido[index].removeClass(classColor);
                        ActaDepartamento.lstDivElegido.splice(index, 1);
                        ActaDepartamento.divElegido = null;
                        ActaDepartamento.listActasSelec.splice(index, 1);
                    }
                })
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                ActaDepartamento.divElegido = div;
                ActaDepartamento.lstDivElegido.push(ActaDepartamento.divElegido);
                ActaDepartamento.listActasSelec.push(value);
                ActaDepartamento.lstDivElegido.forEach(function (elem, index) {
                    var valor = elem.context.rel;
                    dynatable.queries.add("filter" + index, valor);
                })

            } else {
                ActaDepartamento.lstDivElegido.forEach(function (elem, index) {
                    var valor = elem.context.rel;                    
                    dynatable.queries.add("filter" + index, valor);
                })
            }
            dynatable.process();


        },
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
                    confirm: {label: 'Reabrir'},
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
        }, reporteActa: function (item, e) {
            location.href = APP.url('academico/docente/cargaacademica/reporteDeActas?seccion=' + item.attr("rel"));
        }
    };


    $("body").delegate(".reabrir", "click", function () {
        ActaDepartamento.reabrir($(this));
    });

    $("body").delegate(".reporteActa", "click", function () {
        ActaDepartamento.reporteActa($(this));
    });
    $("body").delegate(".ver-modalidades", "click", function () {
        ActaDepartamento.verModalidades($(this));
    });


});