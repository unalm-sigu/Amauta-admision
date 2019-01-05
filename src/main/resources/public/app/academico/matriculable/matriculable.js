Vue.component("multiselect", window.VueMultiselect.default);

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
        modalInhabilitarMatriculable: {
            id: 'modalInhabilitarMatriculable',
            header: true,
            title: 'Inhabilitar Matriculable',
            okbtn: "Aceptar",
            showaccept: true
        },
        matriculableSelected: {}

    },
    mounted: function () {

    },
    methods: {
        style(item) {
            var colorEstado = {MAT: 'success', PMAT: 'warning', NMAT: 'default'};
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
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.alumno.id + '/infoacademico') + $vue.getOrigenURL();
        },
        inhabilitarModal(item) {
            let $vue = this;
            $vue.matriculableSelected = {};
            $vue.matriculableSelected = item;
            $vue.$refs.modalInhabilitarMatriculable.open();
        },
        inhabilitar() {
            let $vue = this;
            if (!$("#formInhabilitar").parsley().validate()) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/matriculable/inhabilitar'),
                data: JSON.stringify($vue.matriculableSelected),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalInhabilitarMatriculable.close();
                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
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
//            $('#formMatriculable').parsley().destroy();
            if (!$("#formMatriculable").parsley().validate()) {
                MODAL.hideWait();
                return;
            }
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
                    } else {
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
        },
        urlMatricula(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.alumno.id + '/gomatricula') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        }
    }
});


