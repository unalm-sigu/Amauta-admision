//$(function () {
//
//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('general/aula/list'),
//            perPageDefault: 10
//        },
//        writers: {
//            _rowWriter: ulWriter
//        },
//        table: {
//            bodyRowSelector: 'tbody tr'
//        }
//    }).bind('dynatable:afterUpdate', function (e, dynatable) {
//        $('[data-toggle="tooltip"]').tooltip();
//    }).data('dynatable');
//
//    function ulWriter(rowIndex, record, columns, cellWriter) {
//        var labelColor = {CRE: 'default', ACT: 'success', INA: 'danger'};
//        record.index = rowIndex;
//
//        record.activar = record.estado == 'ACT' || record.estado == 'CRE';
//        record.esInactivo = record.estado == 'INA';
//        record.colorEstado = labelColor[record.estado];
//        var html = $.templates("#aulaTemplate").render(record);
//        return html;
//    }
//
//    var Aula = {
//        form: null,
//        init: function () {
//
//        },
//        verCambioEstado: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//
//            var record = {
//                form: 'formCambioEstado',
//                id: rec.id,
//                codigo: rec.codigo,
//                seDesactiva: rec.estado == 'ACT',
//                nuevoEstado: (rec.estado == 'ACT') ? 'INA' : 'ACT'
//            };
//
//            MODAL.init("md");
//            if (record.seDesactiva) {
//                MODAL.title("Desactivación de Ambiente");
//                MODAL.buttons('<button type="button" class="btn btn-danger cambio-estado-aula">Desactivar Ambiente</button>');
//            } else {
//                MODAL.title("Activación de Ambiente");
//                MODAL.buttons('<button type="button" class="btn btn-success cambio-estado-aula">Activar Ambiente</button>');
//            }
//            MODAL.body($.templates("#divEstadoAula").render(record));
//            MODAL.show();
//            Aula.form = $("#" + record.form);
//        },
//        cambiarEstado: function (e) {
//            e.preventDefault();
//            var form = Aula.form;
//            if (!form.parsley().validate()) {
//                return;
//            }
//
//            $.ajax({
//                url: APP.url('general/aula/cambioEstado'),
//                type: 'POST',
//                async: true,
//                data: form.serialize(),
//                success: function (response) {
//                    if (response.success) {
//                        MODAL.hide();
//                        notify(response.message, "info");
//                        dynatable.process();
//                    } else {
//                        notify(response.message, "error");
//                    }
//                },
//                error: function () {
//                    notify(GlobalMessages.errorComunicacion, "error");
//                }
//            });
//        },
//        eliminarAula: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            bootbox.confirm({
//                message: '¿Está seguro que desea eliminar el registro del aula <b>' + rec.codigo + '</b>?',
//                buttons: {
//                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
//                    cancel: {label: 'Cancelar', className: 'btn-link'}
//                },
//                callback: function (result) {
//                    if (result) {
//                        $.ajax({
//                            url: APP.url('general/aula/eliminar'),
//                            type: 'POST',
//                            async: true,
//                            data: {id: rec.id},
//                            success: function (response) {
//                                if (response.success) {
//                                    notify(response.message, "info");
//                                    dynatable.process();
//                                } else {
//                                    notify(response.message, "error");
//                                }
//                            },
//                            error: function () {
//                                notify(GlobalMessages.errorComunicacion, "error");
//                            }
//                        });
//                    }
//                }
//            });
//        },
//        verAulasContenido: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            bootbox.alert({
//                message: $.templates("#divAulasContenido").render(rec),
//                buttons: {
//                    ok: {label: 'Aceptar', className: 'btn-primary'}
//                }
//            });
//
//        },
//        verAulasInventario: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            bootbox.alert({
//                message: $.templates("#divAulasInventario").render(rec),
//                buttons: {
//                    ok: {label: 'Aceptar', className: 'btn-primary'}
//                }
//            });
//        },
//        verAulaHorario: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            var idAula = rec.id;
//            var codigo = rec.codigo;
//
//            MODAL.init("lg");
//
//            var aulaHorarioVue = new AulaHorarioVue();
//            aulaHorarioVue.aula = {id: idAula};
//            aulaHorarioVue.dias = [];
//            aulaHorarioVue.horas = [];
//            var component = aulaHorarioVue.$mount();
//
//            MODAL.title("Horario Ambiente " + codigo);
//            MODAL.buttons('');
//            MODAL.body(component.$el);
//            MODAL.show();
//
//        }, editarAula: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            var idAula = rec.id;
//            location.href = APP.url('general/aula/editar/' + idAula);
//        },
//        descargaPDF: function (e, $this) {
//            e.preventDefault();
//            var rec = APP.recDynatable(dynatable, e);
//            var idAula = rec.id;
//            var aulaSuperior = rec.idpabellon;
////            console.log(idAula);
////            console.log(aulaSuperior);
//
//            var now = moment();
//            var day = now.day();
//            var first = parseInt(day) - 1;
//            var init = now.add(-first, 'days').format('DD/MM/YYYY');
//            var end = now.add(6, 'days').format('DD/MM/YYYY');
//
//            $.fileDownload("/general/aula/generatorpdf", {
//                httpMethod: "POST",
//                data: {
//                    strAula: JSON.stringify({id: idAula}),
//                    strAulaSuperior: JSON.stringify({id: aulaSuperior}),
//                    fechaInicio: init,
//                    fechaFin: end
//                },
//                successCallback: function (responseHtml, url) {
////                    console.log('aqui');
//                },
//                onFail: function (e) {
//                    console.log(e);
//                },
//                failCallback: function (responseHtml, url) {
//                    notify(GlobalMessages.errorComunicacion, 'error')
//                }
//            });
//        }
//    };
//
//    Aula.init();
//
//    $("body").delegate(".change-estado", "click", function (e) {
//        Aula.verCambioEstado(e, $(this));
//    });
//
//    $("body").delegate(".cambio-estado-aula", "click", function (e) {
//        Aula.cambiarEstado(e);
//    });
//
//    $("body").delegate(".eliminar-aula", "click", function (e) {
//        Aula.eliminarAula(e, $(this));
//    });
//
//    $("body").delegate(".ver-aulas-contenido", "click", function (e) {
//        Aula.verAulasContenido(e, $(this));
//    });
//
//    $("body").delegate(".ver-aula-horario", "click", function (e) {
//        Aula.verAulaHorario(e, $(this));
//    });
//
//    $("body").delegate(".ver-contenido-aula", "click", function (e) {
//        Aula.verAulasInventario(e, $(this));
//    });
//    $("body").delegate(".editarAula", "click", function (e) {
//        Aula.editarAula(e, $(this));
//    });
//    $("body").delegate(".descarga-pdf", "click", function (e) {
//        Aula.descargaPDF(e, $(this));
//    });
//
//});


