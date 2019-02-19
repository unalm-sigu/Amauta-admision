
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        ingresantesURL: APP.url('ingresante/resultadoslab/list'),
        tipoSangreList: [],
        rhList: [1, -1]
    },
    mounted: function () {
        let $vue = this;
        $(".decimal").numeric({negative: false});

        $vue.$refs.raptorRL.afterProcess = () => {
            $(".decimal").numeric({negative: false});
        };

        $vue.loadTipoSangre();
        console.log(" $vue.$refs.raptorRL", $vue.$refs.raptorRL._props);
    },
    methods: {

        guardarNumeroMuestra(item) {
            let $vue = this;
            if (item.laboratorio.tipoSangreEnum != null) {
                item.laboratorio.tipoSangre = item.laboratorio.tipoSangreEnum.name;
            }
            delete item.laboratorio.tipoSangreEnum;

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

        labelrh(item) {
            if (item == -1) {
                return "Negativo"
            } else {
                return "Positivo"
            }
        },
    }
});







        