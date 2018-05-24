Vue.component("multiselect", window.VueMultiselect.default);
Vue.component("date-picker", window.DatePicker.default);
//Vue.component("date-picker", window.VueBootstrapDatetimePicker.default);
new Vue({
    el: '#colaboradorFormVue',
    data: {
        tipoDoc: JSON.parse(tipoDocumentoJson),
        persona: {},
        oficina: {id: JSON.parse(oficinaId)},
        sexo: sexoJson,
        area: JSON.parse(areaJson),
        compania: JSON.parse(companiaJson),
        funciones: JSON.parse(funcionesJson),
        colabo: JSON.parse(colaboradorJson),
        funcionColaborador: [],
        colaborador: {},
        personaValidTemp: {},
        newCola: false,
        colaboradorData: {},
        sexos: [{id: 'M', value: 'Masculino'}, {id: 'F', value: 'Femenino'}],
        seleccionado: {},
        config: {
            format: "dd/MM/yyyy",
            useCurrent: false
        }
    },
    computed: {

    },
    created() {
        let $vue = this;
        if ($vue.colabo.id != 0) {
            $vue.colaborador = $vue.colabo;
            console.dir($vue.colaborador)
            $vue.persona = $vue.colaborador.persona;
            $vue.persona.tipoDocumento = $vue.colaborador.persona.tipoDocumento;
            $vue.newCola = true;
        }

    },
    mounted: function () {
        let $vue = this;
        $('.numeric').numeric({negative: false});

        $global.$on("personaSeleccionada", function (personaSelec) {
            $vue.seleccionado = personaSelec;
        });
    },
    methods: {
        seleccionar: function () {
            let $vue = this;
            $vue.persona = $vue.seleccionado;
            if ($vue.persona != undefined) {
                $vue.restringirValores();
            }
            $("#myModal").modal('hide');
        },
        searchPerson: function () {
            $("#myModal").modal('show');
        },
        verificarDoc: function () {
            let $vue = this;
            if ($vue.persona.numeroDocIdentidad == undefined || $vue.persona.tipoDocumento == undefined) {
                return;
            }
            if ($vue.persona.id == null || $vue.persona.id == undefined) {
                $vue.temp = $vue.persona;
            }
            $.ajax({
                url: APP.url('general/oficina/validarDoc'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.persona),
                success: function (response) {
                    if (!response.success) {
                        $vue.persona = response.data;
                        $vue.restringirValores();
                    } else {
                        $vue.temp.numeroDocIdentidad = $vue.persona.numeroDocIdentidad;
                        $vue.temp.tipoDocumento = $vue.persona.tipoDocumento;
                        $vue.persona = {};
                        $vue.personaValidTemp = {};
                        $vue.persona.numeroDocIdentidad = $vue.temp.numeroDocIdentidad;
                        $vue.persona.tipoDocumento = $vue.temp.tipoDocumento;
                    }
                }
            });
        },
        verificarEmail(e) {
            let $vue = this;
            console.log($vue.persona.emailCompania)
            if ($vue.persona.emailCompania === undefined || $vue.persona.emailCompania == '') {
                return;
            }
            $.ajax({
                url: APP.url('general/oficina/validarEmail'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.persona),
                success: function (response) {
                    if (!response.success) {
                        var self = $(e.currentTarget);
                        self.btnDisabled();

                        var response = [];
                        response.item = 'emailCompania';
                        response.message = 'El email ya existe';

                        var FieldInstance = $('[name=' + response.item + ']').parsley(),
                                errorName = response.item;
                        window.ParsleyUI.removeError(FieldInstance, errorName);

                        // now display the error
                        window.ParsleyUI.addError(FieldInstance, errorName, response.message);
                        self.btnEnable();
                        return;

                    } else {
                        $vue.removerError();
                    }
                }
            });
        },
        regresar: function () {
            let $vue = this;
            location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
        },
        updateColaborador: function (id) {
            return;
            $.ajax({
                url: APP.url('general/oficina/updateColaborador'),
                type: 'POST',
                data: id,
                success: function (response) {
                }
            });
        },
        update(e) {
            let $vue = this;
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate()) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            $vue.colaboradorData.colaborador = $vue.colaborador;
            $vue.colaboradorData.perfilCompanias = $vue.colaborador.funcionColaborador;
            $vue.colaboradorData.oficinaMean = $vue.oficina;
//            $vue.colaboradorData.fechaInicio = $vue.convertDate($vue.colaboradorData.fechaInicio);
//            $vue.colaboradorData.fechaRegistro = $vue.convertDate($vue.colaboradorData.fechaRegistro);
            console.log(JSON.stringify($vue.colaboradorData))
            //return;
            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/updateColaborador'),
                contentType: "application/json",
                data: JSON.stringify($vue.colaboradorData),
                success: function (response) {

                    if (response.success) {
//                        $vue.colaborador = {}
//                        $vue.persona = {}
                        //location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');

                    }
                }
            });
        },
        save(e) {
            let $vue = this;
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate()) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            $vue.colaborador.persona = $vue.persona;
            $vue.colaboradorData.colaborador = $vue.colaborador;
            $vue.colaboradorData.perfilCompanias = $vue.colaborador.funcionColaborador;
            $vue.colaboradorData.oficinaMean = $vue.oficina;

            return;
            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/saveColaborador'),
                contentType: "application/json",
                data: JSON.stringify($vue.colaboradorData),
                success: function (response) {
                    if (response.success) {
                        $vue.colaborador = {}
                        $vue.persona = {}
                        location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
                        notify("Se agregó exitosamente al colaborador", 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        },
        removerError: function () {
            var response = [];
            response.item = 'emailCompania';
            response.message = 'El email ya existe';

            var FieldInstance = $('[name=' + response.item + ']').parsley(),
                    errorName = response.item;
            window.ParsleyUI.removeError(FieldInstance, errorName);

        },
        restringirValores: function () {
            let $vue = this;
            if ($vue.persona.id == null || $vue.persona.id == undefined) {
                $vue.temp = $vue.persona;
            }
            $vue.personaValidTemp = {};
            console.log($vue.persona);
            var map = new Map(Object.entries($vue.persona));
            Object.keys($vue.persona).forEach(function (elem) {
                if (elem == 'materno' && map.get(elem) !== null) {
                    $vue.personaValidTemp.materno = true;
                } else if (elem == 'paterno' && map.get(elem) !== null) {
                    $vue.personaValidTemp.paterno = true;
                } else if (elem == 'nombres' && map.get(elem) !== null) {
                    $vue.personaValidTemp.nombres = true;
                } else if (elem == 'emailCompania' && map.get(elem) !== null) {
                    $vue.personaValidTemp.emailCompania = true;
                } else if (elem == 'sexo' && map.get(elem) !== null) {
                    $vue.personaValidTemp.sexo = true;
                }

            })
            $vue.removerError();

        }
    }
});
