$(function () {

    var $global = new Vue({});
    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {horario: []};
        },
        methods: {
            incluirAlumno(id) {
                $global.$emit("incluirAlumno", id);
            },
            verHorario(id) {
                $global.$emit("verHorario", id);
            },
            verCurso(id) {
                $global.$emit("verCurso", id);
            },
            verAlumno(id) {
                $global.$emit("verAlumno", id);
            },
            eliminar(id) {
                $global.$emit("eliminar", id);
            },
        }
    });

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        mounted: function () {
            var $vue = this;
            $vue.createDynatable();
        },
        methods: {
            createDynatable: function () {
                var $vue = this;
                $('#dynaTable').bind('dynatable:init', function (e, dynatable) {
                    $('.dynatable-search').wrapAll('<div class="row m-b-sm"><div class="col-md-12" id="opopop"/></div>');
                    $('.dynatable-paginate, .dynatable-record-count').wrapAll('<div class="col-md-12"/>');
                    $('.dynatable-search').addClass('col-md-2');
                    $('.dynatable-search').find('input')
                            .addClass('form-control input-sm')
                            .attr('placeholder', 'Buscar');
                });
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/horario/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function (e) {
                    $('.dynatable-paginate li').first().remove();
                    var records = dynatable.settings.dataset.records;
                    for (var i = 0, max = records.length; i < max; i++) {
                        var dynatableRowTemplate = new DynatableRowTemplate();
                        dynatableRowTemplate.horario = records[i];
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

    new Vue({
        el: '#main',
        data: {
            horario: {},
            alumno: {},
            addAlumnoModal: {
                id: 'modalAddAlumno',
                header: 'False',
                tittle: 'Agregar Alumno',
                okbtn: 'Agregar Alumno'
            },
        },
        created() {
            let $vue = this;
        },
        mounted: function () {
            let $vue = this;

            $global.$on("incluirAlumno", function (id) {
                $vue.incluirAlumno(id);
            });
            $global.$on("verHorario", function (id) {
                $vue.verHorario(id);
            });
            $global.$on("verCurso", function (id) {
                $vue.verCurso(id);
            });
            $global.$on("verAlumno", function (id) {
                $vue.verAlumno(id);
            });
            $global.$on("eliminar", function (id) {
                $vue.eliminar(id);
            });
        },
        methods: {
            nuevo() {
                this.$refs.modalAddAlumno.open();
            },
            createAlumno(id) {
                var $vue = this;
                $.ajax({
                    method: 'POST',
                    url: APP.url("academico/horariocachimbo/ingresante/addAlumno"),
                    data: {id: id},
                    success: function (response) {
                        if (response.success) {
                            $vue.reloadDinatable();
                        } else {
                            notify(response.message, 'error');
                        }
                    }
                });
            },
            incluirAlumno(id) {},
            verHorario(id) {},
            verCurso(id) {},
            verAlumno(id) {},
            eliminar(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea eliminar el horario?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/horario/delete'),
                                data: {id: id},
                                success: function (response) {
                                    if (response.success) {
                                        notify(response.message, 'info');
                                        dynatable.process();
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
});
