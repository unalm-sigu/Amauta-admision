$(function() {

    var Encuesta = {
        init: function() {
            Encuesta.makeProgress();
            $('.f1 fieldset:first').fadeIn('slow');
            $('select.form-control').select2({minimumInputLength: -1});
        },
        body: $('body'),
        barProgress: function(progressLineObject, direction) {
            var numberOfSteps = progressLineObject.data('number-of-steps');
            var nowValue = progressLineObject.data('now-value');
            var newValue = 0;
            if (direction == 'right') {
                newValue = nowValue + (100 / numberOfSteps);
            } else if (direction == 'left') {
                newValue = nowValue - (100 / numberOfSteps);
            }
            progressLineObject.attr('style', 'width: ' + newValue + '%;').data('now-value', newValue);
        },
        saveRespuesta: function(opcion) {
            var pEncuesta = opcion.attr('name');
            var padre = opcion.parents('.radio:first');
            var padreGroup = opcion.parents('.form-group:first').parents('div:first');

            padreGroup.find('.fa-spinne').addClass('hidden');
            padreGroup.find('.fa-close').addClass('hidden');
            padreGroup.find('.fa-check-circle').addClass('hidden');
            padre.find('.fa-spinner').removeClass('hidden');

            var start = new Date().getTime();

            var lapso = 1000;
            setTimeout(function() {
                padre.find('.fa-check-circle').removeClass('hidden');
                padre.find('.fa-spinner').addClass('hidden');
            }, lapso);

        },
        marcarRespuesta: function(checkbox) {
            var pEncuesta = checkbox.attr('name');
            var data = {id: pEncuesta, 'opcion.id': checkbox.val()};

            if (checkbox.is(":checked")) {
                data.push({valido: "ok"});
            }

        },
        reenviarRespuesta: function(opcion) {
            var padre = opcion.parents('.radio:first');
            Encuesta.saveRespuesta(padre.find('input[type=radio]:first'));
        },
        saveOtro: function(self) {
        },
        saveMultiSelect: function(self) {

        },
        makeProgress: function() {
            var progressLine = $('.f1-progress-line');
            var stepLine = $('.f1-step');
            var cant = 0;
            $(".f1-step").each(function(i, item) {
                if (!$(item).hasClass("hide")) {
                    cant++;
                }
            });
            var width = 100 / (cant * 2);
            var w2 = 100 / cant;

            stepLine.attr('style', 'width: ' + w2 + '%;');

            progressLine
                    .attr('style', 'width: ' + width + '%;')
                    .data('now-value', width)
                    .data('number-of-steps', cant);

            Encuesta.recountStep();


        },
        findStep: function(numStep) {
            var step = null;
            $(".f1-step").each(function(i, item) {
                if ($(item).attr("ref") == numStep) {
                    step = $(item);
                }
            });
            return step;
        },
        findNextStep: function(numStep) {
            var step = null;
            var buscar = false;
            $(".f1-step").each(function(i, item) {
                if (buscar && !$(item).hasClass("hide")) {
                    step = $(item);
                    buscar = false;
                }
                if ($(item).attr("ref") == numStep) {
                    buscar = true;
                }
            });
            return step;
        },
        findPreviousStep: function(numStep) {
            var step = null;
            var buscar = false;
            $($(".f1-step").get().reverse()).each(function(i, item) {
                if (buscar && !$(item).hasClass("hide")) {
                    step = $(item);
                    buscar = false;
                }
                if ($(item).attr("ref") == numStep) {
                    buscar = true;
                }
            });
            return step;
        },
        findDivStep: function(numStep) {
            var div = null;
            $("fieldset").each(function(i, item) {
                var item = $(item);
                if (item.attr("ref") == numStep) {
                    div = $(item);
                }
            });
            return div;
        },
        recountStep: function() {
            var number = 1;
            var numStep = 0;
            $(".f1-step").each(function(i, item) {
                if (!$(item).hasClass("hide")) {
                    var stepIcon = $(item).find(".f1-step-icon");
                    numStep = $(item).attr("ref");
                    var divStep = Encuesta.findDivStep(numStep);
                    var btnSubmit = divStep.find(".btn-submit");
                    var btnNext = divStep.find(".btn-next");
                    btnSubmit.addClass("hide");
                    btnNext.removeClass("hide");
                    stepIcon.html(number);
                    number++;
                }
            });
            var divStep = Encuesta.findDivStep(numStep);
            var btnSubmit = divStep.find(".btn-submit");
            var btnNext = divStep.find(".btn-next");
            btnSubmit.removeClass("hide");
            btnNext.addClass("hide");
        },
        validateStep: function(parentFieldset) {
            var nextStep = true;
            var rel = parentFieldset.attr("rel");
            var inp = $("input[name='" + rel + "']");
            if (inp.length) {
                if (!inp.is(":checked") && inp.attr("type") == "radio") {
                    nextStep = false;
                    swal({
                        title: "    ",
                        text: "Debe responder la pregunta",
                        timer: 1200,
                        type: "warning",
                        confirmButtonColor: "#DD6B55",
                        confirmButtonText: "Aceptar"
                    });
                }
            }

            if (nextStep) {
                parentFieldset.find('input[type="text"], select').each(function(i, item) {
                    var $this = $(item);
                    var validar = false;
                    validar = (!validar) ? $this.hasClass("preTexto") : validar;
                    validar = (!validar) ? $this.hasClass("preOtro") : validar;
                    validar = (!validar) ? $this.hasClass("preTextoMulti") : validar;
                    if (validar && $(item).is('[required]') && $(item).val() == "") {
                        $this.addClass('input-error');
                        nextStep = false;
                        swal({
                            title: "    ",
                            text: "Debe llenar el cuadro de texto",
                            timer: 1200,
                            type: "warning",
                            confirmButtonColor: "#DD6B55",
                            confirmButtonText: "Aceptar"
                        });
                    } else {
                        $this.removeClass('input-error');
                    }
                });
            }

            return nextStep;
        },
        goNextStep: function(btn) {
            var parentFieldset = btn.parents('fieldset');
            var numStep = parentFieldset.attr("ref");
            var currentActiveStep = btn.parents('.f1').find('.f1-step.active');
            var progressLine = btn.parents('.f1').find('.f1-progress-line');
            var nextStep = Encuesta.validateStep(parentFieldset);

            if (nextStep) {
                parentFieldset.fadeOut(400, function() {
                    currentActiveStep.removeClass('active').addClass('activated');
                    var stepNext = Encuesta.findNextStep(numStep);
                    stepNext.addClass('active');
                    Encuesta.barProgress(progressLine, 'right');

                    var nroDivItem = stepNext.attr("ref");
                    var divNextStep = Encuesta.findDivStep(nroDivItem);
                    divNextStep.fadeIn(1000);
                    scrollToClass($('.f1'), 20);
                });
            }
        },
        goPreviousStep: function(btn) {
            var currentActiveStep = btn.parents('.f1').find('.f1-step.active');
            var progressLine = btn.parents('.f1').find('.f1-progress-line');
            var nroItem = currentActiveStep.attr("ref");

            btn.parents('fieldset').fadeOut(400, function() {
                currentActiveStep.removeClass('active');
                var stepPrev = Encuesta.findPreviousStep(nroItem);
                stepPrev.removeClass('activated').addClass('active');
                Encuesta.barProgress(progressLine, 'left');
                var nroDivItem = stepPrev.attr("ref");
                var divNextStep = Encuesta.findDivStep(nroDivItem);
                divNextStep.fadeIn();
                scrollToClass($('.f1'), 20);
            });
        },
        closeProcess: function(btn) {
            var parentFieldset = btn.parents('fieldset');
            var endStep = Encuesta.validateStep(parentFieldset);

            if (endStep) {

                swal({
                    text: "¿Está seguro que desea finalizar la encuesta?",
                    icon: "warning",
                    type: "warning",
                    dangerMode: true,
                    showCancelButton: true,
                    closeOnConfirm: false,
                    buttons: {
                        cancel: "No",
                        confirm: "Si, estoy seguro"
                    }
                }).then((willDelete) => {
                    if (willDelete) {
                        swal("Encuesta finalizada", "Gracias por tu tiempo", "success", {buttons: {ok: "Aceptar"}});
                    }
                });

            }
        },
        sendForm: function() {
            swal("Encuesta finalizada", "Gracias por tu tiempo", "success");
        },
        changeOption: function($this) {
            var fieldset = $this.parents('fieldset');

            fieldset.find(".step-option").each(function() {
                if ($(this).attr("ref")) {
                    var ref = $(this).attr("ref").split(",");
                    for (var i = 0; i < ref.length; i++) {
                        var step = Encuesta.findStep(ref[i]);
                        step.addClass("hide");
                        var div = Encuesta.findDivStep(ref[i]);
                        div.find("input[type=radio]").each(function(i, item) {
                            $(item).prop('checked', false);
                        });
                    }

                }
            });

            fieldset.find("input[type=text]").each(function() {
                $(this).val("");
                $(this).prop('disabled', true);
                $(this).prop('required', false);
            });

            var divItem = $this.closest('div');
            divItem.find("input[type=text]").each(function() {
                $(this).prop('disabled', false);
                $(this).prop('required', true);
            });

            if ($this.attr("ref")) {
                var ref = $this.attr("ref").split(",");
                for (var i = 0; i < ref.length; i++) {
                    var step = Encuesta.findStep(ref[i]);
                    step.removeClass("hide");
                }
            }
            Encuesta.makeProgress();
        },
        changeCheckbox: function($this) {
            var fieldset = $this.parents('fieldset');
            var numStep = fieldset.attr("ref");
            var divItem = $this.closest('label');

            if ($this.is(":checked")) {
                divItem.find("input[type=text], select").each(function() {
                    var type = $(this).attr("type");
                    $(this).prop('disabled', false);
                    $(this).prop('required', true);
                });
            } else {
                divItem.find("input[type=text], select").each(function() {
                    var type = $(this).attr("type");
                    if (type == "text") {
                        $(this).val("");
                    } else {
                        $(this).select2("val", "");
                    }
                    $(this).prop('disabled', true);
                    $(this).prop('required', false);
                });
            }
        }
    };



    Encuesta.body.delegate("#wizardform  input:radio", 'change', function(e) {
        Encuesta.saveRespuesta($(this));
    });

    Encuesta.body.delegate("#wizardform  input:checkbox", 'change', function(e) {
        Encuesta.marcarRespuesta($(this));
    });

    Encuesta.body.delegate(".fa-close", 'click', function(e) {
        Encuesta.reenviarRespuesta($(this));
    });

    Encuesta.body.delegate(".preOtro", 'change', function(e) {
        Encuesta.saveOtro($(this));
    });

    Encuesta.body.delegate(".preTexto", 'change', function(e) {
        Encuesta.saveOtro($(this));
    });

    Encuesta.body.delegate(".preTextoMulti", 'change', function(e) {
        Encuesta.saveMultiSelect($(this));
    });

    $('.f1 .btn-next').on('click', function() {
        Encuesta.goNextStep($(this));
    });

    $('.f1 .btn-previous').on('click', function() {
        Encuesta.goPreviousStep($(this));
    });

    $('.f1 .btn-submit').on('click', function() {
        Encuesta.closeProcess($(this));
    });

    $(".step-option").change(function() {
        Encuesta.changeOption($(this));
    });

    $(".step-checkbox").change(function() {
        Encuesta.changeCheckbox($(this));
    });

    Encuesta.init();

});
