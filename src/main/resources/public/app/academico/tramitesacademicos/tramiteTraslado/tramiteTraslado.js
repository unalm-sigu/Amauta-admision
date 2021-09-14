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
            return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
        },
        urlReporte(item) {
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
                },
                error: function () {
                    $vue.$refs.modalTraslado.close();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        labelColor(item) {
            switch (item) {
                case  'SOL':
                    return "label label-default"
                    break;
                case  'ANU':
                    return "label label-danger"
                    break;
                default :
                    return "label label-primary"
                    break;
            }
        },
        anularTarmite(item) {
            let $vue = this;
            swal({
                title: "Seguro que desea anular el registro",
                icon: "warning",
                buttons: ["Cancelar", "Anular"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    _axios.get(APP.url('academico/tramiteacademico/tramiteTraslado/anular/' + item.id)).
                            then(({data}) => {
                                notify(data, 'info');
                                $vue.$refs.load.loadRemoteData();
                            },()=>{});
                }
            });
        }
    }
})