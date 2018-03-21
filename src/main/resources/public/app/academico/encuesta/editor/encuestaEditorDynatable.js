var DynatableRow = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {encuesta: {}};
    },
    methods: {
        update: function(encuesta) {
            $global.$emit("update", encuesta);
        },
        preguntas: function(encuesta) {
            $global.$emit("preguntas", encuesta);
        },
        preview: function(encuesta) {
            $global.$emit("preview", encuesta);
        },
        eliminar: function(encuesta) {
            $global.$emit("eliminar", encuesta);
        },
        duplicar: function(encuesta) {
            $global.$emit("duplicar", encuesta);
        },
        estado: function(encuesta) {
            $global.$emit("estado", encuesta);
        },
        sinEncuesta: function(encuesta) {
            $global.$emit("sinEncuesta", encuesta);
        },
        configuracion: function(encuesta) {
            $global.$emit("configuracion", encuesta);
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
                    ajaxUrl: APP.url('academico/encuesta/editor/list'),
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
                    dynatableRow.encuesta = records[i];
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

