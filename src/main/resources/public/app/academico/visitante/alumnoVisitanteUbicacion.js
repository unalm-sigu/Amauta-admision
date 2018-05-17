Vue.component("ubicacionnacer", {
    template: "#ubicacionNacerTemp",
    data: function () {
        return {
            persona: {
                tipoDocumento: {id: null},
                paisNacer: {id: null},
                nacionalidad: {id: null},
                paisDomicilio: {id: null},
                ubicacionDomicilio: {id: null},
                ubicacionNacer: {id: null}
            }
        }
    },
    props: {
        persona: {
            tipoDocumento: {id: null},
            paisNacer: {id: null},
            nacionalidad: {id: null},
            paisDomicilio: {id: null},
            ubicacionDomicilio: {id: null},
            ubicacionNacer: {id: null}
        }
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.find(".buscar-distrito").select2(vue.buscarDistrito(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.find(".buscar-distrito").select2(vue.buscarDistrito(vue));
    },
    methods: {
        buscarDistrito: function (vue) {
            return {
                placeholder: "  ",
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allDistritos"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vue.persona.ubicacionNacer.id != "") {
                        callback(vue.persona.ubicacionNacer);
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarDistrito").render(info);
                },
                formatSelection: function (info) {
                    return info.distrito;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});