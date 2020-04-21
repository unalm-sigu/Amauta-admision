$(function () {


    var dynatable = $('#dynaTable').dynatable({
        features: {
            sort: false,
            perPageSelect: false,
            search: false,
            recordCount: false
        },
        dataset: {
            ajaxUrl: APP.url('seguridad/rol/list'),
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
        $("#opopop").prepend($("#headDynatable"));
        $('#headDynatable').removeClass('hide');
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {
        record.index = rowIndex;

        var html = $.templates("#rolTemplate").render(record);
        var outerHTML = $(html).prop('outerHTML');
        return outerHTML;
    }


    Rol = {
        body: $('body'),
        divActivo: null,
        init: function () {
        },
        form: null,
        formPerfi: null,
        menuActivo: null,
        mibox: null,
        createTree: function (listaMenus) {
            var html = "";
            $.each(listaMenus, function (i, menu) {
                if (menu.nodes) {
                    var menusHijos = Rol.createTree(menu.nodes);
                    menu.menusHijos = menusHijos;
                }
                menu.crearMenuHijo = !(menu.tipo == 'OPCION' || menu.tipo == 'BOTON');
                menu.asignarPermisos = (menu.tipo == 'OPCION' || menu.tipo == 'BOTON' || menu.tipo == 'MENU' || menu.tipo == 'SUB_MENU');

                html += $.templates("#menuSistemaTemplate").render(menu);
            });
            return html;
        },
        cancelarMenu: function () {
            Rol.form = null;
            $('#formMenu').html('');
        },
        deleteRol: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var rol = self.attr('rel');

            bootbox.confirm({
                message: MESSAGES.confirmDelete,
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            url: APP.url('seguridad/rol/delete'),
                            type: 'POST',
                            async: true,
                            data: {id: rol},
                            success: function (response) {
                                if (response.success) {
                                    notify("Registro eliminado con éxito", "info");
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
            if (Rol.divActivo != null) {
                Rol.divActivo.removeClass("active");
                Rol.divActivo.css("background", "transparent");
            }
            div.addClass("active");
            Rol.divActivo = div;
        },
        showInfo: function (rel, e) {
            $.ajax({
                url: APP.url('seguridad/rol/listRol'),
                type: 'POST',
                async: true,
                data: {rol: rel},
                success: function (response) {
                    if (response.success) {
                        var html = Rol.createTree(response.data);
                        $('#tree').html(html);
                        Rol.stylesMenu();
                        if (response.data.length === 0) {
                            $('#tree').html("No se han encontrado Menus asignados a este rol.");
                        }
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        editarRol: function (rel) {
            $.ajax({
                method: 'POST',
                url: APP.url('seguridad/rol/editarRol'),
                data: {id: rel},
                success: function (response) {
                    $('#rolModal').html(response);
                    $('#viewModal').modal('show');
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        saveRol: function (e) {
            e.preventDefault();
            var form = $("#formulario");
//            if (!form.parsley().validate()) {
//                return;
//            }

            $.ajax({
                url: APP.url('seguridad/rol/save'),
                type: 'POST',
                data: form.serialize(),
                success: function (response) {
                    if (response.success) {
                        $('#viewModal').modal('hide');
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
        },
        newRol: function () {
            $.ajax({
                method: 'POST',
                url: APP.url('seguridad/rol/nuevoRol'),
                success: function (response) {
                    $('#rolModal').html(response);
                    $('#viewModal').modal('show');
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    };

    Rol.body.delegate('#nuevoTitulo', 'click', function (e) {
        Rol.nuevoTitulo(e);
    });
    Rol.body.delegate('.showInfo', 'click', function (e) {
        Rol.showInfo($(this).attr('rel'), e);
    });
    Rol.body.delegate('.delete-rol', 'click', function (e) {
        Rol.deleteRol(e);
    });
    Rol.body.delegate('.editar-rol', 'click', function (e) {
        Rol.editarRol($(this).attr('rel'));
    });
    Rol.body.delegate('.save-rol', 'click', function (e) {
        Rol.saveRol(e);
    });
    Rol.body.delegate('.new-rol', 'click', function (e) {
        Rol.newRol();
    });

    Rol.init();
});