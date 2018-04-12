Vue.component("multiselect", window.VueMultiselect.default)
let $dynatable = null;
$('#dynaTable').dynatable({});


Vue.component("dynatable", {
    template: "#dynatableTemplate",
    props: ["project", "dynatable"],
    mounted: function () {
        let $vue = this;
        $vue.listCostoDocumento = [];
        $vue.createDynatable();
        $global.$on("reloadDyntable", function () {
            $vue.listCostoDocumento = [];
            $dynatable.process();
        });

        $('.dynatable-search').addClass('col-md-2 pull-right');
        $('.dynatable-search').find('input')
                .addClass('form-control input-sm')
                .attr('placeholder', 'Buscar');

     
    },
    methods: {
        createDynatable: function () {
            let $vue = this;

            $dynatable = $('#dynaTable').dynatable({
                dataset: {
                    type: 'GET',
                    ajaxUrl: APP.url("tramite/costodocuemento/list"),
                    perPageDefault: 10
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).data('dynatable');
            $("body").delegate(".modalUpdate", "click", function () {
                $global.$emit("modalUpdate", $(this).attr("rel"), $vue.listCostoDocumento);
            });
          

        },
        writter: function (rowIndex, record, columns, cellWriter) {
            let $vue = this;
            $vue.listCostoDocumento.push(record);
            record.index = rowIndex;
            var html = $.templates("#dynatableRowTemplate").render(record);
            return $(html).prop('outerHTML');
        }
    }
});
new Vue({
    el: '#tipoConstanciaVue',
    data: {
        tipoConstancia: JSON.parse(tipoDocumentoJson),
        idiomas: JSON.parse(idiomasJson),
        isNew: true,
        isOld: false,
        costoDocumento: {}
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
        $global.$on("checking", function (valor) {
            $vue.checking(valor);
        });

    },
    methods: {
        checking: function(valor) {
            console.log(valor)
        },
        modalUpdate: function (id, lista) {
            let $vue = this;
            lista.forEach(function (elem) {
                if (id == elem.id) {
                    $vue.costoDocumento = elem;
                }
            })
            $("#myModal").modal('show');
            $vue.isNew = false;
        },
        nuevo: function () {
            let $vue = this;
             $vue.isNew = true;
            $vue.costoDocumento = {};
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
            console.log($vue.costoDocumento);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/costodocuemento/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.costoDocumento),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                        $vue.costoDocumento = {}
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
            console.log($vue.costoDocumento);
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/costodocuemento/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.costoDocumento),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                        $vue.costoDocumento = {}
                    }
                }
            });
            $("#myModal").modal('hide');
        }
    }
});
