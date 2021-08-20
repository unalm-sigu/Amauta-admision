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
    },
    mutations: {
        SET_RESOLUCION(state, resolucion) {
            state.resolucion = resolucion;
        }
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
    }
});