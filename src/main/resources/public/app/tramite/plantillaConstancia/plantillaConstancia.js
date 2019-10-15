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
            $("body").delegate(".contenido", "click", function () {
                $global.$emit("contenido", $(this).attr("rel"));
            });
            $("body").delegate(".eliminar", "click", function () {
                $global.$emit("eliminar", $(this).attr("rel"));
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
        isNew: true,
        isOld: false
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
        $global.$on("contenido", function (id) {
            $vue.contenido(id);
        });
        $global.$on("eliminar", function (id) {
            $vue.eliminar(id);
        });
    },
    methods: {
        contenido: function (id) {

            location.href = APP.url('tramite/plantillaconstancia/' + id)

        },
        modalUpdate: function (id, lista) {
            let $vue = this;
            lista.forEach(function (elem) {
                if (id == elem.id) {
                    $vue.plantilla = elem;
                }
            })
            $vue.isNew = false;
            $vue.isOld = true;
            console.log($vue.plantilla);
            $("#myModal").modal('show');
        },
        nuevo: function () {
            let $vue = this;
            $vue.isNew = true;
            console.log($vue.isNew);
            $vue.plantilla = {};

            $vue.isNew = true;
            $vue.isOld = false;
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
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                        $vue.plantilla = {};
                        console.log($vue.isNew);
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
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        eliminar: function (id) {

            let $vue = this;
            $vue.plantilla = {id: id}

            var dialog = bootbox.confirm({
                message: "¿Está seguro que desea eliminar la plantilla?",
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                       
                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/plantillaconstancia/delete'),
                            contentType: "application/json",
                            data: JSON.stringify($vue.plantilla),
                            success: function (response) {
                                if (response.success) {
                                    $global.$emit("reloadDyntable");
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                            }
                        });

                    }
                }
            });

        }
    }
});
