var DynatableRowTemplate = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function () {
        return {cicloAcademico: []};
    },
    methods: {
        eliminar: function (id) {
            $global.$emit("eliminar", id);
        },
        editar: function (id) {
            $global.$emit("editar", id);
        },
        cerrarCiclo: function (id) {
            $global.$emit("cerrarCiclo", id);
        },
        activarCiclo: function (id) {
            $global.$emit("activarCiclo", id);
        },
        desactivarCiclo: function (id) {
            $global.$emit("desactivarCiclo", id);
        },
        anularCiclo: function (id) {
            $global.$emit("anularCiclo", id);
        },
        pendienteCiclo: function (id) {
            $global.$emit("pendienteCiclo", id);
        },
        configurarCiclo: function (id) {
            $global.$emit("configurarCiclo", id);
        },
        visible(item) {
            $.ajax({
                method: 'POST',
                url: APP.url('academico/cicloacademico/changeVisiblelogin'),
                contentType: "application/json",
                data: JSON.stringify(item),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        }
    }
});

let  dynatable = null;

Vue.component("dynatable", {
    template: "#dynatableTemplate",
    mounted: function () {
        var vue = this;
        vue.createDynatable();
    },
    methods: {
        createDynatable: function () {
            var vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/cicloacademico/list'),
                    perPageDefault: 10
                },
                writers: {_rowWriter: vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function (e) {
                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    dynatableRowTemplate.cicloAcademico = records[i];
                    var component = dynatableRowTemplate.$mount();
                    $('#dynaTbody').append(component.$el);
                }
            }).data('dynatable');
        },
        writter: function (rowIndex, record, columns, cellWriter) {


            return "";
        }
    }
});
