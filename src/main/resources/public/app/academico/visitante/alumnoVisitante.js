$(function() {

    var $global = new Vue({});
    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function() {
            return {becado: {id: null, alumno: {}}};
        },
        methods: {
            eliminar(id) {
                $global.$emit("eliminar", id);
            },
            editar(id) {
                $global.$emit("editar", id);
            },
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
                        ajaxUrl: APP.url('academico/visitante/alumno/list'),
                        perPageDefault: 8,
                        ajaxData: {id: $vue.curso},
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function(e) {

                    var records = dynatable.settings.dataset.records;

                    for (var i = 0, max = records.length; i < max; i++) {
                        var dynatableRowTemplate = new DynatableRowTemplate();
                        dynatableRowTemplate.becado = records[i];
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
        },
        created() {
            let $vue = this;
        },
        mounted: function() {
            let $vue = this;
            $global.$on("eliminar", function(id) {
                $vue.eliminar(id);
            });
            $global.$on("editar", function(id) {
                $vue.editar(id);
            });
        },
        methods: {
            editar(id) {
                var vue = this;
            },
            eliminar(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea eliminar el alumno becado?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function(result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/becado/alumno/delete'),
                                data: {id: id},
                                success: function(response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        dynatable.process();
                                    } else {
                                        notify(response.message, 'error');
                                    }
                                },
                                error: function() {
                                    notify(MESSAGES.errorComunicacion, "error");
                                }
                            });
                        }
                    }
                });
            }
        }
    });
});
