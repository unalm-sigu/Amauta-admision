
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#ingresantesVUE',
    data: {
        turnoSelected: {},
        ingresantesURL: '',
        turnos: [],
        laboratorioActual: JSON.parse(laboratorioActual),
    },
    mounted: function () {
        let $vue = this;
        console.log("laboratorioActual", $vue.laboratorioActual);
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

                    setTimeout(function () {
                        $vue.$refs.raptorML.loadRemoteData();
                    }, 50);
                }
            })
        },
        cambiarTurno() {
            let $vue = this;
            console.log("turnosel", $vue.turnoSelected)
            $vue.ingresantesURL = APP.url('ingresante/muestraslab/list/' + $vue.turnoSelected.id);
            setTimeout(function () {
                $vue.$refs.raptorML.loadRemoteData();
            }, 50);
        },
        asignarNumLab(item) {
            console.log("item selected", item)
            let $vue = this;
            if (item.laboratorio.historiaClinica.id === "") {
                item.laboratorio.historiaClinica = null;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/muestraslab/saveLaboratorio'),
                data: JSON.stringify(item.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.log(response);
                        $vue.laboratorioActual.numero = response.numeroLaboratorio;
                        item.laboratorio.numeroLaboratorio = response.numeroLaboratorio;
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        }
    }
});







        