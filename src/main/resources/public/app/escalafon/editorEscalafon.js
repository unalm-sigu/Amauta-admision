const package = '/app/escalafon';


const EditorEscalafon = httpVueLoader(package + '/EditorEscalafon.vue');

const GeneralEscalafon = httpVueLoader(package + '/general/GeneralEscalafon.vue');
const IdiomaEscalafon = httpVueLoader(package + '/idioma/IdiomaEscalafon.vue');
const DistincionEscalafon = httpVueLoader(package + '/distincion/DistincionEscalafon.vue');
const AcademicaEscalafon = httpVueLoader(package + '/academico/AcademicoEscalafon.vue');
const ExperienciaEscalafon = httpVueLoader(package + '/experiencia/ExperienciaEscalafon.vue');
const ExperienciaAsesor = httpVueLoader(package + '/experienciaAsesor/ExperienciaAsesor.vue');
const ProduccionEscalafon = httpVueLoader(package + '/produccion/ProduccionEscalafon.vue');
const InvestigacionEscalafon = httpVueLoader(package + '/investigacion/InvestigacionEscalafon.vue');


const router = new VueRouter({
    routes: [
        {
            name: 'editor', path: '/', redirect: '/general', component: EditorEscalafon,
            children: [
                {name: 'general', path: 'general', component: GeneralEscalafon},
                {name: 'idioma', path: 'idioma', component: IdiomaEscalafon},
                {name: 'distincion', path: 'distincion', component: DistincionEscalafon},
                {name: 'academico', path: 'academico', component: AcademicaEscalafon},
                {name: 'experiencia', path: 'experiencia', component: ExperienciaEscalafon},
                {name: 'experienciaAsesor', path: 'experiencia/asesor', component: ExperienciaAsesor},
                {name: 'investigacion', path: 'investigacion', component: InvestigacionEscalafon},
                {name: 'produccion', path: 'produccion', component: ProduccionEscalafon}
            ]
        }
    ]
});

const store = new Vuex.Store({
    state: {
        escalafon: {id: null}
    },
    mutations: {
        SET_ESCALAFON(state, escalafon) {
            state.escalafon = escalafon;
        },
        SET_LIST_PRODUCCION(state, listItem) {
            state.escalafon.produccionEscalafon = listItem;
        },
        SET_LIST_INVESTIGACION(state, listItem) {
            state.escalafon.investigacionEscalafon = listItem;
        },
        SET_LIST_EXP_ASESOR(state, listItem) {
            state.escalafon.experienciaAsesor = listItem;
        },
        SET_LIST_EXP(state, listItem) {
            state.escalafon.experienciaEscalafon = listItem;
        },
        SET_LIST_ACADEMICO(state, listItem) {
            state.escalafon.academicoEscalafon = listItem;
        },
        SET_LIST_DISTINCION(state, listItem) {
            state.escalafon.distincionEscalafon = listItem;
        },
        SET_LIST_IDIOMA(state, listItem) {
            state.escalafon.idiomaEscalafon = null;
            setTimeout(function () {
                state.escalafon.idiomaEscalafon = listItem;
            }, 500);
        }
    },
    actions: {
    }
});

const CONFIG_DATE = {format: 'DD/MM/YYYY', locale: 'es', showClear: true, showClose: true};


new Vue({
    el: '#escalafonvue',
    router,
    store
})


