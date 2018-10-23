Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cuotagpohorasVUE',
    data: {
        cuotagpohorasURL: APP.url('academico/cuotagpohoras/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Configurar Cuotas de Anexo',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },        
        anexoTempo: {},
        anexos: [],
        grupos: [],
        cuotas: [],
        cuotasBD: [],
    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.loadGrupos();
    },
    methods: {
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
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/allAnexos")
            }).then(response => {
                if (response.success) {
                    $vue.anexos = response.data;
                    $vue.$refs.modalConfirmar.open();
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
        }
    }

});