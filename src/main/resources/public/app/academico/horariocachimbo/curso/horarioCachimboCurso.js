
var $global = new Vue({});
var ItemCursoTemplate = Vue.component("itemCurso", {
    template: "#itemCursoTemplate",
    data: function () {
        return {curso: {}, total: 0};
    },
    methods: {
        deleteItem(id) {
            $global.$emit("deleteItem", id);
        }
    },
    watch: {
        curso: {
            handler: function (after, before) {
                $global.$emit("updateTotalCredito", after, before);
            },
            deep: true,
        }
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
        seleccionarSecciones(id) {
            $global.$emit("seleccionarSecciones", id);
        },
        styleCursoCarrera(curso) {
            if ((curso.oferta > curso.demanda) && curso.demanda != 0) {
                return 'label label-success'
            } else if ((curso.oferta < curso.demanda)) {
                return 'label label-danger'
            } else {
                return 'label label-primary'
            }
        },
        styleCursoCarreraTotal(curso) {
            if ((curso.ofertaTotal > curso.demandaTotal) && curso.demandaTotal != 0) {
                return 'label label-success'
            } else if ((curso.ofertaTotal < curso.demandaTotal)) {
                return 'label label-danger'
            } else {
                return 'label label-primary'
            }
        }
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
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/horariocachimbo/curso/list'),
                    perPageDefault: 10,
                    ajaxData: {id: $vue.curso},
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function (e) {

                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    for (var g = 0; g < records[i].grupos.length; g++) {
                        //records[i].grupos[g].classGpoSeccion = "col-md-" + records[i].grupos[g].cantidadSecciones;
                        for (var s = 0; s < records[i].grupos[g].secciones.length; s++) {
                            var tipo = records[i].grupos[g].secciones[s].tipo;
                            var seleccionado = records[i].grupos[g].secciones[s].seleccionado;
                            var esTCUR = (tipo === "TCUR");
                            var clazz = esTCUR ? "text-warning" : "";
                            clazz += (seleccionado && !esTCUR) ? " text-primary" : "";
                            clazz += (seleccionado) ? " underline bold" : "";
                            records[i].grupos[g].secciones[s].classTipo = clazz;
                        }
                    }
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
        curso: {id: null},
        cursos: [{id: null, creditos: null}],
        total: 0,
        verAnexo: false,
        anexo: "",
        carrera: {},
        grupoSecciones: [],
        checkedSeccion: [],
        todos: 0,
        msgTodos: "Seleccionar todas las claves",
        addCursoCarreraModal: {
            id: 'modalAddCursoCarrera',
            header: true,
            title: 'Agregar Curso',
            okbtn: 'Agregar Curso',
            showaccept: true,
        },
        addSeleccionarClaveModal: {
            id: 'modalAddSeleccionarClave',
            header: true,
            title: 'Seleccionar Claves',
            okbtn: 'Guardar',
            modalsize: 'modal-lg',
            showaccept: true,
        }
    },
    created() {
        let $vue = this;
    },
    mounted: function () {
        let $vue = this;
        $global.$on("eliminar", function (id) {
            $vue.eliminar(id);
        });
        $global.$on("seleccionarSecciones", function (id) {
            $vue.seleccionarSecciones(id);
        });
        $global.$on("deleteItem", function (id) {
            $vue.deleteItem(id);
        });
        $global.$on("updateTotalCredito", function (after, before) {
            $vue.updateTotalCredito(after, before);
        });
    },
    methods: {
        styleMain(sinHorario) {
            console.log(sinHorario)
            if (sinHorario > 0) {
                return 'text-danger'
            }
        },
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

            this.$refs.modalAddCursoCarrera.open();

            $('#formCursoCarrera').parsley().destroy();
            $('#formCursoCarrera [name="curso.id"]').select2(vue.selectCurso(vue)).on("change.select2", function (e) {
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
                    url: APP.url("academico/horariocachimbo/curso/searchcarrera"),
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
                    return $.templates("#divBuscarCarrera").render(info);
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

            $('#formCursoCarrera [name="curso.id"]').select2('data', '');
            $('[name="carrera.id"]').select2('data', '');

            vue.curso = [];
            vue.carrera = [];
            vue.total = 0;
            $('#tableCurso tbody tr').remove();
            vue.agregarItem();
            console.log($('#tableCurso tbody tr:first').find('td:last-child'));
            $('#tableCurso tbody tr:first').find('td:last-child').html('');
        },
        selectCurso(self) {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un curso",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/horariocachimbo/curso/searchcurso"),
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
                    return $.templates("#divBuscarCurso").render(info);
                },
                formatSelection: function (info) {
                    self.curso = info;
                    return info.codigo + " - " + info.curso;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        updateTotalCredito(after, before) {
            var vue = this;
            var newTotal = 0;
            if (!vue.total) {
                vue.total = 0;
            }
            if (before.creditos) {
                newTotal = newTotal - before.creditos;
            }
            if (after.creditos) {
                newTotal = newTotal + after.creditos;
            }
            vue.total = vue.total + newTotal;
        },
        createCursoCarrera(id) {
            var vue = this;
            var valid = $('#formCursoCarrera').parsley().validate();
            if (valid != true) {
                return;
            }
            $.ajax({
                method: 'POST',
                url: APP.url('academico/horariocachimbo/curso/addcurso'),
                data: $('#formCursoCarrera').serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'success');
                        vue.$refs.modalAddCursoCarrera.close();
                        dynatable.process();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
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
                            }, error: function () {
                                notify(Messages.errorComunicacion, "error");
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
            var vue = this;
            var self = $(e.currentTarget);
            var cre = self.attr("rel");
            if (cre != '') {
                vue.total = vue.total - parseInt(cre);
            }
            var tr = self.closest('tr');
            tr.remove();
        },
        seleccionarSecciones(id) {
            var vue = this;
            var record = vue.getRecord(id);
            vue.curso = record;
            vue.todos = 0;
            vue.checkedSeccion = [];
            vue.grupoSecciones = record.grupos;
            vue.msgTodos = "Seleccionar todas las claves";

            for (var grupo of vue.grupoSecciones) {
                for (var secc of grupo.secciones) {
                    if (secc.seleccionado) {
                        vue.checkedSeccion.push(secc.id);
                    }
                }
            }

            vue.$refs.modalAddSeleccionarClave.open();
        },
        getRecord(id) {
            return dynatable.settings.dataset.records.find(item => item.id === id);
        },
        createSeleccionarClave() {
            var vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('academico/horariocachimbo/curso/updateseccioncursocachimbo'),
                data: $('#formSeleccionarClave').serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        dynatable.process();
                        vue.$refs.modalAddSeleccionarClave.close();
                    } else {
                        notify(response.message, 'error');
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        verAnexoSeccion(anexo) {
            var vue = this;
            vue.anexo = anexo;
            vue.verAnexo = true;
        },
        checkarAll(grupoSecciones) {
            var vue = this;
            if (vue.todos == 1) {
                for (var grupo of grupoSecciones) {
                    for (var secc of grupo.secciones) {
                        vue.checkedSeccion.push(secc.id);
                    }
                }
                vue.msgTodos = "Desmarcar todas las claves";
            } else {
                vue.msgTodos = "Seleccionar todas las claves";
                vue.checkedSeccion = [];
            }
        }
    }
});

