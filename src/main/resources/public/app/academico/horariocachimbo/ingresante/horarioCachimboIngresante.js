Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#main',
    data: {
        alumnosURL: APP.url(`${rutaModulo}/list`),
        avance: {
            porcentaje: 0,
            mensaje: "",
            estado: "LIBRE",
            procesando: false,
            errores: []
        },
        horario: {},
        alumno: null,
        cursos: [],
        alumnos: [],
        horarios: [],
        addAlumnoModal: {
            id: 'modalAddAlumno',
            header: true,
            title: 'Agregar Alumno',
            okbtn: 'Agregar Alumno'
        },
        ingCantidad: [
            {idgen: 1, estado: 'PEND', nombre: 'Pendiente', cantidad: 0},
            {idgen: 1, estado: 'SUSP', nombre: 'Suspendido', cantidad: 0},
            {idgen: 1, estado: 'CHOR', nombre: 'Con Horario', cantidad: 0},
            {idgen: 1, estado: 'MATR', nombre: 'Matriculado', cantidad: 0}
        ],
        verHorarioModal: {
            id: 'modalVerHorario',
            header: true,
            title: 'Horario',
            modalsize: 'modal-lg',
            cancelbtn: 'Aceptar',
            showaccept: false
        },
        modalErrores: {
            id: 'modalErrores',
            header: true,
            title: 'Errores',
            modalsize: 'modal-md',
            cancelbtn: 'Cerrar',
            showaccept: false
        },
        verCursoModal: {
            id: 'modalVerCurso',
            header: true,
            title: 'Cursos del horario',
            modalsize: 'modal-lg',
            cancelbtn: 'Aceptar',
            showaccept: false
        },
        errores: [],
        isLoading: false
    },
    created() {
        let $vue = this;
    },
    mounted: function () {
        let vue = this;
        vue.callIngresanteCantidad();
        vue.verAvanceMatricula();
    },
    methods: {
        generarPlan() {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea generar plan curricular?',
                buttons: {
                    confirm: {label: 'Si, generar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`academico/planCurricular/asignacionMasivaIngresantes`),
                            success: function (response) {
                                if (response.success) {
                                    vue.reloadDinatable();
                                    notify(response.message, 'success');
                                    MODAL.hideWait();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }});
        },
        styleActividad(item) {
            var total = item.totalActividades - 2;

            if (item.actividadesEjecutadas < total) {
                return " bgr-danger";
            } else {
                return " bgr-success";
            }
        },
        verErrores(item) {
            var vue = this;
            var cadena = item.split("<br/>");
            vue.errores = cadena;
            this.$refs.modalErrores.open();

        },
        countErrores(item) {
            var cadena = item.split("<br/>");
            return cadena.length;
        },
        revisarActividad() {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea revisar la actividad de los ingresantes?',
                buttons: {
                    confirm: {label: 'Si, revisar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/revisarActividad`),
                            success: function (response) {
                                if (response.success) {
                                    vue.reloadDinatable();
                                    notify(response.message, 'success');
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                        MODAL.hideWait();
                    }
                }});
        },
        nuevo() {
            var vue = this;
            this.$refs.modalAddAlumno.open();
            vue.alumno = [];
        },
        customLabel( { codigoMatricula, nombre }) {
            return `${codigoMatricula} – ${nombre}`
        },
        asyncFind(item) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/searchalumno`),
                data: {nombre: item},
                success: function (response) {
                    if (response.success) {
                        vue.alumnos = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        clearAlumno(e) {
            var vue = this;
            vue.alumno = [];
        },
        createAlumno() {
            var vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/addalumno`),
                data: {id: vue.alumno.id},
                success: function (response) {
                    if (response.success) {
                        vue.$refs.modalAddAlumno.close();
                        vue.reloadDinatable();
                        notify(response.message, 'success');
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        reloadDinatable() {
            var vue = this;
            //dynatable.process();
            vue.callIngresanteCantidad();
        },
        buscarHorario(id) {
            var $vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea buscar horario?',
                buttons: {
                    confirm: {label: 'Si, activar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/buscarhorario`),
                            data: {id: id},
                            success: function (response) {
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
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/asignarhorario`),
                            data: {id: id},
                            success: function (response) {
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
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/retirarhorario`),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    $vue.reloadDinatable();
                                    $vue.$refs.alumnosRaptor.loadRemoteData();
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
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/suspendermatricula`),
                            data: {id: id},
                            success: function (response) {
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
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/activarmatricula`),
                            data: {id: id},
                            success: function (response) {
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
                url: APP.url(`${rutaModulo}/cargaringresantes`),
                data: {id: vue.alumno.id},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        vue.reloadDinatable();
                    } else {
                        notify(response.message, 'error');
                    }
                    self.btnEnable();
                }, error: function () {
                    self.btnEnable();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        eliminarHorarios() {
            var vue = this;
            bootbox.confirm({
                message: '¿Seguro que desea eliminar los horarios?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/eliminarhorarios`),
//                            data: {id: vue.alumno.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    vue.reloadDinatable();
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });

                    }
                }
            });
        },
        callIngresanteCantidad() {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/ingresantecantidad`),
                async: false,
                success: function (response) {
                    if (response.success) {
                        vue.ingCantidad = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

        },
        filtrarIngresante(e) {
            var vue = this;
            var self = $(e.currentTarget);
            var carrera = self.attr('rel');

            e.preventDefault();
            var div = self.closest("div");
            var classColor = 'bg-light';
            var tieneBgColor = div.hasClass(classColor);
            vue.$refs.alumnosRaptor.querie = [];

            if (vue.divElegido != null) {
                vue.divElegido.removeClass(classColor);
                vue.divElegido = null;
            }

            if (!tieneBgColor) {
                div.addClass(classColor);
                vue.divElegido = div;
                if (carrera == 'ERROR') {
                    vue.$refs.alumnosRaptor.querie.push({name: 'alu.errores', value: carrera});
                } else if (carrera == 'FAL_ACT') {

                    vue.$refs.alumnosRaptor.querie.push({name: 'alu.actividad', value: carrera});
                } else {

                    vue.$refs.alumnosRaptor.querie.push({name: 'alu.estado', value: carrera});
                }
            }
            vue.$refs.alumnosRaptor.loadRemoteData();

        },
        verHorario(id) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url("academico/horariocachimbo/generar/verhorario"),
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        vue.horarios = response.data;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            this.$refs.modalVerHorario.open();
        },
        verCurso(id) {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url("academico/horariocachimbo/generar/vercurso"),
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        var cursos = response.data;
                        for (var i = 0, max = cursos.length; i < max; i++) {
                            cursos[i].verCurso = (cursos[i].loop == 0);
                        }

                        vue.cursos = cursos;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            this.$refs.modalVerCurso.open();
        },
        deleteAlumno(id) {
            var $vue = this;

            bootbox.confirm({
                message: '¿Seguro que desea eliminar el registro?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-primary"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/delete`),
                            data: {id: id},
                            success: function (response) {
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
        matricularIngresantes() {
            var $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/matricular`),
                success: function (response) {
                    $vue.verAvanceMatricula();
                    if (response.success) {
                        $vue.reloadDinatable();
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verAvanceMatricula() {
            var $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/getAvanceMatricula`),
                success: function (response) {
                    $vue.avance = response.data;
                    $vue.reloadDinatable();
                    if (response.success) {
                        setTimeout(function () {
                            $vue.verAvanceMatricula();
                        }, 1000);
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});


