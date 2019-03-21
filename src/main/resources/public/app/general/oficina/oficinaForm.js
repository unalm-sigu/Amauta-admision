Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#oficinaFormVUE',
    data: {
        oficina: JSON.parse(oficinaJson), //{instanciaReferencia: {}, tipoOficina: {}, oficinaSuperior: {}},
        tiposOficina: JSON.parse(tiposOficinaJson),
        oficinaSuperior: [],
        referencias: [],
        personas: [],
        cargosJefe: [],
        hayInstancia: false
    },
    mounted() {
        let $vue = this;
        $vue.oficina.instanciaReferencia = null;
        $vue.tipoSelect($vue.oficina.tipoOficina);
    },
    watch: {

//        tipoOficina(value) {
//            let $vue = this;
//            console.log(value)
//            if (value != undefined) {
//                $vue.tipoSelect(value);
//            }
//        }
    },
    methods: {

        findOficinaSuperior(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url('general/oficina/allUnidadSuperior'),
                dataType: 'json',
                type: 'POST',
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.oficinaSuperior = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        allPersonas(nombre) {
            let $vue = this;
            axios.post('/general/oficina/allPersona', {nombre: nombre})
                    .then(response => {
                        if (response.data.success) {
                            $vue.personas = response.data.data;
                        } else {
                            notify(response.data.message, "error");
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        notify(MESSAGES.errorComunicacion, "error");
                    });
        },
        allCargoJefe(nombre) {
            let $vue = this;

            $.ajax({
                url: APP.url('general/oficina/allCargo'),
                dataType: 'json',
                type: 'POST',
                data: {nombre: nombre},
                success: function (response) {
                    if (response.success) {
                        $vue.cargosJefe = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        tipoSelect(tipoOficina) {
            let $vue = this;
            if (tipoOficina.id == undefined) {
                $vue.oficina.instanciaReferencia = null;
                $vue.oficina.instanciaOficina = null;
                return;
            }

            $.ajax({
                url: APP.url('general/oficina/allReferencia'),
                dataType: 'json',
                type: 'POST',
                data: {tipo: tipoOficina.id},
                success: function (response) {
                    let ubicado = false;
                    $vue.hayInstancia = false;

                    if (response.success) {
                        console.log($vue.oficina.instanciaOficina)
                        $vue.referencias = response.data;
                        if ($vue.referencias.length > 0) {
                            $vue.hayInstancia = true;
                        }
                        for (var i = 0; i < $vue.referencias.length; i++) {
                            if ($vue.oficina.instanciaOficina == $vue.referencias[i].id) {
                                ubicado = true;
                                $vue.oficina.instanciaReferencia = Object.assign({}, $vue.referencias[i], {});
                            }
                        }
                        if (!ubicado) {
                            $vue.oficina.instanciaReferencia = null;
                            $vue.oficina.instanciaOficina = null;
                        }

                    } else {
                        $vue.oficina.instanciaReferencia = null;
                        $vue.oficina.instanciaOficina = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verRef(value) {
            let $vue = this;
            $vue.oficina.instanciaOficina = value.id;
        },
        save() {
            let $vue = this;
            let target = $("#formControl");
            target.parsley().destroy();
            target.parsley();
            if (target.parsley().validate() !== true) {
                return;
            }
            var data = {};
            data = Object.assign({}, $vue.oficina);
            if ($vue.oficina.instanciaOficina != undefined) {
                // data.instanciaOficina = $vue.oficina.instanciaOficina.id;
            }
            $.ajax({
                url: APP.url('general/oficina/save'),
                dataType: 'json',
                type: 'POST',
                contentType: "application/json",
                async: true,
                data: JSON.stringify(data),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "success");
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        classLabel(item) {
            //ACT("Activo"), CRE("Creado"), INA("Inactivo"), RES("Resolución");
            var color = {ACT: "success", CRE: "warning", INA: "danger", RES: "primary"};
            return "label-" + color[item.estado];

        }
    }
});
    