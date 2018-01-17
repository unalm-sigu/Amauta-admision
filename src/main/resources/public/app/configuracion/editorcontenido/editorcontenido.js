$(function() {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('configuracion/editorcontenido/list'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter
        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    function ulWriter(rowIndex, record, columns, cellWriter) {

        var content;
        var img;
        content = record.contenido;
        if (content != null) {
            content = content.replace(/<[^>]*>/g, '');

            if (content.length > 200) {
                content = content.substring(0, 200);
                content = content + "...";
            }
        }
        img = "<center><img src='" + record.imgUrl + "' class='img-responsive'/></center>";

        record.img = img;
        record.index = rowIndex;
        record.content = content;

        var html = $.templates("#templateContenidos").render(record);
        return html;
    }

    $('#dynaTable').bind('dynatable:afterUpdate', function(e, dynatable) {
        EditorContenido.Init();
    });

    EditorContenido = {
        Init: function() {
            $(document).ready(function() {
                $('[data-toggle="tooltip"]').tooltip();
            });

            $(document).ready(function() {
                $('[data-toggle="popover"]').popover({html: true});
            });
        },
        nuevo: function() {
            $.ajax({
                method: 'POST',
                url: APP.url('configuracion/editorcontenido/nuevo'),
                success: function(response) {
                    $('#contenidoModal').html(response);
                    $('#viewModal').modal('show');
                    EditorContenido.formulario();
                },
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        formulario: function() {
            $('#formulario').ajaxForm({
                beforeSend: function() {
                    $("#saveContenido").html('<i class="fa fa-spinner fa-spin fa-lg"></i> Guardando');
                    $("#saveContenido").attr('disabled', true);
                },
                uploadProgress: function(e, position, total, percent) {
                },
                success: function(response) {
                },
                complete: function(response) {
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
                error: function() {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        saveContenido: function() {

            var form = $("#formulario");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        }

    };

    $("body").delegate("#nuevoContenido", "click", function(e) {
        EditorContenido.nuevo();
    });
    $("body").delegate("#saveContenido", "click", function(e) {
        EditorContenido.saveContenido();
    });


});