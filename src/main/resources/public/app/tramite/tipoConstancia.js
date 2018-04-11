Vue.component("multiselect", window.VueMultiselect.default)
let $dynatable = null;
$('#dynaTable').dynatable({});


Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.listTipoDocumento = [];
        $vue.createDynatable();
        $global.$on("reloadDyntable", function () {
            $vue.listTipoDocumento = [];
            $dynatable.process();
        });

        $('.dynatable-search').addClass('col-md-2 pull-right');
        $('.dynatable-search').find('input')
                .addClass('form-control input-sm')
                .attr('placeholder', 'Buscar');

        $('multiselect').select2({
            placeholder: {
                id: $vue.oficina, // the value of the option
            }
        });
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    type: 'GET',
                    ajaxUrl: APP.url("tramite/tipoconstancia/list"),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');
            $("body").delegate(".modalUpdate", "click", function () {
                $global.$emit("modalUpdate", $(this).attr("rel"), $vue.listTipoDocumento);
            });

        },
        writter: function (rowIndex, record, columns, cellWriter) {
            let $vue = this;
            $vue.listTipoDocumento.push(record);
            record.index = rowIndex;
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});
new Vue({
    el: '#tipoConstanciaVue',
    data: {
        tipoConstancia: {},
        listTipoDocumento: [],
        isNew: true
    },
    computed: {

    },
    created() {
        let $vue = this;

    },
    mounted: function () {
        let $vue = this;
        $global.$on("modalUpdate", function (id, lista) {
            $vue.modalUpdate(id, lista);
        });

    },
    methods: {
        modalUpdate: function (id, lista) {
            let $vue = this;
            lista.forEach(function (elem) {
                if (id == elem.id) {
                    $vue.tipoConstancia = elem;
                }
            })
            $("#myModal").modal('show');
            $vue.isNew = false;
        },
        nuevo: function () {
            $("#myModal").modal('show');
        },
        update: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.tipoConstancia);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/tipoconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.tipoConstancia),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                        $vue.tipoConstancia = {}
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        save: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.tipoConstancia);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/tipoconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.tipoConstancia),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                        $vue.tipoConstancia = {}
                    }
                }
            });
            $("#myModal").modal('hide');
        }
    }
});
