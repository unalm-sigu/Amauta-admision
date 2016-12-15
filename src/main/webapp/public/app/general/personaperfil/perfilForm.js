$(function () {
    Perfil = {
        initPersona: function () {
            
            $('#fechaInicio').datepicker();
            
            
            if ($('#cid').val() == "") {

                $('#idPersona').select2({
                    minimumInputLength: 1,
                    ajax: {
                        url: APP.url("general/personaperfil/searchPersona"),
                        dataType: 'json',
                        type: 'post',
                        data: function (term, page) {
                            return {nombre: term};
                        },
                        results: function (info, page) {
                            return {results: info.data};
                        }
                    },
                    formatResult: function (info) {
                        return info.nombre;
                    },
                    formatSelection: function (info) {
                        return info.nombre;
                    },
                    escapeMarkup: function (m) {
                        return m;
                    }
                });


                $("#compania").select2("val", "");

            }
        }
    };

    Perfil.initPersona();

    $('body').delegate("#cboCargo", "change", function () {
        Perfil.cambiarCargo();
    });

    $('body').delegate("#idPersona", "change", function () {
        var $this = $(this);
        
    });

});