var $global = new Vue({});

var DynatableRowTemplate = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {carrera: []};
    },
});

let  dynatable = null;

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function() {
        var $vue = this;
        $vue.createDynatable();
    },
    methods: {
        createDynatable: function() {
            var $vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/horariocachimbo/carrera/list'),
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"},
                features: {
                    paginate: false,
                    search: false
                }
            }).bind("dynatable:afterUpdate", function(e) {

                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    dynatableRowTemplate.carrera = records[i];
                    var component = dynatableRowTemplate.$mount();
                    $('#dynaTbody').append(component.$el);
                }
            }).data('dynatable');

        },
        writter: function(rowIndex, record, columns, cellWriter) {
            return "";
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
    mounted: function() {
        let $vue = this;
    },
});

