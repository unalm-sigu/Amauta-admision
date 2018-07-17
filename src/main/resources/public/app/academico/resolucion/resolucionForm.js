Vue.component("multiselect", window.VueMultiselect.default);
Vue.component('file-upload', VueUploadComponent);

var app = new Vue({
    el: '#pageGpoResolucionForm',
    data: {
        URL_TRAMITES: APP.url('academico/resolucion/listTramites'),
        resolucion: null,
        tiposResoluciones: null,
        files: []
    }, created: function () {
        this.loadResolucionForm();
    }, mounted: function () {
        let $vue = this;

    }, methods: {
        loadResolucionForm: function () {
            let $vue = this;
            $.ajax({
                url: APP.url('academico/resolucion/loadModalResolucion'),
                type: 'post',
                success: function (response) {
                    if (response.success) {
                        $vue.resolucion = response.data.resolucionJson;
                        $vue.tiposResoluciones = response.data.tiposResolucionesJson;
                        console.dir(response.data);
                    } else {
                        notify(MESSAGES.errorComunicacion, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        }, activacionTramite(tramite) {
            if (!tramite.activo) {
                tramite.seleccionado = false;
            }
        },
        saveResolucion(event) {
            if (event) {
                event.preventDefault();
            }
            let $vue = this;
            var form = $("[id='frmResolucion']");
            form.find(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            form.find('.multiselect__input').each(function () {
                var input = $(this);
                let element = input.closest('.multiselect').find('.multiselect__tags-wrap');

                if (element.css('display') != 'none' && element.html() != "") {
                    $(this).removeAttr("required");
                }
            });

            form.parsley().destroy();
            form.parsley();
            if (!form.parsley().validate()) {
                return;
            }
            console.log("Resolucion");
            console.dir(this.resolucion);
            console.log("tramites");
            console.dir(this.$refs.tblResoluciones.data);

            this.resolucion.tramites = this.$refs.tblResoluciones.data;

            $.ajax({
                url: APP.url('academico/resolucion/saveResolucion'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: false,
                data: JSON.stringify($vue.resolucion),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
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
})