
var $global = new Vue({});
var DynatableRowTemplate = Vue.component("dynatableRow", {
    template: "#dynatableRowTemplate",
    data: function () {
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
    mounted: function () {
        var $vue = this;
        $vue.createDynatable();
    },
    methods: {
        createDynatable: function () {
            var $vue = this;
            dynatable = $('#dynaTable').dynatable({
                dataset: {
                    ajaxUrl: APP.url('academico/becado/alumno/list'),
                    perPageDefault: 8,
                    ajaxData: {id: $vue.curso},
                },
                writers: {_rowWriter: $vue.writter},
                table: {bodyRowSelector: "tbody tr"}
            }).bind("dynatable:afterUpdate", function (e) {

                var records = dynatable.settings.dataset.records;
                for (var i = 0, max = records.length; i < max; i++) {
                    var dynatableRowTemplate = new DynatableRowTemplate();
                    records[i].verFacultad = (records[i].codigoCarrera != records[i].codigoFacultad);
                    dynatableRowTemplate.becado = records[i];
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
        becado: {id: null, alumno: {persona: {}}, cicloBeca: {}, universidad: {}, paisDestino: {}},
        addAlumnoBecadoModal: {
            id: 'modalAddAlumnoBecado',
            header: true,
            title: 'Agregar Alumno Becado',
            okbtn: 'Guardar Alumno Becado'
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
        $global.$on("editar", function (id) {
            $vue.editar(id);
        });
    },
    methods: {
        editar(id) {
            var vue = this;
            $(location).attr('href', APP.url('academico/becado/alumno/' + id + '/update'));

            return;

            //var vue = this;
            vue.becado = {id: null, alumno: {persona: {}}, cicloBeca: {}, universidad: {}, paisDestino: {}};

            $.ajax({
                url: APP.url('academico/becado/alumno/update'),
                type: 'POST',
                async: false,
                data: {id: id},
                success: function (response) {
                    if (response.success) {
                        vue.becado = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });

            vue.$refs.modalAddAlumnoBecado.open();

            $('#formAlumnoBecado').parsley().destroy();
            $('[name="alumno.id"]').select2(vue.buscarAlumno());
            $('[name="paisDestino.id"]').select2(vue.buscarPais());
            $('[name="universidadDestino.id"]').select2(vue.buscarUniversidad());
            $('[name="cicloBeca.id"]').select2({minimumResultsForSearch: -1});
            $('[name="monto"]').numeric();

            $('[name="alumno.id"]').select2('data', {
                id: vue.becado.alumno.id,
                nombre: vue.becado.alumno.persona.nombreCompleto
            }).trigger("change");

            $('[name="paisDestino.id"]').select2('data', {
                id: vue.becado.paisDestino.id,
                nombre: vue.becado.paisDestino.nombre
            }).trigger("change");

            $('[name="universidadDestino.id"]').select2('data', {
                id: vue.becado.universidadDestino.id,
                nombre: vue.becado.universidadDestino.nombre
            }).trigger("change");

            $('[name="cicloBeca.id"]').select2('data', {
                id: vue.becado.cicloBeca.id,
                nombre: vue.becado.cicloBeca.descripcion
            }).trigger("change");

        },
        nuevo() {
            var vue = this;
            this.$refs.modalAddAlumnoBecado.open();
            vue.becado = {id: null, alumno: {persona: {}}, cicloBeca: {}, universidad: {}, paisDestino: {}};
            $('#formAlumnoBecado').parsley().destroy();

            $('[name="alumno.id"]').select2(vue.buscarAlumno());
            $('[name="paisDestino.id"]').select2(vue.buscarPais());
            $('[name="universidadDestino.id"]').select2(vue.buscarUniversidad());
            $('[name="cicloBeca.id"]').select2({minimumResultsForSearch: -1});
            $('[name="monto"]').numeric();

            $('[name="alumno.id"]').select2('val', '');
            $('[name="paisDestino.id"]').select2('val', '');
            $('[name="universidadDestino.id"]').select2('val', '');
            $('[name="cicloBeca.id"]').select2('val', '');

        },
        buscarAlumno() {
            var vue = this;
            return {
                allowClear: true,
                placeholder: "Seleccione un alumno",
                minimumInputLength: 1,
                ajax: {
                    url: APP.url("academico/becado/alumno/searchAlumno"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return $.templates("#divBuscarAlumno").render(info);
                },
                formatSelection: function (info) {
                    vue.becado.alumno = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarUniversidad: function () {
            var vue = this;
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allUniversidad"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.becado.universidad = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        buscarPais: function () {
            var vue = this;
            return {
                minimumInputLength: 2,
                ajax: {
                    url: APP.url("comun/buscar/allPaises"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {nombre: term, page: page};
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return info.nombre + " | " + info.codigo;
                },
                formatSelection: function (info) {
                    vue.becado.paisDestino = info;
                    return info.nombre;
                },
                escapeMarkup: function (m) {
                    return m;
                }
            };
        },
        createAlumnoBecado() {

            var vue = this;
            var valid = $('#formAlumnoBecado').parsley().validate();

            if (valid != true) {
                console.log('no valido');
                console.log(valid);
                return;
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/becado/alumno/save'),
                data: $('#formAlumnoBecado').serialize(),
                success: function (response) {
                    if (response.success) {
                        vue.$refs.modalAddAlumnoBecado.close();
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
                message: '¿Seguro que desea eliminar el alumno becado?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url('academico/becado/alumno/delete'),
                            data: {id: id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
                                    dynatable.process();
                                } else {
                                    notify(response.message, 'error');
                                }
                            },
                            error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },

    }
});
