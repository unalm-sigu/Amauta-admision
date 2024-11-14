Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#tipoevaluacionVUE',
    data: {
        URL: APP.url('tipoevaluacion/list'),
        modalNuevo: VUE_MODAL.structFormAjax({
            id: 'modalNuevo',
            header: true,
            title:'Nuevo Tipo de Evaluacion',
            okbtn: 'Guardar'
        }),
        modalEditar: VUE_MODAL.structFormAjax({
            id: 'modalEditar',
            header: true,
            title: 'Editar Tipo Evaluacion',
            okbtn: "Guardar",
            showaccept: true
        }),
        tiposEv: {},
        tiposEditar:{},
        maxIdFirstPage: null,
    },
    methods: {
        openModal(){
            let $vue = this;
            $vue.tiposEv = {};
            $vue.$refs.modalNuevo.open();
        },
        save() {
            let $vue = this;
            var form = $("#formNuevo");
            if (!form.parsley().validate()) {
                return;
            }
            // console.dir($vue);
            // return;

            MODAL.showWait("Espere un momento por favor");
            $.ajax({
                method: 'POST',
                url: APP.url(`tipoevaluacion/save`),
                data: JSON.stringify($vue.tiposEv),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {

                        //this.parents().becaLoad.loadRemoteData();
                        // swal({
                        //     title: 'Guardado satisfactoriamente',
                        //     icon: 'success',
                        //     buttons: {
                        //         ok: 'Aceptar'
                        //     }
                        // }).then((value) => {
                        //     if (value) {
                        //         location.reload();
                        //     }
                        // });
                        location.reload();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.$refs.modalNuevo.close();
                    MODAL.hideWait();

                },
                error: function () {
                    $vue.$refs.modalNuevo.close();
                    notify(Messages.errorComunicacion, "error");
                }

            });
        },
        openEditar(item){
            let $vue = this;
            $vue.tiposEditar = JSON.parse(JSON.stringify(item));
            $vue.$refs.modalEditar.open();
        },
        editar(){
            let $vue = this;
            // if (!$($vue.$refs.formEditar).parsley().validate()) {
            //     return;
            // }
            axios.post(APP.url('tipoevaluacion/update'), $vue.tiposEditar)
                .then(({data}) => {
                    notify(data.message, "info");
                    $vue.$refs.modalEditar.close();
                    $vue.$refs.raptorTiposEvaluacion.loadRemoteData();
                }, () => {
                    notify(data.message, "error");
                    $vue.$refs.modalEditar.close();
                });
        },
        getLastItemId(data) {
            if (data.length > 0) {
                // Solo calcula el maxIdFirstPage una vez
                this.maxIdFirstPage = Math.max(...data.map(item => item.id));
            }
            return this.maxIdFirstPage;

        },

    },
    mounted(){
        // let $vue = this;
        // this.getLastItemId(this.props.data);
    },
})