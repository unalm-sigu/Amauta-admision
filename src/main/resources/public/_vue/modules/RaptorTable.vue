<template>
    <transition>
        <div class="row m-t-md m-b-md" style="position: relative;">

            <div v-if="processing" 
                 class="raptor-table-process-main">
                <span class="raptor-table-process-box">
                    <span v-html="processtext" 
                          class="raptor-table-process-text">
                    </span>
                </span>
            </div>

            <div v-if="searcher">
                <div class="col-md-10">
                    <slot name="header"> </slot>
                </div>

                <div class="col-md-2">
                    <label v-if="labelsearch">&nbsp;</label>
                    <input v-on:change="executeSearch"
                           v-model="search" 
                           placeholder="Buscar"
                           type="text"
                           class="form-control input-sm"/>
                </div>
            </div>
            <div v-else="">
                <div class="col-md-12">
                    <slot name="header"> </slot>
                </div>
            </div>

            <div class="col-md-1 hidden">
                <select class="form-control input-sm">
                    <option> 10 </option>
                    <option> 20 </option>
                    <option> 50 </option>
                    <option> 100 </option>
                </select>
            </div>


            <div class="col-md-12 m-t-sm">
                <slot v-bind:data="data"></slot>
            </div>

            <div class="wrapper">
                <div class="col-md-6" v-if="commented">
                    <small class="text-muted">
                        {{footer}}
                    </small>
                </div>

                <div class="col-md-6 text-right">
                    <uib-pagination v-model="page" 
                                    v-bind="pagination"
                                    v-on:change="loadRemoteData"
                                    class="pagination"
                                    previous-text="Anterior"
                                    next-text="Siguiente"
                                    v-if="paginate"/>
                </div>
            </div>

        </div>

    </transition>
