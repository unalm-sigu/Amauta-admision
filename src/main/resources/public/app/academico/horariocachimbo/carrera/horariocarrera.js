$(function () {

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        props: ["menupadre"],
        components: {
            dynatableRowTemplate: {
                template: "#dynatableRowTemplate",
                methods: {
                    deleteItem() {
                        $global.$emit("deleteItem", $(this).attr("rel"));
                    },
                    updateItem() {
                        $global.$emit("updateItem", $(this).attr("rel"));
                    }
                }
            }
        },
        mounted: function () {
            var $vue = this;
            $vue.createDynatable();
        },
        methods: {
            createDynatable: function () {
                var $vue = this;
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horario/list'),
                        perPageDefault: 4,
                        ajaxData: {id: $vue.menupadre},
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "div"}
                }).data('dynatable');

                $("body").delegate(".deleteItem", "click", function () {
                    $global.$emit("deleteItem", $(this).attr("rel"));
                });
                $("body").delegate(".updateItem", "click", function () {
                    $global.$emit("updateItem", $(this).attr("rel"));
                });
            },
            writter: function (rowIndex, record, columns, cellWriter) {
                record.index = rowIndex;
                var html = $.templates("#dynatableRowTemplate").render(record);
                return $(html).prop('outerHTML');



            }
        }
    });

    new Vue({
        el: '#main',
        data: {
            curso: {}
        },
        created() {
            let $vue = this;
        },
        methods: {
            labeled(estado) {
                return estado == 'ACTIVO' ? 'label-success' : 'label-danger';
            },
            nuevo() {

                this.curso = {id: null, nombre: '', codigo: '', estado: 'INACTIVO'};
                var mimodal = bootbox.confirm({
                    title: "Nuevo Tipo Grupo Horas",
                    message: APP.template.spincenter,
                    buttons: {
                        confirm: {label: "Guardar", className: "btn-info"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            if (mimodal.find('form').parsley().validate() == true) {
                                console.log(mimodal.find('form').parsley().validate());
                                TipoGrupo.saveTipoGrupo(mimodal);
                            }
                        } else {
                            mimodal.modal('hide');
                        }
                        return false;
                    }
                });
                $.ajax({
                    url: APP.url('academico/horario/nuevo'),
                    type: 'POST',
                    async: true,
                    success: function (response) {
                        if (response.success) {
                            mimodal.find('.bootbox-body').html(response.data);
                            mimodal.find('[name="tipoCiclo"]').select2({minimumResultsForSearch: -1});
                        } else {
                            notify(response.message, "error");
                            mimodal.modal('hide');
                        }
                    },
                    error: function () {
                        mimodal.modal('hide');
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });


            },
            update(item) {
                this.curso = item;
                $("#myModal").modal('show');
            },
            save() {
                let $vue = this;
                console.log($vue.curso);
                $vue.cursos.push($vue.curso);
                $("#myModal").modal('hide');
            },
            remove(item) {
                let $vue = this;
                console.log(JSON.stringify(item));
                $vue.cursos.pop(item);
            }
        }
    });

});
