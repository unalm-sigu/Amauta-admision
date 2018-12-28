Vue.component("paissearh", {
    template: "#paisTemp",
    data: function () {
        return {
            pais: {
                id: null
            }
        }
    },
    props: {
        pais: {id: null},
        nombre: {type: String, default: ''},
        changevalue: {type: Function, default: () => {
            }},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarPais(vue))
                .on('change.select2', function (e) {
                    if (e.val == '') {
                        var keys = Object.keys(vue.pais);
                        for (var key in keys) {
                            vue.pais['' + keys[key]] = null;
                        }
                    }
                });
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarPais(vue))
                .on('change.select2', function (e) {
                    if (e.val == '') {
                        var keys = Object.keys(vue.pais);
                        for (var key in keys) {
                            vue.pais['' + keys[key]] = null;
                        }
                    }
                    vue.changevalue(vue.pais);
                });
    },
    methods: {
        buscarPais: function (vue) {
            return {
                minimumInputLength: -1,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
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
                    if (vue.pais.id != null) {
                        callback(vue.pais);
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.pais.id = info.id;
                    vue.pais.codigo = info.codigo;
                    vue.pais.nombre = info.nombre;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});