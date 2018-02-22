Vue.component('file-upload', VueUploadComponent);
Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#restriccionMatriculaCargarDatosVUE',
    data: {
        files: [],
        tipo: {
            id: 1,
        },
        cargando: false,
        progreso: 0,
        tipos: []
    },
    beforeMount() {
        let $vue = this;
        $vue.tipos = JSON.parse(tipos);
    },
    computed: {
        titulo() {
            return "Titulo";
        },
        nombreArchivo() {
            let $vue = this;
            if ($vue.files.length === 0) {
                return "Ningún archivo seleccionado";
            } else {
                return $vue.files[0].name;
            }
        }
    },
    methods: {
        inputFilter(newFile, oldFile, prevent) {
            if (newFile && !oldFile) {
            }
        },
        inputFile(newFile, oldFile) {
            let $vue = this;
            if (oldFile && newFile) {
                if (newFile.progress !== oldFile.progress) {
                    this.progreso = newFile.progress;
                    this.cargando = true;
                }

                if (newFile.success !== oldFile.success) {
                    this.cargando = false;
                    this.progreso = 0;
                    if ($vue.files[0].response.success) {
                        notify($vue.files[0].response.message, "info");
                        $vue.$refs.upload.clear();
                    } else {
                        notify($vue.files[0].response.message, "error");
                        $vue.$refs.upload.clear();
                    }
                }
            }
        },
        stopCarga() {
            this.cargando = false;
            this.$refs.upload.clear();
            this.progreso = 0;
        },
        subirArchivo() {
            $(".multiselect__input").each(function () {
                $(this).attr("required", true);
            });
            var valid = $('#form').parsley().validate();
            if (!valid) {
                return;
            }
            let $vue = this;
            $vue.$refs.upload.active = true;
        }
    }
});
