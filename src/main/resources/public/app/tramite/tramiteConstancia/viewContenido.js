Vue.component("multiselect", window.VueMultiselect.default)
new Vue({
    el: '#main',
    data: {
        contenido: contenidoJson,
        incrustaciones: JSON.parse(incrustacionesJson),
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
        addIncrustacion() {
            var $vue = this;
            console.log($vue.plantillaBean);
            $vue.plantillaBean.tramiteDocumentoAcademico = {};
            $vue.plantillaBean.tramiteDocumentoAcademico.id = id;
            axios.post('/tramite/solicitudconstancia/validVariables', $vue.plantillaBean)
                    .then(response => {
                        if (response.data.success) {
                            var myFrame = $("#myframe").contents().find('body');
                            myFrame.html(response.data.data.contenido);
                            $vue.allTramiteIncrustacion();
                            $vue.plantillaBean = {};
                            $vue.$refs.cicloModal.close();
                        } else {
                            notify(response.data.message, "error");
                            $vue.plantillaBean = {};
                            $vue.$refs.cicloModal.close();
                        }
                    });
        },
        borrar(item) {
            var $vue = this;
            axios.post('/tramite/solicitudconstancia/deleteIncrustacion', item)
                    .then(response => {
                        if (response.data.success) {
                            $vue.tramiteIncrustaciones = response.data.data;
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "error");
                        }
                    });
        }
    }
});
