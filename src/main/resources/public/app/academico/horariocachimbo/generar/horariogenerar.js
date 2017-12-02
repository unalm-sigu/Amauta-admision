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
            }
        }
    });

    let  dynatable = null;

    Vue.component("dynatable", {
        template: "#dynatableTemplate",
        mounted: function () {
            var vue = this;
            vue.createDynatable();
        },
        methods: {
            createDynatable: function () {
                var vue = this;
                $('#dynaTable').bind('dynatable:init', function (e, dynatable) {
                    $('.dynatable-search').wrapAll('<div class="row m-b-sm"><div class="col-md-12" id="opopop"/></div>');
                    $('.dynatable-paginate, .dynatable-record-count').wrapAll('<div class="col-md-12"/>');
                    $('.dynatable-search').addClass('col-md-2');
                    $('.dynatable-search').find('input')
                            .addClass('form-control input-sm')
                            .attr('placeholder', 'Buscar');
                    $('#opopop').append($('#eliminarSeleccion'));
                });
                dynatable = $('#dynaTable').dynatable({
                    dataset: {
                        ajaxUrl: APP.url('academico/horariocachimbo/horario/list'),
                        perPageDefault: 8
                    },
                    writers: {_rowWriter: vue.writter},
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
            horario: {},
            alumno: {},
            addAlumnoModal: {
                id: 'modalAddAlumno',
                header: true,
                title: 'Agregar Alumno',
                okbtn: 'Agregar Alumno'
            }
        },
        created() {
            let vue = this;
            this.seleccionado = true;
        },
        mounted: function () {
            let vue = this;

            $global.$on("incluirAlumno", function (id) {
                vue.incluirAlumno(id);
            });
            $global.$on("verHorario", function (id) {
                vue.verHorario(id);
            });
            $global.$on("verCurso", function (id) {
                vue.verCurso(id);
            });
            $global.$on("verAlumno", function (id) {
                vue.verAlumno(id);
            });
            $global.$on("eliminar", function (id) {
                vue.eliminar(id);
            });
        },
        methods: {
            addAlumno(id) {
                var vue = this;
                $.ajax({
                    method: 'POST',
                    url: APP.url("academico/horariocachimbo/ingresante/addAlumno"),
                    data: {id: id},
                    success: function (response) {
                        if (response.success) {
                            dynatable.process();
                        } else {
                            notify(response.message, 'error');
                        }
                    }
                });
            },
            eliminarSeleccion() {

                var $vue = this;
                var items = $('#dynaTable>tbody').find('input[type="checkbox"]:checked');


                if (items.length < 1) {
                    swal({
                        text: "Tiene que seleccionar por lo menos un horario",
                        icon: "error",
                        dangerMode: true,
                        button: "Aceptar",
                        timer: 2000
                    });
                    return;
                }
                var horarios = {};
                $.each(items, function (i, v) {
                    var indx = 'horarioCachimbos[' + i + '].id';
                    horarios[indx] = $(v).val();
                });

                swal('¿Seguro que desea eliminar los registros seleccionados?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Aceptar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    console.log((value));
                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url('academico/horariocachimbo/horario/deleteGrupo'),
                        data: horarios,
                        success: function (response) {
                            if (response.success) {
                                notify(response.message, 'info');
                                dynatable.process();
                                return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                            } else {
                                notify(response.message, 'error');
                                return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                            return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    });
                }).catch(err => {
                    if (err) {
                        console.log(err);
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });

            },
            incluirAlumno(id) {
                
                this.$refs.modalAddAlumno.open();

                $('[name="alumno.id"]').select2({
                    allowClear: true,
                    placeholder: "Seleccione un departamento",
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url("comun/buscar/allDepartamentoAcademico"),
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
                        var data = '<span class="block bold">' + info.nombre + '</span>';
                        data += '<span class="block">Facultad de ' + info.facultad + '</span>';
                        return data;
                    },
                    formatSelection: function (info) {
                        return info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                });
                

            },
            verHorario(id) {},
            verCurso(id) {},
            verAlumno(id) {},
            getRecord(id) {
                return dynatable.settings.dataset.records.find(item => item.id === id);
            },
            eliminar(id) {

                var $vue = this;
                var record = this.getRecord(id);

                swal('¿Seguro que desea eliminar el horario de código ' + record.codigo + '?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Aceptar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    $.ajax({
                        method: 'POST',
                        async: false,
                        url: APP.url('academico/horariocachimbo/horario/delete'),
                        data: {id: id},
                        success: function (response) {
                            if (response.success) {
                                notify(response.message, 'info');
                                dynatable.process();
                                return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                            } else {
                                notify(response.message, 'error');
                                return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                            }
                        },
                        error: function () {
                            notify(MESSAGES.errorComunicacion, "error");
                            return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    });
                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });



            }
        }
    });
});
