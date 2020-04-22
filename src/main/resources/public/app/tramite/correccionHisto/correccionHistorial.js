Vue.component("multiselect", window.VueMultiselect.default);
console.log(tipoDocumentoJson);
new Vue({
    el: '#corrHisto',
    data: {
        alumnos: [],
        alumno: null,
        tipoDocumento: JSON.parse(tipoDocumentoJson),
        corrHistoURL: APP.url('tramite/updateHistorial/list'),
        tramiteModal: VUE_MODAL.structFormAjax({
            id: 'tramiteModal',
            header: true,
            title: 'Tramite Correccion historial',
            okbtn: 'Guardar'
        }),
        isLoading: false,
        correccionhisto: {},
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
            let $vue = this;
            $vue.file = this.$refs.myFiles.files[0];
        },
        classColor(item) {
            switch (item) {
                case 'REV_HIS':
                    return "label label-default";
                    break;
                case 'RES_CON':
                    return "label label-success";
                    break;
                default:
                    return "label label-primary";
                    break;

            }
        },
        save() {
            let $vue = this;
            var form = $("#form");
            if (!form.parsley().validate()) {
                return;
            }
            $vue.correccionhisto.tipoDocumento = $vue.correccionhisto.tipoDocumento.name;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/updateHistorial/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.correccionhisto),
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
                    notify(Messages.errorComunicacion, "error");
                }
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
                    notify(Messages.errorComunicacion, "error");
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
