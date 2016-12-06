$(function () {

    NotasAcademicas = {
        cambioNA: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");

            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Cambio de nota");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="cmbGuardar">Guardar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleCambioNota'),
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
        verDetalleReporte: function ($this, e) {
            e.preventDefault();

            /*
             MODAL.buttons(
             '<a class="btn btn-success">Aprobar</a>' +
             '<a class="btn btn-warning">Observar</a>' +
             '<a class="btn btn-danger">Rechazar</a>');
             */
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/getEvaluacion'),
                type: 'POST',
                async: false,
                data: {evaluacion: $this.attr("rel")},
                success: function (response) {
                    MODAL.init("md");
                    MODAL.title("Detalle Evaluación " + response.data.tEvaluacionNombre + " " + response.data.numero);

                    MODAL.body($.templates("#divEvaluacion").render(response.data));
                    MODAL.show();
                    var today = new Date();
                    var checkin = $('#calFechaEval').datepicker({
                        format: "dd/mm/yyyy",
                    }).on('changeDate', function (ev) {
                        /* var newDate = new Date(ev.date)
                         newDate.setMonth(newDate.getMonth() + INTERVAL_MMONTHS);
                         checkout.setEndDate(newDate);
                         checkout.setDate(newDate);
                         ConsMorosos.buscarMorosos($(this)); */
                    }).data('datepicker');
                    checkin.setEndDate(today);
                    checkin.setDate(response.data.evaFechaRealizada);
                    var buttons = "";


                    if (response.data.evaFechaIngresoNota == "") {
                        buttons = buttons + '<a href="#" class="btn btn-warning activar-eval"  rel="true">Activar</a>';
                        checkin.setDate("");
                    } else {
                        buttons = '<a class="btn btn-success activar-eval"  rel="false">Modificar Fecha Eva.</a>';
                    }
                    MODAL.buttons(buttons);

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        activarEvaluacion: function ($this, e) {
            var activacion = Boolean($this.attr('rel'));
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/activarEvaluacion'),
                type: 'POST',
                async: false,
                data: {
                    evaluacion: $("#txtEvaluacionId").val(),
                    fechaEvaluacion: $("#calFechaEval").val(),
                    activacion: activacion
                },
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        if (activacion == true) {

                            $("#txtCodeSel").val(response.data.evaSeleccionada);
                            $("span[name='" + response.data.evaSeleccionada + "']").css("display", "none");
                            $("input[name='" + response.data.evaSeleccionada + "']").css("display", "");
                            $("input[name='" + response.data.evaSeleccionada + "']").val("");
                            /*
                             <input th:name="${evaluacion.tipoEvaluacion.codigo}+${evaluacion.numero}" 
                             type="text" 
                             class="form-control nota-alumno"
                             readonly="false"/>
                             */
                        }
                    } else {
                        notify(response.message, "error");
                    }
                    MODAL.hide();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hide();
                }
            });
        },
        grabarNotas: function () {
            var evaluacion = $("#txtCodeSel").val();
            var jsonObj = [];
            $("input[name='" + evaluacion + "']").each(function () {

                var alumno = $(this).attr("rel");
                var evaluacion = $(this).attr("title");
                var nota = $(this).val();

                var item = {};

                item["nota"] = nota;
                item["alumno"] = {id: alumno};
                item["evaluacion"] = {id: evaluacion};
                jsonObj.push(item);
            });
            if ($("#txtCodeSel").val() != "") {
                $.ajax({
                    url: APP.url('academico/docente/cargaacademica/saveIngresoNotas'),
                    type: 'POST',
                    async: false,
                    data: JSON.stringify(jsonObj),
                    dataType: "json",
                    contentType: "application/json",
                    success: function (response) {
                        console.dir(response);
                        if (response.success) {
                            notify(response.message, "info");
                            $("#txtCodeSel").val("");
                            $("span[name='" + response.data.evaSeleccionada + "']").css("display", "");
                            $("input[name='" + response.data.evaSeleccionada + "']").css("display", "none");
                            // $("input[name='" + response.data.evaSeleccionada + "']").val("");

                            $("input[name='" + response.data.evaSeleccionada + "']").each(function () {
                                var alumno = $(this).attr("rel");
                                var evaluacion = $(this).attr("title");
                                var nota = $(this).val();
                                $("span[name='" + response.data.evaSeleccionada + "']").each(function () {
                                    var alumnoSpan = $(this).attr("class");
                                    if (parseInt(alumno) == parseInt(alumnoSpan)) {
                                        $(this).html(nota);
                                    }
                                });
                            });
                        } else {
                            notify(response.message, "error");
                        }

                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");

                    }
                });
            } else {
                bootbox.alert({
                    message: "Seleccionar evaluación e ingresar notas.",
                    size: 'small'
                });
            }
        }
    };
    $('.nota-alumno').keyup(function (event) {
        var keyCode = (event.keyCode ? event.keyCode : event.which);
        if (keyCode == 13) {
            var index = $('.nota-alumno').index(this) + 1;
            $('.nota-alumno').eq(index).focus();
        }
    });
    $('.activar-evaluacion').click(function (event) {
        var record = {};
        MODAL.init("md");
        MODAL.title("Activación de evaluación");
        MODAL.buttons('<a class="btn btn-primary" id="btnActivarEvaluacion">Activar</a>');
        MODAL.body($.templates("#divActivarEvaluacion").render(record));
        MODAL.show();
    });
    $("body").delegate("#btnActivarEvaluacion", "click", function (e) {
        MODAL.hide();
        var evaluacion = 23;
        location.href = APP.url("academico/docente/cargaacademica/") + evaluacion + "/evaluacion";
    });
    $("body").delegate(".solicitar-cambio-nota", "click", function (e) {
        NotasAcademicas.cambioNA($(this), e);
    });

    $("body").delegate(".detalle-reporte", "click", function (e) {
        NotasAcademicas.verDetalleReporte($(this), e);
    });

    $("body").delegate(".activar-eval", "click", function (e) {
        NotasAcademicas.activarEvaluacion($(this), e);
    });

    $("body").delegate("#cmbSaveNotas", "click", function (e) {
        NotasAcademicas.grabarNotas();
    });

});
