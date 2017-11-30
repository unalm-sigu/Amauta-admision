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
                $('#dynaTable').bind('dynatable:init', function (e, dynatable) {
                    $('.dynatable-search').wrapAll('<div class="row m-b-sm"><div class="col-md-12" id="opopop"/></div>');
                    $('.dynatable-paginate, .dynatable-record-count').wrapAll('<div class="col-md-12"/>');
                    $('.dynatable-search').addClass('col-md-2');
                    $('.dynatable-search').find('input')
                            .addClass('form-control input-sm')
                            .attr('placeholder', 'Buscar');
                });
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/carrera/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function (e) {
                    $('.dynatable-paginate li').first().remove();
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
