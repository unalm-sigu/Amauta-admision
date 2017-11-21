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
        var labelColor = {CRE: 'default', ACT: 'success', INA: 'danger'};
        record.index = rowIndex;
        record.esActivo = record.estado == 'ACT';
        record.esInactivo = record.estado == 'INA';
        record.colorEstado = labelColor[record.estado];
        var html = $.templates("#orientacionTemplate").render(record);
        return html;
    }


    var CarreraForm = {
        modalidadEstudio: $('.modalidadEstudio'),
        formCarrera: $("#formularioCarrera"),
        modalOrientacion: $("#modalOrientacion"),
        formModalOrientacion: null,
        formCambioEstado: null,
        init: function () {
            $('[name="facultad.id"]').select2();
            CarreraForm.modalidadEstudio.select2();

            if (CarreraForm.modalidadEstudio.val() != '') {
                CarreraForm.loadTipoCarrera(CarreraForm.modalidadEstudio);
            } 
        },
        viewModalAddOrientacion: function (e) {
            e.preventDefault();
            CarreraForm.formCarrera.parsley().destroy();

            var record = {
                form: "formOrientacion",
                idCarrera: $('.idCarrera').val()
            };

            MODAL.init("md");
            MODAL.title("Nueva Orientación");
            MODAL.body($.templates("#divFormOrientacion").render(record));
            MODAL.buttons('<a class="btn btn-primary guardar-orientacion">Guardar</a>')
            MODAL.show();

            CarreraForm.formModalOrientacion = $("#" + record.form);
            $('[name="idOrientacion"]').val("");
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

            var nombreOrientacion = form.find('[name="nombreOrientacion"]').val();
            var idCarrera = form.find('[name="idCarrera"]').val();
            var idOrientacion = form.find('[name="idOrientacion"]').val();

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
        viewEstadoOrientacion: function (e, $this) {
            e.preventDefault();

            var estado = $this.attr("rev");
            var record = {
                form: "formEstadoOrientacion",
                activo: estado == 'ACT',
                id: $this.attr("rel")
            };
            MODAL.init('md');
            MODAL.title('');
            MODAL.body($.templates("#divEstadoOrientacion").render(record));
            MODAL.buttons('<button type="button" class="btn btn-primary cambio-estado-orientacion">Aceptar</button>');
            MODAL.show();
            CarreraForm.formCambioEstado = $("#" + record.form);
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
                        var data = response.data;
                        var record = {
                            form: "formOrientacion",
                            idOrientacion: data.id,
                            nombreOrientacion: data.nombreOrientacion
                        };

                        MODAL.init("md");
                        MODAL.title("Editar Orientación");
                        MODAL.body($.templates("#divFormOrientacion").render(record));
                        MODAL.buttons('<a class="btn btn-primary guardar-orientacion">Guardar</a>')
                        MODAL.show();

                        CarreraForm.formModalOrientacion = $("#" + record.form);

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cambioEstadoOrientacion: function (e) {
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
        loadTipoCarrera: function ($this) {
            var codigo = $this.find("option:selected").attr("rel");
            console.log("::: " + $this)
            if (codigo == 'EPG') {
                $(".divTipoCarrera").removeClass("hide");

                $('.tipo').select2({
                    placeholder: "Seleccione un tipo de carrera",
                    minimumInputLength: -1,
                    ajax: {
                        url: APP.url("academico/carrera/allTiposCarrera"),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function (response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function (info) {
                        return info.nombre;
                    },
                    formatSelection: function (info) {
                        return info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                });
            } else if (codigo == 'PRE') {
                $(".divTipoCarrera").addClass("hide");
                $('[name="tipo"]').attr("value", "SEM");
            } 
        }
    };
    CarreraForm.init();

    $("body").delegate(".add-orientacion", "click", function (e) {
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
    $("body").delegate(".view-estado-orientacion", "click", function (e) {
        CarreraForm.viewEstadoOrientacion(e, $(this));
    });
    $("body").delegate(".editar-orientacion", "click", function (e) {
        CarreraForm.viewEditarOrientacion(e, $(this));
    });
    $("body").delegate(".cambio-estado-orientacion", "click", function (e) {
        CarreraForm.cambioEstadoOrientacion(e);
    });
    $("body").delegate(".modalidadEstudio", "change", function (e) {
        console.log("change!!!")
        CarreraForm.loadTipoCarrera($(this));
    });

});
