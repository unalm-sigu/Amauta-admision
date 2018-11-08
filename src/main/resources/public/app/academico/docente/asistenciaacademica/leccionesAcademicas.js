Vue.component("multiselect", window.VueMultiselect.default);

var app = new Vue({
    el: '#asistenciaAcademicaApp',
    data: {
        URL_LECCIONES: APP.url('academico/docente/asistenciaacademica/listLeccionesAcademicas'),
        seccion: null,
        reprogramacionModal: {
            id: 'modalReprogramacion',
            header: true,
            title: 'Reprogramación Clases',
            okbtn: 'Aceptar',
            modalsize: 'modal-lg'
        },
        reprogramacionAttr: {
            fechaInicioOrigen: null,
            fechaFinOrigen: null,
            disabledDaysArg: null,
            disabledDatesArg: null
        },
        leccionReprogramada: {
            aula: null,
            motivo: "",
            fechaOrigen: "",
            fechaReprogramada: "",
            horaInicio: "",
            horaFin: ""
        },
        aulasOptions: []
    }, created: function () {
        this.seccion = JSON.parse(seccionJson);
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        }, editarLeccion(temaLeccion, e) {
            e.preventDefault();
            location.href = APP.url('academico/docente/asistenciaacademica/' + temaLeccion.id + '/editar');
        }, saveReprogramacion() {
            let $vue = this;
            $vue.leccionReprogramada.seccion = $vue.seccion;

            $('#frmReprogramacion').find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });

            var form = $("[id='frmReprogramacion']");
            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                url: APP.url('academico/docente/asistenciaacademica/saveReprogramacion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.leccionReprogramada),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.tblLecciones.loadRemoteData();
                        $vue.$refs.modalReprogramacion.close();
                    } else {
                        notify(response.message, "error");
                        // $vue.$refs.tblMatriculasSeccion.loadRemoteData();
                    }
                    MODAL.hideWait();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        }, reprogramarClase() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/docente/asistenciaacademica/loadModalReprogramacion'),
                type: 'post',
                data: {
                    seccion: $vue.seccion.id
                },
                success: function (response) {
                    if (response.success) {

                        $vue.reprogramacionAttr.fechaInicioOrigen = response.data.fechaInicio;
                        $vue.reprogramacionAttr.fechaFinOrigen = response.data.fechaFin;
                        $vue.reprogramacionAttr.disabledDaysArg = response.data.disabledDaysArg;
                        $vue.reprogramacionAttr.disabledDatesArg = response.data.disabledDatesArg;

                        $vue.$refs.modalReprogramacion.open();
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        }, asyncFindAulas(nombre) {
            let $vue = this;
            console.dir(nombre);
            if (nombre != null && nombre != "") {
                $.ajax({
                    url: APP.url("academico/docente/asistenciaacademica/asyncFindAulas"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.aulasOptions = response.data;
                    if ($vue.aulasOptions == null) {
                        $vue.aulasOptions = [];
                    }
                })
            } else {
                $vue.aulasOptions = [];
            }
        }, seleccionarAula() {
            let $vue = this;
            if ($vue.aulaSeleccionada == null) {
                return;
            }
            console.dir($vue.aulaSeleccionada);
            /*
             $.ajax({
             method: 'POST',
             url: APP.url('academico/gposeccion/seleccionarAula'),
             data: {
             seccion: $vue.seccionModal.id,
             aula: $vue.tabAulas['especificas'].aulasEspecificaSel.id
             },
             success: function (response) {
             if (response.success) {
             $vue.tabAulas.aulaSel = response.data;
             $vue.selectAula($vue.tabAulas['especificas'].aulasEspecificaSel);
             } else {
             if (response.total > 0) {
             $vue.tabAulas['especificas'].errores = response.data;
             } else {
             $vue.tabAulas['especificas'].errores = [];
             }
             notify(response.message, "error");
             }
             }, error: function () {
             notify(MESSAGES.errorComunicacion, "error");
             }
             });
             */

        }
    }
})