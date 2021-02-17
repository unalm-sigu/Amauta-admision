Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#tramiteTraslado',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteTraslado/list'),
        carreras: JSON.parse(carrerasJson),
        modalTraslado: {
            id: 'modalTraslado',
            header: true,
            title: 'Agregar Traslado ',
            okbtn: "Guardar",
            showaccept: true
        },
        traslado: {},
        alumnos: [],
        isLoading: false

    }, created: function () {

    }, mounted: function () {

    }, methods: {
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
        urlReporte(item) {
            let $vue = this;
            return APP.url('academico/tramiteacademico/tramiteTraslado/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.traslado = {};
            $vue.$refs.modalTraslado.open();
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
        saveTraslado() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/tramiteTraslado/save'),
                data: JSON.stringify($vue.traslado),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalTraslado.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalTraslado.close();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        labelColor(item) {
            switch (item) {
                case  "SOL":
                    "label label-success"
                    break;
                default :
                    "label label-primary"
                    break;
            }
        }
    }
})