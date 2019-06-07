Vue.component("cancelar-seccion-component", {
    template: "#cancelarSeccionComp",
    props: {
        seccion: {type: Object, default: {}, required: false}
    }, data: function () {
        return {
            matriculasSeccion: [],
            configConfirmAction: VUE_MODAL.structConfirm({})
        }
    },
    mounted: function () {
        let $vue = this;
        $vue.loadComponent();
    },
    methods: {
        loadComponent() {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/gposeccion/loadCancelarSeccionComp'),
                type: 'POST',
                data: {seccion: $vue.seccion.id},
                success(response) {
                    if (response.success) {
                        $vue.matriculasSeccion = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        verTipoCarrera(item) {
            return (item.carrera.tipo == "MAE" || item.carrera.tipo == "DOC");
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        verFacultad(item) {
            return (item.modalidadEstudio.codigo == "PRE" && item.carrera.codigo != item.carrera.facultad.codigo);
        },
        saveCancelación() {
            console.log("save");
        },
        verCancelarSeccion() {
            let $vue = this;
            $vue.seccionWorking = Object.assign({}, $vue.seccion);

            let alus = $vue.seccionWorking.matriculados == 1
                    ? "el alumno matriculado será retirado"
                    : ("los " + $vue.seccionWorking.matriculados + " alumnos matriculados serán retirados");

            $vue.configConfirmAction = VUE_MODAL.structConfirm({
                message: "Al cancelar esta sección, " + alus + ".<br/><br/>¿Desea continuar?",
                okbtn: "Si, cancelar",
                okclass: "btn-danger",
                okbtnprocessing: '<i class="fa fa-spinner fa-pulse fa-fw"></i> Cancelando...',
                okaction: $vue.cancelarSeccion
            });

            $vue.$refs.modalConfirmAction.open();
        },
        cancelarSeccion() {
            let $vue = this;
            $global.$emit('cancelarSeccion', $vue.seccion);
        }
    }
});