$(function() {

    var $global = new Vue({});

    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function() {
            return {horario: []};
        },
        methods: {
            buscarHorario(id) {
                $global.$emit("buscarHorario", id);
            },
            asignarHorario(id) {
                $global.$emit("asignarHorario", id);
            },
            retirarHorario(id) {
                $global.$emit("retirarHorario", id);
            },
            suspenderMatricula(id) {
                $global.$emit("suspenderMatricula", id);
            },
            activarMatricula(id) {
                $global.$emit("activarMatricula", id);
            },
        }
    });

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        mounted: function() {
            var $vue = this;
            $vue.createDynatable();
        },
        methods: {
            createDynatable: function() {
                var $vue = this;

                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/ingresante/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function(e) {

                    var records = dynatable.settings.dataset.records;
                    for (var i = 0, max = records.length; i < max; i++) {
                        var dynatableRowTemplate = new DynatableRowTemplate();
                        dynatableRowTemplate.horario = records[i];
                        var component = dynatableRowTemplate.$mount();
                        $('#dynaTbody').append(component.$el);
                    }
                }).data('dynatable');
            },
            writter: function(rowIndex, record, columns, cellWriter) {
                return "";
            }
        }
    });

    new Vue({
        el: '#main',
        data: {
            horario: {},
            alumno: {},
            addAlumnoModal: {
                id: 'modalAddAlumno',
                header: true,
                title: 'Agregar Alumno',
                okbtn: 'Agregar Alumno'
            },
        },
        created() {
            let $vue = this;
        },
        mounted: function() {

            let $vue = this;
            $global.$on("buscarHorario", function(id) {
                $vue.buscarHorario(id);
            });
            $global.$on("asignarHorario", function(id) {
                $vue.asignarHorario(id);
            });
            $global.$on("retirarHorario", function(id) {
                $vue.retirarHorario(id);
            });
            $global.$on("suspenderMatricula", function(id) {
                $vue.suspenderMatricula(id);
            });
            $global.$on("activarMatricula", function(id) {
                $vue.activarMatricula(id);
            });

        },
        methods: {
            nuevo() {
                var vue = this;
                this.$refs.modalAddAlumno.open();
                $('[name="alumno.id"]').select2({
                    allowClear: true,
                    placeholder: "Seleccione un alumno",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url("academico/horariocachimbo/ingresante/searchAlumno"),
                        dataType: 'json',
                        type: 'post',
                        data: function(term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function(response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function(element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function(info) {
                        return $.templates("#divBuscarAlumno").render(info);
                    },
                    formatSelection: function(info) {
                        vue.printFullData(info);
                        return info.nombre;
                    },
                    escapeMarkup: function(m) {
                        return m;
                    }
                }).on("change.select2", function(e) {
                    if (e && e.removed) {
                        if (e.val == '') {
                            vue.clearAlumno(e);
                        }
                    }
                });
                $('[name="alumno.id"]').select2('data', '');
                vue.alumno = [];
            },
            printFullData(info) {
                var vue = this;
                vue.alumno = info;
            },
            clearAlumno(e) {
                var vue = this;
                vue.alumno = [];
            },
            createAlumno(id) {
                var vue = this;
                var valid = $('[name="alumno.id"]').parsley().validate();
                if (valid != true) {
                    return;
                }
                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/horariocachimbo/ingresante/addAlumno'),
                    data: {id: vue.alumno.id},
                    success: function(response) {
                        if (response.success) {
                            vue.$refs.modalAddAlumno.close();
                            vue.reloadDinatable();
                        } else {
                            notify(response.message, 'error');
                        }
                    }, error: function() {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            },
            reloadDinatable() {
                dynatable.process();
            },
            buscarHorario(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea buscar horario?',
                    buttons: {
                        confirm: {label: 'Si, activar', className: "btn-primary"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/ingresante/buscarHorario'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.reloadDinatable();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                            });
                        }
                    }
                });
            },
            asignarHorario(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea asignar horario al alumno?',
                    buttons: {
                        confirm: {label: 'Si, Asignar', className: "btn-primary"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/ingresante/asignarHorario'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.reloadDinatable();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                            });
                        }
                    }
                });
            },
            retirarHorario(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea retirar el horario?',
                    buttons: {
                        confirm: {label: 'Si, retirar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/ingresante/retirarHorario'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.reloadDinatable();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                            });
                        }
                    }
                });
            },
            suspenderMatricula(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea suspender la matrícula?',
                    buttons: {
                        confirm: {label: 'Si, suspender', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/ingresante/suspenderMatricula'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.reloadDinatable();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                            });
                        }
                    }
                });
            },
            activarMatricula(id) {

                var $vue = this;

                bootbox.confirm({
                    message: '¿Seguro que desea activar la matrícula?',
                    buttons: {
                        confirm: {label: 'Si, activar', className: "btn-primary"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/ingresante/activarMatricula'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        $vue.reloadDinatable();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                }
                            });
                        }
                    }
                });

            },
            cargarIngresantes(e) {
                var self = $(e.currentTarget);
                var vue = this
                self.btnDisabled();
                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/horariocachimbo/ingresante/cargarIngresantes'),
                    data: {id: vue.alumno.id},
                    success: function(response) {
                        if (response.success) {
                            notify(response.message, 'info');
                            vue.reloadDinatable();
                        } else {
                            notify(response.message, 'error');
                        }
                        self.btnEnable();
                    }, error: function() {
                        self.btnEnable();
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            },
        }
    });
});
