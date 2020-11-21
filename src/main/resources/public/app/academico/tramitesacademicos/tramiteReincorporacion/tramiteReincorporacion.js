Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#retiroExcepcional',
    data: {
        URL_TRAMITES: APP.url('academico/tramiteacademico/tramiteReincorporacion/list'),
        ciclos: JSON.parse(ciclosJson),
        modalResolucion: {
            id: 'modalResolucion',
            header: true,
            title: 'Agregar Reincorporación ',
            okbtn: "Guardar",
            showaccept: true
        },
        reincorporacion: {},
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
            return APP.url('academico/tramiteacademico/tramiteReincorporacion/' + item.tramite.id + '/reporte');
        },
        nuevo() {
            let $vue = this;
            $vue.reincorporacion = {};
            $vue.$refs.modalResolucion.open();
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
        saveRetiro() {
            let $vue = this;
            if (!$("#form").parsley().validate()) {
                return;
            }
            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/tramiteReincorporacion/save'),
                data: JSON.stringify($vue.reincorporacion),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.loadRemoteData();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalResolucion.close();
                    MODAL.hideWait();
                },
                error: function () {
                    $vue.$refs.modalResolucion.close();
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