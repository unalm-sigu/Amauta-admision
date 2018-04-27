var DynatableRow = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {tipoConstancia: {}};
    },
    methods: {
        eliminar: function(tipoConstancia) {
            $global.$emit("eliminar", tipoConstancia);
        },
        updateTipo: function(tipoConstancia) {
            $global.$emit("updateTipo", tipoConstancia);
        },
    }
});
let $dynatable = null;
Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function() {
        let $vue = this;
        $vue.createDynatable();
    },
    methods: {
        createDynatable: function() {
            let $vue = this;
            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    type: 'GET',
                    ajaxUrl: APP.url("tramite/tipoconstancia/list"),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function(e) {
                var records = $dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRow = new DynatableRow();
                    dynatableRow.tipoConstancia = records[i];
                    dynatableRow.tipoConstancia.index = i;
                    var component = dynatableRow.$mount();
                    $('#dynaTbody').append(component.$el);
                }
            }).data('dynatable');

            $("body").delegate(".checking1", "click", function() {
                $global.$emit("checking", false);
            });

            $("body").delegate(".checking", "click", function() {
                $global.$emit("checking", true);
            });

        },
        writter: function(rowIndex, record, columns, cellWriter) {
            return "";
        }
    }
});