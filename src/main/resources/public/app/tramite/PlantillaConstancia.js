Vue.component("multiselect", window.VueMultiselect.default)
Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.listPlantillas = [];
        $vue.createDynatable();
        $global.$on("reloadDyntable", function () {
            $vue.listPlantillas = [];
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
                    ajaxUrl: APP.url("tramite/plantillaconstancia/list"),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');
            $("body").delegate(".modalUpdate", "click", function () {
                $global.$emit("modalUpdate", $(this).attr("rel"), $vue.listPlantillas);
            });
        },
        writter: function (rowIndex, record, columns, cellWriter) {
            let $vue = this;
            $vue.listPlantillas.push(record);
            record.index = rowIndex;
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});
new Vue({
    el: '#colaboradorVue',
    data: {
        tipoConstancia: JSON.parse(tipoDocumentoJson),
        idiomas: JSON.parse(idiomasJson),
        plantilla: {},
        isNew: true
    },
    computed: {

    },
    created() {
        let $vue = this;
        console.log($vue.tipoConstancia);
        console.log($vue.idiomas);
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
                    $vue.plantilla = elem;
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
            console.log($vue.plantilla);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');

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
            console.log($vue.plantilla);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');

                    }
                }
            });
            $("#myModal").modal('hide');
        }
    }
});
