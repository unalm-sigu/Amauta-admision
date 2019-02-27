
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#resultadosVUE',
    data: {
        turnoSelected: {},
        ingresantesURL: APP.url('ingresante/resultadoslab/list'),
        tipoSangreList: [],
        rhList: [],
        itemSelected: {laboratorio:''},
        newObservacionModal: {
            id: 'modalObservacion',
            header: true,
            title: 'Observaciones',
            okbtn: 'Aceptar',
            showaccept: true
        },

    },
    mounted: function () {
        let $vue = this;
        $(".decimal").numeric({negative: false});

        $vue.$refs.raptorRL.afterProcess = () => {
            $(".decimal").numeric({negative: false});
        };

        $vue.loadTipoSangre();
        $vue.loadFactorRh();
        console.log(" $vue.$refs.raptorRL", $vue.$refs.raptorRL._props);
    },
    methods: {

        guardarNumeroMuestra(item) {
            let $vue = this;
            if (item.laboratorio.tipoSangreEnum != null) {
                item.laboratorio.tipoSangre = item.laboratorio.tipoSangreEnum.name;
            }
            delete item.laboratorio.tipoSangreEnum;

            if (item.laboratorio.factorRHEnum != null) {
                item.laboratorio.factorRH = item.laboratorio.factorRHEnum.name;
            }
            delete item.laboratorio.factorRHEnum;

            console.log("item selected", item)

            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/resultadoslab/saveLaboratorio'),
                data: JSON.stringify(item.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.log("response", response.data);
                        notify(response.message, 'info');
                        $vue.$refs.raptorRL.loadRemoteData();
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        },

        loadTipoSangre() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/resultadoslab/tipoSangreList"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("tipoSangreList", response);
                $vue.tipoSangreList = response.data;
            })
        },

        loadFactorRh() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/resultadoslab/factorRhList"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("factorRhList", response);
                $vue.rhList = response.data;
            })
        },
        abrirObservaciones(item) {
            let $vue = this;
            $vue.itemSelected = item;            
            $vue.$refs.modalObservacion.open();
        },
        cerrarObservacion() {
            let $vue = this;
            $vue.$refs.modalObservacion.close();

        }
    }
});







        