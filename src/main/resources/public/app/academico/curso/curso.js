new Vue({
    el: '#cursosVUE',
    data: {
        inicioURL: 'academico/curso',
        cursoURL: APP.url('academico/curso/list'),
        estadoClass: {ACT: 'label-success', CRE: 'label-default', INA: 'label-danger'},
        configEstado: {
            id: 'modalCambioEstado',
            header: true,
            title: 'Cambio de estado',
            okclass: 'btn-success',
            okbtn: 'Si, cambiar',
            showaccept: true,
            disabledBtns: false,
            message: "Nada",
            accion: "Nada",
            motivo: "",
            idCurso: 0
        }
    },
    mounted: function () {
    },
    methods: {
        urlNuevoCurso() {
            let $vue = this;
            location.href = APP.url($vue.inicioURL + '/nuevo') + $vue.getOrigenURL();
        },
        urlEditar(item) {
            let $vue = this;
            return APP.url($vue.inicioURL + '/' + item.id + '/editar') + $vue.getOrigenURL();
        },
        activar(item, accion) {
            let $vue = this;
            $vue.configEstado.accion = accion;
            $vue.configEstado.idCurso = item.id;
            $vue.configEstado.motivo = "";
            $vue.configEstado.disabledBtns = false;

            if (accion == "activar") {
                $vue.configEstado.title = "Activar Curso";
                $vue.configEstado.okbtn = "Si, activar";
                $vue.configEstado.okclass = "btn-success";
                $vue.configEstado.message = '¿Está seguro que desea activar el curso <b class="text-primary">' + item.codigo + ' ' + item.nombre + '</b>?';
            } else {
                $vue.configEstado.title = "Desactivar Curso";
                $vue.configEstado.okbtn = "Si, desactivar";
                $vue.configEstado.okclass = "btn-warning";
                $vue.configEstado.message = '¿Está seguro que desea desactivar el curso <b class="text-primary">' + item.codigo + ' ' + item.nombre + '</b>?';
            }
            $vue.$refs.modalEstado.open();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        saveEstado() {
            let $vue = this;
            let okBtn = $vue.configEstado.okbtn;
            let estado = $vue.configEstado.accion == 'activar' ? 'ACT' : 'INA';
            $vue.configEstado.disabledBtns = true;
            $vue.configEstado.okbtn = '<i class="fa fa-spinner fa-spin"></i> Procesando petición..';

            $.ajax({
                url: APP.url('academico/curso/cambiarEstadoCurso'),
                type: 'POST',
                async: true,
                data: {
                    id: $vue.configEstado.idCurso,
                    estado: estado,
                    motivoAnulacion: $vue.configEstado.motivo
                },
                success: function (response) {
                    $vue.configEstado.okbtn = okBtn;
                    if (response.success) {
                        $vue.$refs.modalEstado.close();
                        notify(response.message, "info");
                        $vue.$refs.raptorCursos.loadRemoteData();
                    } else {
                        $vue.configEstado.disabledBtns = false;
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    $vue.configEstado.okbtn = okBtn;
                    $vue.configEstado.disabledBtns = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
})