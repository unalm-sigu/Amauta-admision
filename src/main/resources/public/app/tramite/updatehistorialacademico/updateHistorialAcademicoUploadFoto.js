Vue.component("vueupload", {
    template: "#fileuploadTemplate",
    props: {
        mensajeerror: '',
        rutaFotoTemporal:null,
        fullRutaFotoTemporal:null
    },
    date: function () {
        return {rutaFotoTemporal: null, fullRutaFotoTemporal:null,mensajeerror: ''}
    },
    mounted: function () {

        let vue = this;
        let self = $(vue.$el);

        $global.$on("open", function () {
            vue.open();
        });

        $('#fileupload').fileupload({
            url: APP.url('tramite/solicitudconstancia/updatehistorial/upload'),
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
                        vue.fullRutaFotoTemporal = e.target.result;
                    };
                    reader.readAsDataURL(data.files[0]);
                }
                data.submit();
            },
            done: function (e, data) {
                if (data.result.success) {
                    var ruta = data.result.data.ruta;
                    if (data.result.data.ok == true) {
                        notify(data.result.message, "info");
                        vue.mensajeerror = '';
                        vue.rutaFotoTemporal = ruta;
                    } else {
                        vue.mensajeerror = data.result.data.nocumplerequisito;
                        vue.rutaFotoTemporal = '';
                        vue.fullRutaFotoTemporal = '';
                    }
                } else {
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
    updated: function () {
        let vue = this;
        this.$nextTick(function () {
            let self = $(vue.$el);
        });
    },
    methods: {
        open: function () {
            let vue = this;
            let self = $(vue.$el);
            self.find('#fileupload').trigger('click');
        }
    },
});

