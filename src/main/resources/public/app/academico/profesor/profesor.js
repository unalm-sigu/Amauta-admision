$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('academico/profesor/all'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter

        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).bind('dynatable:afterUpdate', function (e, dynatable) {
        $("#opopop").prepend($("#headDynatable"));
        $('#headDynatable').removeClass('hide');
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        record.index = rowIndex;
        var colorEstado = {ACT: "success", INA: "default"};
        var nameEstado = {ACT: "Activo", INA: "Inactivo"};

        record.colorEstado = colorEstado[record.estado];
        record.nameEstado = nameEstado[record.estado];

        var html = $.templates("#profesorTemplate").render(record);
        return $(html).prop('outerHTML');
    }

    var Docente = {
        init: function () {
            $("#departamento").select2({
                placeholder: "Todos los departamentos",
                allowClear: true
            });
        },
        body: $("body"),
        form: {},
        estado: function (e) {

            e.preventDefault();
            var self = $(e.currentTarget);
            var estado = self.attr('rev');
            var id = self.attr('rel');

            Docente.form.id = id;

            var mimodal = bootbox.confirm({
                title: "Cambiar Estado",
                size: 'md',
                message: '¿Seguro que desea cambiar el estado del docente?',
                buttons: {
                    confirm: {label: "Sí, aceptar", className: "btn-info"},
                    cancel: {label: "Cancelar", className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        Docente.saveEstado(mimodal);
                    } else {
                        mimodal.modal('hide');
                    }
                    return false;
                }
            });
        },
        saveEstado: function (mimodal) {
            $.ajax({
                url: APP.url('academico/profesor/estado'),
                type: 'POST',
                async: true,
                data: Docente.form,
                success: function (response) {
                    if (response.success) {
                        dynatable.process();
                        notify(response.message, "success");
                        mimodal.modal('hide');
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    mimodal.modal('hide');
                    notify(Messages.errorComunicacion, "error");
                }
            });
        },
        loadDepartamento: function () {
            var dpto = $("#departamento").val();
            if (dpto == null || dpto == "") {
                dynatable.queries.remove("departamento");
                dynatable.process();
                return;
            }
            dynatable.queries.add("departamento", dpto);
            dynatable.process();

        }
    };

    Docente.init();

    Docente.body.delegate(".estado", "click", function (e) {
        Docente.estado(e);
    });

    Docente.body.delegate(".reporte", "click", function (e) {
        var dpto = $("#departamento").val();
        console.log(dpto);
        if (dpto == null || dpto == "") {
            notify("Seleccione el departamento");
            return;
        }
        $.fileDownload("/academico/profesor/reporteEntregaMateriales", {
            httpMethod: "POST",
            data: {departamento: dpto},
            successCallback: function (responseHtml, url) {

            },
            onFail: function (e) {
                console.log(e);
            },
            failCallback: function (responseHtml, url) {
                notify(Messages.errorComunicacion, 'error')
            }
        });
    });

    $("#departamento").change(function () {
        Docente.loadDepartamento();
    });

});


Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#mainvue',
    data: {
        urlfilter: APP.url("tramite/aula/list"),
        facultades: JSON.parse(jFacultades),
        departamentos: JSON.parse(jDepartamentos),
        departamentosSelectos: [],
        facultad: null,
        departamento: null,
        tipoGrado: null,
        grados: [{id: 'PRE', nombre: 'Solo pregrado'}, {id: 'EPG', nombre: 'Solo posgrado'}]
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        openModalReporte: function () {
            let vue = this;
            vue.$refs.modalReporte.open();
        },
        downloadReporte() {
            let vue = this;
            let data = {};
            if (vue.facultad != null) {
                data = {params: {facultad: vue.facultad.id}};
            }

            axios_blob.get("/academico/profesor/reporteEntregaMateriales", data)
                    .then(response => {
                        UTIL_BLOB.save(response);
                        vue.$refs.modalReporte.close();
                    }, () => {
                        vue.$refs.modalReporte.stop();
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        openModalReporteCargaAcademica() {
            let vue = this;
            vue.$refs.modalReporteCargaAcademica.open();
        },
        downloadReporteCargaAcademica() {

            let vue = this;
            let data = {params: {
                    departamento: vue.departamento ? vue.departamento.id : '',
                    tipoGrado: vue.tipoGrado ? vue.tipoGrado.id : '',
                    facultad: vue.facultad ? vue.facultad.id : '',
                }};

            axios_blob.get("/academico/profesor/reporteCargaAcademica", data)
                    .then(response => {
                        UTIL_BLOB.save(response);
                        vue.$refs.modalReporteCargaAcademica.close();
                    }, () => {
                        vue.$refs.modalReporteCargaAcademica.stop();
                        notify(Messages.errorComunicacion, 'error')
                    });
        },
        changeFacultad() {
            let $vue = this;

            $vue.departamentosSelectos = [];
            $vue.departamento=null;
            
            if ($vue.facultad) {

                $vue.departamentos.map((x, i) => {
                    if (x.facultad.id == $vue.facultad.id) {
                        $vue.departamentosSelectos.push(x)
                    }
                });

                $vue.departamentosSelectos;

            }
        }
    }
});
