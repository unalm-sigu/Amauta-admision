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
        SET_STATE_REMOVE_FILTER_FACULTAD(state) {
            state.filterFacultad = null;
        },
        SET_STATE_IS_EDICION(state) {
            state.isEdicion = true;
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
        removeFilterFacultad(context) {

            context.commit('SET_STATE_REMOVE_FILTER_FACULTAD');

        },
        setIsEdicion(context) {

            context.commit('SET_STATE_IS_EDICION');

        },
    }
});

var AppliedFilter = {
    methods: {
        filtroFacultadSeleccionado(filtroOficina, item) {
            let $vue = this;
            if (!item.alumno) {
                return true;
            }
            if ($vue.visualizarSoloSeleccionados && filtroOficina != null) {
                return ($vue.visualizarSoloSeleccionados && (filtroOficina.instanciaOficina == item.alumno.carrera.facultad.id));
            }
            if ($vue.visualizarSoloSeleccionados) {
                return item.seleccionado;
            }
            if (filtroOficina) {
                return filtroOficina.instanciaOficina == item.alumno.carrera.facultad.id;
            }
            return true;
        },
    }
}