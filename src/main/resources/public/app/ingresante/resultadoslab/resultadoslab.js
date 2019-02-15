
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        ingresantesURL: APP.url('ingresante/resultadoslab/list'),
    },
    mounted: function () {
        let $vue = this;
        $(".decimal").numeric({negative: false});
        
        $vue.$refs.raptorRL.afterProcess = () => {
            $(".decimal").numeric({negative: false});
        };
    },
    methods: {

        guardarNumeroMuestra(item) {
            console.log("item selected", item)
            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/resultadoslab/saveLaboratorio'),
                data: JSON.stringify(item.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.log("response", response.data);
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        }
    }
});







        