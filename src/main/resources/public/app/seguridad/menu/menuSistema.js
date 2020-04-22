$(function () {

    MenuForm = {
        body: $('body'),
        divActivo: null,
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
                    notify(Messages.errorComunicacion, "error");
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
                        //var html = $.templates("#menuSistemaTemplate").render(response.data);
                        var html = MenuForm.createTree(response.data);
                        $('#tree').html(html);
                        MenuForm.stylesMenu();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        createTree: function (listaMenus) {
            var html = "";
            $.each(listaMenus, function (i, menu) {
                if (menu.nodes) {
                    var menusHijos = MenuForm.createTree(menu.nodes);
                    menu.menusHijos = menusHijos;
                }
                menu.crearMenuHijo = !(menu.tipo == 'OPCION' || menu.tipo == 'BOTON');
                menu.asignarPermisos = (menu.tipo == 'OPCION' || menu.tipo == 'BOTON' || menu.tipo == 'MENU' || menu.tipo == 'SUB_MENU');

                html += $.templates("#menuSistemaTemplate").render(menu);
                //console.log();
            });
            return html;
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
                var html = $.templates("#menutemplate").render({});
                var outerHTML = $(html).prop('outerHTML');
                mibox.find('.bootbox-body').html(outerHTML);
            });
        },
        addEditMenu: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var menuEdit = self.attr('rel');
            MenuForm.activarDiv(self.closest(".segnal"));

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
                        $('#formMenu').find('[name="tipo"]').trigger('change');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
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
            MenuForm.activarDiv(self.closest(".segnal"));
            $.ajax({
                url: APP.url('seguridad/menu/nuevo'),
                type: 'POST',
                async: true,
                data: {menuSuperior: menuSuperior},
                success: function (response) {
                    if (response.success) {
                        $('#formMenu').html(response.data);
                        $('#formMenu').find('[name="tipo"]').select2({});
                        $('#formMenu').find('[name="icono"]').iconpicker();
                        //$('#formMenu').find('[name="menuSuperior.id"]').val(menuSuperior);
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
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
            MenuForm.activarDiv(self.closest(".segnal"));

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
                                notify(Messages.errorComunicacion, "error");
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
            MenuForm.activarDiv(self.closest(".segnal"));
            MenuForm.menuActivo = menu;
            MenuForm.mibox = null;
            var mibox = bootbox.alert({
                title: "Roles Asignado al Menú",
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
                    notify(Messages.errorComunicacion, "error");
                    MenuForm.mibox.modal('hide');
                }
            });
        },
        btnRolAsignar: function (e) {

            e.preventDefault();
            var values = $('select#rolPorAsignar option:selected');
            //console.log(values);
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
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        btnRolDesasignar: function (e) {
            e.preventDefault();
            var values = $('select#rolAsignados option:selected');
            //console.log(values);
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
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        stylesMenu: function () {
            $('.tree li:has(ul)').addClass('parent_li').find(' > div > span').attr('title', 'Colapsar este menú');
            $('.tree li.parent_li > div > span').on('click', function (e) {

                var children = $(this).parent("div").parent('li.parent_li').find(' > ul > li');
                //$("#formMenu").text(children.html())
                if (children.is(":visible")) {
                    children.hide('fast');
                    $(this).attr('title', 'Expandir este menú').find(' > i').addClass('fa-folder').removeClass('fa-folder-open-o');
                } else {
                    children.show('fast');
                    $(this).attr('title', 'Colapsar este menú').find(' > i').addClass('fa-folder-open-o').removeClass('fa-folder');
                }
                e.stopPropagation();
            });
        },
        activarDiv: function (div) {
            if (MenuForm.divActivo != null) {
                MenuForm.divActivo.removeClass("active");
                MenuForm.divActivo.css("background", "transparent");
            }
            div.addClass("active");
            MenuForm.divActivo = div;
        },
        tipochange: function (e) {
            var self = $(e.currentTarget);
            var tipoValor = self.val();
            var fruits = ["MENU_PADRE", "MENU"];
            var a = fruits.indexOf(tipoValor);
            if (a < 0) {
                $('#formMenu').find('[name="icono"]').parents('.form-group:first').hide();
                $('#formMenu').find('[name="icono"]').parents('.form-group:first').val('');
            } else {
                $('#formMenu').find('[name="icono"]').parents('.form-group:first').show();
            }
        },
        rutachange: function (e) {
            var self = $(e.currentTarget);
            var tipoValor = self.val();
            var fruits = ["SUB_MENU", "MENU", "OPCION", "BOTON"];
            var a = fruits.indexOf(tipoValor);
            if (a < 0) {
                $('#formMenu').find('[name="ruta"]').parents('.form-group:first').hide();
                $('#formMenu').find('[name="ruta"]').parents('.form-group:first').val('');
            } else {
                $('#formMenu').find('[name="ruta"]').parents('.form-group:first').show();
            }
        },
        itemUp: function (e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var menu = self.attr('rel');
            //console.log('up');
            $.ajax({
                url: APP.url('seguridad/menu/itemMenuUp'),
                type: 'POST',
                async: false,
                data: {id: menu},
                success: function (response) {
                    if (response.success) {
                        MenuForm.reloadlist();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        itemDown: function (e) {
            //console.log('down');
            e.preventDefault();
            var self = $(e.currentTarget);
            var menu = self.attr('rel');
            $.ajax({
                url: APP.url('seguridad/menu/itemMenuDown'),
                type: 'POST',
                async: false,
                data: {id: menu},
                success: function (response) {
                    if (response.success) {
                        MenuForm.reloadlist();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
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
    MenuForm.body.delegate('.segnal', 'mouseover', function (e) {
        $(this).css("background", "#eee");
        $(this).find('.btn-down').show();
        $(this).find('.btn-up').show();
    });
    MenuForm.body.delegate('.segnal', 'mouseout', function (e) {
        if (!$(this).hasClass("active")) {
            $(this).css("background", "transparent");
        }
        $(this).find('.btn-down').hide();
        $(this).find('.btn-up').hide();
    });
    MenuForm.body.delegate('[name="tipo"]', 'change', function (e) {
        MenuForm.tipochange(e);
        MenuForm.rutachange(e);
    });
    MenuForm.body.delegate('.btn-up', 'click', function (e) {
        MenuForm.itemUp(e);
    });
    MenuForm.body.delegate('.btn-down', 'click', function (e) {
        MenuForm.itemDown(e);
    });

    MenuForm.init();
});