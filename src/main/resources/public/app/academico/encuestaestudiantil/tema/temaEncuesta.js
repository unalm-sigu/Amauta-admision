$(function() {

    var Division = {
        body: $('body'),
        form: null,
        init: function() {
            Division.reloadlist();
        },
        reloadlist: function() {
            $.ajax({
                url: APP.url('academico/encuestaestudiantil/editor/tema/list'),
                type: 'POST',
                async: true,
                data: {idEncuesta: encuesta},
                success: function(response) {
                    if (response.success) {
                        var html = Division.createTree(response.data);
                        $('#tree').html(html).treeview();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        createTree: function(data) {
            var html = "";
            $.each(data, function(i, menu) {

                if (menu.nodes) {
                    var menusHijos = Division.createTree(menu.nodes);
                    menu.menusHijos = menusHijos;
                }

                var tipos = {TEMA: 'Tema', SUBTITULO: 'SubTitulo', BLOQUE: 'Bloque'};
                menu.delete = 'delete' + tipos[menu.tipo];
                menu.edit = 'edit' + tipos[menu.tipo];
                var tiposAdd = {TEMA: 'SubTitulo', SUBTITULO: 'Bloque', BLOQUE: ''};
                menu.add = 'add' + tiposAdd[menu.tipo];
                menu.crearMenuHijo = !(menu.tipo == 'BLOQUE');

                html += $.templates("#divisionTemplate").render(menu);
            });
            return html;
        },
        tema: {
            nuevo: function(e) {

                e.preventDefault();
                Division.form = null;
                var html = $.templates("#temaForm").render({});

                $('#formulario').html(html);
                $('#formulario').find('.cancelar').on('click', function() {
                    $('#formulario').html('');
                });
                $('#formulario').find('.guardar').on('click', function() {
                    Division.form = $('#formulario').find('form:first');
                    if (!Division.form.parsley().validate()) {
                        return false;
                    }
                    var subbb = $('#formulario').find('[name=subtitulosVisibles]').val();
                    var preee = $('#formulario').find('[name=preguntasVisibles]').val();
                    if (Division.validarGrupo(subbb, preee)) {
                        notify('Valores no consistentes', "error");
                        return false;
                    }
                    Division.tema.save();
                    $('#formulario').html('');
                });

                $('input[type="number"]').numeric();

            },
            save: function() {
                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/saveTema'),
                    type: 'POST',
                    async: false,
                    data: Division.form.serialize(),
                    success: function(response) {
                        if (response.success) {
                            Division.reloadlist();
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function() {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                });
            },
            update: function(e) {
                e.preventDefault();
                var self = $(e.currentTarget);
                var tema = self.attr('rel');
                Division.form = null;

                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/updateTema'),
                    type: 'POST',
                    async: true,
                    data: {id: tema},
                    success: function(response) {
                        if (response.success) {

                            $('#formulario').html(response.data);
                            $('#formulario').find('.cancelar').on('click', function() {
                                $('#formulario').html('');
                            });
                            $('#formulario').find('.actualizar').on('click', function() {
                                Division.form = $('#formulario').find('form:first');
                                if (!Division.form.parsley().validate()) {
                                    return false;
                                }
                                var subbb = $('#formulario').find('[name=subtitulosVisibles]').val();
                                var preee = $('#formulario').find('[name=preguntasVisibles]').val();
                                if (Division.validarGrupo(subbb, preee)) {
                                    notify('Valores no consistentes', "error");
                                    return false;
                                }
                                Division.tema.save();
                                $('#formulario').html('');
                            });

                            $('input[type="number"]').numeric();

                        } else {
                            notify(response.message, "error");
                            $('#formulario').html('');
                        }
                    },
                    error: function() {
                        $('#formulario').html('');
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                });

            },
            delete: function(e) {

                e.preventDefault();
                var self = $(e.currentTarget);
                var menu = self.attr('rel');

                bootbox.confirm({
                    message: "¿Seguro que desea eliminar el tema?",
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {

                            $.ajax({
                                url: APP.url('academico/encuestaestudiantil/editor/tema/deleteTema'),
                                type: 'POST',
                                async: true,
                                data: {id: menu},
                                success: function(response) {
                                    if (response.success) {
                                        Division.reloadlist();
                                    } else {
                                        notify(response.message, "error");
                                    }
                                },
                                error: function() {
                                    notify(GlobalMessages.errorComunicacion, "error");
                                }
                            });

                        }
                    }
                });
            }
        },
        subTitulo: {
            nuevo: function(e) {
                e.preventDefault();
                var self = $(e.currentTarget);
                var tema = self.attr('rel');
                Division.form = null;

                var html = $.templates("#subTituloForm").render({});

                $('#formulario').html(html);
                $('#formulario').find('[name="temaExamen.id"]').val(tema);
                $('#formulario').find('.cancelar').on('click', function() {
                    $('#formulario').html('');
                });
                $('#formulario').find('.guardar').on('click', function() {
                    Division.form = $('#formulario').find('form:first');
                    if (!Division.form.parsley().validate()) {
                        return false;
                    }
                    var subbb = $('#formulario').find('[name=bloquesVisibles]').val();
                    var preee = $('#formulario').find('[name=preguntasVisibles]').val();
                    if (Division.validarGrupo(subbb, preee)) {
                        notify('Valores no consistentes', "error");
                        return false;
                    }
                    Division.subTitulo.save();
                    $('#formulario').html('');
                });
                $('input[type="number"]').numeric();

            },
            save: function() {
                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/saveSubTitulo'),
                    type: 'POST',
                    async: false,
                    data: Division.form.serialize(),
                    success: function(response) {
                        if (response.success) {
                            Division.reloadlist();
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function() {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                });
            },
            update: function(e) {
                e.preventDefault();
                var self = $(e.currentTarget);
                var tema = self.attr('rel');
                Division.form = null;

                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/updateSubTitulo'),
                    type: 'POST',
                    async: true,
                    data: {id: tema},
                    success: function(response) {
                        if (response.success) {

                            $('#formulario').html(response.data);
                            $('#formulario').find('.cancelar').on('click', function() {
                                $('#formulario').html('');
                            });
                            $('#formulario').find('.guardar').on('click', function() {
                                Division.form = $('#formulario').find('form:first');
                                if (!Division.form.parsley().validate()) {
                                    return false;
                                }
                                var subbb = $('#formulario').find('[name=bloquesVisibles]').val();
                                var preee = $('#formulario').find('[name=preguntasVisibles]').val();
                                if (Division.validarGrupo(subbb, preee)) {
                                    notify('Valores no consistentes', "error");
                                    return false;
                                }
                                Division.subTitulo.save();
                                $('#formulario').html('');
                            });

                            $('input[type="number"]').numeric();


                        } else {
                            notify(response.message, "error");
                            $('#formulario').html('');
                        }
                    },
                    error: function() {
                        notify(GlobalMessages.errorComunicacion, "error");
                        $('#formulario').html('');
                    }
                });
            },
            delete: function(e) {

                e.preventDefault();
                var self = $(e.currentTarget);
                var menu = self.attr('rel');

                bootbox.confirm({
                    message: "¿Seguro que desea eliminar el Subtítulo?",
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {

                            $.ajax({
                                url: APP.url('academico/encuestaestudiantil/editor/tema/deleteSubTitulo'),
                                type: 'POST',
                                async: true,
                                data: {id: menu},
                                success: function(response) {
                                    if (response.success) {
                                        Division.reloadlist();
                                    } else {
                                        notify(response.message, "error");
                                    }
                                },
                                error: function() {
                                    notify(GlobalMessages.errorComunicacion, "error");
                                }
                            });

                        }
                    }
                });
            }
        },
        bloque: {
            nuevo: function(e) {

                e.preventDefault();
                Division.form = null;

                var self = $(e.currentTarget);
                var subtitulo = self.attr('rel');
                var html = $.templates("#bloqueForm").render({});

                console.log(subtitulo)
                $('#formulario').html(html);
                $('#formulario').find('[name="subTituloExamen.id"]').val(subtitulo);
                $('#formulario').find('textarea').attr('id', 'modalTextarea');

                tinymce.remove("textarea");

                tinymce.init({
                    selector: 'textarea',
                    height: 350,
                    menubar: false,
                    language: "es",
                    content_css: APP.url('vendor/mathquill/mathquill.css'),
                    plugins: [
                        'advlist autolink lists charmap print preview equationeditor',
                        'searchreplace visualblocks code fullscreen',
                        'insertdatetime table contextmenu paste image code'
                    ],
                    toolbar: 'undo redo |  styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | charmap | image | equationeditor ',
                    image_title: true,
                    automatic_uploads: true,
                    images_upload_url: APP.url('academico/encuestaestudiantil/editor/pregunta/upload'),
                    file_picker_types: 'image',
                    file_picker_callback: function(cb, value, meta) {
                        var input = document.createElement('input');
                        input.setAttribute('type', 'file');
                        input.setAttribute('accept', 'image/*');

                        input.onchange = function() {
                            var file = this.files[0];

                            var reader = new FileReader();
                            reader.readAsDataURL(file);
                            reader.onload = function() {

                                var id = 'blobid' + (new Date()).getTime();
                                var blobCache = tinymce.activeEditor.editorUpload.blobCache;
                                var base64 = reader.result.split(',')[1];
                                var blobInfo = blobCache.create(id, file, base64);
                                blobCache.add(blobInfo);

                                cb(blobInfo.blobUri(), {title: file.name});
                            };
                        };

                        input.click();
                    }
                });


                $('#formulario').find('.cancelar').on('click', function() {
                    $('#formulario').html('');
                });

                $('#formulario').find('.guardar').on('click', function() {
                    Division.form = $('#formulario').find('form:first');
                    if (!Division.form.parsley().validate()) {
                        return false;
                    }
                    Division.bloque.save();
                    $('#formulario').html('');
                });

                $('input[type="number"]').numeric();
            },
            save: function() {
                tinymce.triggerSave();
                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/saveBloque'),
                    type: 'POST',
                    async: false,
                    data: Division.form.serialize(),
                    success: function(response) {
                        if (response.success) {
                            Division.reloadlist();
                            notify(response.message, "info");
                        } else {
                            notify(response.message, "error");
                        }
                    },
                    error: function() {
                        notify(GlobalMessages.errorComunicacion, "error");
                    }
                });
            },
            update: function(e) {
                e.preventDefault();
                var self = $(e.currentTarget);
                var tema = self.attr('rel');
                Division.form = null;

                $.ajax({
                    url: APP.url('academico/encuestaestudiantil/editor/tema/updateBloque'),
                    type: 'POST',
                    async: false,
                    data: {id: tema},
                    success: function(response) {
                        if (response.success) {

                            $('#formulario').html(response.data);
                            $('#formulario').find('textarea').attr('id', 'modalTextarea');

                            tinymce.remove("textarea");

                            tinymce.init({
                                selector: 'textarea',
                                height: 350,
                                menubar: false,
                                language: "es",
                                content_css: APP.url('vendor/mathquill/mathquill.css'),
                                plugins: [
                                    'advlist autolink lists charmap print preview equationeditor',
                                    'searchreplace visualblocks code fullscreen',
                                    'insertdatetime table contextmenu paste image code'
                                ],
                                toolbar: 'undo redo |  styleselect | bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | charmap | image | equationeditor ',
                                image_title: true,
                                automatic_uploads: true,
                                images_upload_url: APP.url('academico/encuestaestudiantil/editor/pregunta/upload'),
                                file_picker_types: 'image',
                                file_picker_callback: function(cb, value, meta) {
                                    var input = document.createElement('input');
                                    input.setAttribute('type', 'file');
                                    input.setAttribute('accept', 'image/*');

                                    input.onchange = function() {
                                        var file = this.files[0];

                                        var reader = new FileReader();
                                        reader.readAsDataURL(file);
                                        reader.onload = function() {

                                            var id = 'blobid' + (new Date()).getTime();
                                            var blobCache = tinymce.activeEditor.editorUpload.blobCache;
                                            var base64 = reader.result.split(',')[1];
                                            var blobInfo = blobCache.create(id, file, base64);
                                            blobCache.add(blobInfo);

                                            cb(blobInfo.blobUri(), {title: file.name});
                                        };
                                    };

                                    input.click();
                                }
                            });


                            $('#formulario').find('.cancelar').on('click', function() {
                                $('#formulario').html('');
                            });

                            $('#formulario').find('.guardar').on('click', function() {
                                Division.form = $('#formulario').find('form:first');
                                if (!Division.form.parsley().validate()) {
                                    return false;
                                }
                                Division.bloque.save();
                                $('#formulario').html('');
                            });

                            $('input[type="number"]').numeric();


                        } else {
                            notify(response.message, "error");
                            $('#formulario').html('');
                        }
                    },
                    error: function() {
                        notify(GlobalMessages.errorComunicacion, "error");
                        $('#formulario').html('');
                    },
                });

            },
            delete: function(e) {

                e.preventDefault();
                var self = $(e.currentTarget);
                var menu = self.attr('rel');

                bootbox.confirm({
                    message: "¿Seguro que desea eliminar el bloque?",
                    buttons: {
                        confirm: {label: "Si, eliminar", className: "btn-danger"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {

                            $.ajax({
                                url: APP.url('academico/encuestaestudiantil/editor/tema/deleteBloque'),
                                type: 'POST',
                                async: true,
                                data: {id: menu},
                                success: function(response) {
                                    if (response.success) {
                                        Division.reloadlist();
                                    } else {
                                        notify(response.message, "error");
                                    }
                                },
                                error: function() {
                                    notify(GlobalMessages.errorComunicacion, "error");
                                }
                            });

                        }
                    }
                });
            }
        },
        itemSort: function(e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var postData = {};

            postData['instancia'] = self.attr('rel');
            postData['tipo'] = self.attr('data-tipo');
            postData['itemSort'] = self.attr('rev');

            $.ajax({
                url: APP.url('academico/encuestaestudiantil/editor/tema/itemSort'),
                type: 'POST',
                async: false,
                data: postData,
                success: function(response) {
                    if (response.success) {
                        Division.reloadlist();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });

        },
        estado: function(e) {
            e.preventDefault();
            var self = $(e.currentTarget);
            var postData = {};
            postData['instancia'] = self.attr('rel');
            postData['tipo'] = self.attr('data-tipo');
            bootbox.confirm({
                size: "small",
                title: "Estado",
                message: "¿Desea modificar el estado del registro?",
                buttons: {
                    confirm: {label: "Si, Acepto", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuestaestudiantil/editor/tema/estado'),
                            type: 'POST',
                            async: false,
                            data: postData,
                            success: function(response) {
                                if (response.success) {
                                    Division.reloadlist();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        validarGrupo: function(subbb, preee) {

            console.log(subbb);
            console.log(preee);
            var valor1 = parseInt(subbb);
            var valor2 = parseInt(preee);
            console.log(valor1);
            console.log(valor2);

            if (valor1 == 0 && valor2 == 0) {
                console.log('ambos iguales a cero');
                return true;
            }

            if (valor1 > 0 && valor2 > 0) {
                console.log('ambos mayores a cero');
                return true;
            }

            return false;
        }
    };


    Division.body.delegate('#nuevoTema', 'click', function(e) {
        Division.tema.nuevo(e);
    });

    Division.body.delegate('.editTema', 'click', function(e) {
        Division.tema.update(e);
    });
    Division.body.delegate('.deleteTema', 'click', function(e) {
        Division.tema.delete(e);
    });


    Division.body.delegate('.addSubTitulo', 'click', function(e) {
        Division.subTitulo.nuevo(e);
    });
    Division.body.delegate('.editSubTitulo', 'click', function(e) {
        Division.subTitulo.update(e);
    });
    Division.body.delegate('.deleteSubTitulo', 'click', function(e) {
        Division.subTitulo.delete(e);
    });


    Division.body.delegate('.addBloque', 'click', function(e) {
        Division.bloque.nuevo(e);
    });
    Division.body.delegate('.editBloque', 'click', function(e) {
        Division.bloque.update(e);
    });
    Division.body.delegate('.deleteBloque', 'click', function(e) {
        Division.bloque.delete(e);
    });


    Division.body.delegate('.btnSort', 'click', function(e) {
        Division.itemSort(e);
    });

    Division.body.delegate('.estado', 'click', function(e) {
        Division.estado(e);
    });

    Division.body.delegate('.segnal', 'mouseover', function(e) {
        $(this).css("background", "#eee");
        $(this).find('.btnSort').show();
    });
    Division.body.delegate('.segnal', 'mouseout', function(e) {
        if (!$(this).hasClass("active")) {
            $(this).css("background", "transparent");
        }
        $(this).find('.btnSort').hide();
    });

    Division.init();
});