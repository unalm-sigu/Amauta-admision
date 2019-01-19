Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);
new Vue({
    el: '#main',
    data: {
        reservaaula: {tipoSolicitante: null, tramite: {alumno: {}, empresa: {}, docente: {}}},
        reservaaulaedit: JSON.parse(reservaAulaJson),
        urlfilter: APP.url("tramite/aula/filteraula"),
        institucion: {pais: {}},
        dataInstitucionModal: {
            id: 'idInstitucionModal',
            header: true,
            title: 'Agregar Institución',
            okbtn: 'Agregar Institución'
        },
        rangofecha: true,
        solofecha: false,
        variosambiente: true,
        soloambiente: false,
        isactiveguardar: false,
        todos: true,
        solodisponible: false,
        reservados: [],
        moduloselecto: {id: null},
        dias: [],
        horas: [],
        jsonaulahorario: []
    },
    mounted: function () {

        let $vue = this;

        if ($vue.reservaaulaedit != null) {
            if ($vue.reservaaulaedit.id != null) {
                $vue.reservaaula = $vue.reservaaulaedit;
                $vue.reservados = $vue.reservaaulaedit.reservados;
                $vue.reloadaulalist();
            }
        }

        $global.$on("changehorario", function () {
            $vue.changehorario();
        });

    },
    updated: function () {
        let $vue = this;
    },
    methods: {
        changeSoloFecha() {
            let $vue = this;
            $vue.reservados = [];
            $vue.rangofecha = !$vue.rangofecha;
        },
        changeRangoFecha() {
            let $vue = this;
            $vue.reservados = [];
            $vue.solofecha = !$vue.solofecha;
            $vue.changefilteraula();
        },
        addInstitucion() {
            let $vue = this;
            $vue.institucion = {pais: {}};
            $vue.$refs.nuevaInstitucionModal.open();
        },
        saveInstitucionModal() {
            let $vue = this;
            let miform = $($vue.$refs.formInstitucionModal);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $.ajax({
                url: APP.url('tramite/aula/saveInstitucion'),
                type: 'POST',
                async: false,
                data: miform.serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.tramite.empresa = response.data;
                        $vue.$refs.nuevaInstitucionModal.close();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        changeSoloAmbiente() {
            let $vue = this;
            $vue.variosambiente = !$vue.variosambiente;
        },
        changeVariosAmbientes() {
            let $vue = this;
            $vue.soloambiente = !$vue.soloambiente;
        },
        changeFechaInicio() {
            let $vue = this;
            if ($vue.reservaaula.fechaFin == undefined) {
                $vue.reservaaula.fechaFin = $vue.reservaaula.fechaInicio;
            }
            $vue.reservados = [];
            $vue.changefilteraula();
        },
        changeFechaFin() {
            let $vue = this;
            $vue.reservados = [];
            $vue.changefilteraula();
        },
        guardarTramite() {
            let $vue = this;
            let miform = $($vue.$refs.formtramite);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.reservaaula.reservados = $vue.reservados;
            $vue.reservaaula.diahora = $vue.jsonaulahorario;
            $vue.isactiveguardar = true;
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('tramite/aula/save'),
                data: JSON.stringify($vue.reservaaula),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        var urll = APP.url('tramite/aula');
                        $(location).attr('href', urll);
                    } else {
                        notify(response.message, "error");
                        $vue.isactiveguardar = false;
                    }
                },
                error() {
                    notify(MESSAGES.errorComunicacion, "error");
                    $vue.isactiveguardar = false;
                }
            });
        },
        changeTodos() {

            let $vue = this;
            $vue.solodisponible = !$vue.solodisponible;
        },
        changeSoloDisponible() {

            let $vue = this;
            $vue.todos = !$vue.todos;
            $vue.changefilteraula();
        },
        deleteReservado(reserva) {

            let $vue = this;
            let indx = $vue.reservados.indexOf(reserva);
            $vue.reservados.splice(indx, 1);
            $vue.changefilteraula();
        },
        addAula(aula) {

            let $vue = this;
            $vue.reservados.push(aula);
            $vue.changefilteraula();
        },
        changefilteraula() {

            let $vue = this;

            $vue.$refs.raptor.querie.push({name: 'solodisponible', value: $vue.solodisponible});
            $vue.$refs.raptor.querie.push({name: 'fechainicio', value: $vue.reservaaula.fechaInicio});
            $vue.$refs.raptor.querie.push({name: 'horainicio', value: $vue.reservaaula.horaInicio});
            $vue.$refs.raptor.querie.push({name: 'horafin', value: $vue.reservaaula.horaFin});
            $vue.$refs.raptor.querie.push({name: 'rangofecha', value: $vue.rangofecha});

            $vue.$refs.raptor.querie.push({name: 'fechafin', value: $vue.rangofecha ? $vue.reservaaula.fechaFin : ''});
            $vue.$refs.raptor.querie.push({name: 'modulo', value: $vue.moduloselecto.id != null ? $vue.moduloselecto.id : ''});

            var diahora = $vue.jsonaulahorario.map(function (v, i) {
                return v.id;
            });

            $vue.$refs.raptor.querie.push({name: 'diahora', value: diahora.toString()});

            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });

            $vue.$refs.raptor.querie.push({name: 'aulas', value: $vue.reservados.length > 0 ? aulass.toString() : ''});

            $vue.$refs.raptor.loadRemoteData();
        },
        changehorario() {
            let $vue = this;
            $vue.reservados = [];
            $vue.changefilteraula();
        },
        changemodulo() {
            let $vue = this;
            $vue.changefilteraula();
        }
    }
});
