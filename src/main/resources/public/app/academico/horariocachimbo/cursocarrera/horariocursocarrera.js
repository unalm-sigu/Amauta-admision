$(function () {

    var $global = new Vue({});

    var ItemCursoTemplate = Vue.component("itemCurso", {
        template: "#itemCursoTemplate",
        data: function () {
            return {curso: [], total: 0};
        },
        methods: {
            deleteItem(id) {
                $global.$emit("deleteItem", id);
            },
        }
    });

    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {curso: []};
        },
        methods: {
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
                        ajaxUrl: APP.url('academico/horariocachimbo/curso/list'),
                        perPageDefault: 8,
                        ajaxData: {id: $vue.curso},
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function (e) {
         
                    var records = dynatable.settings.dataset.records;
                    for (var i = 0, max = records.length; i < max; i++) {
                        var dynatableRowTemplate = new DynatableRowTemplate();
                        dynatableRowTemplate.curso = records[i];
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
            curso: {},
            cursos: [{id: null, creditos: null}],
            total: 0,
            carrera: {},
            addCursoCarreraModal: {
                id: 'modalAddCursoCarrera',
                header: true,
                title: 'Agregar Curso',
                okbtn: 'Agregar Curso'
            },
        },
        created() {
            let $vue = this;
        },
        mounted: function () {
            let $vue = this;
            $global.$on("eliminar", function (id) {
                $vue.eliminar(id);
            });
            $global.$on("deleteItem", function (id) {
                $vue.deleteItem(id);
            });
        },
        methods: {
            filtrarCurso(e) {
                var vue = this;
                var self = $(e.currentTarget);
                var carrera = self.attr('rel');

                e.preventDefault();
                var div = self.closest("div");
                var classColor = 'bg-light';
                var tieneBgColor = div.hasClass(classColor);
                dynatable.queries.remove("car.id");

                if (vue.divElegido != null) {
                    vue.divElegido.removeClass(classColor);
                    vue.divElegido = null;
                }

                if (!tieneBgColor) {
                    div.addClass(classColor);
                    vue.divElegido = div;
                    dynatable.queries.add("car.id", carrera);
                }
                dynatable.process();
            },
            nuevo() {
                var vue = this;
                vue.cursos = [];
                vue.total = 0;
                this.$refs.modalAddCursoCarrera.open();
                $('#formCursoCarrera').parsley().destroy();
                $('[name="curso.id"]').select2(vue.selectCurso(vue)).on("change.select2", function (e) {
                    if (e && e.removed) {
                        if (e.val == '') {
                            vue.curso = [];
                        }
                    }
                });
                $('[name="carrera.id"]').select2({
                    allowClear: true,
                    placeholder: "Seleccione un carrera",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url("academico/horariocachimbo/curso/searchCarrera"),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function (response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function (info) {
                        var data = '<span class="h5 block bold">' + info.nombre + '</span>';
                        data += '<span class="block"> Facultad de ' + info.facultad + '</span>';
                        return data;
                    },
                    formatSelection: function (info) {
                        vue.carrera = info;
                        return info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                }).on("change.select2", function (e) {
                    if (e && e.removed) {
                        if (e.val == '') {
                            vue.carrera = [];
                        }
                    }
                });

                $('[name="curso.id"]').select2('data', '');
                $('[name="carrera.id"]').select2('data', '');

                vue.curso = [];
                vue.carrera = [];

                $('#tableCurso tbody tr').not(':first').remove();

            },
            selectCurso(self) {
                var vue = this;
                return {
                    allowClear: true,
                    placeholder: "Seleccione un curso",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url("academico/horariocachimbo/curso/searchCurso"),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term, page: page};
                        },
                        results: function (response, page) {
                            return {results: response.data};
                        }
                    },
                    initSelection: function (element, callback) {
                        if (element.val() != "") {
                            var datos = {
                                id: element.val(),
                                nombre: element.attr("rel")
                            };
                            callback(datos);
                        }
                    },
                    formatResult: function (info) {
                        var data = '<span class="h5 block bold">' + info.nombre + '</span>';
                        data += '<span class="block">Dep. Academico  ' + info.departamentoAcademico + '</span>';
                        data += '<span class="text-sm block"> Código ' + info.codigo + ' T.P.C ' + info.codigo + '</span>';
                        return data;
                    },
                    formatSelection: function (info) {
                        self.curso = info;
                        vue.total = info.creditos + vue.total;
                        return info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                };
            },
            createCursoCarrera(id) {
                var vue = this;
                var valid = $('#formCursoCarrera').parsley().validate();
                if (valid != true) {
                    return;
                }
                $.ajax({
                    method: 'POST',
                    url: APP.url('academico/horariocachimbo/curso/addCurso'),
                    data: $('#formCursoCarrera').serialize(),
                    success: function (response) {
                        if (response.success) {
                            vue.$refs.modalAddCursoCarrera.close();
                            dynatable.process();
                        } else {
                            notify(response.message, 'error');
                        }
                    }, error: function () {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                });
            },
            eliminar(id) {
                var $vue = this;
                bootbox.confirm({
                    message: '¿Seguro que desea eliminar el curso?',
                    buttons: {
                        confirm: {label: 'Si, eliminar', className: "btn-danger"},
                        cancel: {label: 'Cancelar', className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                method: 'POST',
                                url: APP.url('academico/horariocachimbo/curso/delete'),
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
            },
            agregarItem() {
                var vue = this;
                var curso = {id: null, creditos: null};

                var itemCursoTemplate = new ItemCursoTemplate();
                itemCursoTemplate.curso = curso;
                var component = itemCursoTemplate.$mount();
                $('#tableCurso tbody').append(component.$el);
                $('#tableCurso tbody tr:last').find('.cursoItem').select2(vue.selectCurso(itemCursoTemplate)).on("change.select2", function (e) {
                    if (e && e.removed) {
                        if (e.val == '') {
                            itemCursoTemplate.curso = [];
                        }
                    }
                });
            },
            deleteItem(e) {
                var self = $(e.currentTarget);
                var tr = self.closest('tr');
                tr.remove();
            }
        }
    });
});
