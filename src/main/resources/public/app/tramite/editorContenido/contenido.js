new Vue({
    el: '#main',
    data: {
        variables: [],
        alumno:{}
    },
    mounted() {
        let vue = this;
        CKEDITOR.replace('contenido', {height: 380});
    },
    methods: {
        updateContenido: function () {
            let vue = this;
            for (instance in CKEDITOR.instances) {
                CKEDITOR.instances[instance].updateElement();
            }
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/updateContenido'),
                data: $('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                        vue.variables = response.data.variablePlantilla;
                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
        previewPdf:function(){},
        previewHtml:function(){},
    }
});
