Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitebachiller/list'),
        modalTramBachiller: {
            id: 'modalTramBachiller',
            header: true,
            title: 'Agregar Tramite Bachiller ',
            okbtn: "Guardar",
            showaccept: true
        },
        tramiteBachiller: {},
        alumnos: [],
        isLoading: false
    }, created: function () {

    }, mounted: function () {

    }, methods: {
        nuevo() {
            let $vue = this;
            $vue.tramiteBachiller = {};
            $vue.$refs.modalTramBachiller.open();
        },
        saveTramiteBachiller() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/tramitebachiller/save'),
                data: JSON.stringify($vue.tramiteBachiller),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalTramBachiller.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalTramBachiller.close();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/tramitecondicional/allAlumnoByNombre"),
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
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        urlAcademico(item) {
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporteBachiller(item) {

            return APP.url('academico/tramiteacademico/tramitebachiller/' + item.tramite.id + '/reporte');
        },
        anular(item) {
            let $vue = this;
            bootbox.confirm({
                message: '¿Desea anular el tramite bachiller del alumno?',
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url("academico/tramiteacademico/tramitebachiller/anular"),
                            contentType: "application/json",
                            method: 'POST',
                            data: JSON.stringify(item)
                        }).then(response => {
                            if (response.success) {
                                $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                                notify(response.message, "success");
                            } else {
                                notify(response.message, "error");
                            }
                            MODAL.hideWait();
                        })
                    }
                }
            });
        }
    }
})