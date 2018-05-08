Vue.component("vueupload", {
    template: "#fileuploadTemplate",
    props: {
        solicitud: {},
    },
    date: function() {
        return {solicitud: {}}
    },
    mounted: function() {

        let vue = this;
        let self = $(vue.$el);

        $global.$on("open", function() {
            vue.open();
        });

        $('#fileupload').fileupload({
            url: APP.url('tramite/solicitudconstancia/updatehistorial/upload'),
            maxNumberOfFiles: 1,
            dataType: 'json',
            dropZone: '#dragarea',
            add: function(e, data) {

                $global.$emit('MODAL-WAIT-OPEN', 'Cargando');

                $('#fileuploadtrigger').btnDisabled();
                $('#btnSavePersona').btnDisabled();

                if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                    notify("Formato de archivo no soportado.", "error");
                    $('#fileuploadtrigger').btnEnable();
                    $('#btnSavePersona').btnEnable();
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    return;
                }

                if (data.files && data.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function(e) {
                        $('#imgDocument').attr('src', e.target.result);
                    };
                    reader.readAsDataURL(data.files[0]);
                }

                data.submit();
            },
            progress: function(e, data) {
            },
            done: function(e, data) {

                $('input:submit').removeAttr("disabled");

                if (data.result.success) {
                    var ruta = data.result.data.ruta;
                    $('#avatar').val(ruta);
                    notify(data.result.message, "info");
                } else {
                    notify(data.result.message, "error");
                }

                $('#fileuploadtrigger').btnEnable();
                $('#btnSavePersona').btnEnable();

                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');

            },
            fail: function(e, data) {
                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                notify(data.result.message, "error");
            }
        });
    },
    updated: function() {
        let vue = this;
        this.$nextTick(function() {
            let self = $(vue.$el);
        });
    },
    methods: {
        open: function() {
            let vue = this;
            $('#fileupload').trigger('click');
        }
    },
});

