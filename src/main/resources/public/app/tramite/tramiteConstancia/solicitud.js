Vue.component("multiselect", window.VueMultiselect.default)

console.log(JSON.parse(solicitudJson));
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
        solicitud: JSON.parse(solicitudJson),
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
            {id: 4, name: "Historial"},
            {id: 5, name: "Matricula"},
            {id: 6, name: "Retiro Ciclo"}
        ],
        alumno: {},
        ordenesMerdito: []

    },
    mounted: function () {
        let $vue = this;
        if ($vue.solicitud.tramite != null) {
            $vue.tramite = $vue.solicitud.tramite;
        }
        $global.$on("reset-loading-data-alumno", function () {
            $vue.resetLoadingData();
        });

    },
    methods: {
        resetLoadingData() {
            let $vue = this;
            $vue.loadPages.historial = false;
            $vue.loadPages.avance = false;
            $vue.loadPages.matricula = false;
            $vue.loadPages.horario = false;
            $vue.loadPages.malla = false;
            $vue.loadPages.retirociclo = false;
            $vue.loadPages.retirocurso = false;
            $vue.reloadAlumno();
        },
        reloadAlumno() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/data'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.alumno = response.data;
                    }
                }
            });
        },
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
            $vue.tabId = tab.id;
            if ($vue.tabId === 4 && !$vue.loadPages.historial) {
                $vue.$refs.loadHistorial.cargaHistorial();
                $vue.loadPages.historial = true;
            }
            if ($vue.tabId === 5 && !$vue.loadPages.matricula) {
                $vue.$refs.loadMatricula.obtenerDatos();
                $vue.loadPages.matricula = true;
            }
            if ($vue.tabId === 6 && !$vue.loadPages.retirociclo) {
                $vue.$refs.compRetiroCiclo.obtenerDatos();
                $vue.loadPages.retirociclo = true;
            }
        }
        ,
        styleMenu(index) {
            let $vue = this;
            let id = $vue.tabId;
            if (index == id) {
                return "active";
            }
        }
        ,
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
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
        ,
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