var DynatableRowTemplate = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {cicloAcademico: []};
    },
    methods: {
        eliminar: function(id) {
            $global.$emit("eliminar", id);
        },
        editar: function(id) {
            $global.$emit("editar", id);
        },
        cerrarCiclo: function(id) {
            $global.$emit("cerrarCiclo", id);
        },
        activarCiclo: function(id) {
            $global.$emit("activarCiclo", id);
        },
        anularCiclo: function(id) {
            $global.$emit("anularCiclo", id);
        },
    }
});

let  dynatable = null;

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function() {
        var vue = this;
        vue.createDynatable();
    },
    methods: {
        createDynatable: function() {
            var vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/cicloacademico/list'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function(e) {
                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    dynatableRowTemplate.cicloAcademico = records[i];
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
        ciclos: [{id: null}],
        cicloAcademico: {id: null},
        motivoCerrar: "",
        motivoAnular: "",
        addCicloAcademicoaModal: {
            id: 'modalAddCicloAcademico',
            header: true,
            title: 'Ciclo académico',
            okbtn: 'Guardar'
        },
        addCerrarCicloModal: {
            id: 'modalAddCerrarCiclo',
            header: true,
            title: 'Cerrar Ciclo académico',
            okbtn: 'Aceptar'
        },
        addAnularCicloModal: {
            id: 'modalAddAnularCiclo',
            header: true,
            title: 'Anular Ciclo académico',
            okbtn: 'Aceptar'
        },
    },
    created() {
        let vue = this;
    },
    mounted: function() {

        let vue = this;

        $('#modalidad').select2({minimumResultsForSearch: -1}).on("change.select2", function(e) {
            vue.changeModalidad(e.val);
        });

        $global.$on("eliminar", function(id) {
            vue.eliminar(id);
        });
        $global.$on("editar", function(id) {
            vue.editar(id);
        });
        $global.$on("cerrarCiclo", function(id) {
            vue.cerrarCiclo(id);
        });
        $global.$on("activarCiclo", function(id) {
            vue.activarCiclo(id);
        });
        $global.$on("anularCiclo", function(id) {
            vue.anularCiclo(id);
        });

    },
    methods: {
        changeModalidad: function(id) {
            dynatable.queries.remove("me.id");
            dynatable.queries.add("me.id", id);
            dynatable.process();
        },
        formClear: function() {
            $('#formCicloAcademico').parsley('destroy');
            $('[name="modalidadEstudio.id"]').select2({minimumResultsForSearch: -1});
            $('[name="numeroCiclo"]').select2({minimumResultsForSearch: -1});
            $(".numerico").numeric({negatice: false});
            $('[name="modalidadEstudio.id"]').select2('val', '');
            $('[name="numeroCiclo"]').select2('val', '');
        },
        nuevo: function() {
            var vue = this;
            vue.$refs.modalAddCicloAcademico.open();
            vue.cicloAcademico = {};
            vue.formClear();
        },
        editar: function(id) {
            var vue = this;
            vue.$refs.modalAddCicloAcademico.open();
            vue.cicloAcademico = {};
            vue.formClear();
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/update'),
                data: {id: id},
                success: function(response) {
                    if (response.success) {
                        vue.cicloAcademico = response.data;
                        $('[name="modalidadEstudio.id"]').select2('val', response.data.modalidadEstudio.id);
                        $('[name="numeroCiclo"]').select2('val', response.data.numeroCiclo);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveCicloAcademico: function() {
            var vue = this;
            var valid = $('#formCicloAcademico').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/save'),
                data: $('#formCicloAcademico').serialize(),
                success: function(response) {
                    if (response.success) {
                        vue.$refs.modalAddCicloAcademico.close();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function(id) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/delete'),
                            data: {id: id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        getRecord: function(id) {
            return dynatable.settings.dataset.records.find(item => item.id === id);
        },
        saveCerrarCiclo: function() {
            var vue = this;
            var valid = $('#formCerrarCiclo').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/cerrar'),
                data: $('#formCerrarCiclo').serialize(),
                success: function(response) {
                    if (response.success) {
                        vue.$refs.modalAddCerrarCiclo.close();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveAnularCiclo: function() {
            var vue = this;
            var valid = $('#formAnularCiclo').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/anular'),
                data: $('#formAnularCiclo').serialize(),
                success: function(response) {
                    if (response.success) {
                        vue.$refs.modalAddAnularCiclo.close();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cerrarCiclo: function(id) {
            var vue = this;
            vue.motivoCerrar = "";
            vue.cicloAcademico.id = id;
            vue.$refs.modalAddCerrarCiclo.open();
        },
        anularCiclo: function(id) {
            var vue = this;
            vue.motivoAnular = "";
            vue.cicloAcademico.id = id;
            vue.$refs.modalAddAnularCiclo.open();
        },
        activarCiclo: function(id) {
            bootbox.confirm({
                message: '¿Seguro que desea activar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, Activar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function(result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/activar'),
                            data: {id: id},
                            success: function(response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function() {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
    }
});

