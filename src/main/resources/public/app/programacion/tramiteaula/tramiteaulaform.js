Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('date-picker', VueBootstrapDatetimePicker.default);

new Vue({
    el: '#main',
    data: {
        tramite: {alumno: {}, empresa: {}, docente: {}},
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
                    $vue.tramite.horaInicio = $(this).val();
                });

        $($vue.$refs.horaFin).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24, timeFormat: 'H:i'})
                .on('change', function () {
                    $vue.tramite.horaFin = $(this).val();
                });
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
            $vue.tramite.horaInicio = $(this).val();
        });

        $($vue.$refs.horaFin).timepicker({
            minuteStep: 15,
            showSeconds: false,
            showMeridian: false,
            defaultTime: false,
            maxHours: 24,
            timeFormat: 'H:i'}).on('change', function () {
            $vue.tramite.horaFin = $(this).val();
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
            if ($vue.tramite.fechaFin == undefined) {
                $vue.tramite.fechaFin = $vue.tramite.fechaInicio;
            }
        },
        guardarTramite() {
            let $vue = this;
            let miform = $($vue.$refs.formtramite);
            let valid = miform.parsley().validate();
            if (!valid) {
                return;
            }
            $vue.isactiveguardar = true;
            $.ajax({
                method: 'POST',
                async: true,
                url: APP.url('academico/alumno'),
                data: JSON.stringify($vue.tramite),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        var urll = APP.url('tramite/aula');
                        $(location).attr('href', urll);
                    } else {
                        notify(response.message, "error");
                    }
                    $vue.isactiveguardar = false;
                },
                error() {
                    $vue.isactiveguardar = false;
                    notify(MESSAGES.errorComunicacion, "error");
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
    }
});
