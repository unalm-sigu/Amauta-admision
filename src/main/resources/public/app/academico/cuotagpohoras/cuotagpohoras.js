Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#cuotagpohorasVUE',
    data: {
        cuotagpohorasURL: APP.url('academico/cuotagpohoras/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Agregar Cuotas de Grupos',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        cuotagpohorasEdit: {},
        cuotagpohorasSelect: {},
        anexoTempo: {},
        anexos: [],
        grupos: [],
        cuotas: []
    },
    mounted() {
        $(".numerico").numeric({negative: false});
    },
    methods: {
        verGruposAnexo(item) {
            console.log(item.nombre);

            let $vue = this;
            $vue.confirmarModal.title = "Grupos";
            $vue.grupoTempo = {id: '', codigo: {}, letra: {}};
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/cuotagpohoras/allGrupos")
            }).then(response => {
                if (response.success) {
                    $vue.grupos = response.data;
                    $vue.cuotas = [];
                    for (var i = 0; i < $vue.grupos.length; i++) {
                        $vue.cuotas.push({grupoHoras: $vue.grupos[i], anexoBoletin: item, cuotas: 0});
                    }
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
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
        editar(item) {
            
        },

        verNuevoAnexo() {
            let $vue = this;
            $vue.confirmarModal.title = "Nuevo Anexo";
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
        }

    }

});