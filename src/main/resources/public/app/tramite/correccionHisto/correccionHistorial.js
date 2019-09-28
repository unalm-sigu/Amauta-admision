Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#corrHisto',
    data: {
        alumnos: [],
        alumno: null,
        corrHistoURL: APP.url('tramite/updateHistorial/list'),
        tramiteModal: VUE_MODAL.structFormAjax({
            id: 'tramiteModal',
            header: true,
            title: 'Tramite Correccion historial',
            okbtn: 'Guardar'
        }),
        isLoading: false,
        file: {}
    },
    computed: {

    },
    created() {

    },
    mounted: function () {

    },
    methods: {
        searchAlumnos(nombre) {
            let $vue = this;
            if (nombre != null && nombre != "") {
                $vue.isLoading = true;
                $.ajax({
                    url: APP.url("tramite/solicitudconstancia/searchalumno"),
                    type: 'post',
                    data: {nombre: nombre},
                }).then(response => {
                    $vue.alumnos = response.data;
                    $vue.isLoading = false;
                })
            }
        },
        customLabel( { persona, codigo}) {
            if (persona == undefined) {
                return;
            }
            return `${codigo} - ${persona.apellidosNombres}`;
        },
        previewFiles() {
            this.file = this.$refs.myFiles.files[0];
        },
        classColor(item) {

        },
        save() {
            let $vue = this;
            var form = $("#form");
            if (!form.parsley().validate()) {
                return;
            }

            let formData = new FormData();
            formData.append('file', $vue.file);
            formData.append('alumno', $vue.alumno.id);
            AXIOS.post('/tramite/updateHistorial/save',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
            ).then(function (response) {
                $vue.$refs.load.repreload();
                console.log($vue.datos);
            }).catch(function () {
                console.log('FAILURE!!');
            });
            this.$refs.tramiteModal.close();
        },
        updateEstado(tramite, estado) {
            let $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/updateHistorial/updateEstado'),
//                contentType: "application/json",
                data: {tramite: tramite.id, accionTramite: estado.id},
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.load.repreload();
                        notify(response.message, "success");
                    } else {
                        notify(response.message, 'error');
                    }
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                }, error: function () {
                    $global.$emit('MODAL-WAIT-CLOSE', 'Cargando');
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        procesarTramite(item, event) {
            event.preventDefault();

            location.href = APP.url("academico/procesar/" + item.id);
        },
        nuevo() {
            let $vue = this;
            $vue.alumno = {};
            $vue.file = {};
            this.$refs.tramiteModal.open();
        }
    }
});
