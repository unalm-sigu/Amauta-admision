Vue.component("multiselect", window.VueMultiselect.default)

console.log(JSON.parse(tiposDocumentoAcademicoJson));
new Vue({
    el: '#main',
    data: {
        dataCargarFoto: VUE_MODAL.structFormAjax({
            id: 'modalCargarFoto',
            header: true,
            title: 'Cargar Fotografía',
            okbtn: 'Aceptar'
        }),
        ciclosModal: VUE_MODAL.structFormAjax({
            id: 'ciclosModal',
            header: true,
            title: 'Ciclos Alumno',
            okbtn: 'Aceptar'
        }),
        haveParams: false,
        alumnos: [],
        persona: {},
        mensajeerror: "",
        ciclos: [],
        ciclo: {},
        tramite: {},
        loadPages: {
            inicio: false,
            tramite: false,
        },
        tabId: 2,
        tabs: [
            {id: 1, name: "Inicio"},
            {id: 2, name: "Tramite"},
            {id: 3, name: "Orden Merito"},
        ],
        alumno: {},
        ordenesMerdito:[]

    },
    computed: {

    },
    created() {

    },
    mounted: function () {

    },
    methods: {
        updateTabs: function (tab) {

            let $vue = this;
            $vue.tabId = tab.id;

            if ($vue.tabId === 5 && !$vue.loadPages.tramite) {
//                $vue.$refs.loadTramite.cargaHorario();
                $vue.loadPages.tramite = true;
            }
            if ($vue.tabId === 1) {
//                $vue.$refs.loadInicio.findAlumno($vue.tramite.alumno.id);
            }
        },
        styleMenu(index) {
            let $vue = this;
            let id = $vue.tabId;
            if (index == id) {
                return "active";
            }
        },

        createCargarFoto: function () {
            var $vue = this;
            $global.$emit('MODAL-WAIT-OPEN', 'Cargando');
            $vue.solicitud.tramite = $vue.tramite;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/solicitudconstancia/onlyfoto'),
                contentType: "application/json",
                data: JSON.stringify($vue.solicitud),
                success: function (response) {
                    if (response.success) {
                        $vue.$refs.cargarFoto.close();
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
        getImage(event) {
            var $vue = this;
            $vue.file = event.target.files[0];
            let formData = new FormData();
            formData.append('file', $vue.file);
            AXIOS.post('/tramite/solicitudconstancia/upload',
                    formData,
                    {
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }
            ).then(function (response) {
                $vue.persona.rutaFotoTemporal = response.data.data.ruta;
                console.log(response);
            }).catch(function () {
                console.log('FAILURE!!');
            });
        },
        subirFoto() {
            let $vue = this;
            $vue.persona = $vue.tramite.alumno.persona;
            $vue.$refs.cargarFoto.open();
        },
        elegir() {
            let $vue = this;
            if ($vue.ciclo.id != null) {
                $vue.haveParams = false;
                $vue.$refs.ciclosModal.close();
            } else {
                notify("Debe seleccionar el parametro", "error");
                $vue.$refs.ciclosModal.open();
            }
        },

    }
});
Vue.config.debug = true;
Vue.config.devtools = true