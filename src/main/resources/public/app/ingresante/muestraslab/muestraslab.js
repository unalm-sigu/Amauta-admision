
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        ingresantesURL: APP.url(''),
        turnos: [],
    },
    mounted: function () {
        let $vue = this;
        $vue.loadTurnos();
    },
    methods: {
        loadTurnos() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/muestraslab/turnos"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("turnos", response);
                $vue.turnos = response.data;
                if ($vue.turnos.length > 1) {
                    $vue.turnoSelected = $vue.turnos[0];
                    $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
                    this.$refs.raptorML.loadRemoteData();
                }
            })
        },
        cambiarTurno() {
            let $vue = this;
            $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
            this.$refs.raptorML.loadRemoteData();
        },
        AsignarNumLab(item) {

        }
    }
});







        