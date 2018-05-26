Vue.component("departamentosearch", {
    template: "#departamentoTemp",
    data: function () {
        return {
            departamento: {
                id: null
            }
        }
    },
    props: {
        departamento: {
            id: null
        },
        nombre: {type: String, default: ''},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarDepartamento(vue));
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarDepartamento(vue));
    },
    methods: {
        buscarDepartamento: function (vue) {
            return {
                minimumInputLength: 2,
                placeholder: " ",
                ajax: {
                    url: APP.url("academico/departamento/allDepartamento"),
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
                    if (vue.departamento.id!= null) {
                        callback(vue.departamento);
                    }
                },
                formatResult: function (info) {
                    return '<p>' + info.nombre + '</p>  ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                formatSelection: function (info) {
                    vue.departamento.id = info.id;
                    vue.departamento.nombre = info.nombre;
                    vue.departamento.codigo = info.codigo;
                    return '<p>' + info.nombre + '</p>   ' + '<p class="bold text-xs"> ' + info.facultadName + '</p>';
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});