new Vue({
    el: '#main',
    data: {
        ciclos: [{id: null}],
        cicloAcademico: {id: null},
        motivoAnular: "",
        margen: [],
        yearActivo: null,
        addCicloAcademicoaModal: {
            id: 'modalAddCicloAcademico',
            header: true,
            title: 'Ciclo académico',
            okbtn: 'Guardar'
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
    mounted: function () {

        let vue = this;

        $('#modalidad').select2({minimumResultsForSearch: -1}).on("change.select2", function (e) {
            vue.changeModalidad(e.val);
        });

        $global.$on("eliminar", function (id) {
            vue.eliminar(id);
        });
        $global.$on("editar", function (id) {
            vue.editar(id);
        });
        $global.$on("cerrarCiclo", function (id) {
            vue.cerrarCiclo(id);
        });
        $global.$on("activarCiclo", function (id) {
            vue.activarCiclo(id);
        });
        $global.$on("desactivarCiclo", function (id) {
            vue.desactivarCiclo(id);
        });
        $global.$on("pendienteCiclo", function (id) {
            vue.pendienteCiclo(id);
        });
        $global.$on("anularCiclo", function (id) {
            vue.anularCiclo(id);
        });
        $global.$on("configurarCiclo", function (id) {
            vue.configurarCiclo(id);
        });
        vue.margen = margen;
        vue.filtroInicial();

    },
    methods: {
        changeModalidad: function (id) {
            dynatable.queries.remove("modalidad");
            dynatable.queries.add("modalidad", id);
            dynatable.process();
        },
        formClear: function () {
            $('#formCicloAcademico').parsley('destroy');
            $('[name="modalidadEstudio.id"]').select2({minimumResultsForSearch: -1});
            $('[name="numeroCiclo"]').select2({minimumResultsForSearch: -1});
            $(".numerico").numeric({negatice: false});
            $('[name="modalidadEstudio.id"]').select2('val', '');
            $('[name="numeroCiclo"]').select2('val', '');
        },
        nuevo: function () {
            var vue = this;
            vue.$refs.modalAddCicloAcademico.open();
            vue.cicloAcademico = {};
            vue.formClear();
        },
        editar: function (id) {
            var vue = this;
            vue.$refs.modalAddCicloAcademico.open();
            vue.cicloAcademico = {};
            vue.formClear();
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/update'),
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        vue.cicloAcademico = response.data;
                        $('[name="modalidadEstudio.id"]').select2('val', response.data.modalidadEstudio.id);
                        $('[name="numeroCiclo"]').select2('val', response.data.numeroCiclo);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveCicloAcademico: function () {
            var vue = this;
            var valid = $('#formCicloAcademico').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/save'),
                data: $('#formCicloAcademico').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.modalAddCicloAcademico.close();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        eliminar: function (id) {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/delete'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        getRecord: function (id) {
            return dynatable.settings.dataset.records.find(item => item.id === id);
        },
        saveAnularCiclo: function () {
            var vue = this;
            var valid = $('#formAnularCiclo').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/anular'),
                data: $('#formAnularCiclo').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.modalAddAnularCiclo.close();
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        cerrarCiclo: function (id) {
            bootbox.confirm({
                message: '¿Seguro que desea cerrar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, Aceptar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/cerrar'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        anularCiclo: function (id) {
            var vue = this;
            vue.$refs.modalAddAnularCiclo.open();
            vue.motivoAnular = "";
            vue.cicloAcademico.id = id;
        },
        activarCiclo: function (id) {
            bootbox.confirm({
                message: '¿Seguro que desea activar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si, Activar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/activar'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        desactivarCiclo: function (id) {
            bootbox.confirm({
                message: '¿Seguro que desea desactivar el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si,Desactivar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/desactivar'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        pendienteCiclo: function (id) {
            bootbox.confirm({
                message: '¿Seguro que desea poner como pendiente el ciclo académico?',
                buttons: {
                    confirm: {label: 'Si,Aceptar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/pendiente'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        configurarCiclo: function (id) {
            bootbox.confirm({
                message: '¿Seguro que desea iniciar la configuración del ciclo académico?',
                buttons: {
                    confirm: {label: 'Si,Aceptar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/cicloacademico/configurar'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(MESSAGES.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        cambiarPeriodo: function (periodo) {
            var vue = this;
            dynatable.queries.remove("periodo");
            if (vue.yearActivo == periodo) {
                vue.yearActivo = null;
            } else {
                vue.yearActivo = periodo;
                dynatable.queries.add("periodo", vue.yearActivo);
            }
            dynatable.process();
        },
        filtroInicial: function () {
            var vue = this;
            var id = $('#modalidad').val();
            var periodo = margen[1];
            vue.yearActivo = periodo;
            dynatable.queries.remove("periodo");
            dynatable.queries.add("periodo", periodo);
            dynatable.queries.remove("modalidad");
            dynatable.queries.add("modalidad", id);
            dynatable.process();

        }
    }
});

