Vue.use(Vuex);
const store = new Vuex.Store({
    state: {
        alumno: {persona:{}},
    },
    mutations: {
        SET_ALUMNO(state, alumno) {
            state.alumno = alumno;
        },
    },
    getters: {
        getAlumno: state => state.alumno,
    },
    actions: {
        async fetchAlumno(context) {
            let response = await axios.get("/academico/historico/alumno/" + ID_ALUMNO+"/find");
            context.commit('SET_ALUMNO', response.data);
        },
    }
});
