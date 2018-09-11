Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#infoAcademico',
    data: {
        alumno: JSON.parse(alumnoJson),
        ciclo: JSON.parse(cicloJson),
        tabId: 1,
        objtab: {},
        alumnoInfo: {},
        loadPages: {
            historial: false,
            avance: false,
            matricula: false,
            horario: false,
            malla: false,
            aportes: false
        }

    },
    computed: {
        tituloHorario() {
            return 'Horario de Clases ' + this.ciclo.descripcion;
        }
    },
    created() {
        let $vue = this;
        $vue.flag = true;
        $vue.facu = true;
        $vue.tabs = [
            {id: 1, name: "Inicio"},
            {id: 2, name: "Historial"},
            {id: 3, name: "Avance"},
            {id: 4, name: "Matricula"},
            {id: 5, name: "Horario"},
            {id: 6, name: "Malla"},
            {id: 7, name: "Aportes"}];

        if ($vue.alumno.modalidadEstudio.codigo == 'VIS' || $vue.alumno.modalidadEstudio.codigo == 'ESP') {
            $vue.facu = false;
        }
        if ($vue.alumno.carrera.codigo == $vue.alumno.carrera.facultad.codigo) {
            $vue.facu = false;
        }
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
            if ($vue.tabId === 3 && !$vue.loadPages.avance) {
                $vue.$refs.loadAvance.cargaAvance();
                $vue.loadPages.avance = true;
            }
            if ($vue.tabId === 4 && !$vue.loadPages.matricula) {
                $vue.$refs.loadMatricula.obtenerDatos();
                $vue.loadPages.matricula = true;
            }
            if ($vue.tabId === 5 && !$vue.loadPages.horario) {
                $vue.$refs.loadHorario.cargaHorario();
                $vue.loadPages.horario = true;
            }
            if ($vue.tabId === 6 && !$vue.loadPages.malla) {
                $vue.$refs.loadMalla.verMalla();
                $vue.loadPages.malla = true;
            }
            if ($vue.tabId === 7) {
                $vue.cargaAportes();
            }
        },
        cargaAportes() {
            let $vue = this;
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + this.alumno.id + '/aportes'),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        $vue.aportes = response.data;
                    }
                }
            });
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
