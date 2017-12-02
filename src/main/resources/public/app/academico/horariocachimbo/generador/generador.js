$(function () {

    var $global = new Vue({});

    Vue.component("autocomplete-doc", {
        template: "#autocomplete-doc",
        props: {
            rel: {
                required: false
            }
        },
        mounted: function () {
            var vm = this
            $(this.$el).select2({
                containerCss: "width:400px !important;",
                containerCssClass: "diegoSelect",
                minimumInputLength: 3,
                ajax: {
                    url: APP.url("academico/gposeccion/buscarDocentes"),
                    dataType: 'json',
                    type: 'post',
                    data: function (term, page) {
                        return {
                            nombre: term,
                            page: page
                        };
                    },
                    results: function (response, page) {
                        return {results: response.data};
                    }
                },
                formatResult: function (info) {
                    return info.apellidosNombres;
                    //$.templates("#divBuscarCurso").render(info);
                },
                formatSelection: function (info) {
                    return info.personaNombre + " " + info.personaPaterno + " " + info.personaMaterno;
                },
                initSelection: function (element, callback) {
                    alert(element.val() + " " + element.attr("rel"));
                    if (element.val() != "") {
                        callback({id: element.val(), apellidosNombres: element.attr("rel")});
                    }
                }, 
                escapeMarkup: function (m) {
                    return m;
                }
            } ).on('select2-selecting', function (e) {
                vm.$emit('input', e.object.id)
            });
        },
        destroyed: function () {
            $(this.$el).off().select2('destroy')
        }
    });

    var DynatableRowTemplate = Vue.component("dynatableRow", {
        template: "#dynatableRowTemplate",
        data: function () {
            return {horario: []};
        },
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
                    $('.dynatable-search').addClass('col-sm-2 col-xs-12');
                    $('.dynatable-search').find('input')
                            .addClass('form-control input-sm')
                            .attr('placeholder', 'Buscar');
                    $('#opopop').append($('.buscar-curso'));
                });
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/horario/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: $vue.writter},
                    table: {bodyRowSelector: "tbody tr"}
                }).bind("dynatable:afterUpdate", function (e) {
                    $('#dynaTable>thead>tr>th>input:checkbox').removeProp('checked');
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
            curso: 0,
        },
        created() {
            let $vue = this;
        },
        mounted: function () {
            let $vue = this;
            $('[name="carrera"]').select2({allowClear: true, placeholder: "Seleccione una carrera"});
        },
        methods: {
            generarHorario(id) {
                console.log('generando hoarrios');
            },
            getRecord(id) {
                return dynatable.settings.dataset.records.find(item => item.id === id);
            }
        }
    });
});
