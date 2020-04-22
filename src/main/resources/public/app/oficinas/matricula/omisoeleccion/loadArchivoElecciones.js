Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#loadArchivoEleccionesVUE',
    data: {
        files: [],
        btnText: 'Iniciar Carga',
        ciclos: JSON.parse(cicloJson),
        datos: [],
        cicloAcademico: null
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {

        startUpload() {
            let $vue = this;
            var form = $("#formLoadFiles");
            if (!form.parsley().validate()) {
                return;
            }

            let formData = new FormData();
            formData.append('file', $vue.file);
            formData.append('cicloAcademico', $vue.cicloAcademico.codigo);
            AXIOS.post('/oficinas/matricula/omisoeleccion/cargarDatos',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
            ).then(response => {

                if (response.data.success) {
//                    notify(response.data.message, "success");
                } else {
                    $vue.datos = response.data.data;
//                    notify(response.data.message, "error");
                }
                console.log($vue.datos);
            }).catch(err => {
                notify(Messages.errorComunicacion, "error");
            });
        },
        getImage(event) {
            var $vue = this;
            $vue.file = event.target.files[0];

        }
    }
});
