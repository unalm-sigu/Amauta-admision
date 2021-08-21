Vue.use(Vuex);

const store = new Vuex.Store({
    state: {
        resolucion: resolucionJson ? JSON.parse(resolucionJson) : {
            reincorporaciones: [],
            retiroCiclo: [],
            cambioNota: [],
            cursoDirigido: [],
            tramiteTraslado: [],
            cambioNotaMasBajas: [],
            tramiteBachiller: [],
            tramiteTitulos: [],
            tramitePracticasPreProfesionales: [],
            readmisiones: [],
            cambioPlanCurriculares: [],
        },
        visualizarSoloSeleccionados: false,
        filterFacultad: null,
        isEdicion: false
    },
    mutations: {
        SET_RESOLUCION(state, resolucion) {
            state.resolucion = resolucion;
        },
        TOGGLE_STATE_SELECCIONADO(state) {
            state.visualizarSoloSeleccionados = !state.visualizarSoloSeleccionados;
        },
        SET_STATE_FILTER_FACULTAD(state, facultad) {
            state.filterFacultad = facultad;
        },
    },
    getters: {
        getResolucion: state => state.resolucion,
    },
    actions: {
        newResolucion(context) {

            context.commit('SET_RESOLUCION', {
                reincorporaciones: [],
                retiroCiclo: [],
                cambioNota: [],
                cursoDirigido: [],
                tramiteTraslado: [],
                cambioNotaMasBajas: [],
                tramiteBachiller: [],
                tramiteTitulos: [],
                tramitePracticasPreProfesionales: [],
                readmisiones: [],
                cambioPlanCurriculares: [],
            });

        },
        toggleSeleccionado(context) {

            context.commit('TOGGLE_STATE_SELECCIONADO');

        },
        setFilterFacultad(context, facultad) {

            context.commit('SET_STATE_FILTER_FACULTAD', {...facultad});

        },
    }
});

var AppliedFilter = {
    methods: {
        filtroFacultadSeleccionado(ofi, item) {
            let $vue = this;
            if (!item.alumno) {
                return true;
            }
            if (!$vue.visualizarSoloSeleccionados && (ofi != null && ofi.instanciaOficina != item.alumno.carrera.facultad.id)) {
                return false;
            } else if ($vue.visualizarSoloSeleccionados && !item.seleccionado) {
                return false;
            }
            return true;
        },
    }
}