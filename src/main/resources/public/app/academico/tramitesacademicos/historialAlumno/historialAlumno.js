Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#asignacionTurnoVUE',
    data: {
        raptorurl: APP.url(rutaModulo + '/list'),
        configModalAsignarTurno: VUE_MODAL.structFormAjax({
            id: 'idModalAsignarTurno',
            header: true,
            form: 'formAsignarTurno',
            title: "Asignar Turno Matrícula",
            okbtn: "Si, confirmar",
            cancelbtn: 'Cancelar',
            showaccept: true
        }),
        matriculaResumen: {},
        turnosAtencion: [],
        turnoSeleccionado: {},
        matriculaTurno: {}
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        listarTurnosAtencion(alumno) {
            let $vue = this;
            axios.get(APP.url(`${rutaModulo}/turnosAtencion/${alumno.id}`))           
                    .then(response => {
                        if (response.status === 200) {
                            $vue.turnosAtencion = response.data;
                        }
                    })
                    .catch(function (error) {
                        notify(Messages.errorComunicacion, "error");
                    });
        },
        validaTurno(item) {
            console.log(item);
        },
        asignarTurno( {id, estado, alumno}){
            let $vue = this;
            $vue.listarTurnosAtencion(alumno);
            const form = $("#" + $vue.configModalAsignarTurno.form);
            form.parsley().reset();
            $vue.matriculaResumen = JSON.parse(JSON.stringify({id, estado, alumno}));
            $vue.$refs.modalAsignarTurno.open();
        },
        procesarAsignacionTurno() {
            let $vue = this;
            const form = $("#" + $vue.configModalAsignarTurno.form);
            if (!form.parsley().validate()) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }
            $vue.matriculaTurno = {
                ...$vue.matriculaTurno,
                'matriculaResumen': $vue.matriculaResumen,
                'turnoAtencion': $vue.turnoSeleccionado
            };
            $vue.$refs.modalAsignarTurno.beginProcessing();
            axios.post(APP.url(rutaModulo + "/procesarTurnoMatricula"), $vue.matriculaTurno)
                    .then(response => {
                        console.log(response);
                        $vue.$refs.modalAsignarTurno.confirmReaction(response.data.success);
                        $vue.$refs.raptorAsignacionTurno.loadRemoteData();
                        $vue.$refs.modalAsignarTurno.close();
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
        }
    }
});