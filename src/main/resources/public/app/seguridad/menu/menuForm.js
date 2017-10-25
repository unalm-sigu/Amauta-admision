$(function () {

    MenuForm = {
        body: $('body'),
        init: function () {
            MenuForm.reloadlist();
        },
        form: null,
        formPerfi: null,
        menuActivo: null,
        mibox: null,
        nuevoTitulo: function (e) {
            e.preventDefault();
            MenuForm.form = null;
            var mibox = bootbox.confirm({
                title: "Nuevo Título",
                message: "<i class='fa fa-spinner fa-spin' aria-hidden='true'></i>",
                buttons: {
                    confirm: {label: "Crear Nuevo Título", className: "btn-primary"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MenuForm.form = mibox.find('form:first');
                        if (!MenuForm.form.parsley().validate()) {
                            return false;
                        }
                        MenuForm.addNuevoTitulo();
                    }
                }
            }).on('shown.bs.modal', function () {
                var html = $.templates("#titulotemplate").render({});
                var outerHTML = $(html).prop('outerHTML');
                mibox.find('.bootbox-body').html(outerHTML);
            });
        },
        addNuevoTitulo: function () {
            $.ajax({
                url: APP.url('seguridad/menu/save'),
                type: 'POST',
                async: false,
                data: MenuForm.form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MenuForm.reloadlist();
                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        reloadlist: function () {
            $.ajax({
                url: APP.url('seguridad/menu/list'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        var html = $.templates("#menuListTemplate").render(response.data);
                        $('#tree').html(html);
                        //$('#tree').treeview();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        addMenu: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var padre = self.attr('rel');
            MenuForm.form = null;
            var mibox = bootbox.confirm({
                title: "Nuevo Menú",
                message: "<i class='fa fa-spinner fa-spin' aria-hidden='true'></i>",
                buttons: {
                    confirm: {label: "Crear Nuevo Menú", className: "btn-primary"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        MenuForm.form = mibox.find('form:first');
                        if (!MenuForm.form.parsley().validate()) {
                            return false;
                        }
                        MenuForm.addNuevoTitulo();
                    }
                }
            }).on('shown.bs.modal', function () {
                var html = $.templates("#menuTemplate").render({});
                var outerHTML = $(html).prop('outerHTML');
                mibox.find('.bootbox-body').html(outerHTML);
            });
        },
        addEditMenu: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var menuEdit = self.attr('rel');
            $.ajax({
                url: APP.url('seguridad/menu/update'),
                type: 'POST',
                async: true,
                data: {id: menuEdit},
                success: function (response) {
                    if (response.success) {
                        $('#formMenu').html(response.data);
                        $('#formMenu').find('[name="tipo"]').select2({});
                        $('#formMenu').find('[name="icono"]').iconpicker();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        removeEditMenu: function (data) {

            $('#formMenu').html('');

        },
        addSubMenu: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var menuSuperior = self.attr('rel');
            $.ajax({
                url: APP.url('seguridad/menu/nuevo'),
                type: 'POST',
                async: true,
                success: function (response) {
                    if (response.success) {
                        $('#formMenu').html(response.data);
                        $('#formMenu').find('[name="tipo"]').select2({});
                        $('#formMenu').find('[name="icono"]').iconpicker();
                        $('#formMenu').find('[name="menuSuperior.id"]').val(menuSuperior);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        btnSaveMenu: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            self.btnDisabled();
            MenuForm.form = null;
            MenuForm.form = $('#formMenu').find('form:first');
            MenuForm.addNuevoTitulo();
            self.btnEnable();
            $('#formMenu').html('');
        },
        cancelarMenu: function () {
            MenuForm.form = null;
            $('#formMenu').html('');
        },
        deleteMenu: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var menu = self.attr('rel');

            bootbox.confirm({
                message: "¿Seguro que desea eliminar el menú?",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('seguridad/menu/delete'),
                            type: 'POST',
                            async: true,
                            data: {id: menu},
                            success: function (response) {
                                if (response.success) {
                                    MenuForm.reloadlist();
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
        perfilMenu: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var menu = self.attr('rel');
            MenuForm.menuActivo = menu;
            MenuForm.mibox = null;
            var mibox = bootbox.alert({
                title: "Roles de Menú",
                message: "<i class='fa fa-spinner fa-spin' aria-hidden='true'></i>",
                buttons: {
                    ok: {label: "Aceptar", className: "btn-link"}
                },
            });
            MenuForm.mibox = mibox;
            MenuForm.ajaxPerfilMenu();
        },
        ajaxPerfilMenu: function () {
            $.ajax({
                url: APP.url('seguridad/menu/perfiles'),
                type: 'POST',
                async: false,
                data: {id: MenuForm.menuActivo},
                success: function (response) {
                    if (response.success) {
                        MenuForm.mibox.find('.bootbox-body').html(response.data);
                    } else {
                        notify(response.message, "error");
                        MenuForm.mibox.modal('hide');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MenuForm.mibox.modal('hide');
                }
            });
        },
        btnRolAsignar: function (e) {

            e.preventDefault();
            var values = $('select#rolPorAsignar option:selected');
            console.log(values);
            if (values.length < 1) {
                notify('Seleccione uno o más roles para asignar.', "error");
                return;
            }

            var form = $('<form/>');
            var menu = MenuForm.menuActivo;

            $.each(values, function (i, v) {
                var input1 = $('<input/>', {type: 'hidden', name: 'menuRol[' + i + '].menu.id', value: menu});
                var input2 = $('<input/>', {type: 'hidden', name: 'menuRol[' + i + '].rol.id', value: $(v).val()});
                form.append(input1);
                form.append(input2);
            });

            $.ajax({
                url: APP.url('seguridad/menu/asignarRol'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MenuForm.ajaxPerfilMenu();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        btnRolDesasignar: function (e) {
            e.preventDefault();
            var values = $('select#rolAsignados option:selected');
            console.log(values);
            if (values.length < 1) {
                notify('Seleccione uno o más roles para desasignar.', "error");
                return;
            }

            var form = $('<form/>');
            var menu = MenuForm.menuActivo;

            $.each(values, function (i, v) {
                var input1 = $('<input/>', {type: 'hidden', name: 'menuRol[' + i + '].menu.id', value: menu});
                var input2 = $('<input/>', {type: 'hidden', name: 'menuRol[' + i + '].rol.id', value: $(v).val()});
                form.append(input1);
                form.append(input2);
            });

            $.ajax({
                url: APP.url('seguridad/menu/desAsignarRol'),
                type: 'POST',
                async: false,
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        MenuForm.ajaxPerfilMenu();
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

    MenuForm.body.delegate('#nuevoTitulo', 'click', function (e) {
        MenuForm.nuevoTitulo(e);
    });
    MenuForm.body.delegate('.addMenu', 'click', function (e) {
        MenuForm.addSubMenu(e);
    });
    MenuForm.body.delegate('.editMenu', 'click', function (e) {
        MenuForm.addEditMenu(e);
    });
    MenuForm.body.delegate('.deleteMenu', 'click', function (e) {
        MenuForm.deleteMenu(e);
    });
    MenuForm.body.delegate('.perfilMenu', 'click', function (e) {
        MenuForm.perfilMenu(e);
    });
    MenuForm.body.delegate('#btnSaveMenu', 'click', function (e) {
        MenuForm.btnSaveMenu(e);
    });
    MenuForm.body.delegate('#cancelarMenu', 'click', function (e) {
        MenuForm.cancelarMenu(e);
    });
    MenuForm.body.delegate('#btnRolAsignar', 'click', function (e) {
        MenuForm.btnRolAsignar(e);
    });
    MenuForm.body.delegate('#btnRolDesasignar', 'click', function (e) {
        MenuForm.btnRolDesasignar(e);
    });

    MenuForm.init();
});