Vue.component("universidadsearch", {
    template: "#universidadTemp",
    data: function () {
        return {
            universidad: {
                id: null
            }
        }
    },
    props: {
        universidad: {id: null},
        nombre: {type: String, default: ''},
        pais: {type: Object, default: {id:null}},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarUniversidad(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarUniversidad(vue));
    },
    methods: {
        buscarUniversidad: function (vue) {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allUniversidadXpais"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page,pais:vue.pais.id};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vue.universidad.id != null) {
                        callback(vue.universidad);
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.universidad.id = info.id;
                    vue.universidad.codigo = info.codigo;
                    vue.universidad.nombre = info.nombre;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});