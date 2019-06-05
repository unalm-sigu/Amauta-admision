Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cuotagpohorasVUE',
    data: {
        cuotagpohorasURL: APP.url('academico/cuotagpohoras/list'),
        pagination: {'total-items': 0, 'items-per-page': 100, 'max-size': 3, 'boundary-link-numbers': true},
        confirmarModal: VUE_MODAL.structFormAjax({
            id: 'modalConfirmar',
            header: true,
            title: 'Configurar Cuotas de Anexo',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar'
        }),
        anexoTempo: {},
        anexoGrid: {},
        anexos: [],
        grupos: [],
        cuotas: [],
        cuotasBD: [],
        departamentoAcademico: null,
        esInicio: false
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.loadGrupos();
        $vue.esInicio = true;

        let anx = $vue.$refs.raptorCuotaGpoHoras.getParameterByName('queries[anexo-cuotas]');
        anx = (anx == null) ? '' : anx;
        if (anx == '') {
            $vue.esInicio = false;
            $vue.$refs.raptorCuotaGpoHoras.repreload();
        }

        $vue.loadAnexos();

    },
    methods: {
        loadInfoAnexoInicio() {
            let $vue = this;
            if (!$vue.esInicio) {
                return;
            }

            let anx = $vue.$refs.raptorCuotaGpoHoras.getParameterByName('queries[anexo-cuotas]');
            anx = (anx == null) ? '' : anx;
            if (anx != '') {
                for (var i = 0; i < $vue.anexos.length; i++) {
                    if (anx == $vue.anexos[i].id) {
                        $vue.esInicio = false;
                        $vue.anexoGrid = $vue.anexos[i];
                        $vue.changeAnexoMain();
                    }

                }
            }
            $vue.esInicio = false;

        },
        changeAnexoMain() {
            let $vue = this;
            $vue.$refs.raptorCuotaGpoHoras.querie = [];
            $vue.$refs.raptorCuotaGpoHoras.ajaxdata = {anexo: $vue.anexoGrid.id};
            $vue.$refs.raptorCuotaGpoHoras.loadRemoteData();
            $vue.$refs.raptorCuotaGpoHoras.changeUrl('queries[anexo-cuotas]', $vue.anexoGrid.id);
        },
        verCuotasByAnexo(item) {
            console.log(item.Id);

            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/" + item.id + "/allCuotasByAnexo")
            }).then(response => {
                if (response.success) {
                    $vue.cuotasBD = response.data;
                    $vue.cuotas = [];
                    for (var i = 0; i < $vue.grupos.length; i++) {
                        let cuo = $vue.getCuotaGrupo($vue.grupos[i], $vue.cuotasBD);
                        if (cuo == null) {
                            $vue.cuotas.push({grupoHoras: $vue.grupos[i], anexoBoletin: item, cuotas: 0});
                        } else {
                            $vue.cuotas.push(cuo);
                        }

                    }
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        getCuotaGrupo(gpo, cuotas) {
            for (var i = 0; i < cuotas.length; i++) {
                if (gpo.codigo == cuotas[i].grupoHoras.codigo) {
                    return cuotas[i];
                }
            }
            return null;
        },
        verGuardar() {
            var form = $("#formGruposCuotas");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea guarda esta cuota?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        setTimeout(function () {
                            $vue.guardar();
                        }, 200);
                    }
                }
            });
        },
        guardar() {
            let $vue = this;
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/save"),
                data: JSON.stringify($vue.cuotas)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalConfirmar.close();
                    $vue.$refs.raptorCuotaGpoHoras.loadRemoteData();
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verNuevoAnexo() {
            let $vue = this;
            $vue.cuotasBD = [];
            $vue.cuotas = [];
            $vue.confirmarModal.title = "Configurar Cuotas por Anexo";
            $vue.anexoTempo = {id: '', departamentoAcademico: {}, anexoSuperior: {}};
            $vue.loadAnexos();
            $vue.$refs.modalConfirmar.open();
        },
        loadAnexos() {
            let $vue = this;
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/allAnexos")
            }).then(response => {
                if (response.success) {
                    $vue.anexos = response.data;
                    $vue.loadInfoAnexoInicio();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        loadGrupos() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/allGrupos")
            }).then(response => {
                if (response.success) {
                    $vue.grupos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        loadDepartamentos() {
            let $vue = this;
            $vue.departamentoTempo = {id: '', facultad: {}};
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("general/lejaniadepartamento/allDepartamentos")
            }).then(response => {
                if (response.success) {
                    $vue.departamentos = response.data;
                    $vue.$refs.modalConfirmar.open();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        editarGpoSecciones(item) {
            let $vue = this;
            let first = item.idsGposSecciones.split(",")[0];
            let lista = Base64.encode(item.idsGposSecciones);
            location.href = APP.url("academico/gposeccion/" + first + "/editar") + $vue.getOrigenURL() + "&ids=" + lista;
        },
        getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        },
    }

});