Vue.component("rolsearh", {
    template: "#rolsearhtemp",
    props: {
        rolsuperior: {id: null},
        nombre: {type: String, default: ''},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscar(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscar(vue));
    },
    methods: {
        buscar: function (vue) {
            return {
                minimumInputLength: -1,
                allowClear: true,
                placeholder: "  ",
                ajax: {
                    url: APP.url("seguridad/rol/allRolSuperior"),
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
                    if (vue.rolsuperior.id != null) {
                        callback(vue.rolsuperior);
                    }
                },
                formatResult: function (info) {
                    return info.codigo + ' - ' + info.nombre;
                },
                formatSelection: function (info) {
                    vue.rolsuperior.nombre = info.nombre;
                    vue.rolsuperior.codigo = info.codigo;
                    vue.rolsuperior.id = info.id;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});