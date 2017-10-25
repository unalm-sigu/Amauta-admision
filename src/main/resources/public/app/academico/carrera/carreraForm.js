$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/carrera/listOrientacion/' + $('[name="id"]').val()),
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
        var labelColor = {ACT: 'success', INA: 'danger'};
        record.index = rowIndex;
        record.esActivo = record.estado == 'ACT';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#orientacionTemplate").render(record);
        return html;
    }


    var CarreraForm = {
        formCarrera: $("#formularioCarrera"),
        modalOrientacion: $("#modalOrientacion"),
        formModalOrientacion: $("#formOrientacion"),
        modalCambioEstado: $("#modalCambioEstado"),
        formCambioEstado: $("#formCambioEstado"),
        init: function () {
            $('[name="facultad.id"]').select2();
            $('[name="modalidadEstudio.id"]').select2();
            $('[name="tipo"]').select2();
        },
        viewModalAddOrientacion: function (e) {
            e.preventDefault();
            CarreraForm.formCarrera.parsley().destroy();
            CarreraForm.modalOrientacion.modal("show");
            $('[name="idOrientacion"]').val("");
            $('[name="idCarrera"]').val($('.idCarrera').val())
            $('[name="nombreOrientacion"]').val("");
        },
        saveUpdateCarrera: function (e) {
            e.preventDefault();
            var form = CarreraForm.formCarrera;
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        },
        deleteOrientacion: function ($this) {
            bootbox.confirm({
                message: MESSAGES.confirmDelete,
                title: "Eliminar Orientación",
                buttons: {
                    confirm: {label: 'Eliminar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/carrera/deleteOrientacion'),
                            type: 'POST',
                            async: true,
                            data: {idOrientacion: $this.attr("rel"), idCarrera: $this.attr("rev")},
                            success: function (response) {
                                if (response.success) {
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
                    }
                }
            });
        },
        guardarOrientacion: function (e) {
            var nombreOrientacion = $('[name="nombreOrientacion"]').val();
            var idCarrera = $('[name="idCarrera"]').val();
            var idOrientacion = $('[name="idOrientacion"]').val();

            e.preventDefault();
            var form = CarreraForm.formModalOrientacion;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/saveOrientacion'),
                type: 'POST',
                async: true,
                data: {
                    nombreOrientacion: nombreOrientacion,
                    idCarrera: idCarrera,
                    idOrientacion: idOrientacion},
                success: function (response) {
                    if (response.success) {
                        CarreraForm.modalOrientacion.modal("hide");
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
        viewModalCambioEstado: function (e, $this) {
            e.preventDefault();
            CarreraForm.formCambioEstado.parsley().destroy();
            CarreraForm.modalCambioEstado.modal("show");
            var modal = CarreraForm.formCambioEstado;
            modal.find('[name="motivoAnulacion"]').val("");
            modal.find('[name="id"]').val($this.attr("rel"));
            var estado = $this.attr("rev");
             estado == 'INA' ? $(".tituloCambioEstado").text("Activar Orientación") : $(".tituloCambioEstado").text("¿Por qué motivo desea cambiar el estado?");
            estado == 'INA' ?
                    $(".campoMotivo").html('¿Desea activar el estado de la carrera?') :
                    $(".campoMotivo").html("<textarea class='form-control' name='motivoAnulacion' required='true'></textarea>");
        },
        viewEditarOrientacion: function (e, $this) {
            e.preventDefault();
            CarreraForm.formCarrera.parsley().destroy();
            $.ajax({
                url: APP.url('academico/carrera/editarOrientacion'),
                type: 'POST',
                async: true,
                data: {id: $this.attr("rel")},
                success: function (response) {
                    if (response.success) {
                        CarreraForm.modalOrientacion.modal("show");
                        var modal = CarreraForm.modalOrientacion;
                        var data = response.data;
                        modal.find('[name="idOrientacion"]').val(data.id);
                        modal.find('[name="nombreOrientacion"]').val(data.nombreOrientacion);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cambioEstado: function (e) {
            e.preventDefault();
            var form = CarreraForm.formCambioEstado;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/cambioEstadoOrientacion'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        CarreraForm.modalCambioEstado.modal("hide");
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
        }
    };
    CarreraForm.init();
    $(".add-orientacion").click(function (e) {
        CarreraForm.viewModalAddOrientacion(e);
    });
    $(".save-update-carrera").click(function (e) {
        CarreraForm.saveUpdateCarrera(e);
    });
    $("body").delegate(".guardar-orientacion", "click", function (e) {
        CarreraForm.guardarOrientacion(e);
    });
    $("body").delegate(".delete-orientacion", "click", function () {
        CarreraForm.deleteOrientacion($(this));
    });
    $("body").delegate(".view-cambio-estado", "click", function (e) {
        CarreraForm.viewModalCambioEstado(e, $(this));
    });
    $("body").delegate(".editar-orientacion", "click", function (e) {
        CarreraForm.viewEditarOrientacion(e, $(this));
    });
    $("body").delegate(".cambio-estado", "click", function (e) {
        CarreraForm.cambioEstado(e);
    });
});
