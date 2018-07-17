Vue.component("vueupload", {
    template: "#fileuploadTemplate",
    props: {
        persona: {},
        mensajeerror: ''
    },
    date: function () {
        return {persona: {}, mensajeerror: ''}
    },
    mounted: function () {

        let vue = this;
        let self = $(vue.$el);

        $global.$on("open", function () {
            vue.open();
        });

        $('#fileupload').fileupload({
            url: APP.url('academico/profesor/upload'),
            maxNumberOfFiles: 1,
            dataType: 'json',
            dropZone: '#dragarea',
            add: function (e, data) {
                $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
                if (data.files[0].type.search(/(\.|\/)(jpe?g|png)$/i) == -1) {
                    notify("Formato de archivo no soportado.", "error");
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    return;
                }
                if (data.files && data.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        vue.persona.fullRutaFotoTemporal = e.target.result;
                    };
                    reader.readAsDataURL(data.files[0]);
                }
                data.submit();
            },
            progress: function (e, data) {
            },
            done: function (e, data) {
                if (data.result.success) {
                    var ruta = data.result.data.ruta;
                    vue.mensajeerror = '';
                    vue.persona.rutaFotoTemporal = ruta;
                    notify(data.result.message, "info");
                } else {
                    vue.persona.fullRutaFotoTemporal = '';
                    vue.persona.rutaFotoTemporal = '';
                    notify(data.result.message, "error");
                }
                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
            },
            fail: function (e, data) {
                $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                notify(data.result.message, "error");
            }
        });
    },
    methods: {
        open: function () {
            let vue = this;
            $('#fileupload').trigger('click');
        }
    },
});

