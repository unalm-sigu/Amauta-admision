new Vue({
    el: '#graduadosVUE',
    data: {
        rutaGraduados: APP.url(`${rutaModulo}/list`),
        seleccionado: -1,
        bgColorClass: ['text-primary', 'text-success', 'text-warning', 'text-dark'],
        textoResumen: {titulo: 'Titulados', bachiller: 'Bachilleres', maestria: 'Maestros', doctorado: 'Doctores'},
        resumen: [...Array(4).keys()]
    },
    mounted: function () {
        let $vue = this;
        $vue.loadResumen();
    },
    methods: {
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verTipoGrado(item, tipo) {
            let $vue = this;
            if ($vue.seleccionado === item) {
                $vue.seleccionado = -1;
                $vue.addFilterRaptorTable('tipo-grado');
                return;
            }
            $vue.addFilterRaptorTable('tipo-grado', tipo);
            $vue.seleccionado = item;
        },
        addFilterRaptorTable(name, value) {
            let $vue = this;
            $vue.$refs.raptorGraduados.querie.push({name: name, value: value});
            $vue.$refs.raptorGraduados.loadRemoteData();
        },
        loadResumen() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url(`${rutaModulo}/resumen`),
                success: function (response) {
                    if (response.success) {
                        $vue.resumen = Object.entries(response.data);
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        classEstado(status) {
            if (status.codigo === 'ACEP') {
                return "text-success";
            } else if (status.codigo === 'RCHZ') {
                return "text-danger";
            } else if (status.codigo === 'ANU') {
                return "text-warning";
            } else if (status.codigo === 'PEND') {
                return "text-primary";
            }
            return "";
        },
        anularTramite(item) {
            let $vue = this;

            bootbox.confirm({
                message: '¿Desea anular el grado académico del alumno?',
                callback: function (result) {
                    console.log(result)
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        $.ajax({
                            method: 'POST',
                            url: APP.url(`${rutaModulo}/anular`),
                            data: JSON.stringify(item),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {

                                    $vue.$refs.raptorGraduados.loadRemoteData();
                                    notify(response.message, "success");
                                } else {
                                    notify(response.message, "error");
                                }

                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                        MODAL.hideWait();
                    }

                }
            });

        },
        forzarSituacionEgresado(item) {
            let $vue = this;
            swal({
                title: "Seguro que desea cambiarle la situación académica ha egresado.",
                icon: "warning",
                buttons: ["Cancelar", "Aceptar"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    axios_.get(APP.url('academico/graduado/cambiarsituacionacademica/' + item.alumno.id)).
                            then(({data}) => {
                                notify(data, "info");
                            }, () => {});

                }
            });
        }
    }
});

