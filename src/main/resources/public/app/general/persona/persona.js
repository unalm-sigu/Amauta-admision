$(function () {

    var dynatable = $('#dynaTable').dynatable({
        dataset: {
            ajaxUrl: APP.url('general/persona/allPersona'),
            perPageDefault: 10
        },
        writers: {
            _rowWriter: ulWriter

        },
        table: {
            bodyRowSelector: 'tbody tr'
        }
    }).data('dynatable');

    $('#dynaTable').bind('dynatable:afterUpdate', function (e, dynatable) {
        Persona.moveDivBusqueda();
    });

    function ulWriter(rowIndex, record, columns, cellWriter) {

        record.index = rowIndex;
        record.tieneCelular = (!(record.celular == "" || record.celular == null));
        record.tieneTelefono = (!(record.telefono == "" || record.telefono == null));
        record.tieneEmail = (!(record.email == "" || record.email == null));

        var html = $.templates("#templatePersona").render(record);
        return html;
    }

    Persona = {
        verInfoPersona: function ($this, e) {

            e.preventDefault();
            var persona = $this.attr("rel");

            $.ajax({
                url: APP.url('general/persona/infoPersona'),
                type: 'POST',
                async: true,
                data: {persona: persona},
                success: function (response) {
                    MODAL.init("MD");
                    MODAL.title("Información");
                    MODAL.body(response);
                    MODAL.buttons('<a class="btn btn-primary" id="btnPersona" rel="' + persona + '">Editar</a>');
                    MODAL.show();
                },
                error: function () {
                    notify(GlobalMessages.errorComunicacion, "error");
                }
            });
        },
        moveDivBusqueda: function () {

            $("#opopop").prepend($("#divBuscar"));
            $('#divBuscar').removeClass('hide');

            $("#rol").select2({
                placeholder: "Seleccione un rol",
                allowClear: true
            });

        },
        editarPersona: function ($this, e) {

            e.preventDefault();
            var persona = $this.attr("rel");

            var origen = location.pathname + location.search;

            var form = $("#formPersonaEdit");
            form.find("input").val(origen);
            form.attr("action", APP.url('general/persona/' + persona + '/edicion'));
            form.submit();
        },
        nuevoPersona: function ($this, e) {
            e.preventDefault();
            var origen = location.pathname + location.search;
            console.log('aqui enviando a post');
            var form = $("#formPersonaEdit");
            form.find("input").val(origen);
            form.attr("action", APP.url('general/persona/nuevo'));
            form.submit();
        },
        buscaPersona: function ($this) {

            var search = $this.val();

            console.log("rol: " + search);

            if (search == "") {
                dynatable.queries.remove("rol");
            } else {
                dynatable.queries.add("rol", search);
            }

            dynatable.process();


        }
    };

    $("body").delegate(".info-persona", "click", function (e) {
        Persona.verInfoPersona($(this), e);
    });

    $("body").delegate(".modificar", "click", function (e) {
        Persona.editarPersona($(this), e);
    });

    $("body").delegate("#btnPersona", "click", function (e) {
        Persona.editarPersona($(this), e);
    });

    $('body').delegate('#btnAddPersona', 'click', function (e) {
        Persona.nuevoPersona($(this), e);
    });

});