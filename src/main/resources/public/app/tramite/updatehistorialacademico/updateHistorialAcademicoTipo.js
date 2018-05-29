Vue.component("tipodocumentosearh", {
    template: "#tipoDocumentoTemp",
    props: {
        tipo: {id: null},
        nombre: {type: String, default: ''},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarTipodocumento(vue)).
                on("change.select2", function (el) {
                    $global.$emit("changeTipo");
                });
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarTipodocumento(vue)).
                on("change.select2", function (el) {
                    $global.$emit("changeTipo");
                });
    },
    methods: {
        buscarTipodocumento: function (vue) {
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("tramite/solicitudconstancia/updatehistorial/allTipoDocumento"),
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
                    if (vue.tipo.id != null) {
                        callback(vue.tipo);
                    }
                },
                formatResult: function (info) {
                    return info.tipoName + ' - ' + info.nombre;
                },
                formatSelection: function (info) {
                    vue.tipo.nombre = info.nombre;
                    vue.tipo.tipo = info.tipo;
                    vue.tipo.costoCiclo = info.costoCiclo;
                    vue.tipo.requiereFoto = info.requiereFoto;
                    vue.tipo.id = info.id;
                    vue.tipo.tipoName = info.tipoName;
                    vue.tipo.precioDocumento = info.precioDocumento;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});