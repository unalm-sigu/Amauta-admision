//
//$(function () {
//
//    $('#frmSubirEgresados').ajaxForm({
//        beforeSend: function () {
//            $('#progress .progress-bar').css('width', 0 + '%');
//        },
//        uploadProgress: function (e, position, total, percent) {
//            $('#progress .progress-bar').css('width', percent + '%');
//        },
//        success: function () {
//            $('#progress .progress-bar').css('width', 100 + '%');
//        },
//        complete: function (response) {
//            var json = response.responseJSON;
//            if (json.success) {
//                $("#cmbSubirEgresados").html('Carga finalizada');
//
//                alert("subio bien");
//            } else {
//                alert("subio mal");
//                $("#cmbSubirEgresados").html('Iniciar Carga');
//                $('#mensajeRespuesta').text(json.message).addClass("alert-danger").removeClass("alert-success").removeClass("hide");
//                $("#footerLoadAbonos").find("a").each(function (i, item) {
//                    $(item).removeAttr("disabled");
//                });
//            }
//        },
//        error: function (error) {
//            alert("error");
//            $('#mensajeRespuesta').text("Error de comunicacion con el servidor").addClass("alert-danger");
//            $("#footerLoadAbonos").find("a").each(function (i, item) {
//                $(item).removeAttr("disabled");
//            });
//        }
//    });
//
//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('academico/matriculable/list'),
//            perPageDefault: 15
//        },
//        writers: {
//            _rowWriter: ulWriter
//        },
//        table: {
//            bodyRowSelector: 'tbody tr'
//        }
//    }).data('dynatable');
//
//    function ulWriter(rowIndex, record, columns, cellWriter) {
//
//        var colorEstado = {ACT: 'success', FAPR: 'warning', FRES: 'warning'};
//        record.colorEstado = colorEstado[record.estado];
//        if (record.colorEstado == undefined) {
//            record.colorEstado = 'danger';
//        }
//
//        var html = $.templates("#matriculableTemplate").render(record);
//        return html;
//    }
//
//    Matriculable = {
//        modalMatriculable: $("#modalMatriculable"),
//        divElegido: null,
//        verModalidades: function ($this, e) {
//            e.preventDefault();
//            var div = $this.closest("div");
//            var classColor = 'bg-light';
//            var tieneBgColor = div.hasClass(classColor);
//            dynatable.queries.remove("moe.codigo");
//            if (Matriculable.divElegido != null) {
//                Matriculable.divElegido.removeClass(classColor);
//                Matriculable.divElegido = null;
//            }
//
//            if (!tieneBgColor) {
//                div.addClass(classColor);
//                Matriculable.divElegido = div;
//                var estado = $this.attr("rel");
//                dynatable.queries.add("moe.codigo", estado);
//            }
//            dynatable.process();
//        },
//        viewModal: function (e, $this) {
//            e.preventDefault();
//            Matriculable.modalMatriculable.modal("show");
//            $('[name="motivo"]').val("");
//            $('[name="id"]').val($this.attr("rel"));
//        },
//        nuevoModal: function () {
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/nuevo'),
//                success: function (response) {
//                    $('#matriculableModal').html(response);
//                    $('#viewModal').modal('show');
//                    dynatable.process();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        findCiclo: function (e, $this) {
//            e.preventDefault();
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/ciclo'),
//                success: function (response) {
//                    console.log(Matriculable);
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                    MODAL.hideWait();
//                }
//            });
//        },
//        generarMatriculables: function (e, $this) {
//            e.preventDefault();
//            MODAL.showWait("Espere un momento por favor");
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/generar'),
//                success: function (response) {
//                    MODAL.hideWait();
//                    dynatable.process();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                    MODAL.hideWait();
//                }
//            });
//        },
//        generarPrioridad: function (e, $this) {
//            e.preventDefault();
//            MODAL.showWait("Espere un momento por favor");
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/generarPrioridad'),
//                success: function (response) {
//                    MODAL.hideWait();
//                    dynatable.process();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                    MODAL.hideWait();
//                }
//            });
//        },
//        eliminarPrioridad: function (e, $this) {
//            e.preventDefault();
//            MODAL.showWait("Espere un momento por favor");
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/eliminarPrioridad'),
//                success: function (response) {
//                    MODAL.hideWait();
//                    dynatable.process();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                    MODAL.hideWait();
//                }
//            });
//        },
//        modalAsignarTurno: function (e, $this) {
//            //  e.preventDefault();
//            MODAL.init("sm");
//            MODAL.title("Tipo de Matricula");
//            MODAL.show();
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/modalAsignarTurno'),
//                success: function (response) {
//                    MODAL.body(response);
//                    dynatable.process();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        modalAsignarTurno: function (e, $this) {
//            //  e.preventDefault();
//            MODAL.init("sm");
//            MODAL.title("Tipo de Matricula");
//            MODAL.show();
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/modalAsignarTurno'),
//                success: function (response) {
//                    MODAL.body(response);
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        modalSubirEgresado: function (e, $this) {
//            //  e.preventDefault();
//            MODAL.init("lg");
//            MODAL.title("Subir Egresados");
//            MODAL.show();
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/modalSubirEgresados'),
//                success: function (response) {
//                    MODAL.buttons('<a class="btn btn-success" id="cmbSubirEgresados">Iniciar Carga</a>');
//                    MODAL.body(response);
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        procesarTipoMatricula: function (e, $this) {
//            e.preventDefault();
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/matriculable/procesarTipoMatricula'),
//                data: {
//                    confTurnoAtencion: $this.attr("alt")
//                },
//                success: function (response) {
//                    MODAL.hide();
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        verCursos: function ($this) {
//            var rel = $this.attr("rel");
//            $.ajax({
//                method: 'POST',
//                url: APP.url('academico/alumno/' + rel + '/matricula/origen/matriculable'),
//                success: function (response) {
//                    $('#cursosModal').html(response);
//                    $('#viewModal').modal('show');
//                },
//                error: function () {
//                    notify(MESSAGES.errorComunicacion, "error");
//                }
//            });
//        },
//        initLoadEgresados() {
//            if (!$('#frmSubirEgresados').parsley().validate()) {
//                return;
//            }
//
//            $("#footerLoadAbonos").find("a").each(function (i, item) {
//                $(item).attr("disabled", "disabled");
//            });
//            $('#mensajeRespuesta').addClass("hide");
//            $("#cmbSubirEgresados").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Cargando datos');
//            $('#frmSubirEgresados').submit();
//        }
//    };
//
//    $("body").delegate(".ver-modalidades", "click", function (e) {
//        Matriculable.verModalidades($(this), e);
//    });
//    $("body").delegate(".ver-cursos", "click", function () {
//        Matriculable.verCursos($(this));
//    });
//    $("body").delegate("#agregarMatriculable", "click", function (e) {
//        Matriculable.nuevoModal(e, $(this));
//    });
//    $("body").delegate("#generarMatriculables", "click", function (e) {
//        Matriculable.generarMatriculables(e, $(this));
//    });
//
//    $("body").delegate("#generarPrioridad", "click", function (e) {
//        Matriculable.generarPrioridad(e, $(this));
//    });
//
//    $("body").delegate("#asignarTurnos", "click", function (e) {
//        Matriculable.modalAsignarTurno(e, $(this));
//    });
//
//    $("body").delegate("#subirEgresado", "click", function (e) {
//        Matriculable.modalSubirEgresado(e, $(this));
//    });
//
//    $("body").delegate(".procesar-tipo-matricula", "click", function (e) {
//        Matriculable.procesarTipoMatricula(e, $(this));
//    });
//
//    $("body").delegate(".procesar-tipo-matricula", "click", function (e) {
//        Matriculable.procesarTipoMatricula(e, $(this));
//    });
//
//    $("body").delegate("#cmbSubirEgresados", "click", function () {
//        Matriculable.initLoadEgresados();
//    });
//
//});
Vue.component("multiselect", window.VueMultiselect.default)
console.log(JSON.parse(cicloJson));
console.log(JSON.parse(resumenJson));
new Vue({
    el: '#matriculableVUE',
    data: {
        matriculaURL: APP.url('academico/matriculable/list'),
        ciclo: JSON.parse(cicloJson),
        resumen: JSON.parse(resumenJson),
        configTurno: [],
        alumno: {},
        alumnos: [],
        modalTurno: {
            id: 'modalTurno',
            header: true,
            title: 'Asignar Turno',
            okbtn: "Guardar",
            showaccept: false
        },
        modalMatriculable: {
            id: 'modalMatriculable',
            header: true,
            title: 'Agregar Matriculable',
            okbtn: "Guardar",
            showaccept: true
        },

    },
    mounted: function () {

    },
    methods: {
        style(item) {
            var colorEstado = {ACT: 'success', FAPR: 'warning', FRES: 'warning'};
            var res = colorEstado[item];
            if (res == undefined) {
                return "label label-danger";
            }
            return "label label-" + res;
        },
        customConfig( {tipoEnum, eventoCicloAcademico}) {
            if (tipoEnum != null) {
                return tipoEnum.value - eventoCicloAcademico.eventoAcademico.nombre;
        }
        },
        modal() {
            let $vue = this;
            $vue.alumno = {};
            $vue.$refs.modalMatriculable.open();
        },
        generarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        eliminarPrioridad() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/eliminarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findCiclo() {
            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/ciclo'),
                success: function (response) {
                    $vue.ciclo = response.data;
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        findConfiguraciones() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/configuracionesTurno'),
                success: function (response) {
                    if (response.data.length == 0) {
                        notify("No hay configuración de turnos", "error");
                    } else {
                        $vue.configTurno = response.data;
                        $vue.$refs.modalTurno.open();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        procesarTipoMatricula(item) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/procesarTipoMatricula'),
                data: {
                    confTurnoAtencion: item.id
                },
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.modalTurno.close();
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        finalizarPrioridad(item) {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/finalizarPrioridad'),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        generarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/generar'),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        limpiarMatriculables() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/limpiarMatriculable'),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        finalizarMatriculable() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/finalizarMatriculable'),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = response.data;
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        saveMatriculable() {
            let $vue = this;
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/saveMatriculable'),
                contentType: "application/json",
                data: JSON.stringify($vue.alumno),
                success: function (response) {
                    if (response.success) {
                        $vue.findCiclo();
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalMatriculable.close();
                        MODAL.hideWait();
                        notify(response.message, "success");
                    }else{
                        MODAL.hideWait();
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/matriculable/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        }
    }
});


