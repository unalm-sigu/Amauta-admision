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
        }, nuevoGrupoSec: function ($this, e) {
            MODAL.hide();
            MODAL.init("md");
            MODAL.title("Curso : ");
            MODAL.show();
            MODAL.buttons('<a class="btn btn-success" id="btnSaveGpo">Aceptar</a>');
            MODAL.body('');
            $.ajax({
                url: APP.url('academico/gposeccion/nuevo'),
                type: 'POST',
                async: false,
                success: function (response) {
                    MODAL.body(response);
                    $(".select2").select2();
                    $("#cboCurso").select2(GrupoSeccion.buscarCursoSel).on('select2-selecting', function (e) {
                        $("#cboCurso").val(e.object.id);

                        $.ajax({
                            url: APP.url('academico/gposeccion/' + e.object.id + '/findCurso'),
                            type: 'POST',
                            async: false,
                            success: function (response) {
                                var curso = response.data;
                                $("#codCur").html(curso.cursoCodigo);
                                $("#tpcCur").html(curso.cursoTpc);
                            },
                            error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    });

                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, buscarCursoSel: {
            minimumInputLength: 3,
            ajax: {
                url: APP.url("academico/gposeccion/buscarCursos"),
                dataType: 'json',
                type: 'post',
                data: function (term, page) {
                    return {
                        nombre: term,
                        page: page
                    };
                },
                results: function (response, page) {
                    return {results: response.data};
                }
            },
            formatResult: function (info) {
                return $.templates("#templateCursosProgBody").render(info);
            },
            formatSelection: function (info) {
                return info.cursoCodigo + " - " + info.cursoNombre;
            },
            escapeMarkup: function (m) {
                return m;
            }
        }, cambiarAnexos($this, e) {
            var anexo = $("#cboAnexos").val();
            if (!isNaN(anexo)) {
                if (anexo > 0) {
                    $.ajax({
                        url: APP.url('academico/gposeccion/' + anexo + '/cambiarAnexo'),
                        type: 'POST',
                        async: false,
                        success: function (response) {
                            var anexo = response.data;
                            $("#spnCategoria").html(anexo.anexoCodigo + " - " + anexo.anexoNombre);
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                        }
                    });
                }
            }
        },
        saveHeaderGrupo: function () {


            var form = $("[id='frmPlanCurricular']");
            // form.submit();

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }

            bootbox.confirm({
                message: "¿Está seguro que desea grabar?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    MODAL.showWait("Espere un momento por favor");
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/gposeccion/saveGpoHeader'),
                            type: 'POST',
                            async: true,
                            data: form.serialize(),
                            success: function (response) {
                                if (response.success) {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "info");
                                    location.href = APP.url('academico/gposeccion/' + response.data.gruposeccion + '/succesSave');
                                } else {
                                    MODAL.hideWait();
                                    MODAL.hide();
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                MODAL.hideWait();
                                MODAL.hide();
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });


                    }
                }
            });
        }
    }

    $("body").delegate(".view-count", "click", function (e) {
        GrupoSeccion.viewCount($(this), e);
    });

    $("body").delegate(".nuevo-grupo-sec", "click", function (e) {
        GrupoSeccion.nuevoGrupoSec($(this), e);
    });

    $("body").delegate("#cboAnexos", "change", function (e) {
        GrupoSeccion.cambiarAnexos($(this), e);
    });

    $("body").delegate("#btnSaveGpo", "click", function (e) {
        GrupoSeccion.saveHeaderGrupo();
    });

});