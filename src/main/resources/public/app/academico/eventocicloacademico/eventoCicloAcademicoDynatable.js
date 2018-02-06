var DynatableRowTemplate = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {eventoCicloAcademico: {eventoAcademico: {}}};
    },
    methods: {
        eliminar: function(id) {
            $global.$emit("eliminar", id);
        },
        editar: function(id) {
            $global.$emit("editar", id);
        },
    }
});

let  dynatable = null;

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function() {
        var vue = this;
        vue.createDynatable();
    },
    methods: {
        createDynatable: function() {
            var vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/eventocicloacademico/list'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function(e) {
                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    dynatableRowTemplate.eventoCicloAcademico = records[i];
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