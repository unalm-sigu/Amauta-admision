new Vue({
    el: '#editorContenidoVue',
    data: {
        editorContenidoURL: APP.url('configuracion/editorcontenido/list')
    },
    mounted: function () {
        let $vue = this;
        console.log(this.editorContenidoURL);

        $("body").delegate("#saveContenido", "click", function (e) {
            var form = $("#formulario");
            if (!form.parsley().validate()) {
                return;
            }
            form.submit();
        });

    },
    methods: {
        returnTitle(item) {
            let $vue = this;
            if (item == null) {
                return;
            }
            item = item.replace(/<[^>]*>/g, '');
            if (item.length > 200) {
                item = item.substring(0, 200);
                item = item + "...";
            }
            return item;
        },
        returnImg(item) {
            if (item == null) {
                return;
            }
            let img = "<center><img src='" + item + "' class='img-responsive'/></center>";
            return img;
        },
        nuevo() {
            let $vue = this;
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/nuevo'),
                success: function (response) {
                    $('#contenidoModal').html(response);
                    $('#viewModal').modal('show');
                    $vue.formulario();
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        formulario() {
            $('#formulario').ajaxForm({
                beforeSend: function () {
                    $("#saveContenido").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Guardando');
                    $("#saveContenido").attr('disabled', true);
                },
                uploadProgress: function (e, position, total, percent) {
                },
                success: function (response) {
                },
                complete: function (response) {
                    var json = response.responseJSON;
                    if (json.success) {
                        notify(json.message, "info");
                        $('#viewModal').modal('hide');
                        $("#saveContenido").html('Nuevo Contenido');
                        $("#saveContenido").attr('disabled', false);
                    } else {
                        notify(json.message, "error");
                        $("#saveContenido").html('Nuevo Contenido');
                        $("#saveContenido").attr('disabled', false);
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveContenido() {
            var form = $("#formulario");
            if (!form.parsley().validate()) {
                return;
            }
            form.submit();
        }
    }
});
//
//
//$(function() {
//    var dynatable = $('#dynaTable').dynatable({
//        dataset: {
//            ajaxUrl: APP.url('configuracion/editorcontenido/list'),
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
//        var content;
//        var img;
//        content = record.contenido;
//        if (content != null) {
//            content = content.replace(/<[^>]*>/g, '');
//
//            if (content.length > 200) {
//                content = content.substring(0, 200);
//                content = content + "...";
//            }
//        }
//        img = "<center><img src='" + record.imgUrl + "' class='img-responsive'/></center>";
//
//        record.img = img;
//        record.index = rowIndex;
//        record.content = content;
//        var html = $.templates("#templateContenidos").render(record);
//        return html;
//    }
//    $('#dynaTable').bind('dynatable:afterUpdate', function(e, dynatable) {
//        EditorContenido.Init();
//    });