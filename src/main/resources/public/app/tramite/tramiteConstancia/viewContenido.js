Vue.component("multiselect", window.VueMultiselect.default)
new Vue({
    el: '#main',
    data: {
        contenido: contenidoJson,
        incrustaciones: JSON.parse(incrustacionesJson),
        tramite: JSON.parse(tramiteJson),
        plantillaBean: {},
        tramiteIncrustaciones: [],
        ciclos: [],
        cicloModal: {
            id: 'cicloModal',
            header: true,
            title: 'Agregar Ciclo',
            okbtn: 'Agregar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true
        },
        id: id
    },
    computed: {

    },
    created() {

    },
    mounted: function () {
        var myFrame = $("#myframe").contents().find('body');
        myFrame.html(this.contenido);
        this.allTramiteIncrustacion();
    },
    methods: {
        allTramiteIncrustacion() {
            var $vue = this;
            var params = new URLSearchParams();
            params.append('idTramiteAcademico', $vue.id);
            axios.post('/tramite/solicitudconstancia/allTramiteIncrustaciones', params)
                    .then(response => {
                        $vue.tramiteIncrustaciones = response.data.data;
                    });
        },
        addVariable() {
            var $vue = this;
            $vue.plantillaBean.cicloEstudiado = {};
            if ($vue.plantillaBean.plantillaDocumentoAcademico == null) {
                notify("Seleccione una incrustación", "error");
            }
            if ($vue.plantillaBean.plantillaDocumentoAcademico.id == 45) {
                $vue.$refs.cicloModal.open();
            } else {
                this.addIncrustacion();
            }
        },
        searchCiclos(nombre) {
            var $vue = this;
            var params = new URLSearchParams();
            params.append('nombre', nombre);
            axios.post('/tramite/solicitudconstancia/allCicloAcademico', params)
                    .then(response => {
                        $vue.ciclos = response.data.data;
                    });
        },
        elegir() {

        },
        addIncrustacion() {
            var $vue = this;
            console.log($vue.plantillaBean);
            $vue.plantillaBean.tramiteDocumentoAcademico = {};
            $vue.plantillaBean.tramiteDocumentoAcademico.id = this.id;
            axios.post('/tramite/solicitudconstancia/validVariables', $vue.plantillaBean)
                    .then(response => {
                        if (response.data.success) {
                            var myFrame = $("#myframe").contents().find('body');
                            myFrame.html(response.data.data.contenido);
                            $vue.allTramiteIncrustacion();
                            $vue.plantillaBean = {};
//                            $vue.$refs.cicloModal.close();
                        } else {
                            notify(response.data.message, "error");
                            $vue.plantillaBean = {};
//                            $vue.$refs.cicloModal.close();
                        }
                    });
        },
        borrar(item, idx) {
            var $vue = this;
            axios.post('/tramite/solicitudconstancia/deleteIncrustacion', item)
                    .then(response => {
                        if (response.data.success) {
                            var myFrame = $("#myframe").contents().find('body');
                            myFrame.html(response.data.data);
                            $vue.tramiteIncrustaciones.splice(idx, 1);
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    });
        },
        procesarTramite(accion) {

            let $vue = this;
            $vue.accionSeleccionada = accion;

            $vue.processingAjaxData = {
                tramite: $vue.tramite.id,
                accionTramite: null,
                accionTramiteDoc: $vue.accionSeleccionada.id
            }

            $.ajax({
                method: 'POST',
                url: APP.url('academico/tramiteacademico/procesarTramite'),
                dataType: "json",
                contentType: "application/json",
                type: 'POST',
                async: true,
                data: JSON.stringify($vue.processingAjaxData),
                success: function (response) {
                    if (response.success) {
                        notify(response.message, "info");
                        location.href = APP.url('tramite/solicitudconstancia');
                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});
