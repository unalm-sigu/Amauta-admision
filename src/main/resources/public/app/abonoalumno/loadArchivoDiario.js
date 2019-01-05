Vue.component('file-upload', VueUploadComponent);
new Vue({
    el: '#loadArchivoVUE',
    data: {
        files: [],
        progress: 0,
        btnText: 'Iniciar Carga',
        datos: [],
        mensaje: '',
        clase: ''
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        inputFilter(newFile, oldFile, prevent) {
            let $vue = this;
            if (newFile && !oldFile) {
                // Before adding a file
                // Filter system files or hide files
                if (/(\/|^)(Thumbs\.db|desktop\.ini|\..+)$/.test(newFile.name)) {
                    return prevent()
                }
                // Filter php html js file
                if (/\.(php5?|html?|jsx?)$/i.test(newFile.name)) {
                    return prevent()
                }
                if (!/\.(xls)$/i.test(newFile.name)) {
                    $vue.mensaje = 'TIPO DE ARCHIVO NO VALIDO';
                    $vue.clase = 'alert-warning';
                    return prevent();
                }
            }
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            if (newFile && !oldFile) {
                // add
                console.log('add', newFile)
            }
            if (newFile && oldFile) {
                // update
                if (newFile.progress !== oldFile.progress) {
                    $vue.progress = newFile.progress;
                    $('#progress .progress-bar').css('width', $vue.progress + '%');
                }
//                console.log('update', newFile)
                if (newFile.success !== oldFile.success) {
                    if (newFile.response.success) {

                        if (newFile.response.data != null) {
                            notify(newFile.response.message, "error");
                            $vue.datos = newFile.response.data;
                        } else {
                            notify(newFile.response.message, "info");
                            setTimeout(function () {
                                location.href = APP.url("abonoalumno");
                            }, 1200);
                        }
                    } else {
                        $vue.btnText = 'Iniciar Carga';
                        $vue.mensaje = newFile.response.message;
                        $vue.clase = 'alert-danger';
                        $("#footerLoadAbonos").find("a").each(function (i, item) {
                            $(item).removeAttr("disabled");
                        });
                    }
                    console.log(newFile.response);
                }
            }
            if (!newFile && oldFile) {
                // remove
                console.log('remove', oldFile)
            }
        },
        stopUpload() {
            let $vue = this;
            $vue.$refs.upload.active = false;
            $vue.$refs.upload.clear();
            $vue.progress = 0;
            $('#progress .progress-bar').css('width', $vue.progress + '%');
        },
        startUpload() {
            let $vue = this;
            $vue.datos = [];
            $vue.mensaje = '';
            $vue.clase = '';
            $vue.$refs.upload.active = true;
        }
    }
});
