new Vue({
    el: '#main',
    data: {
        curso: {},
        cursos: [],
        btnAgregar: false,
        encuestaSelected: {},
        addCursoModal: {
            id: 'modalAddCurso',
            header: true,
            title: 'Cursos sin encuesta',
            showaccept: false,
            cancelbtn: 'Cerrar'
        }
    },
    mounted: function() {
        let vue = this;
        $global.$on("update", function(encuesta) {
            vue.update(encuesta);
        });
        $global.$on("preguntas", function(encuesta) {
            vue.preguntas(encuesta);
        });
        $global.$on("preview", function(encuesta) {
            vue.preview(encuesta);
        });
        $global.$on("eliminar", function(encuesta) {
            vue.eliminar(encuesta);
        });
        $global.$on("duplicar", function(encuesta) {
            vue.duplicar(encuesta);
        });
        $global.$on("estado", function(encuesta) {
            vue.estado(encuesta);
        });
        $global.$on("sinEncuesta", function(encuesta) {
            vue.sinEncuesta(encuesta);
        });
        $global.$on("configuracion", function(encuesta) {
            vue.configuracion(encuesta);
        });
    },
    methods: {
        update: function(encuesta) {
            var urll = APP.url('academico/encuesta/editor/' + encuesta.id + '/update');
            $(location).attr('href', urll);
        },
        preguntas: function(encuesta) {
            var urll = APP.url('academico/encuesta/editor/pregunta/' + encuesta.id);
            $(location).attr('href', urll);
        },
        preview: function(encuesta) {
            var urll = APP.url('academico/encuesta/editor/' + encuesta.id + '/preview');
            $(location).attr('target', '_blank').attr('href', urll);
        },
        eliminar: function(encuesta) {
            bootbox.confirm({
                message: "¿Seguro que desea eliminar la encuesta " + encuesta.codigo + "?",
                size: "medium",
                buttons: {
                    confirm: {label: "Si, eliminar", className: "btn-danger"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuesta/editor/delete'),
                            type: 'POST',
                            async: false,
                            data: {id: encuesta.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        duplicar: function(encuesta) {
            bootbox.confirm({
                message: "¿Seguro que desea crear una nueva encuesta en base al " + encuesta.codigo + "?",
                size: "medium",
                buttons: {
                    confirm: {label: "Si, duplicar", className: "btn-success"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuesta/editor/duplicar'),
                            type: 'POST',
                            async: false,
                            data: {id: encuesta.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        estado: function(encuesta) {

            var action = (encuesta.estado == "ACT") ? "desactivar" : "activar";
            var btnClass = (encuesta.estado == "ACT") ? "danger" : "primary";
            var mymodal = bootbox.confirm({
                size: "medium",
                message: "¿Está seguro que desea <strong>" + action + "</strong> la encuesta " + encuesta.codigo + "?",
                buttons: {
                    confirm: {label: "Si, " + action, className: "btn-" + btnClass},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('academico/encuesta/editor/estado'),
                            type: 'POST',
                            async: true,
                            data: {id: encuesta.id},
                            success: function(response) {
                                if (response.success) {
                                    dynatable.process();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        sinEncuesta: function(encuesta) {

            var vue = this;
            vue.encuestaSelected = encuesta;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuesta/editor/allcursosinencuesta'),
                data: {
                    'id': vue.encuestaSelected.id,
                },
                async: false,
                success: function(response) {
                    if (response.success) {
                        vue.cursos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            vue.curso = {};
            vue.$refs.modalAddCurso.open();

            $('[name="curso.id"]').
                    select2(vue.selectCurso(vue)).
                    on("change.select2", function(e) {

                        if (e && e.removed) {
                            if (e.val == '') {
                                vue.curso = {};
                            }
                        }

                    });

            $('[name="curso.id"]').select2('val', '');
        },
        configuracion: function(encuesta) {
            var urll = APP.url('academico/encuesta/editor/' + encuesta.id + '/configuracion');
            $(location).attr('target', '_blank').attr('href', urll);
        },
        selectCurso() {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un curso",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/encuesta/editor/searchcurso"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function(info) {
                    return $.templates("#divBuscarCurso").render(info);
                },
                formatSelection: function(info) {
                    vue.curso = info;
                    return info.codigo + " - " + info.curso;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        agregarCurso: function() {
            var vue = this;
            vue.btnAgregar = true;
            if (vue.curso.id == null) {
                notify("No hay curso seleccionado  para agregar", "error");
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/encuesta/editor/addcursosinencuesta'),
                data: {
                    'curso.id': vue.curso.id,
                    'encuestaEstudiantil.encuesta.id': vue.encuestaSelected.id
                },
                async: false,
                success: function(response) {
                    if (response.success) {
                        vue.cursos.push(vue.curso);
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.btnAgregar = false;
                }, error: function() {
                    vue.btnAgregar = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

            vue.curso = {};
            $('[name="curso.id"]').select2('val', '');
        },
        deleteCursoSinEncuesta(curso) {
            var vue = this;
            let idx = vue.cursos.map(item => item.id).indexOf(curso.id);
            if (idx > -1) {
                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/encuesta/editor/removecursosinencuesta'),
                    data: {
                        'curso.id': curso.id,
                        'encuestaEstudiantil.encuesta.id': vue.encuestaSelected.id
                    },
                    async: false,
                    success: function(response) {
                        if (response.success) {
                            notify(response.message, 'info');
                            vue.cursos.splice(idx, 1);
                        } else {
                            notify(response.message, 'error');
                        }
                    }, error: function() {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            }
        },
        removeCurso: function(curso) {
            var vue = this;

            swal({
                text: "¿Está seguro que desea eliminar el curso?",
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
                    vue.deleteCursoSinEncuesta(curso);
                }
            });

        }
    }
});
