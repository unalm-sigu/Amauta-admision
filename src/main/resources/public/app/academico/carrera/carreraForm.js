$(function () {
    CarreraForm = {
        init: function () {
            $('[name="modalidadEstudio.id"]').select2();
        },
        viewModal: function (e) {
            e.preventDefault();
//            var form = $("#formLoadFile");
            form.parsley().destroy();

            $("#modalGuia").modal("show");
            $('[name="nombreGuia"]').val("");

        },
        saveUpdateRol: function (e) {
            e.preventDefault();
            var form = $("#formularioCarrera");
            if (!form.parsley().validate()) {
                return;
            }

            form.submit();
        },
        deleteGuia: function ($this) {
            bootbox.confirm({
                message: MESSAGES.confirmDelete,
                title: "Eliminar Guia",
                buttons: {
                    confirm: {label: 'Eliminar'},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        location.href = APP.url("sorteo/roles/deleteGuia/" + $this.attr("rel") + "/" + $this.attr("rev"));
                    }
                }
            });
        }
    };

    CarreraForm.init();

    $(".add-orientacion").click(function (e) {
        CarreraForm.viewModal(e);
    });
    $(".save-update-rol").click(function (e) {
        CarreraForm.saveUpdateRol(e);
    });
    $("body").delegate(".archivo", "change", function () {
        CarreraForm.validarArchivo($(this));
    });
    $("body").delegate(".delete-guia", "click", function () {
        CarreraForm.deleteGuia($(this));
    });
});
