$(function () {

    var sistemaNotasValidate = "";
    var letrasNota = "";
    var message = "";
    var NSP = "NSP";

    NotasAcademicas = {
        init: function () {


            if (sistemaNotasValidate == "") {
                $.ajax({
                    url: APP.url('academico/docente/cargaacademica/getSistemaNotas'),
                    type: 'POST',
                    async: false,
                    data: {sistemaNotas: $("#txtSistemaNotas").val()},
                    success: function (response) {

                        sistemaNotasValidate = response.data;
                        if (sistemaNotasValidate.letras != "") {
                            var letrasArg = sistemaNotasValidate.letras.split(",");

                            for (var i = 0; i < letrasArg.length; i++) {
                                var letra = letrasArg[i];
                                sistemaNotasValidate[letra] = JSON.parse(sistemaNotasValidate[letra]);
                            }
                        }
                    },
                    error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }

            window.Parsley.addValidator('notaNumerica', {
                requirementType: 'string',
                validateString: function (value, requirement) {
                    var nota = value;
                    if (isNaN(value)) {
                        if (value != NSP) {
                            return false;
                        }
                    }
                    return true;
                },
                messages: {
                    //Cette valeur doit être un multiple de %s
                    en: "La nota debe ser numérica.",
                    es: "La nota debe ser numérica."
                }
            });

            window.Parsley.addValidator('notaMinima', {
                requirementType: 'string',
                validateString: function (value, requirement) {
                    var nota = value;
                    if (!isNaN(value)) {
                        nota = parseFloat(nota);
                        if (nota < parseFloat(requirement)) {
                            message = "La nota no debe ser menor que el valor mínimo."
                            return false;
                        }
                    }
                    return true;
                },
                messages: {
                    //Cette valeur doit être un multiple de %s
                    en: "Nota inferior al valor mínimo.",
                    es: "Nota inferior al valor mínimo."
                }
            });

            window.Parsley.addValidator('notaMaxima', {
                requirementType: 'string',
                validateString: function (value, requirement) {
                    var nota = value;
                    if (!isNaN(value)) {
                        nota = parseFloat(nota);
                        if (nota > parseFloat(requirement)) {
                            message = "La nota no debe ser mayor que el valor máximo."
                            return false;
                        }
                    }
                    return true;
                },
                messages: {
                    //Cette valeur doit être un multiple de %s
                    en: "Nota superior al valor mínimo.",
                    es: "Nota superior al valor mínimo."
                }
            });

            NotasAcademicas.revisarNotas();

        },
        cambioNA: function ($this, e) {
            e.preventDefault();
            var tr = $this.closest("tr");
            var idx = tr.attr("rel");


            MODAL.hide();
            MODAL.init("lg");
            MODAL.title("Cambio de nota");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="cmbGuardarCambio">Guardar</a>');

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/detalleCambioNota'),
                type: 'POST',
                async: false,
                data: {matriculaSeccion: $this.attr("rel")},
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
                data: {
                    evaluacion: $this.attr("rel"),
                    docenteSeccion: $("#txtDocSec").val()
                },
                success: function (response) {
                    var data = response.data;
                    MODAL.init("md");

                    if (data.estado == "CERRADA") {
                        MODAL.title("Activar evaluación: " + data.tEvaluacionNombre + " " + data.numero);
                    } else {
                        MODAL.title("Resumen estadístico de " + data.tEvaluacionNombre + " " + data.numero);
                    }

                    response.data.form = "frmActivate";
                    MODAL.body($.templates("#divEvaluacion").render(data));
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
                    checkin.setDate(data.evaFechaRealizada);
                    var buttons = "";


                    if (data.estado == "CERRADA") {
                        buttons = buttons + '<a href="#" class="btn btn-warning activar-eval" rel="true">Activar</a>';
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
            var activacion = $this.attr('rel');

            var form = $("[id='frmActivate']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            if ($("#txtCodeSel").val() != "") {
                bootbox.alert("Tiene una evaluacion pendiente, verifique.");
                return;
            }
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

                        if (activacion == "true" || activacion == true) {
                            $("#txtCodeSel").val(response.data.evaId);
                            $("span[name='" + response.data.evaId + "']").css("display", "none");
                            $("input[title='" + response.data.evaId + "']").css("display", "");
                            $("input[title='" + response.data.evaId + "']").addClass("nota-alumno");
                            $("input[title='" + response.data.evaId + "']").val("");
                            /*
                             <input th:name="${evaluacion.tipoEvaluacion.codigo}+${evaluacion.numero}" 
                             type="text" 
                             class="form-control nota-alumno"
                             readonly="false"/>
                             */

                        }
                        notify(response.message, "info");
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
            $("input[title='" + evaluacion + "']").each(function () {
                $(this).attr("data-parsley-whitespace", "trim");
                $(this).attr("required", true);

                //  $(this).attr("data-parsley-sistema-nota", "true");
                if (sistemaNotasValidate.esNumerico == "true" || sistemaNotasValidate.esNumerico == true) {
                    //      $(this).attr("data-parsley-min", sistemaNotasValidate.valorInicial);
                    //      $(this).attr("data-parsley-max", sistemaNotasValidate.valorFinal);
                    $(this).attr("data-parsley-nota-numerica", "true");
                    $(this).attr("data-parsley-nota-minima", sistemaNotasValidate.valorInicial);
                    $(this).attr("data-parsley-nota-maxima", sistemaNotasValidate.valorFinal);
                    $(this).attr("data-parsley-pattern", "[0-9]{0,3}\.?[0-9]{0,2}");//^ $

                    //  $(this).attr("data-parsley-pattern", "^[0-9]*\.[0-9]{2}$");

                } else {
                    var letters = NSP + "|";

                    var letrasArg = sistemaNotasValidate.letras.split(",");
                    for (var i = 0; i < letrasArg.length; i++) {
                        letters += letrasArg[i] + "|";
                    }
                    $(this).attr("data-parsley-pattern", "(" + letters.substring(0, letters.length - 1) + ")");
                }

                var alumno = $(this).attr("rel");
                var evaluacion = $(this).attr("title");
                var nota = $(this).val();

                var item = {};

                item["nota"] = nota;
                item["alumno"] = {id: alumno};
                item["evaluacion"] = {id: evaluacion};
                jsonObj.push(item);
            });
            var form = $("[id='frmNotas']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            if ($("#txtCodeSel").val() == "") {
                bootbox.alert({
                    message: "Seleccionar evaluación e ingresar notas.",
                    size: 'small'
                });
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea registrar las notas, de la evaluación?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url('academico/docente/cargaacademica/saveIngresoNotas'),
                            type: 'POST',
                            async: false,
                            data: JSON.stringify(jsonObj),
                            dataType: "json",
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $("#txtCodeSel").val("");
                                    $("span[name='" + response.data.evaId + "']").css("display", "");
                                    $("input[title='" + response.data.evaId + "']").css("display", "none");
                                    // $("input[name='" + response.data.evaSeleccionada + "']").val("");

                                    $("input[title='" + response.data.evaId + "']").each(function () {
                                        var alumno = $(this).attr("rel");
                                        var nota = $(this).val();


                                        $(this).removeAttr("data-parsley-nota-minima");
                                        $(this).removeAttr("data-parsley-nota-maxima");
                                        $(this).removeAttr("data-parsley-nota-numerica");
                                        $(this).removeAttr("data-parsley-type");
                                        $(this).removeAttr("required");
                                        $(this).removeAttr("data-parsley-whitespace");
                                        $(this).removeAttr("data-parsley-pattern");
                                        $(this).removeClass("nota-alumno");


                                        $("span[name='" + response.data.evaId + "']").each(function () {
                                            var alumnoSpan = $(this).attr("class");
                                            if (parseInt(alumno) == parseInt(alumnoSpan)) {
                                                $(this).html('<span class="nota-academica">' + nota + '</span>');
                                            }
                                        });
                                    });
                                    NotasAcademicas.revisarNotas();
                                } else {
                                    notify(response.message, "error");
                                }

                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");

                            }

                        });
                        MODAL.hideWait();

                    }
                }
            });

        },
        solicitarCambio: function () {
            var form = $("[id='frmCambioNota']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/docente/cargaacademica/solicitarCambio'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        MODAL.hide();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");

                }

            });
            MODAL.hideWait();
        },
        revisarNotas: function () {
            $(".nota-academica").each(function (i, v) {
                $(this).removeClass();
                $(this).addClass("nota-academica");
            });
            $(".nota-academica").each(function (i, v) {
                var $this = $(this);
                var val = $this.text();
                if (val == "NSP") {
                    $this.addClass("label label-warning");
                } else if (sistemaNotasValidate.letras != "") {

                } else {
                    var nota = parseFloat(val);
                    if (nota >= sistemaNotasValidate.minimoAprobatorio) {
                        $this.addClass("text-primary");
                    } else {
                        $this.addClass("text-danger");
                    }
                }
            });
        },
        cambiarTipoEvalForChange: function ($this, e) {
            var evaluacion = $this.val();
            var alumno = $("#txtAlumnoCambiarNota").val();
            $.ajax({
                url: APP.url('academico/docente/cargaacademica/cambiarEvaluacion'),
                type: 'POST',
                async: false,
                data: {
                    evaluacion: evaluacion,
                    alumno: alumno
                },
                success: function (response) {
                    if (response.success) {
                        $("#txtNotaAnterior").val(response.data.nota);
                        $("[name='notaInicial']").val(response.data.nota);
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }

            }
            );
        },
        reporteActaNotas: function ($this, e) {

            var alumno = $("#txtAlumnoCambiarNota").val();
            location.href = APP.url('academico/docente/cargaacademica/reporteDeActas?docenteSeccion=') + $("#txtDocSec").val();

        }
    };
    NotasAcademicas.init();

    $("body").delegate('.nota-alumno', 'keyup', function (event) {
        var keyCode = (event.keyCode ? event.keyCode : event.which);
        if (keyCode == 13) {
            var index = $('.nota-alumno').index(this) + 1;
            $('.nota-alumno').eq(index).focus();
            $('.nota-alumno').eq(index).select();

            var nota = $(this);
            var notaFloat = parseFloat(nota.val());
            nota.val(notaFloat.toFixed(2));
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

    $("body").delegate("#cmbGuardarCambio", "click", function (e) {
        NotasAcademicas.solicitarCambio();
    });

    $("body").delegate("#cboTipoEvalForChange", "change", function (e) {
        NotasAcademicas.cambiarTipoEvalForChange($(this), e);
    });

    $("body").delegate("#cmbReporteNotas", "click", function (e) {
        NotasAcademicas.reporteActaNotas($(this), e);
    });
});
