Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#oficinaFormVUE',
    data: {
        oficina: JSON.parse(oficinaJson), //{instanciaReferencia: {}, tipoOficina: {}, oficinaSuperior: {}, cargo: {}},
        tiposOficina: JSON.parse(tiposOficinaJson),
        oficinaSuperior: [],
        referencias: [],
        personas: [],
        cargosJefe: [],
        hayInstancia: false,
        inicio: true,
    },
    mounted() {
        let $vue = this;
        if ($vue.oficina.tipoOficina.id != undefined) {
            $vue.loadReferencias($vue.oficina.tipoOficina);
        }
        $vue.oficina.codigo = VUE.revisarCodigo($vue.oficina.codigo);
        $vue.oficina.nombre = VUE.revisarNombreObjeto($vue.oficina.nombre);
        $vue.oficina.anexos = VUE.revisarAnexos($vue.oficina.anexos);
        $vue.oficina.telefonos = VUE.revisarTelefonos($vue.oficina.telefonos);
        $vue.oficina.email = VUE.revisarEmail($vue.oficina.email);

    },
    methods: {

        findOficinaSuperior(nombre) {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/allUnidadSuperior'),
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
            axios.post(rutaModulo + '/allPersona', {nombre: nombre})
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
                url: APP.url(rutaModulo + '/allCargo'),
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
        loadReferencias(tipoOficina) {
            let $vue = this;

            $.ajax({
                url: APP.url(rutaModulo + '/allReferencia'),
                dataType: 'json',
                type: 'POST',
                data: {tipo: tipoOficina.id},
                success: function (response) {
                    $vue.hayInstancia = false;
                    $vue.oficina.instanciaReferencia = {};

                    if (response.success) {
                        $vue.referencias = response.data;
                        if ($vue.referencias.length > 0) {
                            $vue.hayInstancia = true;
                        }
                        if ($vue.inicio) {
                            $vue.inicio = false;
                            if ($vue.oficina.instanciaOficina == undefined) {
                                return;
                            }
                            for (var i = 0; i < $vue.referencias.length; i++) {
                                if ($vue.oficina.instanciaOficina == $vue.referencias[i].id) {
                                    $vue.oficina.instanciaReferencia = $vue.referencias[i];
                                }
                            }
                        }

                    } else {
                        $vue.oficina.instanciaReferencia = null;
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        verRef(value) {
        },
        save() {
            let $vue = this;


            let form = $("#formOficina");
            form.parsley().destroy();
            form.parsley();
            if (form.parsley().validate() !== true) {
                return;
            }

            var data = Object.assign({}, $vue.oficina);
            if ($vue.oficina.instanciaReferencia.id != undefined) {
                data.instanciaOficina = $vue.oficina.instanciaReferencia.id;
            }
            $.ajax({
                url: APP.url(rutaModulo + '/save'),
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
            var color = {ACT: "success", CRE: "warning", INA: "danger", RES: "primary"};
            return "label-" + color[item.estado];

        },
        revisar(tipo, ofi, campo) {
            let $vue = this;

            if (tipo == 'CODIGO') {
                ofi[campo] = VUE.revisarCodigo(ofi[campo]);

            } else if (tipo == 'EMAIL') {
                ofi[campo] = VUE.revisarEmail(ofi[campo]);

            } else if (tipo == 'NOMBRE') {
                ofi[campo] = VUE.revisarNombreObjeto(ofi[campo]);

            } else if (tipo == 'ANEXOS') {
                ofi[campo] = VUE.revisarAnexos(ofi[campo]);

            } else if (tipo == 'TELEFONOS') {
                ofi[campo] = VUE.revisarTelefonos(ofi[campo]);
            }
        }
    }
});

window.Parsley
        .addValidator('nombreObjeto', {
            requirementType: 'string',
            validateString(value) {
                return value !== value.toUpperCase();
            },
            messages: {
                es: 'Este valor no pueder ser todo en mayúsculas'
            }
        });
    