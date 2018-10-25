Vue.component("funcionsearh", {
    template: "#funcionsearhtemp",
    props: {
        funcion: {id: null},
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
                placeholder: "Seleccione una función para agregar",
                ajax: {
                    url: APP.url("seguridad/rol/allperfilcompania"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page,tipo:'FUNCION'};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                initSelection: function (element, callback) {
                    if (vue.funcion.id != null) {
                        callback(vue.funcion);
                    }
                },
                formatResult: function (info) {
                    return info.codigo + ' - ' + info.nombre;
                },
                formatSelection: function (info) {
                    vue.funcion.nombre = info.nombre;
                    vue.funcion.codigo = info.codigo;
                    vue.funcion.id = info.id;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});