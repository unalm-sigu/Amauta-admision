Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramiteRetiroCiclo',
    data: {
        URL_RETIROS: APP.url("academico/tramiteretirociclo/list"),
        ciclos: JSON.parse(ciclosJson),
        rutaMatricula: rutaMatricula,
        idUsuario: idUsuario,
        matriculaResumen: {},
        tramiteRetiroCiclo: {},
        modalRetiroCiclo: {
            id: 'modalRetiroCiclo',
            header: true,
            title: 'Agregar Tramite Retiro Ciclo ',
            okbtn: "Guardar",
            showaccept: true
        },
        alumnos: []
    }, methods: {
        modal() {
            let $vue = this;
            $vue.tramiteRetiroCiclo = {};
            $vue.$refs.modalRetiroCiclo.open();
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/tramiteretirociclo/allAlumnoByNombre"),
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
        saveTramiteRetiroCiclo() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteretirociclo/save'),
                data: JSON.stringify($vue.tramiteRetiroCiclo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        $vue.$refs.modalRetiroCiclo.close();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        update(item, val) {
            let $vue = this;

            item.estado = val == 0 ? 'RCHZ' : 'ACEP'
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteretirociclo/update'),
                data: JSON.stringify(item),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                        if (item.estado == 'RCHZ') {
                            $vue.matriculaResumen = response.data;
                            $vue.updateEnMatricula();
                        }
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        },
        updateEnMatricula() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url($vue.rutaMatricula + "/matricula/deleteMatricula"),
                data: {idMatriculaResumen: $vue.matriculaResumen.id, idUsuario: $vue.idUsuario},
                success: function (response) {
                    if (response.success) {
//                        $vue.$refs.load.loadRemoteData();
//                        notify(response.message, "success");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                    MODAL.hideWait();
                }
            });
        }
    }
})