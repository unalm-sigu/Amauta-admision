var $global = new Vue({});

var ItemCarreraTemplate = Vue.component("itemCarrera", {
    template: "#itemCarreraTemplate",
    data: function() {
        return {carrera: {}};
    },
    methods: {
        deleteItem(id) {
            $global.$emit("deleteItem", id);
        }
    }
});

new Vue({
    el: '#main',
    data: {
        convenio: {},
        addInstitucionModal: {
            id: 'modalAddInstitucion',
            header: true,
            title: 'Crear Institución',
            okbtn: 'Guardar'
        }
    },
    created: function() {
        let vue = this;
    },
    mounted: function() {
        let vue = this;
        $global.$on("deleteItem", function(id) {
            vue.deleteItem(id);
        });
        $(".date").datepicker();
        $('[name="pais.id"]').select2(vue.buscarPais());
        $('[name="paisUbicacion.id"]').select2(vue.buscarPais());
        $('[name="institucion.id"]').select2(vue.buscarEmpresa());
        vue.allCarrerasAfines();
    },
    methods: {
        buscarPais: function() {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), nombre: element.attr("rel"), codigo: element.attr("codigo")});
                    }
                },
                formatResult: function(info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function(info) {
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        buscarEmpresa: function() {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allEmpresa"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        var pais = $('[name="pais.id"]').select2('val');
                        console.log(pais);
                        if (pais == '') {
                            pais = 0;
                        }
                        return {nombre: term, page: page, idPais: pais};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback({id: element.val(), razonSocial: element.attr("rel")});
                    }
                },
                formatResult: function(info) {
                    return info.razonSocial;
                },
                formatSelection: function(info) {
                    return info.razonSocial;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        buscarCarrera: function(self) {
            return {
                allowClear: true,
                placeholder: "Seleccione un carrera",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/convenio/allCarrera"),
                    dataType: 'json',
                    type: 'post',
                    data: function(term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function(response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function(element, callback) {
                    if (element.val() != "") {
                        callback(self.carrera);
                    }
                },
                formatResult: function(info) {
                    return $.templates("#divBuscarCarrera").render(info);
                },
                formatSelection: function(info) {
                    self.carrera = info;
                    return info.nombre;
                },
                escapeMarkup: function(m) {
                    return m;
                }
            };
        },
        submitForm: function(e) {

            var self = $(e.currentTarget);
            self.btnDisabled();

            if (!$("#formConvenioBeca").parsley().validate() == true) {
                self.btnEnable();
                return;
            }

            $.ajax({
                url: APP.url('academico/convenio/save'),
                type: 'POST',
                async: true,
                data: $("#formConvenioBeca").serialize(),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $(location).attr('href', APP.url('academico/convenio'));
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function() {
                    self.btnEnable();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        addInstitucion: function(e) {
            let vue = this;
            vue.$refs.modalAddInstitucion.open();
        },
        addCarreraAfin: function(dato) {
            var vue = this;
            var carrera = {};
            if (dato) {
                console.log('upadte**********');
                console.log(dato);
                carrera = dato;
                console.log('after**********');
                console.log(carrera);
            }
            var itemCarreraTemplate = new ItemCarreraTemplate();
            itemCarreraTemplate.carrera = carrera;
            console.log('***afterset template');
            console.log(itemCarreraTemplate.carrera);
            console.log(itemCarreraTemplate.carrera.id);
            console.log(itemCarreraTemplate.carrera.modalidad);
            var component = itemCarreraTemplate.$mount();
            $('#carreraAfin tbody').append(component.$el);
            $('#carreraAfin tbody tr:last').find('.carreraItem').
                    select2(vue.buscarCarrera(itemCarreraTemplate));
//            $('#carreraAfin tbody tr:last').find('.carreraItem').
//                    select2(vue.buscarCarrera(itemCarreraTemplate)).on("change.select2", function(e) {
//                if (e && e.removed) {
//                    if (e.val == '') {
//                        itemCarreraTemplate.carrera = {};
//                    }
//                }
//            });
            vue.reindexitem();
        },
        createInstitucion: function(e) {
            let vue = this;
            var self = $(e.currentTarget);
            self.btnDisabled();
            if (!$("#formInstitucion").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            $.ajax({
                url: APP.url('academico/convenio/saveInstitucion'),
                type: 'POST',
                async: false,
                data: $("#formInstitucion").serialize(),
                success: function(response) {
                    if (response.success) {
                        notify(response.message, "info");
                        vue.setInstitucion(response.data);
                        vue.$refs.modalAddInstitucion.close();
                        self.btnEnable();
                    } else {
                        notify(response.message, "error");
                        self.btnEnable();
                    }
                },
                error: function() {
                    self.btnEnable();
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        setInstitucion: function(institucion) {
            var idPais = $('[name="pais.id"]').select2('val');
            var test = (idPais == institucion.paisUbicacion);
            if (test) {
                $('[name="institucion.id"]').select2('data', institucion);
            }
        },
        deleteItem(e) {
            var vue = this;
            var self = $(e.currentTarget);
            var tr = self.closest('tr');
            tr.remove();
            vue.reindexitem();
        },
        reindexitem: function() {
            var itemss = $('#carreraAfin').find('input.carreraItem');
            $.each(itemss, function(i, v) {
                $(v).prop('name', "carreraConvenio[" + i + "].carrera.id");
            });
        },
        allCarrerasAfines: function() {
            var vue = this;
            $.ajax({
                url: APP.url('academico/convenio/allCarrerasAfines'),
                type: 'POST',
                async: true,
                data: $("#formConvenioBeca").serialize(),
                success: function(response) {
                    if (response.success) {

                        if (response.total > 0) {
                            $.each(response.data, function(i, v) {
                                vue.addCarreraAfin(v);
                            });
                        } else {
                            vue.addCarreraAfin();
                        }

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});
