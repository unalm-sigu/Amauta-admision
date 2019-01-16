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
        reservados: []
    },
    mounted: function () {

        let $vue = this;

        $($vue.$refs.horaInicio).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24,
            timeFormat: 'H:i'})
                .on('change', function () {
                    $vue.reservaaula.horaInicio = $(this).val();
                });

        $($vue.$refs.horaFin).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24, timeFormat: 'H:i'})
                .on('change', function () {
                    $vue.reservaaula.horaFin = $(this).val();
                });

        if ($vue.reservaaulaedit.id != null) {
            $vue.reservaaula = $vue.reservaaulaedit;
            $vue.reservados = $vue.reservaaulaedit.reservados;
            $vue.reloadaulalist();
        }

    },
    updated: function () {
        let $vue = this;

        $($vue.$refs.horaInicio).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24,
            timeFormat: 'H:i'}).on('change', function () {
            $vue.reservaaula.horaInicio = $(this).val();
        });

        $($vue.$refs.horaFin).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24,
            timeFormat: 'H:i'}).on('change', function () {
            $vue.reservaaula.horaFin = $(this).val();
        });

    },
    methods: {
        changeSoloFecha() {
            let $vue = this;
            $vue.rangofecha = !$vue.rangofecha;
        },
        changeRangoFecha() {
            let $vue = this;
            $vue.solofecha = !$vue.solofecha;
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
        },
        guardarTramite() {
            let $vue = this;
            let miform = $($vue.$refs.formtramite);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.reservaaula.reservados = $vue.reservados;
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
        },
        deleteReservado(reserva) {
            let $vue = this;
            let indx = $vue.reservados.indexOf(reserva);
            $vue.reservados.splice(indx, 1);
            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });
            if ($vue.reservados.length > 0) {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: aulass.toString()});
            } else {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: ''});
            }
            $vue.$refs.raptor.loadRemoteData();
        },
        addAula(aula) {
            let $vue = this;
            $vue.reservados.push(aula);
            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });
            $vue.$refs.raptor.querie.push({name: 'aulas', value: aulass.toString()});
            $vue.$refs.raptor.loadRemoteData();
        },
        reloadaulalist() {
            let $vue = this;
            var aulass = $vue.reservados.map(function (v, i) {
                return v.id;
            });
            if ($vue.reservados.length > 0) {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: aulass.toString()});
            } else {
                $vue.$refs.raptor.querie.push({name: 'aulas', value: ''});
            }
            $vue.$refs.raptor.loadRemoteData();
        }
    }
});