</template>
<script type="text/javascript">
    module.exports = {
        props: {
            processing: {type: Boolean, default: false},
            processtext: {type: String, default: '<i class="fa fa-spinner fa-spin"></i> Cargando información...'},
            timesleep: {type: Number, default: 500},
            search: {type: String, default: ""},
            footer: {type: String, default: ""},
            data: {type: Object, default: []},
            header: {type: Object, default: []},
            url: {type: String, default: null},
            ajaxdata: {type: Object, default: {}},
            searcher: {type: Boolean, default: true},
            labelsearch: {type: Boolean, default: false},
            paginate: {type: Boolean, default: true},
            commented: {type: Boolean, default: true},
            preload: {type: Boolean, default: true},
            page: {type: Object, default: {currentPage: 1}},
            pagination: {type: Object, default: {'total-items': 0, 'items-per-page': 10, 'max-size': 3, 'boundary-link-numbers': true}},
            querie: {type: Object, default: []},
            afterprocess: {type: Function, default: () => {
                }}
        },
        mounted() {
            if (this.preload) {
                this.repreload();
            }
        },
        methods: {
            repreload() {
                this.search = this.getParameterByName('queries[search]');
                this.search = (this.search == null) ? '' : this.search;
                if (this.getParameterByName('page')) {
                    this.page.currentPage = parseInt(this.getParameterByName('page'));
                } else {
                    this.loadRemoteData();
                }
            },
            getParameterByName(name) {
                var url = window.location.href;
                name = name.replace(/[\[\]]/g, '\\$&');
                var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'), results = regex.exec(url);
                if (!results) {
                    return null;
                }
                if (!results[2]) {
                    return '';
                }
                return decodeURIComponent(results[2].replace(/\+/g, ' '));
            },
            changeUrl(key, value) {
                let $vue = this;
                var newUrl = $vue.updateQueryString(key, value);
                history.pushState(null, null, newUrl);
            },
            updateQueryString(key, value) {
                var uri = window.location.href;
                uri = uri.split("[").join("OPEN_BRACKET");
                uri = uri.split("]").join("CLOSE_BRACKET");
                key = key.split("[").join("OPEN_BRACKET");
                key = key.split("]").join("CLOSE_BRACKET");

                var re = new RegExp("([?&])" + key + "=.*?(&|#|$)(.*)", "gi"), hash;
                if (re.test(uri)) {
                    if (typeof value !== 'undefined' && value !== null) {
                        uri = uri.replace(re, '$1' + key + "=" + value + '$2$3');
                    } else {
                        hash = uri.split('#');
                        uri = hash[0].replace(re, '$1$3').replace(/(&|\?)$/, '');
                        if (typeof hash[1] !== 'undefined' && hash[1] !== null) {
                            uri += '#' + hash[1];
                        }
                    }
                } else {
                    if (typeof value !== 'undefined' && value !== null) {
                        var separator = uri.indexOf('?') !== -1 ? '&' : '?';
                        hash = uri.split('#');
                        uri = hash[0] + separator + key + '=' + value;
                        if (typeof hash[1] !== 'undefined' && hash[1] !== null) {
                            uri += '#' + hash[1];
                        }
                    }
                }

                uri = uri.split("OPEN_BRACKET").join("[");
                uri = uri.split("CLOSE_BRACKET").join("]");
                return uri;
            },
            executeSearch() {
                let $vue = this;
                Vue.set($vue.page, 'currentPage', 1);
                $vue.loadRemoteData();
            },
            loadRemoteData(withHide) {
                if (this.url === null) {
                    return;
                }

                let $vue = this;
                $vue.processing = true;
                $vue.footer = "";
                let hideData = (withHide === undefined) ? false : withHide;
                $vue.dataTmp = JSON.parse(JSON.stringify($vue.data));
                if (hideData) {
                    $vue.data = [];
                }
                let params = $vue.generateParams();
                axios.get($vue.url, {params})
                        .then(function (response) {
                            $vue.sleep();
                            $vue.data = response.data.data;
                            if (response.data.header != null) {
                                $vue.header = response.data.header;
                            }
                            $vue.generateFooterInfo(params, response.data);
                            $vue.pagination['total-items'] = response.data.filtered;
                            $vue.processing = false;
                            if (response.data.total > 0 && response.data.filtered == 0) {
                                Vue.set($vue.page, 'currentPage', 1);
                            } else if (response.data.filtered > 0 && response.data.data.length == 0) {
                                Vue.set($vue.page, 'currentPage', 1);
                            }
                        })
                        .catch(function (error) {
                            if (hideData) {
                                $vue.data = $vue.dataTmp;
                            }
                            $vue.processing = false;
                            console.log(error);
                        });
            },
            generateFooterInfo(params, data) {
                let $vue = this;
                let total = data.total;

                setTimeout(function () {
                    $vue.afterprocess();
                }, 500);

                if (total == 0) {
                    $vue.footer = '0 registros';
                    return;
                }

                let first = data.data.length == 0 ? 0 : params.offset + 1;
                let tos = params.offset + data.data.length;
                let filtero = data.filtered;
                let info = "";
                if ($vue.paginate && data.data.length > 0) {
                    info = first + " a " + tos + " de " + filtero + " registros";
                } else {
                    info = data.data.length + " registros";
                }

                if (typeof $vue.search !== "undefined") {
                    if ($vue.search != "") {
                        info += " (filtrados de " + total + " registros)";
                    }
                }

                $vue.footer = info;
            },
            generateParams() {
                let $vue = this;
                let page = $vue.page['currentPage'];
                let perPage = $vue.pagination['items-per-page'];
                let offset = page * perPage - perPage;
                let params = {};
                if ($vue.paginate || $vue.commented) {
                    params = {
                        page: page,
                        offset: offset,
                        perPage: perPage
                    };
                    if (page > 1) {
                        $vue.changeUrl('page', page);
                    } else {
                        $vue.changeUrl('page', null);
                    }
                }
                if ($vue.search !== "") {
                    $vue.changeUrl('queries[search]', $vue.search);
                    params['queries[search]'] = $vue.search;
                } else {
                    $vue.changeUrl('queries[search]', null);
                }

                $vue.querie.forEach(function (elem) {
                    params['queries[' + elem.name + ']'] = elem.value;
                    $vue.changeUrl('queries[' + elem.name + ']', elem.value);
                });

                var returnParams = Object.assign(params, $vue.ajaxdata);
                return returnParams;
            },
            sleep() {
                let $vue = this;
                let start = new Date().getTime();
                for (; ; ) {
                    let after = new Date().getTime();
                    if (after > start + $vue.timesleep) {
                        break;
                    }
                }
            }
        }
    }
</script>