//$(function () {
//    var seccion = $("#seccion").val();
//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('academico/docente/alumnosDocente/' + seccion + '/list'),
//            perPageDefault: 1000
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
//        record.index = rowIndex;
//        record.nro = rowIndex + 1;
//        var html = $.templates("#templateAlumnosDocente").render(record);
//        return html;
//    }
//
//    AlumnosDocente = {
//    };
//
//    $("body").delegate(".alguna-clase", "click", function (e) {
//    });
//
//
//});

new Vue({
    el: '#alumnosDocenteVUE',
    data: {
        matriculados: [],
        seccion: JSON.parse(seccionJson),
    },
    mounted: function () {
        let $vue = this;
        $vue.loadMatriculados();
    },
    methods: {
        loadMatriculados() {
            let $vue = this;
            $.ajax({
                url: APP.url(rutaModulo + '/' + $vue.seccion.id + '/list'),
                type: 'POST',
                success: function (response) {
                    if (response.success) {
                        $vue.matriculados = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }
    }
});


