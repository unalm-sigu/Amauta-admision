//$(function () {
//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('oficinas/matricula/restriccionmatricula/list'),
//            perPageDefault: 10
//        },
//        writers: {
//            _rowWriter: ulWriter
//        },
//        table: {
//            bodyRowSelector: 'tbody tr'
//        }
//    }).data('dynatable');
//
//    function ulWriter(rowIndex, record, columns, cellWriter) {
//        var label = {'Levantado': 'success', 'Restringido': 'warning', 'Anulado': 'primary'};
//        record.label = label[record.estado];
//        record.index = rowIndex;
//        record.editable = record.estado !== "Anulado" && record.estado !== "Levantado";
//        var html = $.templates("#templateDeudaAlumno").render(record);
//        return html;
//    }
//
//    DeudaAlumno = {
//        verModalAnular: function ($this, e) {
//            e.preventDefault();
//            var tr = $this.closest("tr");
//            var idx = tr.attr("rel");
//            var rec = dynatable.settings.dataset.records[idx];
//
//            MODAL.init("md");
//            MODAL.title("Anular restricción");
//            MODAL.body($.templates("#divAnular").render(rec));
//            MODAL.buttons('<a href="#" id="btnAnular" class="btn btn-primary">Guardar</a>');
//            MODAL.show();
//        },
//        verModalEditar: function ($this, e) {
//            e.preventDefault();
//            var tr = $this.closest("tr");
//            var idx = tr.attr("rel");
//            var rec = dynatable.settings.dataset.records[idx];
//
//            MODAL.init("md");
//            MODAL.title("Editar restricción");
//            MODAL.body($.templates("#divEditar").render(rec));
//            MODAL.buttons('<a href="#" id="btnEditar" class="btn btn-primary">Guardar</a>');
//            MODAL.show();
//            $("[name='descripcion']").val(rec.descripcion);
//        },
//        anular: function () {
//            var form = MODAL.getBody().find("[name='formAnular']");
//            form.parsley().destroy();
//            form.parsley();
//            if (!form.parsley().validate()) {
//                return;
//            } $.ajax({
//                url: APP.url('oficinas/matricula/restriccionmatricula/anular'),
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
//                    notify(Messages.errorComunicacion, "error");
//                }
//            });
//            console.log(form.serialize());
//           
//        },
//        levantar: function ($this, e) {
//            e.preventDefault();
//            var tr = $this.closest("tr");
//            var idx = tr.attr("rel");
//            var rec = dynatable.settings.dataset.records[idx];
//
//            bootbox.confirm({
//                message: "¿Está seguro que desea levantar la restricción?",
//                buttons: {
//                    confirm: {label: "Sí, seguro", className: "btn-info"},
//                    cancel: {label: "No", className: "btn-link"}
//                },
//                callback: function (result) {
//                    if (!result) {
//                        return;
//                    }
//                    $.ajax({
//                        url: APP.url('oficinas/matricula/restriccionmatricula/levantar'),
//                        type: 'POST',
//                        async: true,
//                        data: {id: rec.id},
//                        success: function (response) {
//                            if (response.success) {
//                                MODAL.hide();
//                                notify(response.message, "info");
//                                dynatable.process();
//                            } else {
//                                notify(response.message, "error");
//                            }
//                        },
//                        error: function () {
//                            notify(Messages.errorComunicacion, "error");
//                        }
//                    });
//                }
//            });
//        },
//        guardar: function () {
//            var form = MODAL.getBody().find("[name='formEditar']");
//            form.parsley().destroy();
//            form.parsley();
//            if (!form.parsley().validate()) {
//                return;
//            }
//            console.log(form.serialize());
//            $.ajax({
//                url: APP.url('oficinas/matricula/restriccionmatricula/guardar'),
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
//                    notify(Messages.errorComunicacion, "error");
//                }
//            });
//        },
//        loadDeuda: function () {
//            var oficina = $("#oficina").val();
//            dynatable.queries.add("oficina", oficina);
//            dynatable.process();
//        }
//    };
//
//    $("body").delegate(".anular", "click", function (e) {
//        DeudaAlumno.verModalAnular($(this), e);
//    });
//
//    $("body").delegate(".levantar", "click", function (e) {
//        DeudaAlumno.levantar($(this), e);
//    });
//
//    $("body").delegate(".editar", "click", function (e) {
//        DeudaAlumno.verModalEditar($(this), e);
//    });
//
//    $("body").delegate("#btnEditar", "click", function (e) {
//        DeudaAlumno.guardar();
//    });
//
//    $("body").delegate("#btnAnular", "click", function (e) {
//        DeudaAlumno.anular();
//    });
//
//    $("#oficina").change(function () {
//        DeudaAlumno.loadDeuda();
//    });
//
//    DeudaAlumno.loadDeuda();
//
//});
Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#restriccionVUE',
    data: {
        deudasURL: APP.url('oficinas/matricula/restriccionmatricula/list'),
        oficinas: JSON.parse(oficinasJson),
        modalEdit: {
            id: 'modalEdit',
            title: 'Actualizar',
            header: true,
            okbtn: "Actualizar",
            showaccept: true
        },
        modalAnular: {
            id: 'modalAnular',
            header: true,
            title: 'Anular',
            okbtn: "Anular",
            showaccept: true
        },
        modalNuevo: {
            id: 'modalNuevo',
            header: true,
            title: 'Nuevo',
            okbtn: "Guardar",
            showaccept: true
        },
        oficina: null,
        temp: {},
        alumnos: []
    },
    mounted: function () {
        $(".numeric").numeric({negative: false});
    },
    computed: {

    },
    methods: {
        guardar() {

        },
        loadAlumno(nombre) {
            let $vue = this;
            this.isLoading = true

            if (nombre != '' || nombre != null || nombre != undefined) {

                $.ajax({
                    url: APP.url("academico/matriculable/allAlumnoByNombre"),
                    dataType: 'json',
                    type: 'post',
                    data: {nombre: nombre}
                }).then(response => {
                    if (response.success) {
                        $vue.alumnos = response.data;
                    }

                    this.isLoading = false;
                })

            }
        },
        customLabel( {persona, codigo}){
            if (persona != null) {
                return  codigo + " - " + persona.nombreCompleto;
            }
            return "";
        },
        findbyOficina(item, idx) {
            let $vue = this;
            if (idx == 1) {
                $vue.$refs.load.querie.push({name: 'oficina', value: item.id});
                $vue.$refs.load.repreload();
            } else {
                $vue.$refs.load.querie = [];
                $vue.$refs.load.loadRemoteData();
            }
        },
        modal(item, idx) {
            let $vue = this;
            $vue.temp = Object.assign({}, item);
            if (idx == 1) {
                $vue.$refs.modalEdit.open();
            } else if (idx == 2) {
                $vue.$refs.modalAnular.open();
            } else {
                $vue.$refs.modalNuevo.open();
            }
        },
        editar() {
            let $vue = this;
            var form = $("#formEdit");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/guardar'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        MODAL.hide();
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalEdit.close();
        },
        levantar(item) {
            let $vue = this;

            bootbox.confirm({
                message: "¿Está seguro que desea levantar la restricción?",
                buttons: {
                    confirm: {label: "Sí, seguro", className: "btn-info"},
                    cancel: {label: "No", className: "btn-link"}
                },
                callback: function (result) {
                    if (!result) {
                        return;
                    }
                    $.ajax({
                        url: APP.url('oficinas/matricula/restriccionmatricula/levantar'),
                        type: 'POST',
                        async: true,
                        data: {id: item.id},
                        success: function (response) {
                            if (response.success) {
                                notify(response.message, "info");
                                $vue.$refs.load.loadRemoteData();
                            } else {
                                notify(response.message, "error");
                            }
                        },
                        error: function () {
                            notify(Messages.errorComunicacion, "error");
                        }
                    });
                }
            });
        }, anular() {
            let $vue = this;
            var form = $("#formAnular");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/anular'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalAnular.close();
        },
        getClass(item) {
            switch (item) {
                case "REST":
                    return "label-danger"
                    break;
                case "LEV":
                    return "label-success"
                    break;
                case "ANU":
                    return "label-primary"
                    break;
                case "POST":
                    return "label-warning"
                    break;
            }
        },
        save() {
            let $vue = this;
            var form = $("#formNuevo");
            if (!form.parsley().validate()) {
                return;
            }
            $.ajax({
                url: APP.url('oficinas/matricula/restriccionmatricula/save'),
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify($vue.temp),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        $vue.$refs.load.loadRemoteData();
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
            $vue.$refs.modalNuevo.close();
        }
    }
});