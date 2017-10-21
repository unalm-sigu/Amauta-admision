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
        modalCambioEstadoOrientacion: $("#modalCambioEstadoOrientacion"),
        formCambioEstadoOrientacion: $("#formCambioEstadoOrientacion"),
        init: function () {
            $('[name="facultad.id"]').select2();
            $('[name="modalidadEstudio.id"]').select2();
            $('[name="tipo"]').select2();
        },
        viewModal: function (e) {
            e.preventDefault();
            CarreraForm.formCarrera.parsley().destroy();

            CarreraForm.modalOrientacion.modal("show");
            $('[name="idCarrera"]').val($('[name="id"]').val())
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
                    nombreOrientacion: $('[name="nombreOrientacion"]').val(),
                    idCarrera: $('[name="idCarrera"]').val()},
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
        viewModalDesactivar: function (e, $this) {
            e.preventDefault();
            CarreraForm.formCambioEstadoOrientacion.parsley().destroy();
            CarreraForm.modalCambioEstadoOrientacion.modal("show");
            $('[name="motivo"]').val("");
            $('[name="id"]').val($this.attr("rel"));
            $('[name="idCarrera"]').val($this.attr("rev"));
        },
        desactivarOrientacion: function (e) {
            e.preventDefault();
            var form = CarreraForm.formCambioEstadoOrientacion;
            if (!form.parsley().validate()) {
                return;
            }

            $.ajax({
                url: APP.url('academico/carrera/desactivarOrientacion'),
                type: 'POST',
                async: true,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        CarreraForm.modalCambioEstadoOrientacion.modal("hide");
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
        CarreraForm.viewModal(e);
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
    $("body").delegate(".desactivar-orientacion", "click", function (e) {
        CarreraForm.viewModalDesactivar(e, $(this));
    });
    $("body").delegate(".save-desactivar-orientacion", "click", function (e) {
        CarreraForm.desactivarOrientacion(e);
    });

});
