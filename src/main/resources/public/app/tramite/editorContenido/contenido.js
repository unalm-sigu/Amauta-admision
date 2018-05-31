new Vue({
    el: '#main',
    data: {
        variables: [],
        alumno: {id: null},
        contenidoPreview: null,
        dataModalPreview: {
            id: 'modalPreview',
            header: true,
            title: 'Vista previa',
            okbtn: 'Vista previa',
            modalsize: 'modal-lg',
            modalscroll: 'modal-scroll-600',
            showaccept: false
        },
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
        previewPdf: function (el) {

            let vue = this;
            let self = $(el.currentTarget);
            
            self.find('i').removeClass('fa-file-pdf-o').addClass('fa-spinner fa-spin');
            self.prop("disabled", true);
            
            let urll = APP.url('tramite/plantillaconstancia/previewpdf');

            $.fileDownload(urll, {
                httpMethod: "POST",
                data: $('form').serialize(),
            }).done(function () {
                setTimeout(function () {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-file-pdf-o');
                    self.removeProp("disabled");
                }, 2000);
            }).fail(function () {
                setTimeout(function () {
                    self.find('i').removeClass('fa-spinner fa-spin').addClass('fa-file-pdf-o');
                    self.removeProp("disabled");
                }, 2000);
                notify(MESSAGES.errorComunicacion, "error");
            });



        },
        previewHtml: function () {

            let vue = this;
            $global.$emit('MODAL-WAIT-OPEN');

            $.ajax({
                method: 'POST',
                url: APP.url('tramite/plantillaconstancia/preview'),
                data: $('form').serialize(),
                success: function (response) {
                    if (response.success) {
                        $global.$emit('MODAL-WAIT-CLOSE');
                        vue.$refs.modalPreview.open();
                        vue.contenidoPreview = response.data;
                        var myFrame = $("#myframe").contents().find('body');
                        myFrame.html(response.data);
                    } else {
                        $global.$emit('MODAL-WAIT-CLOSE');
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        saveModalPreview: function () {

        }
    }
});
