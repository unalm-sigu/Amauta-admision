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
            aportes: false,
            retirociclo: false,
            retirocurso: false,
        },
        preRequisitos: [],
        modalPreRequisito: {
            id: 'modalPreRequisito',
            header: true,
            title: 'Pre Requisitos ',
            showaccept: false
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
            {id: 8, name: "Retiro Ciclo"},
            {id: 9, name: "Retiro Curso"}
        ];

        if ($vue.alumno.modalidadEstudio.codigo == 'VIS' || $vue.alumno.modalidadEstudio.codigo == 'ESP') {
            $vue.facu = false;
        }
        if ($vue.alumno.carrera.codigo == $vue.alumno.carrera.facultad.codigo) {
            $vue.facu = false;
        }
    },
    mounted: function () {
        let $vue = this;
        $global.$on("update-alumno", function (data) {
            $vue.updateAlumno(data);
        });
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
        updateTabs: function (tab) {

            let $vue = this;
            $vue.tabId = tab.id;
            if ($vue.tabId === 2 && !$vue.loadPages.historial) {
                $vue.$refs.loadHistorial.cargaHistorial();
                $vue.loadPages.historial = true;
            }
            if ($vue.tabId === 3 && !$vue.loadPages.avance) {
                $vue.$refs.loadAvance.loadPlanes();
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
            if ($vue.tabId === 8 && !$vue.loadPages.retirociclo) {
                $vue.$refs.compRetiroCiclo.obtenerDatos();
                $vue.loadPages.retirociclo = true;
            }
            if ($vue.tabId === 9 && !$vue.loadPages.retirocurso) {
                $vue.$refs.compRetiroCurso.obtenerDatos();
                $vue.loadPages.retirocurso = true;
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
        styleMenu(index) {
            let $vue = this;
            let id = $vue.tabId;
            if (index == id) {
                return "active";
            }
        },
        styleEstadoCurr(nombre) {
            if (nombre === 'APR' || nombre === 'EQUIV' || nombre === 'CONV') {
                return "text-success";
            } else if (nombre === 'SIM') {
                return "text-warning";
            } else if (nombre === 'NREQ') {
                return "text-secondary";
            } else if (nombre === 'HAB') {
                return "text-primary";
            } else if (nombre === 'MAT') {
                return "text-primary bold";
            } else if (nombre === 'PEND') {
                return "text-warning ";
            }
        },
        reiniciarPlanes() {
            let $vue = this;
            $vue.reloadAlumno();
            $vue.loadPages.avance = false;
            $vue.loadPages.malla = false;
        },
        reloadPlanAlumno() {
            let $vue = this;
            $vue.reloadAlumno();
            $vue.loadPages.malla = false;
        },
        modalPreRequisitos(cursoCurricula) {
            let $vue = this;
            $vue.preRequisitos = cursoCurricula.prerrequisitos;
            if ($vue.preRequisitos.length == 0) {
                return;
            }
            $vue.$refs.modalPreRequisito.open();
        },
        updateAlumno(data) {
            let $vue = this;
            $vue.alumno = data;
        }
    }
});
