Vue.component("vuelect", {
    template: '  <input type="text" v-bind:required="required" class="form-control" data-trigger="change" v-bind:name="nameinput" v-model="objetogenerico" /> ',
    props: {
        objetogenerico: {type: Object, default: {}},
        initobject: {type: Object, default: {id: null}},
        nameinput: {type: String, default: 'sinnombre'},
        formatselection: {type: String, default: 'nombre'},
        formatresult: {type: String, default: 'nombre'},
        remoteurl: {type: String, default: '/'},
        allowclear: {type: Boolean, default: false},
        required: {type: Boolean, default: false},
        usetemplateresult: {type: Boolean, default: false},
        idtemplateresult: {type: String, default: null},
        usetemplateselection: {type: Boolean, default: false},
        idtemplateselection: {type: String, default: null},
        changevalue: {type: Function, default: () => {
            }},
        postdata: {type: Object, default: {}},
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
                    vue.changevalue(vue.objetogenerico);
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
                    vue.changevalue(vue.objetogenerico);
                });
        self.select2('data', vue.objetogenerico).trigger('change.select2');
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
                        var custumpostdata = {nombre: term, page: page};
                        var keys = Object.keys(vue.postdata);
                        for (var key in keys) {
                            custumpostdata['' + keys[key]] = vue.postdata['' + keys[key]];
                        }
                        return custumpostdata;
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
                    var templatee = "#" + vue.idtemplateresult;
                    if (vue.usetemplateresult) {
                        var SelectRowTemplate = Vue.component("selectRow", {template: templatee, props: {item: {type: Object, default: {}}}});
                        var selectRowTemplate = new SelectRowTemplate();
                        selectRowTemplate.item = info;
                        var itemmcomponent = selectRowTemplate.$mount();
                        return itemmcomponent.$el;
                    }
                    return info[vue.formatresult];
                },
                formatSelection: function (info) {

                    var keys = Object.keys(info);

                    for (var key in keys) {
                        vue.objetogenerico['' + keys[key]] = info['' + keys[key]];
                    }

                    var templatee = "#" + vue.idtemplateselection;

                    if (vue.usetemplateselection) {
                        var SelectionItemTemplate = Vue.component("selectionItem", {template: templatee, props: {item: {type: Object, default: {}}}});
                        var selectionItemTemplate = new SelectionItemTemplate();
                        selectionItemTemplate.item = info;
                        var itemmm = selectionItemTemplate.$mount();
                        return itemmm.$el;
                    }

                    if (vue.formatselection) {
                        var splitedd = vue.formatselection.split('.');
                        if (splitedd.length > 1) {
                            var infoo = info;
                            for (var ii in splitedd) {
                                infoo = infoo[splitedd[ii]];
                            }
                            return infoo;
                        }
                        return info[vue.formatselection];
                    }

                    return info['nombre'];
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        }
    }
});