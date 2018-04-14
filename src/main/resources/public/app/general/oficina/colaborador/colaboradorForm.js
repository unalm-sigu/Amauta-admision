Vue.component("multiselect", window.VueMultiselect.default)

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
        newCola: false,
        valid: true,
        funcion:[]
    },
    computed: {

    },
    created() {
        let $vue = this;
        if ($vue.colabo.id != 0) {
            $vue.colaborador = $vue.colabo;
            $vue.persona = $vue.colaborador.persona;
            $vue.persona.tipoDocumento = $vue.colaborador.tipoDocumento;
            $vue.newCola = true;
        }

    },
    mounted: function () {
        let $vue = this;
        $('.numeric').numeric({negative: false});
    },
    methods: {
        validacion: function (id, valor) {
            let $vue = this;
            $vue.valid = false;
        },
        validar(id, valor) {
            let $vue = this;
            console.log(id);
            console.log(valor);

            if ((id == undefined && valor == undefined) || (id != undefined && valor == null) || (id == undefined && valor != null)) {
                return false;
            }

            return true;
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
                        $vue.removerError();
                        console.log($vue.persona);
                    } else {
                        $vue.temp.numeroDocIdentidad = $vue.persona.numeroDocIdentidad;
                        $vue.persona = {};
                        $vue.persona = $vue.temp;
                        $vue.personaConId = true;
                    }
                }
            });
        },
        verificarEmail(e) {
            let $vue = this;
            if ($vue.persona.emailCompania == undefined) {
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

                    }
                }
            });
        },
        regresar: function () {
            let $vue = this;
            location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
        },
        updateColaborador: function (id) {
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
                $vue.removerError();
                self.btnEnable();
                return;
            }
            self.btnEnable();

            console.log($vue.colaborador);
            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/updateColaborador'),
                contentType: "application/json",
                data: JSON.stringify($vue.colaborador),
                success: function (response) {
                    if (response.success) {
                        $vue.colaborador = {}
                        $vue.persona = {}
                        location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
                        notify(response.message, 'info');

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
                $vue.removerError();
                self.btnEnable();
                return;
            }
            self.btnEnable();

            $vue.colaborador.persona = $vue.persona;
            $vue.funcion = [];
            $vue.colaborador.funcionColaborador.forEach(function (elem) {
                $vue.funcion.push($vue.funcion)
            })
            $vue.colaborador.funcionColaborador = {funcion: $vue.funcion};
            $vue.colaborador.
                    console.log($vue.colaborador);
            $.ajax({
                method: 'POST',
                url: APP.url('general/oficina/saveColaborador'),
                contentType: "application/json",
                data: JSON.stringify($vue.colaborador),
                success: function (response) {
                    if (response.success) {
                        $vue.colaborador = {}
                        $vue.persona = {}
                        location.href = APP.url("general/oficina/" + $vue.oficina.id + "/colaboradores");
                        notify(response.message, 'info');

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

        }
    }
});
