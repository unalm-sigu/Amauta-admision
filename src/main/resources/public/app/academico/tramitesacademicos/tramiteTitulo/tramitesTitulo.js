Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramitesAcademicos',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramitetitulo/list'),
        modalTramTitulo: {
            id: 'modalTramTitulo',
            header: true,
            title: 'Agregar Tramite Título ',
            okbtn: "Guardar",
            showaccept: true
        },
        tramiteTitulo: {},
        alumnos: [],
        isLoading: false
    }, created: function () {

    }, mounted: function () {

    }, methods: {
        nuevo() {
            let $vue = this;
            $vue.tramiteTitulo = {};
            $vue.$refs.modalTramTitulo.open();
        },
        saveTramiteTitulo() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/tramitetitulo/save'),
                data: JSON.stringify($vue.tramiteTitulo),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.tblTramitesAcademicos.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalTramTitulo.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalTramTitulo.close();
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
            let $vue = this;
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        urlReporteBachiller(item) {

            axios_blob.get(APP.url('academico/tramiteacademico/tramitetitulo/' + item.tramite.id + '/reporte'))
                    .then(response => {
                        UTIL_BLOB.save(response);
                    }, (error) => {
                        notify(error.response.data.message, 'error')
                    });

        },
        anular(item) {
            let $vue = this;
            bootbox.confirm({
                message: '¿Desea anular el tramite titulo del alumno?',
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            url: APP.url("academico/tramiteacademico/tramitetitulo/anular"),
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