Vue.component("ubicacionsearch", {
    template: "#ubicacionTemp",
    data: function () {
        return {
            ubicacion: {
                id: null
            }
        }
    },
    props: {
        ubicacion: {
            id: null
        },
        nombre: {type: String, default: ''},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarDistrito(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarDistrito(vue));
    },
    methods: {
        buscarDistrito: function (vue) {
            return {
                placeholder: "  ",
                minimumInputLength: -1,
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
                    if (vue.ubicacion.id != "") {
                        callback(vue.ubicacion);
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarDistrito").render(info);
                },
                formatSelection: function (info) {
                    vue.ubicacion.id = info.id;
                    vue.ubicacion.codigo = info.codigo;
                    vue.ubicacion.nombre = info.nombre;
                    vue.ubicacion.distrito = info.distrito;
                    return info.distrito;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});