var DynatableRow = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function() {
        return {solicitud: {}, rowActive: 0};
    },
    mounted: function() {
        let vue = this;
        $global.$on("cambiarActivo", function(id) {
            vue.cambiarActivo(id);
        });
    },
    methods: {
        eliminar: function(id) {
            $global.$emit("eliminar", id);
        },
        imprimirr: function(solicitud, el) {
            $global.$emit("imprimirr", solicitud, el);
        },
        cancelar: function(id) {
            $global.$emit("cancelar", id);
        },
        seleccionar: function(solicitud) {
            $global.$emit("seleccionar", solicitud);
        },
        cambiarActivo: function(id) {
            let vue = this;
            vue.rowActive = id;
        },
        urll: function(pathh) {
            return APP.url(pathh);
        }
    }
});

let  dynatable = null;

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function() {
        let vue = this;
        vue.createDynatable();
        $("#dynatable-record-count-dynaTable").remove();
    },
    methods: {
        createDynatable: function() {
            let vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('tramite/solicitudconstancia/updatehistorial/list'),
                },
                writers: {_rowWriter: vue.writter},
                table: {bodyRowSelector: "tbody tr"},
                features: {
                    paginate: false,
                    search: false
                }
            }).bind("dynatable:afterUpdate", function(e) {
                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRow = new DynatableRow();
                    dynatableRow.solicitud = records[i];
                    dynatableRow.rowActive = vue.rowActive;
                    var component = dynatableRow.$mount();
                    $('#dynaTbody').append(component.$el);
                }
            }).data('dynatable');
        },
        writter: function(rowIndex, record, columns, cellWriter) {
            return '';
        }
    }
});