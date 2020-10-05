Vue.component("multiselect", window.VueMultiselect.default)
new Vue({
    el: '#colaboradorVue',
    data: {
        raptorURL: APP.url("tramite/plantillaconstancia/list"),
        tipoConstancia: JSON.parse(tipoDocumentoJson),
        idiomas: JSON.parse(idiomasJson),
        plantilla: {},
    },
    created() {
        let $vue = this;
    },
    methods: {
        contenido: function (elem) {
            location.href = APP.url('tramite/plantillaconstancia/' + elem.id)
        },
        modalUpdate: function (elem) {
            let $vue = this;
            $vue.plantilla = {...elem}
            $("#myModal").modal('show');
        },
        nuevo: function () {
            let $vue = this;
            $vue.plantilla = {};
            $("#myModal").modal('show');
        },
        update: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.plantilla);
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptor.loadRemoteData();
                        notify(response.message, 'info');
                        $vue.plantilla = {};
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        save: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.plantilla);
            $vue.plantilla.tipoDocumentoAcademico.tipo = $vue.plantilla.tipoDocumentoAcademico.tipo.name;
            $vue.plantilla.tipoDocumentoAcademico.costoCiclo = $vue.plantilla.tipoDocumentoAcademico.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.plantilla),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.raptor.loadRemoteData();
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        eliminar: function (elem) {

            let $vue = this;
            $vue.plantilla = {...elem}

            var dialog = bootbox.confirm({
                message: "¿Está seguro que desea eliminar la plantilla?",
                buttons: {
                    confirm: {label: 'Si, eliminar', className: "btn-danger"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {

                        $.ajax({
                            method: 'POST',
                            url: APP.url('tramite/plantillaconstancia/delete'),
                            contentType: "application/json",
                            data: JSON.stringify($vue.plantilla),
                            success: function (response) {
                                if (response.success) {
                                    $vue.$refs.raptor.loadRemoteData();
                                    notify(response.message, 'info');
                                } else {
                                    notify(response.message, 'error');
                                }
                            }
                        });

                    }
                }
            });

        }
    }
});
