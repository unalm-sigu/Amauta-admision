Vue.component("vuelect", {
    template: '  <input type="text" v-bind:required="required" class="form-control" data-trigger="change" v-bind:name="nameinput" v-model="objetogenerico" /> ',
    props: {
        objetogenerico: {type: Object, default: {}},
        initobject: {type: Object, default: {id:null}},
        nameinput: {type: String, default: 'sinnombre'},
        formatselection: {type: String, default: 'nombre'},
        formatresult: {type: String, default: 'nombre'},
        remoteurl: {type: String, default: '/'},
        allowclear: {type: Boolean, default: false},
        required: {type: Boolean, default: false},
    },
    mounted: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarGenerico(vue))
                .on('change.select2', function (e) {
                    if (e.val == '') {
                        var keys = Object.keys(vue.objetogenerico);
                        for (var key in keys) {
                            vue.objetogenerico['' + keys[key]] = null;
                        }
                    }
                });
    },
    updated: function () {
        let vue = this;
        let self = $(vue.$el);
        self.select2(vue.buscarGenerico(vue))
                .on('change.select2', function (e) {
                    if (e.val == '') {
                        var keys = Object.keys(vue.objetogenerico);
                        for (var key in keys) {
                            vue.objetogenerico['' + keys[key]] = null;
                        }
                    }
                });
    },
    methods: {
        buscarGenerico: function (vue) {
            return {
                placeholder: "  ",
                allowClear: vue.allowclear,
                minimumInputLength: 2,
                ajax: {
                    url: APP.url(vue.remoteurl),
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
                    if (vue.initobject.id != null) {
                        callback(vue.initobject);
                    }
                },
                formatResult: function (info) {
                    return info[vue.formatresult];
                },
                formatSelection: function (info) {

                    var keys = Object.keys(info);

                    for (var key in keys) {
                        vue.objetogenerico['' + keys[key]] = info['' + keys[key]];
                    }

                    return info[vue.formatselection];
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});