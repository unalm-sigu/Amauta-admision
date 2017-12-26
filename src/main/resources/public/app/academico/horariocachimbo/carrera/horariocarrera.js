$(function () {

    var $global = new Vue({});

    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {carrera: []};
        },
    });

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        mounted: function () {
            var $vue = this;
            $vue.createDynatable();
        },
        methods: {
            createDynatable: function () {
                var $vue = this;
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/carrera/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).data('dynatable');
            },
            writter: function (rowIndex, record, columns, cellWriter) {
                var dynatableRowTemplate = new DynatableRowTemplate();
                dynatableRowTemplate.carrera = record;
                var component = dynatableRowTemplate.$mount();
                return component.innerHtml;
            }
        }
    });

    new Vue({
        el: '#main',
        data: {
            carrera: {},
        },
        created() {
            let $vue = this;
        },
        mounted: function () {
            let $vue = this;
        },
    });
    
});