//Vue.component("multiselect", window.VueMultiselect.default)
//Vue.component('date-picker', VueBootstrapDatetimePicker.default);
Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#aulaVUE',
    data: {
        aulasURL: APP.url(rutaModulo + '/list'),
        aula: {},
        tipoAulas: JSON.parse(tipoAulasJson),
        tipoAula: null,
        modalCambioEstado: {
            id: 'modalCambioEstado',
            header: true,
            okbtn: 'Guardar',
            showaccept: true,
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            form: "formCambioPeso"
        },
        modalAulaContenido: {
            id: 'modalAulaContenido',
            header: true,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
        },
        modalInventario: {
            id: 'modalInventario',
            header: true,
            cancelbtn: 'Cerrar',
            cancelclass: 'btn btn-link',
        }
    },
    computed: {
    },
    mounted: function () {
        let $vue = this;
        let tipo = $vue.$refs.raptorAulas.getParameterByName('queries[tipo-aula]');
        if (tipo != null) {
            $vue.tipoAula = {codigo: tipo};
            $vue.$refs.raptorAulas.querie.push({name: 'tipo-aula', value: tipo});
        }
        $vue.$refs.raptorAulas.loadRemoteData();
    },
    methods: {
        verHorarioAula(item) {
            MODAL.init("lg");

            var aulaHorarioVue = new AulaHorarioVue();
            aulaHorarioVue.aula = {id: item.id};
            aulaHorarioVue.dias = [];
            aulaHorarioVue.horas = [];
            var component = aulaHorarioVue.$mount();

            MODAL.title("Horario Ambiente " + item.codigo);
            MODAL.buttons('');
            MODAL.body(component.$el);
            MODAL.show();
        },
        verAulas(item) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.openModal("aulaContenido");
        }, changeTipoAula() {
            let $vue = this;
            $vue.$refs.raptorAulas.querie = [];
            if ($vue.tipoAula != null) {
                $vue.$refs.raptorAulas.querie.push({name: 'tipo-aula', value: $vue.tipoAula.codigo});
            }
            $vue.$refs.raptorAulas.loadRemoteData();
        },
        verInventario(item) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.openModal("inventarioContenido");
        },
        cambiarEstado(item, newEstado) {
            let $vue = this;
            $vue.aula = {};
            $vue.aula = Object.assign({}, item);
            $vue.aula.estado = newEstado; //set nuevo estado
            $("#" + $vue.modalCambioEstado.form).parsley().destroy();
            $vue.openModal("cambioEstado");
        },
        editar(item) {
            location.href = APP.url(rutaModulo + '/editar/' + item.id);
        },
        verInventarioPage(item) {
            location.href = APP.url(rutaModulo + '/inventario/' + item.id);
            // /general/aula/inventario/234
        },
        verInventarioResumen(item) {
            location.href = APP.url(rutaModulo + '/inventario/' + item.id + '/resumen');
            //  /general/aula/inventario/35345/resumen
        },
        horarioSemanalPDF(item) {
            var now = moment();
            var day = now.day();
            var first = parseInt(day) - 1;
            var init = now.add(-first, 'days').format('DD/MM/YYYY');
            var end = now.add(6, 'days').format('DD/MM/YYYY');

            $.fileDownload("/general/aula/generatorpdf", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify({id: item.id}),
                    strAulaSuperior: JSON.stringify({id: item.aulaSuperior.id}),
                    fechaInicio: init,
                    fechaFin: end
                },
                successCallback: function (responseHtml, url) {
//                    console.log('aqui');
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(GlobalMessages.errorComunicacion, 'error')
                }
            });
        },
        reporteProgramacion(item) {
            $.fileDownload("/general/aula/reporteProgramacion", {
                httpMethod: "POST",
                data: {
                    strAula: JSON.stringify({id: item.id})
                },
                successCallback: function (responseHtml, url) {
//                    console.log('aqui');
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(GlobalMessages.errorComunicacion, 'error')
                }
            });
        },
        openModal(tipoModal) {
            let $vue = this;

            if (tipoModal == "cambioEstado") {
                $vue.modalCambioEstado.title = ($vue.aula.estado == 'ACT' ? 'Activación de Ambiente' : 'Desactivación de Ambiente');
                $vue.modalCambioEstado.okbtn = "Guardar";
                $vue.$refs.modalCambioEstado.open();
            }

            if (tipoModal == "aulaContenido") {
                $vue.modalAulaContenido.title = $vue.aula.nombre;
                $vue.$refs.modalAulaContenido.open();
            }

            if (tipoModal == "inventarioContenido") {
                $vue.modalInventario.title = $vue.aula.nombre;
                $vue.$refs.modalInventario.open();
            }
        },
        saveCambioEstado() {
            let $vue = this;
            if ($("#" + $vue.modalCambioEstado.form).parsley().validate() !== true) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }

            $vue.aula.aulasContenido = undefined;
            axios.post("/" + rutaModulo + "/cambioEstado", $vue.aula)
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, "info");
                            $vue.$refs.raptorAulas.loadRemoteData();
                            $vue.$refs.modalCambioEstado.close();
                        } else {
                            notify(response.data.message, "warning");
                        }
                    }).catch(e => {
                notify(GlobalMessages.errorComunicacion, "error");
            });
        },
        eliminarAula(item) {
            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea eliminar el registro del aula <b>' + item.codigo + '</b>?',
                buttons: {
                    confirm: {label: 'Si, eliminar', className: 'btn-danger'},
                    cancel: {label: 'Cancelar', className: 'btn-link'}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: APP.url('general/aula/eliminar'),
                            type: 'POST',
                            async: true,
                            data: {id: item.id},
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, "info");
                                    $vue.$refs.raptorAulas.loadRemoteData();
                                } else {
                                    notify(response.message, "error");
                                }
                            },
                            error: function () {
                                notify(GlobalMessages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
    }
});
 