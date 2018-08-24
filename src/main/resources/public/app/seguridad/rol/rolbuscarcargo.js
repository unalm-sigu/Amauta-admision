Vue.component("cargosearh", {
    template: "#cargosearhtemp",
    props: {
        cargo: {id: null},
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
                minimumInputLength: 2,
                allowClear: true,
                placeholder: "Seleccione un cargo para agregar",
                ajax: {
                    url: APP.url("seguridad/rol/allperfilcompania"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page,tipo:'CARGO'};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vue.cargo.id != null) {
                        callback(vue.cargo);
                    }
                },
                formatResult: function (info) {
                    return info.codigo + ' - ' + info.nombre;
                },
                formatSelection: function (info) {
                    vue.cargo.nombre = info.nombre;
                    vue.cargo.codigo = info.codigo;
                    vue.cargo.id = info.id;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});