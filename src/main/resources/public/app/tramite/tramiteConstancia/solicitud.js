Vue.component("multiselect", window.VueMultiselect.default)
console.log(JSON.parse(tiposDocumentoAcademicoJson));
new Vue({
    el: '#main',
    data: {
       
        tramite: {},
        loadPages: {
            historial: false,
            matricula: false,
            horario: false
        },
        tabId: 1,
        tabs: [
            {id: 1, name: "Inicio"},
            {id: 2, name: "Historial"},
            {id: 3, name: "Matricula"},
            {id: 4, name: "Horario"}
        ]
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
            if ($vue.tabId === 2 && !$vue.loadPages.historial) {
                $vue.$refs.loadHistorial.cargaHistorial();
                $vue.loadPages.historial = true;
            }
            if ($vue.tabId === 3 && !$vue.loadPages.matricula) {
                $vue.$refs.loadMatricula.obtenerDatos();
                $vue.loadPages.matricula = true;
            }
            if ($vue.tabId === 4 && !$vue.loadPages.horario) {
                $vue.$refs.loadHorario.cargaHorario();
                $vue.loadPages.horario = true;
            }
        },
        styleMenu(index) {
            let $vue = this;
            let id = $vue.tabId;
            if (index == id) {
                return "active";
            }
        }
    }
});
