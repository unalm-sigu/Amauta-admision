$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('seguridad/usuario/allUsuarios'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter

        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        Usuario.moveDivBusqueda();
        console.log(window.location.href)
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {
        var labelColor = {ACT: 'success', INA: 'warning'};
        record.labelEstado = labelColor[record.estado];
        record.index = rowIndex;
        record.tieneCelular = (!(record.celular == "" || record.celular == null));
        record.tieneTelefono = (!(record.telefono == "" || record.telefono == null));
        record.tieneEmail = (!(record.email == "" || record.email == null));
        record.rolesUser = [];
        var tmp = record.roles.split("::::");
        for (var i = 0; i < tmp.length; i++) {
            record.rolesUser.push({nombre: tmp[i]});
        }

        var html = $.templates("#templateUsuario").render(record);
        return html;
    }

    Usuario = {
        b64EncodeUnicode(str) {
            // first we use encodeURIComponent to get percent-encoded UTF-8,
            // then we convert the percent encodings into raw bytes which
            // can be fed into btoa.
            return btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g,
                    function toSolidBytes(match, p1) {
                        return String.fromCharCode('0x' + p1);
                    }));
        },
        verInfoUsuario: function ($this, e) {

            e.preventDefault();

            var tr = $this.closest("tr");
            var idx = tr.attr("rel");
            var rec = dynatable.settings.dataset.records[idx];

            if (rec.estado == "INA") {
                bootbox.alert({
                    title: "Estado de Usuario",
                    message: "Este usuario se encuentra desactivado. Para visualizar sus datos, proceda a Activarlo.",
                    callback: function () {
                    }
                });
                return;
            }

            var usuario = $this.attr("rel");

            $.ajax({
                url: APP.url('seguridad/usuario/infoUsuario'),
                type: 'POST',
                async: true,
                data: {usuario: usuario},
                success: function (response) {
                    MODAL.init("MD");
                    MODAL.title("Información del Usuario");
                    MODAL.body(response);
                    MODAL.buttons('<a class="btn btn-primary" id="btnUsuario" rel="' + usuario + '">Editar</a>');
                    MODAL.show();
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        moveDivBusqueda: function () {

            $("#opopop").prepend($("#divBuscar"));
            $('#divBuscar').removeClass('hide');

            $("#rol").select2({
                placeholder: "Seleccione un rol",
                allowClear: true
            });

        },
        editarUsuario: function ($this, e) {

            e.preventDefault();
            var usuario = $this.attr("rel");

            var origen = location.pathname + location.search;

            var form = $("#formUsuarioEdit");
            form.find("input").val(origen);
            form.attr("action", APP.url('seguridad/usuario/' + usuario + '/edicion'));
            form.submit();
        },
        nuevoUsuario: function ($this, e) {
            e.preventDefault();
            APP.goUrlReturn('seguridad/usuario/nuevo');
        },
        buscaUsuario: function ($this) {
            var search = $this.val();
            if (search == "") {
                dynatable.queries.remove("rol");
            } else {
                dynatable.queries.add("rol", search);
            }

            dynatable.process();
        },
        desactivarUsuario: function ($this, e) {
            e.preventDefault();
            var usuario = $this.attr("rel");

            console.log("idUsuario: " + usuario);

            bootbox.confirm({
                message: "¿Seguro que desea desactivar al este usuario?",
                title: "Desactivar Usuario",
                buttons: {
                    confirm: {label: 'Desactivar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('seguridad/usuario/desactivaUsuario'),
                            type: 'POST',
                            async: true,
                            data: {usuario: usuario},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        },
        activarUsuario: function ($this, e) {
            e.preventDefault();
            var usuario = $this.attr("rel");

            console.log("idUsuario: " + usuario);

            bootbox.confirm({
                message: "¿Seguro que desea activar a este usuario?",
                title: "Activar Usuario",
                buttons: {
                    confirm: {label: 'Activar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('seguridad/usuario/activaUsuario'),
                            type: 'POST',
                            async: true,
                            data: {usuario: usuario},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });

        }
    };

    $("body").delegate(".info-usuario", "click", function (e) {
        Usuario.verInfoUsuario($(this), e);
    });

    $("body").delegate(".modificar", "click", function (e) {
        Usuario.editarUsuario($(this), e);
    });

    $("body").delegate("#btnUsuario", "click", function (e) {
        Usuario.editarUsuario($(this), e);
    });

    $("body").delegate(".eliminar", "click", function (e) {
        Usuario.desactivarUsuario($(this), e);
    });

    $("body").delegate(".activar", "click", function (e) {
        Usuario.activarUsuario($(this), e);
    });

    $('body').delegate('#btnAddUsuario', 'click', function (e) {
        Usuario.nuevoUsuario($(this), e);
    });

    $("body").delegate("#rol", "change", function (e) {
        Usuario.buscaUsuario($(this));
    });
});