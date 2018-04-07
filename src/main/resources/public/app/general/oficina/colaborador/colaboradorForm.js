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
        newCola: false
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
        console.log($vue.colabo);
        console.log($vue.funciones);
    },
    mounted: function () {
        let $vue = this;

    },
    methods: {
        regresar: function () {
            let $vue = this;
            $vue.oficina
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
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
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
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            $vue.colaborador.persona = $vue.persona;

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
        }
    }
});
