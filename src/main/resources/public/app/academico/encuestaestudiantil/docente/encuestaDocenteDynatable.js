var DynatableRow = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {encuestaDocente: {}};
    },
    methods: {
        estado: function(encuestaDocente) {
            $global.$emit("estado", encuestaDocente);
        }
    }
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
                    ajaxUrl: APP.url('academico/encuestaestudiantil/docente/list'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"},
                features: {
                    paginate: true,
                    search: true
                }
            }).bind("dynatable:afterUpdate", function(e) {
                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRow = new DynatableRow();
                    dynatableRow.encuestaDocente = records[i];
                    var component = dynatableRow.$mount();
                    $('#dynaTbody').append(component.$el);
                }
            }).data('dynatable');
        },
        writter: function(rowIndex, record, columns, cellWriter) {
            return "";
        }
    }
});

