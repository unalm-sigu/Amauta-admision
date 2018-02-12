
Vue.component("date-picker", window.DatePicker.default);
new Vue({
    el: '#configuracion',
    data: {
        eventos: JSON.parse(eventosJson),
        config: JSON.parse(configJson),
        ciclo: cicloJson,
        tipos: [{id: 1, value: 'Por barrido'},
            {id: 2, value: 'En línea'}],
        lstTabs: [],
//        config: {
//            evento: 1,
//            tipo: 2,
//            turnoDia: 4,
//            duracion: 40,
//            espera: 20,
//            alumnos: 320,
//            fechaInicio: '12/08/2012',
//            fechaFin: '12/08/2012'
//        }

        dias: [],
        horas: [],
        test: ""

    },
    created() {
        let $vue = this;

        $vue.tabs();
        $vue.carga($vue.config[0]);

    },
    mounted() {

        $('.numeric').numeric({negative: false});
        $('1').val();
        console.log($('1').val());
    },
    updated() {
        console.log("Objeto Actualizado y Renderizado");
    },
    methods: {
        convertDate(strDate) {
            var parts = strDate.split("/");
            return new Date(parts[2], parts[1] - 1, parts[0]);
        },
        nuevo() {

            $("#myModal").modal('show');
        },
        save() {
            let $vue = this;
            console.log($vue.config);
            $vue.config.duracion = $('#timeDuracion').val();
            $vue.config.espera = $('#timeEspera').val();
            $vue.config.horaInicio = $('#timeHoraInicio').val();
            console.log($vue.config);

            $.ajax({
                method: 'POST',
                url: APP.url('academico/configuracionturno/configuracion'),
                contentType: "application/json",
                data: JSON.stringify($vue.config),
                success: function (response) {

                }
            });
            $("#myModal").modal('hide');
        },
        carga(config) {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/configuracionturno/list'),
                contentType: "application/json",
                data: JSON.stringify(config),
                success: function (response) {
                    $vue.horas = response.data[0];
                    $vue.dias = response.data[1];
                    $vue.jquery($vue.horas);
                }
            });
        },
        tabs() {
            let $vue = this;
            $vue.config.forEach(function (elem) {
                $vue.lstTabs.push(elem);
            });
            console.log($vue.lstTabs);
        },
        jquery(horas) {
            let $vue = this;
            $(function () {

                $(document).ready(function () {
//toggle `popup` / `inline` mode
                    $.fn.editable.defaults.mode = 'inline';
                    //make status editable
                    $('#status').editable({
                        type: 'select',
                        title: 'Select status',
                        placement: 'right',
                        value: 2,
                        source: [
                            {value: 1, text: 'status 1'},
                            {value: 2, text: 'status 2'},
                            {value: 3, text: 'status 3'}
                        ]
                                /*
                                 //uncomment these lines to send data on server
                                 ,pk: 1
                                 ,url: '/post'
                                 */
                    });

                    horas.forEach(function (elem) {
                        elem.turnos.forEach(function (turnos) {
                            $('#' + turnos.id).editable({
                                url: APP.url('academico/configuracionturno/updateconfiguracion'),
                                contentType:'application/json',
                                type: 'number',
                                pk: turnos.id,
                                title: 'Enter username'
                            });
                        });
                    });

                    $('#timeHoraInicio').timepicker();
                    $('#timeDuracion').timepicker({'timeFormat': 'H:i:s'});
                    $('#timeEspera').timepicker({'timeFormat': 'H:i:s'});

                });
            }
            );
        }
    }

});
        