$(function () {

    $(document).ready(function () {

        $('#timeHoraInicio').timepicker({'timeFormat': 'H:i'});

    });
});
Vue.component("date-picker", window.DatePicker.default);
new Vue({
    el: '#configuracion',
    data: {
        eventos: JSON.parse(eventosJson),
        Arryconfig: JSON.parse(configJson),
        ciclo: JSON.parse(cicloJson),
        tipos: JSON.parse(tiposJson),
        tiposTemp: {},
        lstTabs: [],
        config: {},
        dias: [],
        horas: [],
        idConfig: {},
        test: "",
        flag: true

    },
    created() {
        let $vue = this;
        $vue.tiposTemp = Object.assign({}, $vue.tipos);
        $vue.tabs();


    },
    mounted() {
        $('.numeric').numeric({negative: false});
    },
    updated() {
        let $vue = this;
        $vue.jquery($vue.horas);

    },
    methods: {
        convertDate(strDate) {
            var parts = strDate.split("/");
            return new Date(parts[2], parts[1] - 1, parts[0]);
        },
        nuevo() {

            $("#myModal").modal('show');
        },
        save(e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;

            $vue.config.duracion = $('#timeDuracion').val();
            $vue.config.espera = $('#timeEspera').val();
            $vue.config.horaInicio = $('#timeHoraInicio').val();


            $.ajax({
                method: 'POST',
                url: APP.url('academico/configuracionturno/configuracion'),
                contentType: "application/json",
                data: JSON.stringify($vue.config),
                success: function (response) {
                    $vue.config.id = response.data;
                    $vue.Arryconfig.push($vue.config);
                    $vue.tabs();
                    $vue.carga(response.data);
                    $vue.flag = true;
                    $vue.config = {};

                }
            });
            $("#myModal").modal('hide');
        },
        carga(config) {

            let $vue = this;
            $vue.idConfig = config;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/configuracionturno/list'),
                contentType: "application/json",
                data: JSON.stringify(config),
                success: function (response) {
                    $vue.horas = response.data[0];
                    $vue.dias = response.data[1];
                    $vue.jquery($vue.horas);
                    $vue.flag = false;
                }
            });

        },
        tabs() {
            let $vue = this;
            $vue.lstTabs.splice(0, $vue.lstTabs.length);
            $vue.Arryconfig.forEach(function (elem) {
                $vue.lstTabs.push(elem);
            });
            $vue.carga($vue.lstTabs[$vue.lstTabs.length - 1]);
        },
        active(index) {
            let $vue = this;
            let tabSize = $vue.lstTabs.length - 1;
            if (index == tabSize) {
                return "active";
            }
        },
        eliminar() {
            let $vue = this;

            $.ajax({
                method: 'DELETE',
                url: APP.url('academico/configuracionturno/deleteconfiguracion'),
                contentType: "application/json",
                data: JSON.stringify($vue.idConfig),
                success: function (response) {
                    var i = 0;
                    $vue.lstTabs.forEach(function (elem) {
                        if ($vue.idConfig.id == elem.id || $vue.idConfig == elem.id) {
                            $vue.lstTabs.splice(i, 1);
                            $vue.Arryconfig.splice(i, 1);
                            $vue.horas.splice(0, $vue.horas.length);
                            $vue.dias.splice(0, $vue.dias.length);
                            $vue.tabs();

                        }
                        i++;
                    });
                    $vue.flag = true;
                }
            });
        },
        tipoEvento(evento) {
            let $vue = this;
            $vue.tipos = Object.assign({}, $vue.tiposTemp);

            if ($vue.config.eventoCicloAcademico.codigo == 'MAT-REG') {
                delete $vue.tipos['ONLINE'];
            }
            if ($vue.config.eventoCicloAcademico.codigo == 'MAT-VER') {
                delete $vue.tipos['BARRIDO'];
            }
        },
        jquery(horas) {
            let $vue = this;
            $(function () {
                $(document).ready(function () {
                    $.fn.editable.defaults.mode = 'inline';
                    horas.forEach(function (elem) {
                        elem.turnos.forEach(function (turnos) {
                            $('#' + turnos.id).editable({
                                url: APP.url('academico/configuracionturno/updateturnos'),
                                contentType: 'application/json',
                                type: 'text',
                                pk: turnos.id,
                                success: function (response) {

                                    $vue.horas = response.data.data[0];
                                    $vue.dias = response.data.data[1];
                                    $vue.jquery($vue.horas);
                                }
                            });
                        });
                    });
                });
            });
        }
    }

});
        